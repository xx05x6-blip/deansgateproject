package com.example.deansgateproject.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole(
    val displayName: String,
    val description: String,
    val iconName: String
) {
    RESIDENT(
        displayName = "Resident",
        description = "Browse menus, place orders to your Deansgate tower & track live deliveries",
        iconName = "Home"
    ),
    RUNNER(
        displayName = "Runner",
        description = "Accept orders, collect from local stores, and deliver directly to apartment doors",
        iconName = "DirectionsRun"
    ),
    ADMIN(
        displayName = "Admin",
        description = "Manage Deansgate store catalogs, stock availability, and monitor active orders",
        iconName = "AdminPanelSettings"
    )
}
