package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Driver
import com.example.data.model.Notification
import com.example.data.model.Ride
import com.example.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AppScreen {
    object Splash : AppScreen()
    object Login : AppScreen()
    object Otp : AppScreen()
    object Register : AppScreen()
    object Dashboard : AppScreen()
    object Wallet : AppScreen()
    object Notifications : AppScreen()
    object Profile : AppScreen()
    data class RideRequestDetail(val ride: Ride) : AppScreen()
}

class DriverViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirebaseRepository(application)
    
    // UI Navigation State
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Splash)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Authentication States
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private var verificationId: String = ""

    // Driver Profile State
    private val _driver = MutableStateFlow<Driver?>(null)
    val driver: StateFlow<Driver?> = _driver.asStateFlow()

    // Real-Time Databases States
    private val _rides = MutableStateFlow<List<Ride>>(emptyList())
    val rides: StateFlow<List<Ride>> = _rides.asStateFlow()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    // Loading and Error Indicators
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Global Scanner States (PhonePe Dynamic Scanner)
    val showScannerDialog = MutableStateFlow(false)
    val stepActiveScanner = MutableStateFlow(0)
    val scanResultMessage = MutableStateFlow("")

    // Adjustable Wallet Limit
    private val _walletLimit = MutableStateFlow(50000.0)
    val walletLimit: StateFlow<Double> = _walletLimit.asStateFlow()

    fun updateWalletLimit(newLimit: Double) {
        _walletLimit.value = newLimit
        repository.saveWalletLimit(newLimit)
        viewModelScope.launch {
            repository.addNotification(
                "Limit Settings Adjusted",
                "Your wallet cash-out and scan transaction cap limit has been updated to ₹${String.format("%,.2f", newLimit)}."
            )
        }
    }

    fun openTopupScanner() {
        showScannerDialog.value = true
        stepActiveScanner.value = 1
        scanResultMessage.value = ""
    }

    fun closeTopupScanner() {
        showScannerDialog.value = false
        stepActiveScanner.value = 0
        scanResultMessage.value = ""
    }

    fun completeSimulatedScan(amount: Double) {
        simulatePassengerQrPayment(amount)
        scanResultMessage.value = "PhonePe Terminal: Instantly received ₹${String.format("%,.2f", amount)} and loaded to wallet balance!"
        stepActiveScanner.value = 2
    }

    fun executeUpiPayFromWallet(amount: Double, upiId: String, merchantName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val driverProfile = _driver.value
        if (driverProfile == null) {
            onError("Driver profile not found.")
            return
        }
        if (amount <= 0) {
            onError("Please enter a valid payment amount.")
            return
        }
        if (driverProfile.walletBalance < amount) {
            onError("Insufficient Balance. Your wallet balance is ₹${String.format("%.2f", driverProfile.walletBalance)} but the payment requires ₹${String.format("%.2f", amount)}.")
            return
        }
        if (amount > _walletLimit.value) {
            onError("Payment limit exceeded! This transaction of ₹${String.format("%.2f", amount)} exceeds your current wallet limit of ₹${String.format("%.2f", _walletLimit.value)}. Please raise your wallet limit first.")
            return
        }

        val updatedDriver = driverProfile.copy(
            walletBalance = driverProfile.walletBalance - amount
        )
        repository.saveDriverProfile(updatedDriver, {
            _driver.value = updatedDriver
            repository.addNotification(
                "UPI Payment Debited",
                "Paid ₹${String.format("%,.2f", amount)} securely to $merchantName ($upiId) via scanned UPI Quick QR."
            )
            scanResultMessage.value = "UPI Outflow: Paid ₹${String.format("%,.2f", amount)} successfully from your wallet to $merchantName ($upiId)!"
            stepActiveScanner.value = 2
            onSuccess()
        }, {
            onError("Failed to process payment from wallet.")
        })
    }

    // Repository Mode Flag
    val isDemoMode: Boolean
        get() = repository.isDemoMode

    init {
        _walletLimit.value = repository.getWalletLimit()
        // Evaluate initial authentication status
        viewModelScope.launch {
            val uid = repository.getCurrentUid()
            if (uid != null) {
                // Fetch driver details
                repository.getDriverProfile(uid, { driverProfile ->
                    if (driverProfile != null) {
                        _driver.value = driverProfile
                        _currentScreen.value = AppScreen.Dashboard
                        startObservingData()
                    } else {
                        // Logged in but profile information missing: send to register
                        _currentScreen.value = AppScreen.Register
                    }
                }, {
                    // Fail fallback to register or login
                    _currentScreen.value = AppScreen.Login
                })
            } else {
                // Not authenticated
                _currentScreen.value = AppScreen.Splash
            }
        }
    }

    private fun startObservingData() {
        // Observe live ride requests
        repository.observeRideRequests { list ->
            _rides.value = list
        }
        
        // Observe live notifications
        repository.observeNotifications { list ->
            _notifications.value = list
        }

        // Mirror active driver changes
        viewModelScope.launch {
            repository.localDriver.collect { d ->
                _driver.value = d
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // ==========================================
    // AUTHENTICATION CONTROLLERS
    // ==========================================

    fun setPhoneNumber(num: String) {
        _phoneNumber.value = num
    }

    fun setOtpCode(code: String) {
        _otpCode.value = code
    }

    fun sendOtp(activity: android.app.Activity? = null) {
        val phone = _phoneNumber.value.trim()
        if (phone.length < 10) {
            _errorMessage.value = "Please enter a valid phone number with area code."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        
        repository.sendPhoneOtp(phone, activity, { verId ->
            _isLoading.value = false
            verificationId = verId
            _currentScreen.value = AppScreen.Otp
        }, { ex ->
            _isLoading.value = false
            _errorMessage.value = ex.message ?: "Failed to send verification code. Try again."
        })
    }

    fun verifyOtp() {
        val otp = _otpCode.value.trim()
        if (otp.length < 6) {
            _errorMessage.value = "Enter the 6-digit OTP code."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        repository.verifyOtp(verificationId, otp, { uid ->
            // Authenticated successfully. Fetch profile.
            repository.getDriverProfile(uid, { profile ->
                _isLoading.value = false
                if (profile != null) {
                    _driver.value = profile
                    _currentScreen.value = AppScreen.Dashboard
                    startObservingData()
                } else {
                    // Brand new driver. Send to registration screen.
                    val newDriver = Driver(uid = uid, mobileNumber = _phoneNumber.value)
                    _driver.value = newDriver
                    _currentScreen.value = AppScreen.Register
                }
            }, {
                // Proceed to registration to avoid blockages
                _isLoading.value = false
                val newDriver = Driver(uid = uid, mobileNumber = _phoneNumber.value)
                _driver.value = newDriver
                _currentScreen.value = AppScreen.Register
            })
        }, { ex ->
            _isLoading.value = false
            _errorMessage.value = ex.message ?: "Invalid OTP verification code. Try again."
        })
    }

    // ==========================================
    // REGISTRATION CONTROLLER
    // ==========================================

    fun registerDriver(
        fullName: String,
        vehicleType: String,
        vehicleNumber: String,
        licensePath: String,
        photoPath: String
    ) {
        val currentUid = _driver.value?.uid ?: "driver_register_${System.currentTimeMillis()}"
        
        if (fullName.trim().isEmpty() || vehicleNumber.trim().isEmpty()) {
            _errorMessage.value = "Please fill in all details."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        val newDriver = Driver(
            uid = currentUid,
            fullName = fullName.trim(),
            mobileNumber = _phoneNumber.value,
            vehicleType = vehicleType,
            vehicleNumber = vehicleNumber.trim().uppercase(),
            drivingLicenseUrl = if (licensePath.isEmpty()) "https://example.com/demo_license.jpg" else licensePath,
            profilePhotoUrl = if (photoPath.isEmpty()) "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=200" else photoPath,
            onlineStatus = true, // Default to online on completion
            todayEarnings = 0.0,
            completedTrips = 0,
            pendingTrips = 0,
            walletBalance = 0.0
        )

        repository.saveDriverProfile(newDriver, {
            _isLoading.value = false
            _driver.value = newDriver
            _currentScreen.value = AppScreen.Dashboard
            startObservingData()
            repository.addNotification(
                "Driver Registered Successfully", 
                "Welcome O2 Partner ${newDriver.fullName}! Start your driver shift by toggling Online status."
            )
        }, { ex ->
            _isLoading.value = false
            _errorMessage.value = ex.message ?: "Failed to register profile. Please try again."
        })
    }

    // ==========================================
    // DASHBOARD CONTROLLERS
    // ==========================================

    fun toggleOnlineStatus(isOnline: Boolean) {
        repository.updateOnlineStatus(isOnline) {
            repository.addNotification(
                if (isOnline) "Shift Started (Online)" else "Shift Paused (Offline)", 
                if (isOnline) "You are now online and scanning for ride requests nearby." else "You are offline. You won't receive any new ride requests."
            )
        }
    }

    fun acceptRide(rideId: String) {
        val currentUid = _driver.value?.uid ?: ""
        _isLoading.value = true
        
        repository.updateRideStatus(rideId, "ACCEPTED") {
            _isLoading.value = false
            _currentScreen.value = AppScreen.Dashboard // Return to show active tracking on map!
        }
    }

    fun rejectRide(rideId: String) {
        _isLoading.value = true
        repository.updateRideStatus(rideId, "REJECTED") {
            repository.rejectRide(rideId) {
                _isLoading.value = false
                _currentScreen.value = AppScreen.Dashboard
            }
        }
    }

    fun startTrip(rideId: String) {
        _isLoading.value = true
        repository.updateRideStatus(rideId, "STARTED") {
            _isLoading.value = false
        }
    }

    fun endTrip(rideId: String) {
        _isLoading.value = true
        repository.updateRideStatus(rideId, "COMPLETED") {
            _isLoading.value = false
            // Clean up completed from active lists and refresh
            val updated = _rides.value.filter { r -> r.id != rideId }
            _rides.value = updated
        }
    }

    // ==========================================
    // WALLET CONTROLLERS
    // ==========================================

    fun withdrawFunds(amount: Double, onSuccess: () -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true
        repository.requestWalletWithdrawal(amount, {
            _isLoading.value = false
            onSuccess()
        }, { err ->
            _isLoading.value = false
            onError(err)
        })
    }

    fun withdrawFundsViaUpi(amount: Double, upiId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (upiId.trim().isEmpty() || !upiId.contains("@")) {
            onError("Please enter a valid UPI ID (e.g., username@bank)")
            return
        }
        _isLoading.value = true
        repository.requestWalletWithdrawal(amount, {
            _isLoading.value = false
            repository.addNotification(
                "UPI Payout Sent",
                "Processed instant UPI transfer of ₹${String.format("%,.2f", amount)} directly to $upiId ID. Verified!"
            )
            onSuccess()
        }, { err ->
            _isLoading.value = false
            onError(err)
        })
    }

    fun readAllNotifications() {
        repository.markNotificationsAsRead()
    }

    fun updateDriverBankDetails(
        bankName: String,
        accountNumber: String,
        ifscCode: String,
        holderName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val current = _driver.value ?: return
        val updated = current.copy(
            bankName = bankName,
            bankAccountNumber = accountNumber,
            bankIfscCode = ifscCode,
            bankAccountHolderName = holderName
        )
        _isLoading.value = true
        repository.saveDriverProfile(updated, {
            _isLoading.value = false
            _driver.value = updated
            onSuccess()
        }, { ex ->
            _isLoading.value = false
            onError(ex.message ?: "Failed to save bank details.")
        })
    }

    fun updateDriverUpiDetails(
        upiId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val current = _driver.value ?: return
        if (upiId.trim().isEmpty() || !upiId.contains("@")) {
            onError("Please enter a valid UPI ID (e.g., username@bank)")
            return
        }
        val updated = current.copy(upiId = upiId.trim())
        _isLoading.value = true
        repository.saveDriverProfile(updated, {
            _isLoading.value = false
            _driver.value = updated
            onSuccess()
        }, { ex ->
            _isLoading.value = false
            onError(ex.message ?: "Failed to save UPI ID.")
        })
    }

    fun simulatePassengerQrPayment(amount: Double) {
        val driverProfile = _driver.value ?: return
        val updatedDriver = driverProfile.copy(
            walletBalance = driverProfile.walletBalance + amount,
            todayEarnings = driverProfile.todayEarnings + amount
        )
        repository.saveDriverProfile(updatedDriver, {
            repository.addNotification(
                "Payment Received!",
                "Instant payment of ₹${String.format("%,.2f", amount)} received via Bharat UPI QR Terminal. Added to wallet!"
            )
        }, {})
    }

    // ==========================================
    // DEMO TRIGGER (FAST TESTING DESIGN)
    // ==========================================

    fun simulateNewIncomingRide() {
        val newRandId = "ride_sim_" + (100..999).random()
        val names = listOf("Arjun Dev", "Meera Sen", "Amit Patel", "Sneha Rao", "Rohan Das")
        val locations = listOf(
            Pair("New Delhi Railway Station", "Noida Sector 62"),
            Pair("Nehru Place Complex", "Dwarka Sector 21"),
            Pair("Green Park Market", "Vasant Kunj DLF Mall"),
            Pair("Rajouri Garden Metro", "Karol Bagh Market")
        )
        val selectedRoute = locations.random()
        val dist = (3..25).random() + ((0..9).random() / 10.0)
        val fare = (dist * 22.0 + 40.0).toInt().toDouble() // Base + running rate

        val simulatedRide = Ride(
            id = newRandId,
            customerName = names.random(),
            pickupLocationName = selectedRoute.first,
            dropLocationName = selectedRoute.second,
            estimatedFare = fare,
            distanceKm = dist,
            status = "PENDING",
            pickupLatitude = 28.5 + (0..10).random() / 100.0,
            pickupLongitude = 77.2 + (0..10).random() / 100.0,
            dropLatitude = 28.4 + (0..10).random() / 100.0,
            dropLongitude = 77.1 + (0..10).random() / 100.0
        )

        val updatedRides = _rides.value.toMutableList()
        updatedRides.add(0, simulatedRide)
        _rides.value = updatedRides

        repository.addNotification(
            "New Ride Request Nearby!", 
            "Incoming request from ${simulatedRide.customerName} (₹${simulatedRide.estimatedFare}). View and accept on dashboard!"
        )
    }

    fun logoutFlow(onComplete: () -> Unit) {
        repository.logout {
            onComplete()
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.cleanup()
    }
}
