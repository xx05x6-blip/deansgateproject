package com.example.deansgateproject.data.model

import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class OrderItem(
    val menuItem: MenuItem,
    val quantity: Int = 1,
    val specialNotes: String? = null
) {
    val totalPrice: Double
        get() = menuItem.price * quantity

    val formattedTotalPrice: String
        get() = String.format(Locale.UK, "£%.2f", totalPrice)
}
