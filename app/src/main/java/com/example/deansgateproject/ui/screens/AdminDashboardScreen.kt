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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deansgateproject.data.model.MenuItem
import com.example.deansgateproject.data.model.Order
import com.example.deansgateproject.data.model.OrderStatus
import com.example.deansgateproject.data.model.Store
import com.example.deansgateproject.data.model.UserRole
import com.example.deansgateproject.data.repository.DeliveryRepository
import com.example.deansgateproject.ui.components.GradientButton
import com.example.deansgateproject.ui.components.OrderSummaryCard
import com.example.deansgateproject.ui.theme.CardBorderColor
import com.example.deansgateproject.ui.theme.DarkVioletSurface
import com.example.deansgateproject.ui.theme.DarkVioletSurfaceVariant
import com.example.deansgateproject.ui.theme.DeansgateProjectTheme
import com.example.deansgateproject.ui.theme.DeepObsidianBackground
import com.example.deansgateproject.ui.theme.DividerColor
import com.example.deansgateproject.ui.theme.DressesCategoryColor
import com.example.deansgateproject.ui.theme.ElectronicsCategoryColor
import com.example.deansgateproject.ui.theme.FoodDrinkCategoryColor
import com.example.deansgateproject.ui.theme.GroceriesCategoryColor
import com.example.deansgateproject.ui.theme.OtherCategoryColor
import com.example.deansgateproject.ui.theme.PillActiveContainer
import com.example.deansgateproject.ui.theme.PillInactiveContainer
import com.example.deansgateproject.ui.theme.TextMuted
import com.example.deansgateproject.ui.theme.TextSubtitle
import com.example.deansgateproject.ui.theme.TextWhite
import com.example.deansgateproject.ui.theme.VibrantVioletDark
import com.example.deansgateproject.ui.theme.VibrantVioletLight
import com.example.deansgateproject.ui.theme.VibrantVioletPrimary
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    modifier: Modifier = Modifier,
    repository: DeliveryRepository = DeliveryRepository
) {
    val stores by repository.stores.collectAsState()
    val menuItems by repository.menuItems.collectAsState()
    val orders by repository.orders.collectAsState()
    val isRunnerOnline by repository.isRunnerOnline.collectAsState()

    var selectedAdminSection by remember { mutableIntStateOf(0) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showChangeRunnerPasswordDialog by remember { mutableStateOf(false) }
    var showCsvImportDialog by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<MenuItem?>(null) }

    val availableRunners = repository.availableRunners
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Pipeline Metrics
    val totalOrders = orders.size
    val pendingDispatches = orders.count { it.status == OrderStatus.PLACED || it.status == OrderStatus.ACCEPTED }
    val activeRunners = if (isRunnerOnline) 1 else 0
    val grossRevenue = orders.sumOf { it.totalPrice }
    val formattedRevenue = String.format(Locale.UK, "£%.2f", grossRevenue)

    val adminSections = listOf(
        "Live Orders ($totalOrders)",
        "Dispatch 🚀 ($pendingDispatches)",
        "Analytics 📊",
        "Stores (${stores.size})",
        "Stock & CSV (${menuItems.size})"
    )

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
            // Top Bar & Pipeline Metrics Dashboard Card
            Surface(
                color = DarkVioletSurface,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Row 1: Title on left, LIVE status badge on right
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
                                    .background(PillInactiveContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = VibrantVioletLight,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "OPERATIONS CONTROL",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantVioletLight,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Admin Hub & Spend Analytics",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "LIVE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 2: Action buttons side-by-side
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Change Admin Password 🔑 Action Button
                        Surface(
                            onClick = { showChangePasswordDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = VibrantVioletPrimary.copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, VibrantVioletLight.copy(alpha = 0.5f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Key,
                                    contentDescription = "Admin Password",
                                    tint = VibrantVioletLight,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Admin Pass 🔑",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantVioletLight,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }

                        // Change Runner Password 🏃‍♂️ Action Button
                        Surface(
                            onClick = { showChangeRunnerPasswordDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.DirectionsRun,
                                    contentDescription = "Runner Password",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Runner Pass 🏃",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }

                        // Lock / Logout Admin Button
                        Surface(
                            onClick = { repository.logoutAdmin() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = "Lock Admin",
                                    tint = Color(0xFFFCA5A5),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Lock Admin 🔒",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFCA5A5),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pipeline Metrics Grid
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AdminMetricTile(
                            icon = Icons.AutoMirrored.Rounded.ReceiptLong,
                            label = "Total Orders",
                            value = totalOrders.toString(),
                            accentColor = VibrantVioletLight,
                            modifier = Modifier.weight(1f)
                        )
                        AdminMetricTile(
                            icon = Icons.Rounded.LocalShipping,
                            label = "Pending",
                            value = pendingDispatches.toString(),
                            accentColor = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f)
                        )
                        AdminMetricTile(
                            icon = Icons.AutoMirrored.Rounded.DirectionsRun,
                            label = "Runners",
                            value = activeRunners.toString(),
                            accentColor = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )
                        AdminMetricTile(
                            icon = Icons.Rounded.BarChart,
                            label = "Revenue",
                            value = formattedRevenue,
                            accentColor = Color(0xFF3B82F6),
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                }
            }

            // Modern Pill Toggle Buttons for Admin Sections
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = PillInactiveContainer,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                LazyRow(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(adminSections.size) { index ->
                        val isSelected = selectedAdminSection == index
                        Surface(
                            onClick = { selectedAdminSection = index },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) PillActiveContainer else Color.Transparent
                        ) {
                            Text(
                                text = adminSections[index],
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) TextWhite else TextMuted,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Tab Content
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                when (selectedAdminSection) {
                    0 -> {
                        // Section 0: Live Orders Controls
                        if (orders.isEmpty()) {
                            item {
                                Text(
                                    text = "No active orders recorded yet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted
                                )
                            }
                        } else {
                            items(orders, key = { it.id }) { order ->
                                OrderSummaryCard(
                                    order = order,
                                    userRole = UserRole.ADMIN,
                                    onStatusChange = { newStatus ->
                                        repository.updateOrderStatus(order.id, newStatus)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Order ${order.id} status changed to ${newStatus.displayName}")
                                        }
                                    }
                                )
                            }
                        }
                    }

                    1 -> {
                        // Section 1: Dispatch & Runner Selection Control
                        if (orders.isEmpty()) {
                            item {
                                Text(
                                    text = "No pending orders to dispatch.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted
                                )
                            }
                        } else {
                            items(orders, key = { it.id }) { order ->
                                DispatchOrderCard(
                                    order = order,
                                    availableRunners = availableRunners,
                                    onDispatch = { selectedRunner ->
                                        repository.assignRunnerAndDispatch(order.id, selectedRunner)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Dispatched Order ${order.id} to $selectedRunner 🚀")
                                        }
                                    }
                                )
                            }
                        }
                    }

                    2 -> {
                        // Section 2: Spend/Analytics Bar Chart & Circular Breakdown
                        item {
                            SpendAnalyticsBarChartSection()
                        }
                        item {
                            CircularCategoryBreakdownSection()
                        }
                    }

                    3 -> {
                        // Section 3: Store Settings & Fee Adjustment
                        items(stores, key = { it.id }) { store ->
                            AdminStoreCard(
                                store = store,
                                onToggleOpen = {
                                    repository.toggleStoreOpen(store.id)
                                    scope.launch {
                                        val statusText = if (!store.isOpen) "Opened" else "Closed"
                                        snackbarHostState.showSnackbar("${store.name} is now $statusText")
                                    }
                                },
                                onUpdateFee = { newFee ->
                                    repository.updateStoreDeliveryFee(store.id, newFee)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Updated delivery fee for ${store.name}")
                                    }
                                }
                            )
                        }
                    }

                    4 -> {
                        // Section 4: Store Stock, Product Catalog & CSV Import
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = DarkVioletSurface,
                                border = BorderStroke(1.dp, CardBorderColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Product & Price Management",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "Add or edit products, change prices, descriptions, images and stock",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSubtitle
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        GradientButton(
                                            onClick = { showAddProductDialog = true },
                                            text = "+ Add Item"
                                        )
                                        GradientButton(
                                            onClick = { showCsvImportDialog = true },
                                            text = "CSV 📄"
                                        )
                                    }
                                }
                            }
                        }

                        items(menuItems, key = { it.id }) { item ->
                            val storeName = stores.find { it.id == item.storeId }?.name ?: "Deansgate Store"
                            AdminStockItemCard(
                                item = item,
                                storeName = storeName,
                                onToggleAvailability = {
                                    repository.toggleMenuItemAvailability(item.id)
                                    scope.launch {
                                        val stockState = if (!item.isAvailable) "In Stock" else "Out of Stock"
                                        snackbarHostState.showSnackbar("${item.name} set to $stockState")
                                    }
                                },
                                onEdit = { editingProduct = item },
                                onDelete = {
                                    repository.deleteMenuItem(item.id)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Deleted ${item.name}")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showChangePasswordDialog) {
            ChangeAdminPasswordDialog(
                onDismiss = { showChangePasswordDialog = false },
                repository = repository,
                onSuccess = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Admin password updated successfully! 🔑")
                    }
                }
            )
        }

        if (showChangeRunnerPasswordDialog) {
            ChangeRunnerPasswordDialog(
                onDismiss = { showChangeRunnerPasswordDialog = false },
                repository = repository,
                onSuccess = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Runner password updated successfully! 🏃‍♂️")
                    }
                }
            )
        }

        if (showCsvImportDialog) {
            CsvImportModalDialog(
                onDismiss = { showCsvImportDialog = false },
                onImport = { csvText ->
                    val result = repository.importCsvPrices(csvText)
                    result.onSuccess { count ->
                        scope.launch {
                            snackbarHostState.showSnackbar("CSV Import complete: $count item prices updated! 📄")
                        }
                    }.onFailure { err ->
                        scope.launch {
                            snackbarHostState.showSnackbar("CSV Import error: ${err.message}")
                        }
                    }
                }
            )
        }

        if (showAddProductDialog || editingProduct != null) {
            EditProductModalDialog(
                item = editingProduct,
                onDismiss = {
                    showAddProductDialog = false
                    editingProduct = null
                },
                onSave = { name, description, price, category, imageUrl, isAvailable ->
                    if (editingProduct != null) {
                        repository.updateMenuItem(
                            editingProduct!!.copy(
                                name = name,
                                description = description,
                                price = price,
                                category = category,
                                imageUrl = imageUrl,
                                isAvailable = isAvailable
                            )
                        )
                        scope.launch {
                            snackbarHostState.showSnackbar("Updated $name successfully!")
                        }
                    } else {
                        repository.addMenuItem(
                            name = name,
                            description = description,
                            price = price,
                            category = category,
                            imageUrl = imageUrl
                        )
                        scope.launch {
                            snackbarHostState.showSnackbar("Added $name to catalog!")
                        }
                    }
                    showAddProductDialog = false
                    editingProduct = null
                }
            )
        }
    }
}

@Composable
fun AdminMetricTile(
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PillInactiveContainer),
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = TextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Spend/Analytics Bar Chart Composable with Timeframe & Payment Pill Toggles
// ---------------------------------------------------------------------------
@Composable
fun SpendAnalyticsBarChartSection() {
    var timeframePeriod by remember { mutableStateOf("Weekly") } // "Weekly" or "Monthly"
    var paymentFilter by remember { mutableStateOf("Debit") }   // "Debit" or "Credit"

    val weeklyDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val weeklySpend = listOf(320f, 480f, 650f, 520f, 890f, 1120f, 940f)
    val maxWeeklySpend = 1200f

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkVioletSurface),
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row with Title & Payment Pill Switcher ("Debit | Credit")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "ANALYTICS & SPEND CHART",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = VibrantVioletLight,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Revenue & Order Volume",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                // Payment Method Pill Toggle Button ("Debit | Credit")
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PillInactiveContainer,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        listOf("Debit", "Credit").forEach { type ->
                            val isSelected = paymentFilter == type
                            Surface(
                                onClick = { paymentFilter = type },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) PillActiveContainer else Color.Transparent
                            ) {
                                Text(
                                    text = type,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) TextWhite else TextMuted,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timeframe Period Pill Switcher ("Weekly | Monthly")
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DarkVioletSurfaceVariant,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Weekly", "Monthly").forEach { period ->
                        val isSelected = timeframePeriod == period
                        Surface(
                            onClick = { timeframePeriod = period },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) PillActiveContainer else Color.Transparent,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = period,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = if (isSelected) TextWhite else TextMuted,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bar Chart Visualization
            val barGradient = Brush.verticalGradient(
                listOf(
                    VibrantVioletLight,
                    VibrantVioletDark
                )
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                weeklyDays.forEachIndexed { index, day ->
                    val spendVal = weeklySpend[index]
                    val heightRatio = (spendVal / maxWeeklySpend).coerceIn(0.1f, 1f)
                    val isPeak = spendVal == weeklySpend.maxOrNull()

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Spend label above bar
                        Text(
                            text = "£${spendVal.toInt()}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPeak) VibrantVioletLight else TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // Vertical Bar Track and Fill
                        Box(
                            contentAlignment = Alignment.BottomCenter,
                            modifier = Modifier
                                .width(18.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(PillInactiveContainer)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((100 * heightRatio).dp)
                                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                    .background(if (isPeak) barGradient else Brush.verticalGradient(listOf(PillActiveContainer, VibrantVioletDark)))
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        // Day Label
                        Text(
                            text = day,
                            fontSize = 11.sp,
                            fontWeight = if (isPeak) FontWeight.Bold else FontWeight.Medium,
                            color = if (isPeak) TextWhite else TextMuted
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Circular Breakdown Percentage Badges Section
// ---------------------------------------------------------------------------
data class CategoryBreakdownItem(
    val categoryName: String,
    val percentageText: String, // e.g. "45%" or "25%"
    val percentageValue: Float, // e.g. 0.45f
    val color: Color,
    val icon: ImageVector
)

@Composable
fun CircularCategoryBreakdownSection() {
    val breakdownItems = listOf(
        CategoryBreakdownItem("Food & Drinks", "45%", 0.45f, FoodDrinkCategoryColor, Icons.Rounded.Fastfood),
        CategoryBreakdownItem("Dresses & Fashion", "25%", 0.25f, DressesCategoryColor, Icons.Rounded.Checkroom),
        CategoryBreakdownItem("Groceries & Wine", "15%", 0.15f, GroceriesCategoryColor, Icons.Rounded.LocalGroceryStore),
        CategoryBreakdownItem("Tech & Electronics", "10%", 0.10f, ElectronicsCategoryColor, Icons.Rounded.Devices),
        CategoryBreakdownItem("Other Services", "5%", 0.05f, OtherCategoryColor, Icons.Rounded.PieChart)
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkVioletSurface),
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "SPEND BREAKDOWN METRICS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = VibrantVioletLight,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Category Revenue Distribution",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PillInactiveContainer,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Text(
                        text = "100% TOTAL",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextSubtitle,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Circular Breakdown Badges Grid / List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                breakdownItems.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkVioletSurfaceVariant,
                        border = BorderStroke(1.dp, CardBorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Circular Percentage Badge
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(item.color.copy(alpha = 0.2f))
                                        .background(Brush.radialGradient(listOf(item.color.copy(alpha = 0.3f), Color.Transparent)))
                                ) {
                                    Text(
                                        text = item.percentageText,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Black,
                                        color = item.color
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = item.categoryName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null,
                                            tint = item.color,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Tower resident order distribution",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }

                            // Circular category badge indicator
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = item.color.copy(alpha = 0.15f)
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = item.color,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStoreCard(
    store: Store,
    onToggleOpen: () -> Unit,
    onUpdateFee: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var feeInput by remember(store.deliveryFee) { mutableStateOf(String.format(Locale.UK, "%.2f", store.deliveryFee)) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkVioletSurface),
        border = BorderStroke(1.dp, CardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "${store.category} • ${store.address}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (store.isOpen) "OPEN" else "CLOSED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (store.isOpen) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = store.isOpen,
                        onCheckedChange = { onToggleOpen() },
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
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(12.dp))

            // Delivery Fee Adjustment Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Vertical Delivery Fee (£)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = VibrantVioletLight
                    )
                    Text(
                        text = "Current: ${store.formattedDeliveryFee}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = feeInput,
                        onValueChange = { feeInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantVioletLight,
                            unfocusedBorderColor = CardBorderColor,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.width(90.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    GradientButton(
                        onClick = {
                            val feeVal = feeInput.toDoubleOrNull()
                            if (feeVal != null && feeVal >= 0) {
                                onUpdateFee(feeVal)
                            }
                        },
                        text = "Save Fee"
                    )
                }
            }
        }
    }
}

@Composable
fun AdminStockItemCard(
    item: MenuItem,
    storeName: String,
    onToggleAvailability: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isAvailable) DarkVioletSurface
            else DarkVioletSurfaceVariant
        ),
        border = BorderStroke(1.dp, CardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "$storeName • ${item.category} • ${item.formattedPrice}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = VibrantVioletLight
                    )
                    if (item.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (item.imageUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "📷 Image URL: ${item.imageUrl}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSubtitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit Product",
                            tint = VibrantVioletLight
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete Product",
                            tint = Color(0xFFEF4444)
                        )
                    }
                    Switch(
                        checked = item.isAvailable,
                        onCheckedChange = { onToggleAvailability() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextWhite,
                            checkedTrackColor = PillActiveContainer,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = PillInactiveContainer
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun EditProductModalDialog(
    item: MenuItem?,
    onDismiss: () -> Unit,
    onSave: (name: String, description: String, price: Double, category: String, imageUrl: String, isAvailable: Boolean) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var description by remember { mutableStateOf(item?.description ?: "") }
    var priceStr by remember { mutableStateOf(item?.price?.let { String.format(Locale.UK, "%.2f", it) } ?: "") }
    var category by remember { mutableStateOf(item?.category ?: "General") }
    var imageUrl by remember { mutableStateOf(item?.imageUrl ?: "") }
    var isAvailable by remember { mutableStateOf(item?.isAvailable ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkVioletSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = if (item == null) "Add New Product 🛍️" else "Edit Product & Price ✏️",
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantVioletLight,
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Price (£)", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantVioletLight,
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantVioletLight,
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantVioletLight,
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantVioletLight,
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("In Stock Availability", color = TextWhite, style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = { isAvailable = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextWhite,
                            checkedTrackColor = PillActiveContainer,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = PillInactiveContainer
                        )
                    )
                }
            }
        },
        confirmButton = {
            GradientButton(
                onClick = {
                    val p = priceStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onSave(name, description, p, category, imageUrl, isAvailable)
                    }
                },
                text = if (item == null) "Add Product" else "Save Changes"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun DispatchOrderCard(
    order: Order,
    availableRunners: List<String>,
    onDispatch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRunner by remember { mutableStateOf(availableRunners.firstOrNull() ?: "Alex Concierge") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkVioletSurface),
        border = BorderStroke(1.dp, CardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Order ${order.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "To: ${order.residentName} (${order.deliveryAddress.formattedAddress})",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
                ) {
                    Text(
                        text = order.status.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "SELECT ACTIVE RUNNER FOR DISPATCH:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = TextSubtitle
            )
            Spacer(modifier = Modifier.height(6.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    onClick = { isDropdownExpanded = true },
                    shape = RoundedCornerShape(16.dp),
                    color = PillInactiveContainer,
                    border = BorderStroke(1.dp, CardBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.DirectionsRun,
                                contentDescription = null,
                                tint = VibrantVioletLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedRunner,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                        Text("▼", color = TextMuted, fontSize = 12.sp)
                    }
                }

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier.background(DarkVioletSurfaceVariant)
                ) {
                    availableRunners.forEach { runner ->
                        DropdownMenuItem(
                            text = { Text(runner, color = TextWhite) },
                            onClick = {
                                selectedRunner = runner
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GradientButton(
                onClick = { onDispatch(selectedRunner) },
                icon = Icons.Rounded.LocalShipping,
                text = "Dispatch Order to $selectedRunner",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CsvImportModalDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    var csvText by remember {
        mutableStateOf(
            "Item Name, Price\nFresh Sourdough Loaf, 4.90\nOat Milk Flat White, 3.90\nCloudwater IPA 4-Pack, 11.90\nArtisanal Cheese Board, 9.50"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkVioletSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ReceiptLong,
                    contentDescription = null,
                    tint = VibrantVioletLight,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("CSV Price List Import", color = TextWhite, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Paste or edit CSV lines below (Format: Item Name, Price):",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSubtitle
                )
                OutlinedTextField(
                    value = csvText,
                    onValueChange = { csvText = it },
                    minLines = 5,
                    maxLines = 8,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantVioletLight,
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            GradientButton(
                onClick = {
                    onImport(csvText)
                    onDismiss()
                },
                text = "Import & Update Prices"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardScreenPreview() {
    DeansgateProjectTheme {
        AdminDashboardScreen()
    }
}

@Composable
fun ChangeAdminPasswordDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    repository: DeliveryRepository = DeliveryRepository,
    onSuccess: () -> Unit = {}
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isCurrentPasswordVisible by remember { mutableStateOf(false) }
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    fun submitChangePassword() {
        errorMessage = null
        successMessage = null

        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            errorMessage = "All password fields are required"
            return
        }

        if (newPassword != confirmPassword) {
            errorMessage = "Passwords do not match"
            return
        }

        if (newPassword.length < 4) {
            errorMessage = "New password must be at least 4 characters long"
            return
        }

        val result = repository.changeAdminPassword(
            oldPassword = currentPassword,
            newPassword = newPassword
        )

        result.fold(
            onSuccess = {
                successMessage = "Password changed successfully!"
                onSuccess()
            },
            onFailure = { throwable ->
                errorMessage = throwable.message ?: "Current password incorrect"
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkVioletSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkVioletSurfaceVariant,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Key,
                            contentDescription = null,
                            tint = VibrantVioletLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Change Admin Password 🔑",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Update Operations Control Key",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSubtitle
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Error banner message
                errorMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF450A0A),
                        border = BorderStroke(1.dp, Color(0xFF991B1B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ErrorOutline,
                                contentDescription = "Error",
                                tint = Color(0xFFFCA5A5),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFFCA5A5)
                            )
                        }
                    }
                }

                // Success feedback banner
                successMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF064E3B),
                        border = BorderStroke(1.dp, Color(0xFF047857)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Success",
                                tint = Color(0xFF6EE7B7),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6EE7B7)
                            )
                        }
                    }
                }

                // Current Password Field
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it
                        if (errorMessage != null) errorMessage = null
                    },
                    label = { Text("Current Password", color = TextMuted) },
                    placeholder = { Text("Enter current password", color = TextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = VibrantVioletLight,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isCurrentPasswordVisible = !isCurrentPasswordVisible }) {
                            Icon(
                                imageVector = if (isCurrentPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (isCurrentPasswordVisible) "Hide Password" else "Show Password",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (isCurrentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantVioletLight,
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = VibrantVioletLight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // New Password Field
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        if (errorMessage != null) errorMessage = null
                    },
                    label = { Text("New Password", color = TextMuted) },
                    placeholder = { Text("Enter new password", color = TextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Key,
                            contentDescription = null,
                            tint = VibrantVioletLight,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isNewPasswordVisible = !isNewPasswordVisible }) {
                            Icon(
                                imageVector = if (isNewPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (isNewPasswordVisible) "Hide Password" else "Show Password",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantVioletLight,
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = VibrantVioletLight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Confirm New Password Field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        if (errorMessage != null) errorMessage = null
                    },
                    label = { Text("Confirm New Password", color = TextMuted) },
                    placeholder = { Text("Re-enter new password", color = TextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Key,
                            contentDescription = null,
                            tint = VibrantVioletLight,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (isConfirmPasswordVisible) "Hide Password" else "Show Password",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { submitChangePassword() }
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantVioletLight,
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = VibrantVioletLight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            GradientButton(
                onClick = { submitChangePassword() },
                text = "Save New Password",
                icon = Icons.Rounded.Key,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Close", color = TextMuted)
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun ChangeAdminPasswordDialogPreview() {
    DeansgateProjectTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ChangeAdminPasswordDialog(
                onDismiss = {}
            )
        }
    }
}

@Composable
fun ChangeRunnerPasswordDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    repository: DeliveryRepository = DeliveryRepository,
    onSuccess: () -> Unit = {}
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    fun submitChangeRunnerPassword() {
        errorMessage = null
        successMessage = null

        if (newPassword.isBlank() || confirmPassword.isBlank()) {
            errorMessage = "All password fields are required"
            return
        }

        if (newPassword != confirmPassword) {
            errorMessage = "Passwords do not match"
            return
        }

        if (newPassword.length < 4) {
            errorMessage = "New password must be at least 4 characters long"
            return
        }

        val result = repository.changeRunnerPassword(newPassword = newPassword)

        result.fold(
            onSuccess = {
                successMessage = "Runner password updated successfully!"
                onSuccess()
            },
            onFailure = { throwable ->
                errorMessage = throwable.message ?: "Failed to update runner password"
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkVioletSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.DirectionsRun,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = "Runner Password",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Set new passcode for runner hub",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFCA5A5),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                if (successMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = successMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6EE7B7),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // New Password Field
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        errorMessage = null
                    },
                    label = { Text("New Runner Password", color = TextMuted) },
                    placeholder = { Text("Enter new runner password", color = TextMuted) },
                    trailingIcon = {
                        IconButton(onClick = { isNewPasswordVisible = !isNewPasswordVisible }) {
                            Icon(
                                imageVector = if (isNewPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (isNewPasswordVisible) "Hide Password" else "Show Password",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF10B981),
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = Color(0xFF10B981)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Confirm New Password Field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = null
                    },
                    label = { Text("Confirm New Runner Password", color = TextMuted) },
                    placeholder = { Text("Re-enter new runner password", color = TextMuted) },
                    trailingIcon = {
                        IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = if (isConfirmPasswordVisible) "Hide Password" else "Show Password",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { submitChangeRunnerPassword() }
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF10B981),
                        unfocusedBorderColor = CardBorderColor,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = Color(0xFF10B981)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            GradientButton(
                onClick = { submitChangeRunnerPassword() },
                text = "Save Runner Password",
                icon = Icons.AutoMirrored.Rounded.DirectionsRun,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Close", color = TextMuted)
            }
        },
        modifier = modifier
    )
}

