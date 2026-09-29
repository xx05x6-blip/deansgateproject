package com.example.deansgateproject.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deansgateproject.data.model.ApartmentAddress
import com.example.deansgateproject.data.model.MenuItem
import com.example.deansgateproject.data.model.Order
import com.example.deansgateproject.data.model.OrderItem
import com.example.deansgateproject.data.model.OrderStatus
import com.example.deansgateproject.data.model.Store
import com.example.deansgateproject.data.model.UserRole
import com.example.deansgateproject.data.repository.DeliveryRepository
import com.example.deansgateproject.ui.components.AdminPasswordDialog
import com.example.deansgateproject.ui.components.GradientButton
import com.example.deansgateproject.ui.components.OrderSummaryCard
import com.example.deansgateproject.ui.components.RoleSelectorTopBar
import com.example.deansgateproject.ui.components.RunnerPasswordDialog
import com.example.deansgateproject.ui.theme.CardBorderColor
import com.example.deansgateproject.ui.theme.DarkVioletSurface
import com.example.deansgateproject.ui.theme.DarkVioletSurfaceVariant
import com.example.deansgateproject.ui.theme.DeansgateProjectTheme
import com.example.deansgateproject.ui.theme.DeepObsidianBackground
import com.example.deansgateproject.ui.theme.PillActiveContainer
import com.example.deansgateproject.ui.theme.PillInactiveContainer
import com.example.deansgateproject.ui.theme.TextMuted
import com.example.deansgateproject.ui.theme.TextSubtitle
import com.example.deansgateproject.ui.theme.TextWhite
import com.example.deansgateproject.ui.theme.VibrantVioletLight
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainDeliveryScreen(
    modifier: Modifier = Modifier,
    repository: DeliveryRepository = DeliveryRepository
) {
    val currentRole by repository.currentUserRole.collectAsState()
    val isAdminAuthenticated by repository.isAdminAuthenticated.collectAsState()
    val isRunnerAuthenticated by repository.isRunnerAuthenticated.collectAsState()
    val activeAddress by repository.activeAddress.collectAsState()
    val stores by repository.stores.collectAsState()
    val menuItems by repository.menuItems.collectAsState()
    val orders by repository.orders.collectAsState()

    var showAddressDialog by remember { mutableStateOf(false) }
    var showAdminAuthDialog by remember { mutableStateOf(false) }
    var showRunnerAuthDialog by remember { mutableStateOf(false) }
    var checkoutStore by remember { mutableStateOf<Store?>(null) }
    var checkoutItem by remember { mutableStateOf<MenuItem?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val activeOrdersCount = orders.count { !it.status.isTerminal }

    Scaffold(
        topBar = {
            RoleSelectorTopBar(
                currentRole = currentRole,
                activeAddress = activeAddress,
                onRoleSelected = { newRole ->
                    if (newRole == UserRole.ADMIN && !isAdminAuthenticated) {
                        showAdminAuthDialog = true
                    } else if (newRole == UserRole.RUNNER && !isRunnerAuthenticated) {
                        showRunnerAuthDialog = true
                    } else {
                        repository.switchRole(newRole)
                        scope.launch {
                            snackbarHostState.showSnackbar("Switched to ${newRole.displayName} Mode")
                        }
                    }
                },
                onAddressClick = { showAddressDialog = true },
                activeOrderCount = activeOrdersCount
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepObsidianBackground)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentRole,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "RoleViewTransition"
            ) { role ->
                when (role) {
                    UserRole.RESIDENT -> ResidentView(
                        stores = stores,
                        menuItems = menuItems,
                        orders = orders,
                        onInitiateCheckout = { store, item ->
                            checkoutStore = store
                            checkoutItem = item
                        },
                        onStatusChange = { orderId, newStatus ->
                            repository.updateOrderStatus(orderId, newStatus)
                        }
                    )

                    UserRole.RUNNER -> RunnerDashboardScreen(repository = repository)

                    UserRole.ADMIN -> AdminDashboardScreen(repository = repository)
                }
            }
        }
    }

    if (showAddressDialog) {
        AddressSelectionDialog(
            currentAddress = activeAddress,
            onDismiss = { showAddressDialog = false },
            onSave = { tower, apt ->
                repository.updateActiveAddress(tower, apt)
                showAddressDialog = false
            }
        )
    }

    if (showAdminAuthDialog) {
        AdminPasswordDialog(
            onDismiss = { showAdminAuthDialog = false },
            onAuthenticate = { password ->
                repository.authenticateAdmin(password)
            },
            onSuccess = {
                repository.switchRole(UserRole.ADMIN)
                scope.launch {
                    snackbarHostState.showSnackbar("Authenticated! Switched to Admin Mode")
                }
            }
        )
    }

    if (showRunnerAuthDialog) {
        RunnerPasswordDialog(
            onDismiss = { showRunnerAuthDialog = false },
            onAuthenticate = { password ->
                repository.authenticateRunner(password)
            },
            onSuccess = {
                repository.switchRole(UserRole.RUNNER)
                scope.launch {
                    snackbarHostState.showSnackbar("Authenticated! Switched to Runner Mode")
                }
            }
        )
    }

    if (checkoutStore != null && checkoutItem != null) {
        CheckoutModalDialog(
            store = checkoutStore!!,
            item = checkoutItem!!,
            activeAddress = activeAddress,
            onDismiss = {
                checkoutStore = null
                checkoutItem = null
            },
            onConfirmOrder = { paymentMethod ->
                val newOrder = repository.placeOrder(
                    storeId = checkoutStore!!.id,
                    items = listOf(OrderItem(menuItem = checkoutItem!!, quantity = 1)),
                    notes = "Payment via $paymentMethod • Tower delivery to door"
                )
                if (newOrder != null) {
                    scope.launch {
                        snackbarHostState.showSnackbar("Order placed with ${checkoutStore!!.name} using $paymentMethod!")
                    }
                }
                checkoutStore = null
                checkoutItem = null
            }
        )
    }
}

@Composable
private fun ResidentView(
    stores: List<Store>,
    menuItems: List<MenuItem>,
    orders: List<Order>,
    onInitiateCheckout: (Store, MenuItem) -> Unit,
    onStatusChange: (String, OrderStatus) -> Unit
) {
    val activeOrders = orders.filter { !it.status.isTerminal }
    val pastOrders = orders.filter { it.status.isTerminal }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Active Orders Section
        if (activeOrders.isNotEmpty()) {
            item {
                Text(
                    text = "ACTIVE DELIVERIES (${activeOrders.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = VibrantVioletLight,
                    letterSpacing = 0.8.sp
                )
            }
            items(activeOrders, key = { it.id }) { order ->
                OrderSummaryCard(
                    order = order,
                    userRole = UserRole.RESIDENT,
                    onStatusChange = { newStatus -> onStatusChange(order.id, newStatus) }
                )
            }
        }

        // Deansgate Local Stores Section
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "DEANSGATE SQUARE STORES",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = VibrantVioletLight,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Local venues delivering directly to your tower door",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        items(stores, key = { it.id }) { store ->
            val storeItems = menuItems.filter { it.storeId == store.id }
            StoreCard(
                store = store,
                sampleItems = storeItems,
                onQuickOrder = { item -> onInitiateCheckout(store, item) }
            )
        }

        // Past Orders History
        if (pastOrders.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ORDER HISTORY",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.8.sp
                )
            }
            items(pastOrders, key = { it.id }) { order ->
                OrderSummaryCard(
                    order = order,
                    userRole = UserRole.RESIDENT,
                    onStatusChange = {}
                )
            }
        }
    }
}

@Composable
private fun StoreCard(
    store: Store,
    sampleItems: List<MenuItem>,
    onQuickOrder: (MenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = store.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (!store.isOpen) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF7F1D1D)
                            ) {
                                Text(
                                    text = "CLOSED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextWhite,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = store.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSubtitle,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Rating & ETA chip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PillInactiveContainer,
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = store.rating.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = store.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = store.formattedDeliveryFee,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSubtitle
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    text = store.formattedEta,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSubtitle
                )
            }

            // Quick Order Menu Sample Items
            if (sampleItems.isNotEmpty() && store.isOpen) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "FEATURED ITEMS (TAP TO ORDER):",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sampleItems) { item ->
                        Surface(
                            onClick = { if (item.isAvailable) onQuickOrder(item) },
                            enabled = item.isAvailable,
                            shape = RoundedCornerShape(16.dp),
                            color = PillInactiveContainer,
                            border = BorderStroke(1.dp, CardBorderColor)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = item.formattedPrice,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSubtitle
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(PillActiveContainer)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = "Add",
                                        tint = TextWhite,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckoutModalDialog(
    store: Store,
    item: MenuItem,
    activeAddress: ApartmentAddress,
    onDismiss: () -> Unit,
    onConfirmOrder: (String) -> Unit
) {
    var selectedPaymentMethod by remember { mutableStateOf("Debit") }
    val paymentOptions = listOf("Debit", "Credit")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkVioletSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.ShoppingBag,
                    contentDescription = null,
                    tint = VibrantVioletLight,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm Tower Order", color = TextWhite, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Item & Store Summary Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkVioletSurfaceVariant,
                    border = BorderStroke(1.dp, CardBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = store.name, style = MaterialTheme.typography.labelSmall, color = TextSubtitle)
                        Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Item Price", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(text = item.formattedPrice, style = MaterialTheme.typography.bodySmall, color = TextWhite)
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Delivery Fee", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(text = store.formattedDeliveryFee, style = MaterialTheme.typography.bodySmall, color = TextWhite)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Total to Door", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = VibrantVioletLight)
                            Text(
                                text = String.format(Locale.UK, "£%.2f", item.price + store.deliveryFee),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = VibrantVioletLight
                            )
                        }
                    }
                }

                Text(
                    text = "Delivering to: ${activeAddress.formattedAddress}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSubtitle
                )

                // Payment Method Pill Toggle Buttons ("Debit | Credit")
                Text(
                    text = "PAYMENT METHOD:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PillInactiveContainer,
                    border = BorderStroke(1.dp, CardBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        paymentOptions.forEach { method ->
                            val isSelected = selectedPaymentMethod == method
                            Surface(
                                onClick = { selectedPaymentMethod = method },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) PillActiveContainer else Color.Transparent,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (method == "Debit") Icons.Rounded.Payment else Icons.Rounded.CreditCard,
                                        contentDescription = null,
                                        tint = if (isSelected) TextWhite else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = method,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) TextWhite else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            GradientButton(
                onClick = { onConfirmOrder(selectedPaymentMethod) },
                text = "Place Order"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressSelectionDialog(
    currentAddress: ApartmentAddress,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var selectedTower by remember { mutableStateOf(currentAddress.tower) }
    var apartmentNumber by remember { mutableStateOf(currentAddress.apartmentNumber) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkVioletSurface,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Select Deansgate Apartment", color = TextWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedTower,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tower", color = TextMuted) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantVioletLight,
                            unfocusedBorderColor = CardBorderColor,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(DarkVioletSurfaceVariant)
                    ) {
                        ApartmentAddress.TOWERS.forEach { tower ->
                            DropdownMenuItem(
                                text = { Text(tower, color = TextWhite) },
                                onClick = {
                                    selectedTower = tower
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = apartmentNumber,
                    onValueChange = { apartmentNumber = it },
                    label = { Text("Apartment Number", color = TextMuted) },
                    singleLine = true,
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
                onClick = { onSave(selectedTower, apartmentNumber) },
                enabled = apartmentNumber.isNotBlank(),
                text = "Save Address"
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
fun MainDeliveryScreenPreview() {
    DeansgateProjectTheme {
        MainDeliveryScreen()
    }
}
