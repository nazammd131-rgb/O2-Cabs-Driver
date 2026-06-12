package com.example.data.model

data class Driver(
    val uid: String = "",
    val fullName: String = "",
    val mobileNumber: String = "",
    val vehicleType: String = "", // Bike, Auto, Car
    val vehicleNumber: String = "",
    val drivingLicenseUrl: String = "",
    val profilePhotoUrl: String = "",
    val onlineStatus: Boolean = false,
    val todayEarnings: Double = 0.0,
    val completedTrips: Int = 0,
    val pendingTrips: Int = 0,
    val walletBalance: Double = 0.0,
    val bankName: String = "State Bank of India",
    val bankAccountNumber: String = "30924190243",
    val bankIfscCode: String = "SBIN0001043",
    val bankAccountHolderName: String = "Rajesh Kumar",
    val upiId: String = ""
)
