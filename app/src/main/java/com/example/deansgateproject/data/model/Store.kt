package com.example.deansgateproject.data.model

import kotlinx.serialization.Serializable
import java.util.Locale
import kotlin.math.min

@Serializable
data class Store(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val imageUrl: String = "",
    val rating: Double = 4.8,
    val deliveryFee: Double = 1.99,
    val estimatedDeliveryTimeMinutes: Int = 15,
    val isOpen: Boolean = true,
    val address: String = "Deansgate Square Square, Manchester"
) {
    val formattedDeliveryFee: String
        get() = if (deliveryFee == 0.0) "Free Delivery" else String.format(Locale.UK, "£%.2f delivery", deliveryFee)

    val formattedEta: String
        get() = "${estimatedDeliveryTimeMinutes}-${min(estimatedDeliveryTimeMinutes + 10, 45)} mins"
}
