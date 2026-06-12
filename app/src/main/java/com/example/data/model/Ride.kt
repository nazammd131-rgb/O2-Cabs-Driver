package com.example.data.model

data class Ride(
    val id: String = "",
    val customerName: String = "",
    val pickupLocationName: String = "",
    val dropLocationName: String = "",
    val estimatedFare: Double = 0.0,
    val distanceKm: Double = 0.0,
    val status: String = "PENDING", // PENDING, ACCEPTED, STARTED, COMPLETED, REJECTED
    val pickupLatitude: Double = 0.0,
    val pickupLongitude: Double = 0.0,
    val dropLatitude: Double = 0.0,
    val dropLongitude: Double = 0.0,
    val currentDriverLatitude: Double = 0.0,
    val currentDriverLongitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
