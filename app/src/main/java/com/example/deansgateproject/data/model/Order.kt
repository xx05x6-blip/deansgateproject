package com.example.deansgateproject.data.model

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data class Order(
    val id: String,
    val residentId: String,
    val residentName: String,
    val residentPhone: String = "+44 7700 900123",
    val runnerId: String? = null,
    val runnerName: String? = null,
    val storeId: String,
    val storeName: String,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val status: OrderStatus = OrderStatus.PLACED,
    val deliveryAddress: ApartmentAddress,
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String? = null
) {
    val totalItemCount: Int
        get() = items.sumOf { it.quantity }

    val formattedTotalPrice: String
        get() = String.format(Locale.UK, "£%.2f", totalPrice)

    val formattedTime: String
        get() {
            val formatter = SimpleDateFormat("HH:mm, dd MMM", Locale.UK)
            return formatter.format(Date(createdAt))
        }
}
