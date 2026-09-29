package com.example.deansgateproject.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ApartmentAddress(
    val tower: String = "South Tower",
    val apartmentNumber: String = "1204"
) {
    val formattedAddress: String
        get() = "Apt $apartmentNumber, $tower, Deansgate Square"

    companion object {
        val TOWERS = listOf("South Tower", "West Tower", "East Tower", "North Tower")
    }
}
