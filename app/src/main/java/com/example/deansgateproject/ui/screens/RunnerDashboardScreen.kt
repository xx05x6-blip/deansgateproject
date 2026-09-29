package com.example.deansgateproject.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Elevator
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deansgateproject.data.model.Order
import com.example.deansgateproject.data.model.OrderStatus
import com.example.deansgateproject.data.repository.DeliveryRepository
import com.example.deansgateproject.ui.components.GradientButton
import com.example.deansgateproject.ui.theme.CardBorderColor
import com.example.deansgateproject.ui.theme.DarkVioletSurface
import com.example.deansgateproject.ui.theme.DeansgateProjectTheme
import com.example.deansgateproject.ui.theme.DeepObsidianBackground
import com.example.deansgateproject.ui.theme.DividerColor
import com.example.deansgateproject.ui.theme.PillActiveContainer
import com.example.deansgateproject.ui.theme.PillInactiveContainer
import com.example.deansgateproject.ui.theme.TextMuted
import com.example.deansgateproject.ui.theme.TextSubtitle
import com.example.deansgateproject.ui.theme.TextWhite
import com.example.deansgateproject.ui.theme.VibrantVioletLight
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun RunnerDashboardScreen(
    modifier: Modifier = Modifier,
    repository: DeliveryRepository = DeliveryRepository
) {
    val orders by repository.orders.collectAsState()
    val isRunnerOnline by repository.isRunnerOnline.collectAsState()

    var selectedTowerFilter by remember { mutableStateOf("ALL") }
    var sortHighestFloorFirst by remember { mutableStateOf(true) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Tower filtering
    val towers = listOf("ALL", "SOUTH", "WEST", "EAST", "NORTH")

    val filteredOrders = orders.filter { order ->
        if (selectedTowerFilter == "ALL") true
        else order.deliveryAddress.tower.uppercase(Locale.UK).contains(selectedTowerFilter)
    }.sortedWith { o1, o2 ->
        val floor1 = o1.deliveryAddress.apartmentNumber.take(2).toIntOrNull() ?: 0
        val floor2 = o2.deliveryAddress.apartmentNumber.take(2).toIntOrNull() ?: 0
        if (sortHighestFloorFirst) floor2.compareTo(floor1) else floor1.compareTo(floor2)
    }

    val pendingOrders = orders.filter { it.status == OrderStatus.PLACED || it.status == OrderStatus.ACCEPTED }
    val inTransitOrders = orders.filter { it.status == OrderStatus.PICKED_UP }
    val deliveredTodayOrders = orders.filter { it.status == OrderStatus.DELIVERED }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DeepObsidianBackground)
        ) {
            // Header: Runner Status Switch & Quick Elevator Mode Toggle
            Surface(
                color = DarkVioletSurface,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isRunnerOnline) Color(0xFF10B981).copy(alpha = 0.2f)
                                        else PillInactiveContainer
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.DirectionsRun,
                                    contentDescription = null,
                                    tint = if (isRunnerOnline) Color(0xFF10B981) else TextMuted,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "RUNNER DISPATCH HUB",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = VibrantVioletLight,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isRunnerOnline) Color(0xFF10B981) else Color(0xFFEF4444))
                                    )
                                }
                                Text(
                                    text = if (isRunnerOnline) "Active & Ready for Lift Drops" else "Runner Status: Offline",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isRunnerOnline) "ONLINE" else "OFFLINE",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isRunnerOnline) Color(0xFF10B981) else TextMuted
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = isRunnerOnline,
                                onCheckedChange = { isChecked ->
                                    repository.setRunnerOnline(isChecked)
                                    scope.launch {
                                        val statusText = if (isChecked) "Runner Online" else "Runner Offline"
                                        snackbarHostState.showSnackbar("Runner status changed to $statusText")
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TextWhite,
                                    checkedTrackColor = PillActiveContainer,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = PillInactiveContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Metrics Strip
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MetricCard(
                            label = "Pending",
                            value = pendingOrders.size.toString(),
                            accentColor = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "In Transit",
                            value = inTransitOrders.size.toString(),
                            accentColor = VibrantVioletLight,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "Delivered",
                            value = deliveredTodayOrders.size.toString(),
                            accentColor = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Tower Filter Chips & Lift Direction Sort Toggle (Pill Toggles)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Modern Tower Filter Pill Strip
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = PillInactiveContainer,
                    border = BorderStroke(1.dp, CardBorderColor),
                    modifier = Modifier.weight(1f)
                ) {
                    LazyRow(
                        modifier = Modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(towers) { tower ->
                            val isSelected = selectedTowerFilter == tower
                            Surface(
                                onClick = { selectedTowerFilter = tower },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) PillActiveContainer else Color.Transparent
                            ) {
                                Text(
                                    text = tower,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) TextWhite else TextMuted,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Elevator Sort Toggle Pill
                Surface(
                    onClick = { sortHighestFloorFirst = !sortHighestFloorFirst },
                    shape = RoundedCornerShape(20.dp),
                    color = PillInactiveContainer,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Elevator,
                            contentDescription = "Lift Sort",
                            tint = VibrantVioletLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (sortHighestFloorFirst) "Floor 65➔1" else "Floor 1➔65",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                }
            }

            // Main Delivery Orders List
            if (filteredOrders.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.DirectionsRun,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No active delivery runs found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Select 'ALL' towers or wait for new resident orders.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredOrders, key = { it.id }) { order ->
                        RunnerOrderCard(
                            order = order,
                            onAdvanceStatus = { newStatus ->
                                repository.updateOrderStatus(
                                    orderId = order.id,
                                    newStatus = newStatus,
                                    runnerId = "RUNNER-01",
                                    runnerName = "Alex Concierge"
                                )
                                scope.launch {
                                    snackbarHostState.showSnackbar("${order.id} status updated to ${newStatus.displayName}")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PillInactiveContainer,
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = accentColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun RunnerOrderCard(
    order: Order,
    onAdvanceStatus: (OrderStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (order.status) {
        OrderStatus.PLACED -> Color(0xFFF59E0B)
        OrderStatus.ACCEPTED -> Color(0xFF3B82F6)
        OrderStatus.PICKED_UP -> VibrantVioletLight
        OrderStatus.DELIVERED -> Color(0xFF10B981)
        OrderStatus.CANCELED -> Color(0xFFEF4444)
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkVioletSurface),
        border = BorderStroke(1.dp, CardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Address & Tower Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Apt ${order.deliveryAddress.apartmentNumber}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PillActiveContainer,
                            border = BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Text(
                                text = order.deliveryAddress.tower.uppercase(Locale.UK),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = null,
                            tint = VibrantVioletLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${order.residentName} (${order.residentPhone})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = order.status.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(10.dp))

            // Order items and store info
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "From: ${order.storeName}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = VibrantVioletLight
                )
                Text(
                    text = order.formattedTotalPrice,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                order.items.forEach { item ->
                    Text(
                        text = "• ${item.quantity}x ${item.menuItem.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (!order.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PillInactiveContainer,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Notes,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = order.notes,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = TextSubtitle
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Workflow Buttons: PLACED -> ACCEPTED -> PICKED_UP -> DELIVERED
            when (order.status) {
                OrderStatus.PLACED -> {
                    GradientButton(
                        onClick = { onAdvanceStatus(OrderStatus.ACCEPTED) },
                        icon = Icons.Rounded.CheckCircle,
                        text = "Accept Order (Step 1/3)",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OrderStatus.ACCEPTED -> {
                    GradientButton(
                        onClick = { onAdvanceStatus(OrderStatus.PICKED_UP) },
                        icon = Icons.Rounded.Elevator,
                        text = "Mark Picked Up & Enter Lift (Step 2/3)",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OrderStatus.PICKED_UP -> {
                    GradientButton(
                        onClick = { onAdvanceStatus(OrderStatus.DELIVERED) },
                        icon = Icons.Rounded.CheckCircle,
                        text = "Confirm Delivered to Door (Step 3/3)",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OrderStatus.DELIVERED -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Delivery Completed Successfully", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                    }
                }

                OrderStatus.CANCELED -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Order Canceled",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RunnerDashboardScreenPreview() {
    DeansgateProjectTheme {
        RunnerDashboardScreen()
    }
}
