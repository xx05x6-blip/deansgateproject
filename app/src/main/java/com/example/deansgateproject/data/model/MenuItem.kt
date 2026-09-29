package com.example.deansgateproject.data.model

import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class MenuItem(
    val id: String,
    val storeId: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String = "",
    val isAvailable: Boolean = true
) {
    val formattedPrice: String
        get() = String.format(Locale.UK, "£%.2f", price)
}
