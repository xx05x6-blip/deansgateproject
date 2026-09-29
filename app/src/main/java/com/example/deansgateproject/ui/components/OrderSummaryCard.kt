package com.example.deansgateproject.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deansgateproject.data.model.ApartmentAddress
import com.example.deansgateproject.data.model.MenuItem
import com.example.deansgateproject.data.model.Order
import com.example.deansgateproject.data.model.OrderItem
import com.example.deansgateproject.data.model.OrderStatus
import com.example.deansgateproject.data.model.UserRole
import com.example.deansgateproject.ui.theme.CardBorderColor
import com.example.deansgateproject.ui.theme.DarkVioletSurface
import com.example.deansgateproject.ui.theme.DarkVioletSurfaceVariant
import com.example.deansgateproject.ui.theme.DeansgateProjectTheme
import com.example.deansgateproject.ui.theme.DividerColor
import com.example.deansgateproject.ui.theme.PillActiveContainer
import com.example.deansgateproject.ui.theme.PillInactiveContainer
import com.example.deansgateproject.ui.theme.TextMuted
import com.example.deansgateproject.ui.theme.TextSubtitle
import com.example.deansgateproject.ui.theme.TextWhite
import com.example.deansgateproject.ui.theme.VibrantVioletDark
import com.example.deansgateproject.ui.theme.VibrantVioletLight
import com.example.deansgateproject.ui.theme.VibrantVioletPrimary
import com.example.deansgateproject.ui.theme.VibrantVioletSecondary

@Composable
fun OrderSummaryCard(
    order: Order,
    userRole: UserRole,
    onStatusChange: (OrderStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor by animateColorAsState(
        targetValue = when (order.status) {
            OrderStatus.PLACED -> Color(0xFFF59E0B)   // Amber
            OrderStatus.ACCEPTED -> Color(0xFF3B82F6) // Blue
            OrderStatus.PICKED_UP -> VibrantVioletLight // Violet Accent
            OrderStatus.DELIVERED -> Color(0xFF10B981) // Emerald Green
            OrderStatus.CANCELED -> Color(0xFFEF4444)  // Red
        },
        label = "statusColor"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkVioletSurface
        ),
        border = BorderStroke(1.dp, CardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Store Name & Order ID & Status Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PillInactiveContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Storefront,
                            contentDescription = null,
                            tint = VibrantVioletLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = order.storeName,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${order.id} • ${order.formattedTime}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = order.status.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order Progress Indicator (if active)
            if (!order.status.isTerminal) {
                val progress = when (order.status) {
                    OrderStatus.PLACED -> 0.25f
                    OrderStatus.ACCEPTED -> 0.50f
                    OrderStatus.PICKED_UP -> 0.75f
                    else -> 1.0f
                }
                Column(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = statusColor,
                        trackColor = PillInactiveContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order.status.description,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = TextSubtitle
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(10.dp))

            // Items Summary
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                order.items.forEach { item ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${item.quantity}x ${item.menuItem.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = item.formattedTotalPrice,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextSubtitle
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Special notes if present
            if (!order.notes.isNullOrBlank()) {
                Text(
                    text = "Note: ${order.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSubtitle,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Total Price & Address Line
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkVioletSurfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = VibrantVioletLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${order.residentName} (${order.deliveryAddress.formattedAddress})",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = order.formattedTotalPrice,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = VibrantVioletLight
                )
            }

            // Role-Based Action Buttons
            Spacer(modifier = Modifier.height(12.dp))
            RoleOrderActions(
                order = order,
                role = userRole,
                onStatusChange = onStatusChange
            )
        }
    }
}

@Composable
private fun RoleOrderActions(
    order: Order,
    role: UserRole,
    onStatusChange: (OrderStatus) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        when (role) {
            UserRole.RESIDENT -> {
                if (order.status == OrderStatus.PLACED) {
                    OutlinedButton(
                        onClick = { onStatusChange(OrderStatus.CANCELED) },
                        border = BorderStroke(1.dp, CardBorderColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Cancel Order")
                    }
                }
            }

            UserRole.RUNNER -> {
                when (order.status) {
                    OrderStatus.PLACED -> {
                        GradientButton(
                            onClick = { onStatusChange(OrderStatus.ACCEPTED) },
                            icon = Icons.Rounded.CheckCircle,
                            text = "Accept Order"
                        )
                    }

                    OrderStatus.ACCEPTED -> {
                        GradientButton(
                            onClick = { onStatusChange(OrderStatus.PICKED_UP) },
                            icon = Icons.AutoMirrored.Rounded.DirectionsRun,
                            text = "Mark Picked Up"
                        )
                    }

                    OrderStatus.PICKED_UP -> {
                        GradientButton(
                            onClick = { onStatusChange(OrderStatus.DELIVERED) },
                            icon = Icons.Rounded.CheckCircle,
                            text = "Confirm Delivered"
                        )
                    }

                    else -> {}
                }
            }

            UserRole.ADMIN -> {
                if (!order.status.isTerminal) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onStatusChange(OrderStatus.CANCELED) },
                            border = BorderStroke(1.dp, CardBorderColor),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Cancel")
                        }

                        val nextStatus = when (order.status) {
                            OrderStatus.PLACED -> OrderStatus.ACCEPTED
                            OrderStatus.ACCEPTED -> OrderStatus.PICKED_UP
                            OrderStatus.PICKED_UP -> OrderStatus.DELIVERED
                            else -> null
                        }

                        if (nextStatus != null) {
                            GradientButton(
                                onClick = { onStatusChange(nextStatus) },
                                text = "Advance: ${nextStatus.displayName}"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GradientButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    enabled: Boolean = true
) {
    val gradient = Brush.horizontalGradient(
        listOf(
            Color(0xFF5B21B6),
            Color(0xFF8B5CF6)
        )
    )

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        modifier = modifier.clip(RoundedCornerShape(20.dp))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(gradient)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderSummaryCardPreview() {
    val sampleMenuItem = MenuItem("m1", "s1", "Fresh Sourdough Loaf", "Local bakery", 4.50, "Bakery")
    val sampleOrder = Order(
        id = "ORD-2026-101",
        residentId = "RES-1204",
        residentName = "Sophia Sterling",
        storeId = "s1",
        storeName = "Deansgate General Store",
        items = listOf(OrderItem(sampleMenuItem, 2)),
        totalPrice = 10.50,
        status = OrderStatus.PLACED,
        deliveryAddress = ApartmentAddress("South Tower", "1204"),
        notes = "Leave at front door"
    )

    DeansgateProjectTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            OrderSummaryCard(
                order = sampleOrder,
                userRole = UserRole.RESIDENT,
                onStatusChange = {}
            )
        }
    }
}
