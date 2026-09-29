package com.example.deansgateproject.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus(
    val displayName: String,
    val stepIndex: Int,
    val description: String
) {
    PLACED(
        displayName = "Order Placed",
        stepIndex = 1,
        description = "Order submitted to store and awaiting acceptance"
    ),
    ACCEPTED(
        displayName = "Accepted by Store",
        stepIndex = 2,
        description = "Store is preparing your order"
    ),
    PICKED_UP(
        displayName = "Picked Up",
        stepIndex = 3,
        description = "Runner has collected order and is heading to your tower"
    ),
    DELIVERED(
        displayName = "Delivered",
        stepIndex = 4,
        description = "Safely delivered to your apartment door"
    ),
    CANCELED(
        displayName = "Canceled",
        stepIndex = -1,
        description = "Order was canceled"
    );

    val isTerminal: Boolean
        get() = this == DELIVERED || this == CANCELED
}
