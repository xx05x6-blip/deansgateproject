package com.example.deansgateproject.data.repository

import com.example.deansgateproject.data.model.ApartmentAddress
import com.example.deansgateproject.data.model.MenuItem
import com.example.deansgateproject.data.model.Order
import com.example.deansgateproject.data.model.OrderItem
import com.example.deansgateproject.data.model.OrderStatus
import com.example.deansgateproject.data.model.Store
import com.example.deansgateproject.data.model.UserRole
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object DeliveryRepository {

    const val DEFAULT_ADMIN_PASSWORD = "@Elijah16"
    const val ADMIN_PASSWORD = "@Elijah16"
    var currentAdminPassword = "@Elijah16"
    val adminPassword: String get() = currentAdminPassword

    // Admin authentication state
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    // Runner authentication state
    var currentRunnerPassword = "@Elijah16"
    val runnerPassword: String get() = currentRunnerPassword
    private val _isRunnerAuthenticated = MutableStateFlow(false)
    val isRunnerAuthenticated: StateFlow<Boolean> = _isRunnerAuthenticated.asStateFlow()

    // Available Runners list
    val availableRunners = listOf("Alex Concierge", "David Delivery", "Sarah Runner", "Marcus Concierge")

    // Current active role state
    private val _currentUserRole = MutableStateFlow(UserRole.RESIDENT)
    val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    // Current selected apartment address for the resident
    private val _activeAddress = MutableStateFlow(ApartmentAddress("South Tower", "1204"))
    val activeAddress: StateFlow<ApartmentAddress> = _activeAddress.asStateFlow()

    // Stores catalog state
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores.asStateFlow()

    // Menu items state
    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    // Active & Past Orders state
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // Runner Online/Active state & Phone
    private val _isRunnerOnline = MutableStateFlow(true)
    val isRunnerOnline: StateFlow<Boolean> = _isRunnerOnline.asStateFlow()

    private val _activeRunnerPhone = MutableStateFlow("+44 7700 900456")
    val activeRunnerPhone: StateFlow<String> = _activeRunnerPhone.asStateFlow()

    fun updateActiveRunnerPhone(phone: String) {
        if (phone.isNotBlank()) {
            _activeRunnerPhone.value = phone
        }
    }

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        // Seed Stores
        val sampleStores = listOf(
            Store(
                id = "store_general",
                name = "Deansgate General Store",
                category = "Grocery & Essentials • Deansgate Square",
                description = "Artisanal local grocery, specialty coffee, craft beers, wine & fresh bakery items exclusively serving Deansgate Square apartments.",
                rating = 4.9,
                deliveryFee = 1.50,
                estimatedDeliveryTimeMinutes = 12,
                isOpen = true,
                address = "Podium Level, South Tower, Deansgate Square Apartments"
            )
        )
        _stores.value = sampleStores

        // Seed Menu Items
        val sampleMenuItems = listOf(
            MenuItem("m_gen_1", "store_general", "Fresh Sourdough Loaf", "Baked fresh daily in Manchester by Pollen Bakery", 4.50, "Bakery", isAvailable = true),
            MenuItem("m_gen_2", "store_general", "Oat Milk Flat White", "Single-origin espresso with steamed Oatly oat milk", 3.80, "Beverages", isAvailable = true),
            MenuItem("m_gen_3", "store_general", "Cloudwater IPA 4-Pack", "Local Manchester Cloudwater craft IPA cans", 12.50, "Drinks & Alcohol", isAvailable = true),
            MenuItem("m_gen_4", "store_general", "Artisanal Cheese Board", "Three aged British cheeses, fig chutney & crackers", 8.90, "Deli & Snacks", isAvailable = true),
            MenuItem("m_gen_5", "store_general", "Charcuterie Platter", "Prosciutto di Parma, salami, olives, cornichons & focaccia", 14.00, "Deli & Snacks", isAvailable = true),
            MenuItem("m_gen_6", "store_general", "Truffle & Parmesan Arancini", "Crispy risotto balls served with truffle mayo", 8.50, "Hot Snacks", isAvailable = true)
        )
        _menuItems.value = sampleMenuItems

        // Seed Orders
        val now = System.currentTimeMillis()
        val sampleOrders = listOf(
            Order(
                id = "ORD-2026-101",
                residentId = "RES-1204",
                residentName = "Sophia Sterling",
                residentPhone = "+44 7700 900123",
                runnerId = null,
                runnerName = null,
                storeId = "store_general",
                storeName = "Deansgate General Store",
                items = listOf(
                    OrderItem(sampleMenuItems[0], quantity = 1),
                    OrderItem(sampleMenuItems[1], quantity = 2)
                ),
                totalPrice = 13.60,
                status = OrderStatus.PLACED,
                deliveryAddress = ApartmentAddress("South Tower", "1204"),
                createdAt = now - (1000 * 60 * 8), // 8 mins ago
                notes = "Please leave at front door if no answer"
            ),
            Order(
                id = "ORD-2026-102",
                residentId = "RES-1802",
                residentName = "Marcus Vance",
                residentPhone = "+44 7700 900456",
                runnerId = "RUNNER-01",
                runnerName = "Alex Concierge",
                storeId = "store_general",
                storeName = "Deansgate General Store",
                items = listOf(
                    OrderItem(sampleMenuItems[4], quantity = 1),
                    OrderItem(sampleMenuItems[1], quantity = 1)
                ),
                totalPrice = 19.30,
                status = OrderStatus.ACCEPTED,
                deliveryAddress = ApartmentAddress("West Tower", "1802"),
                createdAt = now - (1000 * 60 * 18),
                notes = "Ring door chime twice"
            ),
            Order(
                id = "ORD-2026-103",
                residentId = "RES-2405",
                residentName = "Elena Rostova",
                residentPhone = "+44 7700 900789",
                runnerId = "RUNNER-01",
                runnerName = "Alex Concierge",
                storeId = "store_general",
                storeName = "Deansgate General Store",
                items = listOf(
                    OrderItem(sampleMenuItems[2], quantity = 1),
                    OrderItem(sampleMenuItems[3], quantity = 1)
                ),
                totalPrice = 22.90,
                status = OrderStatus.PICKED_UP,
                deliveryAddress = ApartmentAddress("East Tower", "2405"),
                createdAt = now - (1000 * 60 * 25),
                notes = "Concierge desk drop off requested"
            ),
            Order(
                id = "ORD-2026-104",
                residentId = "RES-1204",
                residentName = "Sophia Sterling",
                residentPhone = "+44 7700 900123",
                runnerId = "RUNNER-02",
                runnerName = "David Delivery",
                storeId = "store_general",
                storeName = "Deansgate General Store",
                items = listOf(
                    OrderItem(sampleMenuItems[0], quantity = 1),
                    OrderItem(sampleMenuItems[3], quantity = 1)
                ),
                totalPrice = 14.90,
                status = OrderStatus.DELIVERED,
                deliveryAddress = ApartmentAddress("South Tower", "1204"),
                createdAt = now - (1000 * 60 * 120), // 2 hours ago
                notes = null
            )
        )
        _orders.value = sampleOrders
    }

    // Admin authentication
    fun authenticateAdmin(password: String): Boolean {
        val isValid = password == currentAdminPassword
        if (isValid) {
            _isAdminAuthenticated.value = true
        }
        return isValid
    }

    fun changeAdminPassword(oldPassword: String, newPassword: String): Result<Unit> {
        if (oldPassword != currentAdminPassword) {
            return Result.failure(IllegalArgumentException("Current password incorrect"))
        }
        if (newPassword.isBlank() || newPassword.length < 4) {
            return Result.failure(IllegalArgumentException("New password must be at least 4 characters long"))
        }
        currentAdminPassword = newPassword
        return Result.success(Unit)
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
        if (_currentUserRole.value == UserRole.ADMIN) {
            _currentUserRole.value = UserRole.RESIDENT
        }
    }

    // Runner authentication
    fun authenticateRunner(password: String): Boolean {
        val isValid = password == currentRunnerPassword
        if (isValid) {
            _isRunnerAuthenticated.value = true
        }
        return isValid
    }

    fun changeRunnerPassword(newPassword: String): Result<Unit> {
        if (newPassword.isBlank() || newPassword.length < 4) {
            return Result.failure(IllegalArgumentException("New password must be at least 4 characters long"))
        }
        currentRunnerPassword = newPassword
        return Result.success(Unit)
    }

    fun logoutRunner() {
        _isRunnerAuthenticated.value = false
        if (_currentUserRole.value == UserRole.RUNNER) {
            _currentUserRole.value = UserRole.RESIDENT
        }
    }

    // Role switching
    fun switchRole(newRole: UserRole) {
        if (newRole == UserRole.ADMIN && !_isAdminAuthenticated.value) {
            return
        }
        _currentUserRole.value = newRole
    }

    // Update active resident address
    fun updateActiveAddress(tower: String, apartmentNumber: String) {
        _activeAddress.value = ApartmentAddress(tower, apartmentNumber)
    }

    // Place new order
    fun placeOrder(
        storeId: String,
        items: List<OrderItem>,
        notes: String? = null
    ): Order? {
        val store = _stores.value.find { it.id == storeId } ?: return null
        val subtotal = items.sumOf { it.totalPrice }
        val total = subtotal + store.deliveryFee

        val newOrder = Order(
            id = "ORD-${System.currentTimeMillis().toString().takeLast(6)}",
            residentId = "RES-${_activeAddress.value.apartmentNumber}",
            residentName = "Sophia Sterling",
            residentPhone = "+44 7700 900123",
            runnerId = null,
            runnerName = null,
            storeId = store.id,
            storeName = store.name,
            items = items,
            totalPrice = total,
            status = OrderStatus.PLACED,
            deliveryAddress = _activeAddress.value,
            createdAt = System.currentTimeMillis(),
            notes = notes
        )

        _orders.value = listOf(newOrder) + _orders.value
        return newOrder
    }

    // Update status of an existing order
    fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus,
        runnerId: String? = null,
        runnerName: String? = null
    ) {
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId) {
                order.copy(
                    status = newStatus,
                    runnerId = runnerId ?: order.runnerId,
                    runnerName = runnerName ?: order.runnerName
                )
            } else {
                order
            }
        }
    }

    // Store management (Admin)
    fun toggleStoreOpen(storeId: String) {
        _stores.value = _stores.value.map { store ->
            if (store.id == storeId) store.copy(isOpen = !store.isOpen) else store
        }
    }

    fun updateStoreDeliveryFee(storeId: String, newFee: Double) {
        _stores.value = _stores.value.map { store ->
            if (store.id == storeId) store.copy(deliveryFee = newFee) else store
        }
    }

    fun updateStore(updatedStore: Store) {
        _stores.value = _stores.value.map { store ->
            if (store.id == updatedStore.id) updatedStore else store
        }
    }

    // Product & Menu Item management (Admin)
    fun toggleMenuItemAvailability(menuItemId: String) {
        _menuItems.value = _menuItems.value.map { item ->
            if (item.id == menuItemId) item.copy(isAvailable = !item.isAvailable) else item
        }
    }

    fun addMenuItem(
        name: String,
        description: String,
        price: Double,
        category: String,
        imageUrl: String = "",
        storeId: String = "store_general"
    ): MenuItem {
        val newId = "m_item_${System.currentTimeMillis()}"
        val newItem = MenuItem(
            id = newId,
            storeId = storeId,
            name = name,
            description = description,
            price = price,
            category = category,
            imageUrl = imageUrl,
            isAvailable = true
        )
        _menuItems.value = _menuItems.value + newItem
        return newItem
    }

    fun updateMenuItem(updatedItem: MenuItem) {
        _menuItems.value = _menuItems.value.map { item ->
            if (item.id == updatedItem.id) updatedItem else item
        }
    }

    fun deleteMenuItem(menuItemId: String) {
        _menuItems.value = _menuItems.value.filter { it.id != menuItemId }
    }

    fun getMenuItemsForStore(storeId: String): List<MenuItem> {
        return _menuItems.value.filter { it.storeId == storeId }
    }

    // Runner assignment & dispatch (Admin)
    fun assignRunnerAndDispatch(orderId: String, runnerName: String) {
        val runnerId = "RUNNER-${runnerName.replace(" ", "").uppercase(Locale.UK)}"
        updateOrderStatus(
            orderId = orderId,
            newStatus = OrderStatus.ACCEPTED,
            runnerId = runnerId,
            runnerName = runnerName
        )
    }

    // CSV Price List Import (Admin)
    fun importCsvPrices(csvText: String): Result<Int> {
        if (csvText.isBlank()) {
            return Result.failure(IllegalArgumentException("CSV content is empty"))
        }
        var updatedCount = 0
        val lines = csvText.lines()
        _menuItems.value = _menuItems.value.map { item ->
            var newItem = item
            for (line in lines) {
                if (line.isBlank() || line.startsWith("#")) continue
                val parts = line.split(",").map { it.trim() }
                if (parts.size >= 2) {
                    val itemName = parts[0]
                    val priceDouble = parts[1].replace("£", "").toDoubleOrNull()
                    if (priceDouble != null && item.name.equals(itemName, ignoreCase = true)) {
                        newItem = item.copy(price = priceDouble)
                        updatedCount++
                        break
                    }
                }
            }
            newItem
        }
        return Result.success(updatedCount)
    }

    // Toggles runner online status; added to resolve unresolved reference build errors and fix Preview ClassNotFoundException
    fun toggleRunnerOnline() {
        _isRunnerOnline.value = !_isRunnerOnline.value
    }

    fun setRunnerOnline(online: Boolean) {
        _isRunnerOnline.value = online
    }

    // Operations Pipeline Metrics
    fun getTotalRevenue(): Double {
        return _orders.value.sumOf { it.totalPrice }
    }

    fun getPendingDispatchesCount(): Int {
        return _orders.value.count { it.status == OrderStatus.PLACED || it.status == OrderStatus.ACCEPTED }
    }

    fun getActiveRunnersCount(): Int {
        return if (_isRunnerOnline.value) 1 else 0
    }
}
