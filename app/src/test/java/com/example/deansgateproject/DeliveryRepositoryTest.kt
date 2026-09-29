package com.example.deansgateproject

import com.example.deansgateproject.data.model.OrderItem
import com.example.deansgateproject.data.model.OrderStatus
import com.example.deansgateproject.data.model.UserRole
import com.example.deansgateproject.data.repository.DeliveryRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeliveryRepositoryTest {

    private val repository = DeliveryRepository

    @Before
    fun setUp() {
        repository.currentAdminPassword = "@Elijah16"
        repository.currentRunnerPassword = "@Elijah16"
        repository.logoutAdmin()
        repository.logoutRunner()
    }

    @Test
    fun defaultRoleIsResidentAndRoleSwitchingWorks() {
        assertEquals(UserRole.RESIDENT, repository.currentUserRole.value)

        repository.switchRole(UserRole.RUNNER)
        assertEquals(UserRole.RUNNER, repository.currentUserRole.value)

        // Switching to ADMIN requires authentication
        repository.authenticateAdmin("@Elijah16")
        repository.switchRole(UserRole.ADMIN)
        assertEquals(UserRole.ADMIN, repository.currentUserRole.value)

        // Reset
        repository.logoutAdmin()
        assertEquals(UserRole.RESIDENT, repository.currentUserRole.value)
    }

    @Test
    fun authenticateAdminWithCorrectPasswordUnlocksAdminAccess() {
        repository.logoutAdmin()
        assertFalse(repository.isAdminAuthenticated.value)

        val success = repository.authenticateAdmin("@Elijah16")
        assertTrue(success)
        assertTrue(repository.isAdminAuthenticated.value)

        repository.switchRole(UserRole.ADMIN)
        assertEquals(UserRole.ADMIN, repository.currentUserRole.value)

        repository.logoutAdmin()
        assertFalse(repository.isAdminAuthenticated.value)
    }

    @Test
    fun authenticateAdminWithIncorrectPasswordFailsAndBlocksAdminRole() {
        repository.logoutAdmin()
        assertFalse(repository.isAdminAuthenticated.value)

        val success = repository.authenticateAdmin("wrong_password")
        assertFalse(success)
        assertFalse(repository.isAdminAuthenticated.value)

        // Attempting to switch to ADMIN without authentication should be blocked
        repository.switchRole(UserRole.ADMIN)
        assertEquals(UserRole.RESIDENT, repository.currentUserRole.value)
    }

    @Test
    fun logoutAdminResetsAuthenticationStateAndRevertsAdminRole() {
        repository.authenticateAdmin("@Elijah16")
        assertTrue(repository.isAdminAuthenticated.value)

        repository.switchRole(UserRole.ADMIN)
        assertEquals(UserRole.ADMIN, repository.currentUserRole.value)

        repository.logoutAdmin()
        assertFalse(repository.isAdminAuthenticated.value)
        assertEquals(UserRole.RESIDENT, repository.currentUserRole.value)
    }

    @Test
    fun changeAdminPasswordSuccessAndFailure() {
        // Verify default password
        assertEquals("@Elijah16", repository.currentAdminPassword)

        // Attempting to change password with incorrect old password fails
        val incorrectOldResult = repository.changeAdminPassword("wrongOldPass", "newPass123")
        assertTrue(incorrectOldResult.isFailure)
        assertEquals("@Elijah16", repository.currentAdminPassword)

        // Attempting to change password with too short/blank new password fails
        val shortPassResult = repository.changeAdminPassword("@Elijah16", "123")
        assertTrue(shortPassResult.isFailure)
        assertEquals("@Elijah16", repository.currentAdminPassword)

        // Successful password change
        val successResult = repository.changeAdminPassword("@Elijah16", "newAdminPass2026")
        assertTrue(successResult.isSuccess)
        assertEquals("newAdminPass2026", repository.currentAdminPassword)

        // Verify authentication works with new password and fails with old password
        assertTrue(repository.authenticateAdmin("newAdminPass2026"))
        repository.logoutAdmin()
        assertFalse(repository.authenticateAdmin("@Elijah16"))
    }

    @Test
    fun changeRunnerPasswordSuccessAndFailure() {
        assertEquals("@Elijah16", repository.currentRunnerPassword)

        val shortPassResult = repository.changeRunnerPassword("123")
        assertTrue(shortPassResult.isFailure)
        assertEquals("@Elijah16", repository.currentRunnerPassword)

        val successResult = repository.changeRunnerPassword("newRunnerPass2026")
        assertTrue(successResult.isSuccess)
        assertEquals("newRunnerPass2026", repository.currentRunnerPassword)

        assertTrue(repository.authenticateRunner("newRunnerPass2026"))
        repository.logoutRunner()
        assertFalse(repository.authenticateRunner("@Elijah16"))
    }

    @Test
    fun addressSelectionUpdatesActiveAddress() {
        repository.updateActiveAddress("West Tower", "1802")
        val currentAddress = repository.activeAddress.value

        assertEquals("West Tower", currentAddress.tower)
        assertEquals("1802", currentAddress.apartmentNumber)
        assertEquals("Apt 1802, West Tower, Deansgate Square", currentAddress.formattedAddress)
    }

    @Test
    fun storeCatalogAndMenuItemsAreSeeded() {
        val stores = repository.stores.value
        val items = repository.menuItems.value

        assertTrue(stores.isNotEmpty())
        assertTrue(items.isNotEmpty())

        val generalStore = stores.find { it.id == "store_general" }
        assertNotNull(generalStore)
        assertEquals("Deansgate General Store", generalStore?.name)

        val storeItems = repository.getMenuItemsForStore("store_general")
        assertTrue(storeItems.isNotEmpty())
    }

    @Test
    fun placeOrderAddsNewOrderAndCalculatesTotal() {
        val store = repository.stores.value.first()
        val menuItem = repository.menuItems.value.first { it.storeId == store.id }

        val initialOrderCount = repository.orders.value.size
        val newOrder = repository.placeOrder(
            storeId = store.id,
            items = listOf(OrderItem(menuItem = menuItem, quantity = 2)),
            notes = "Ring door chime"
        )

        assertNotNull(newOrder)
        assertEquals(initialOrderCount + 1, repository.orders.value.size)
        assertEquals(OrderStatus.PLACED, newOrder?.status)

        val expectedTotal = (menuItem.price * 2) + store.deliveryFee
        assertEquals(expectedTotal, newOrder?.totalPrice ?: 0.0, 0.01)
    }

    @Test
    fun updateOrderStatusAdvancesStateFlow() {
        val store = repository.stores.value.first()
        val menuItem = repository.menuItems.value.first { it.storeId == store.id }

        val order = repository.placeOrder(
            storeId = store.id,
            items = listOf(OrderItem(menuItem = menuItem, quantity = 1))
        )
        assertNotNull(order)
        val orderId = order!!.id

        repository.updateOrderStatus(orderId, OrderStatus.ACCEPTED, "RUNNER-01", "Alex Concierge")
        var updatedOrder = repository.orders.value.find { it.id == orderId }
        assertEquals(OrderStatus.ACCEPTED, updatedOrder?.status)
        assertEquals("RUNNER-01", updatedOrder?.runnerId)

        repository.updateOrderStatus(orderId, OrderStatus.PICKED_UP)
        updatedOrder = repository.orders.value.find { it.id == orderId }
        assertEquals(OrderStatus.PICKED_UP, updatedOrder?.status)

        repository.updateOrderStatus(orderId, OrderStatus.DELIVERED)
        updatedOrder = repository.orders.value.find { it.id == orderId }
        assertEquals(OrderStatus.DELIVERED, updatedOrder?.status)
    }

    @Test
    fun toggleStoreOpenAndMenuItemAvailability() {
        val store = repository.stores.value.first()
        val initialIsOpen = store.isOpen

        repository.toggleStoreOpen(store.id)
        val toggledStore = repository.stores.value.find { it.id == store.id }
        assertEquals(!initialIsOpen, toggledStore?.isOpen)

        val item = repository.menuItems.value.first()
        val initialAvailable = item.isAvailable

        repository.toggleMenuItemAvailability(item.id)
        val toggledItem = repository.menuItems.value.find { it.id == item.id }
        assertEquals(!initialAvailable, toggledItem?.isAvailable)
    }

    @Test
    fun runnerOnlineStateCanBeToggled() {
        assertTrue(repository.isRunnerOnline.value)

        repository.toggleRunnerOnline()
        assertEquals(false, repository.isRunnerOnline.value)

        repository.setRunnerOnline(true)
        assertEquals(true, repository.isRunnerOnline.value)
    }

    @Test
    fun adminDeliveryFeeUpdateAndPipelineMetrics() {
        val store = repository.stores.value.first()
        repository.updateStoreDeliveryFee(store.id, 2.99)

        val updatedStore = repository.stores.value.find { it.id == store.id }
        assertEquals(2.99, updatedStore?.deliveryFee ?: 0.0, 0.001)

        val totalRevenue = repository.getTotalRevenue()
        assertTrue(totalRevenue > 0.0)

        val pendingDispatches = repository.getPendingDispatchesCount()
        assertTrue(pendingDispatches >= 0)

        val activeRunners = repository.getActiveRunnersCount()
        assertEquals(1, activeRunners)
    }
}
