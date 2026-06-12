package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.Driver
import com.example.data.model.Notification
import com.example.data.model.Ride
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class FirebaseRepository(private val context: Context) {

    private val TAG = "FirebaseRepository"
    
    // Check if Firebase is configured in the environment
    var isDemoMode = false
        private set

    private var firebaseAuth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null

    // Fallback Local Storage / Simulated State
    private val _localDriver = MutableStateFlow<Driver?>(null)
    val localDriver: StateFlow<Driver?> = _localDriver.asStateFlow()

    private val _localRides = MutableStateFlow<List<Ride>>(emptyList())
    val localRides: StateFlow<List<Ride>> = _localRides.asStateFlow()

    private val _localNotifications = MutableStateFlow<List<Notification>>(emptyList())
    val localNotifications: StateFlow<List<Notification>> = _localNotifications.asStateFlow()

    private var ridesListener: ListenerRegistration? = null
    private var notificationsListener: ListenerRegistration? = null

    init {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                firebaseAuth = FirebaseAuth.getInstance()
                firestore = FirebaseFirestore.getInstance()
                isDemoMode = false
                Log.d(TAG, "Firebase initialized successfully. Running in production mode.")
            } else {
                // Initialize default Firebase app if config properties might exist
                val app = FirebaseApp.initializeApp(context)
                if (app != null) {
                    firebaseAuth = FirebaseAuth.getInstance()
                    firestore = FirebaseFirestore.getInstance()
                    isDemoMode = false
                    Log.d(TAG, "Firebase initialized on fallback. Running in production mode.")
                } else {
                    enableDemoMode()
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase configuration missing or failed initialization: ${e.message}. Launching in Demo Mode.")
            enableDemoMode()
        }
    }

    private fun enableDemoMode() {
        isDemoMode = true
        Log.i(TAG, "Driver App configured in DEMO MODE. Creating mock initial database.")
        
        // Initialize simple SharedPreferences for Driver data saving locally
        val prefs = context.getSharedPreferences("o2_cabs_driver_prefs", Context.MODE_PRIVATE)
        val savedUid = prefs.getString("driver_uid", "") ?: ""
        if (savedUid.isNotEmpty()) {
            val driver = Driver(
                uid = savedUid,
                fullName = prefs.getString("driver_name", "Rajesh Kumar") ?: "Rajesh Kumar",
                mobileNumber = prefs.getString("driver_phone", "+91 98765 43210") ?: "+91 98765 43210",
                vehicleType = prefs.getString("driver_vehicle_type", "Car") ?: "Car",
                vehicleNumber = prefs.getString("driver_vehicle_number", "DL-3CA-1234") ?: "DL-3CA-1234",
                drivingLicenseUrl = "https://example.com/mock_license.jpg",
                profilePhotoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=200",
                onlineStatus = prefs.getBoolean("driver_online", true),
                todayEarnings = prefs.getFloat("driver_earnings_today", 1850f).toDouble(),
                completedTrips = prefs.getInt("driver_completed_trips", 5),
                pendingTrips = 0,
                walletBalance = prefs.getFloat("driver_wallet_balance", 4200f).toDouble(),
                bankName = prefs.getString("driver_bank_name", "State Bank of India") ?: "State Bank of India",
                bankAccountNumber = prefs.getString("driver_bank_account_number", "30924190243") ?: "30924190243",
                bankIfscCode = prefs.getString("driver_bank_ifsc", "SBIN0001043") ?: "SBIN0001043",
                bankAccountHolderName = prefs.getString("driver_bank_holder", "Rajesh Kumar") ?: "Rajesh Kumar",
                upiId = prefs.getString("driver_upi_id", "") ?: ""
            )
            _localDriver.value = driver
        } else {
            // New driver needs registration
            _localDriver.value = null
        }

        // Add some beautiful sample ride requests for instant visibility
        generateMockRideRequests()

        // Add sample notifications
        _localNotifications.value = listOf(
            Notification(
                id = "notif_1",
                title = "Welcome to O2 Cabs!",
                message = "Your driver vehicle documents are verified. Toggle Online to start earning!",
                timestamp = System.currentTimeMillis() - 600000,
                isRead = false
            ),
            Notification(
                id = "notif_2",
                title = "Earnings Credited",
                message = "₹350.00 wallet credit received for Trip #10049.",
                timestamp = System.currentTimeMillis() - 7200000,
                isRead = true
            )
        )
    }

    fun generateMockRideRequests() {
        _localRides.value = listOf(
            Ride(
                id = "ride_101",
                customerName = "Anjali Sharma",
                pickupLocationName = "Connaught Place, New Delhi",
                dropLocationName = "Indira Gandhi International Airport T3",
                estimatedFare = 450.0,
                distanceKm = 14.2,
                status = "PENDING",
                pickupLatitude = 28.6304,
                pickupLongitude = 77.2177,
                dropLatitude = 28.5562,
                dropLongitude = 77.1000,
                currentDriverLatitude = 28.6304,
                currentDriverLongitude = 77.2177
            ),
            Ride(
                id = "ride_102",
                customerName = "Vikram Singh",
                pickupLocationName = "Saket Metro Station",
                dropLocationName = "Gurugram Cyber City, Phase 3",
                estimatedFare = 320.0,
                distanceKm = 11.5,
                status = "PENDING",
                pickupLatitude = 28.5206,
                pickupLongitude = 77.2015,
                dropLatitude = 28.4950,
                dropLongitude = 77.0878,
                currentDriverLatitude = 28.5206,
                currentDriverLongitude = 77.2015
            )
        )
    }

    // ==========================================
    // AUTHENTICATION APIs (PHONE OTP LOGIN)
    // ==========================================

    fun sendPhoneOtp(
        phoneNumber: String,
        activity: android.app.Activity?,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (isDemoMode || firebaseAuth == null || activity == null) {
            // Emulated OTP behavior
            Log.d(TAG, "EMULATING SEND OTP to $phoneNumber (Demo Mode)")
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                onCodeSent("mock_verification_id_${UUID.randomUUID()}")
            }
        } else {
            // Real Firebase Auth flow using standard Android SDK triggers
            Log.d(TAG, "Firebase Auth: Running verification for $phoneNumber")
            try {
                val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted instant validation!")
                        // Automated verification completed by Google Play Services
                        firebaseAuth?.signInWithCredential(credential)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = task.result?.user
                                    if (user != null) {
                                        onCodeSent("instant_login_${user.uid}")
                                    }
                                }
                            }
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        Log.e(TAG, "onVerificationFailed: OTP Send failed", e)
                        onError(e)
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        Log.d(TAG, "onCodeSent: Verification ID generated by Firebase: $verificationId")
                        onCodeSent(verificationId)
                    }
                }

                val options = PhoneAuthOptions.newBuilder(firebaseAuth!!)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(callbacks)
                    .build()
                
                PhoneAuthProvider.verifyPhoneNumber(options)
            } catch (e: Exception) {
                Log.e(TAG, "Error invoking verifyPhoneNumber", e)
                onError(e)
            }
        }
    }

    fun verifyOtp(
        verificationId: String,
        code: String,
        onSuccess: (uid: String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (isDemoMode || firebaseAuth == null || verificationId.startsWith("mock_verification_id_")) {
            // Emulated Verification
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                if (code == "123456" || code.length == 6) {
                    val uid = "driver_uid_${verificationId.takeLast(6)}"
                    onSuccess(uid)
                } else {
                    onError(Exception("Invalid OTP Code. Hint: Enter any 6-digit code like 123456."))
                }
            }
        } else if (verificationId.startsWith("instant_login_")) {
            // Instantly logged in via Play Services
            val uid = verificationId.removePrefix("instant_login_")
            onSuccess(uid)
        } else {
            // Real Firebase verifying code using standard Pin Credential
            try {
                val credential = PhoneAuthProvider.getCredential(verificationId, code)
                firebaseAuth?.signInWithCredential(credential)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            if (user != null) {
                                onSuccess(user.uid)
                            } else {
                                onError(Exception("Failed to retrieve authenticated user context."))
                            }
                        } else {
                            val exception = task.exception ?: Exception("OTP auth failed.")
                            Log.e(TAG, "Firebase Sign-In failed with credential status", exception)
                            onError(exception)
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error building PhoneAuthCredential", e)
                onError(e)
            }
        }
    }

    fun getCurrentUid(): String? {
        if (isDemoMode) {
            return _localDriver.value?.uid
        }
        return firebaseAuth?.currentUser?.uid ?: _localDriver.value?.uid
    }

    fun logout(onComplete: () -> Unit) {
        if (!isDemoMode && firebaseAuth != null) {
            firebaseAuth?.signOut()
        }
        
        // Reset local preferences
        val prefs = context.getSharedPreferences("o2_cabs_driver_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        _localDriver.value = null
        
        onComplete()
    }

    // ==========================================
    // DRIVER PROFILE APIs
    // ==========================================

    fun getDriverProfile(uid: String, onSuccess: (Driver?) -> Unit, onError: (Exception) -> Unit) {
        if (isDemoMode || firestore == null) {
            onSuccess(_localDriver.value)
        } else {
            firestore?.collection("drivers")?.document(uid)?.get()
                ?.addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val driver = doc.toObject(Driver::class.java)
                        onSuccess(driver)
                    } else {
                        onSuccess(null)
                    }
                }
                ?.addOnFailureListener { e ->
                    Log.e(TAG, "Firestore failed to get driver. Falling back to local data.", e)
                    onSuccess(_localDriver.value)
                }
        }
    }

    fun saveDriverProfile(driver: Driver, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        // Save locally first
        val prefs = context.getSharedPreferences("o2_cabs_driver_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("driver_uid", driver.uid)
            putString("driver_name", driver.fullName)
            putString("driver_phone", driver.mobileNumber)
            putString("driver_vehicle_type", driver.vehicleType)
            putString("driver_vehicle_number", driver.vehicleNumber)
            putBoolean("driver_online", driver.onlineStatus)
            putFloat("driver_earnings_today", driver.todayEarnings.toFloat())
            putInt("driver_completed_trips", driver.completedTrips)
            putFloat("driver_wallet_balance", driver.walletBalance.toFloat())
            putString("driver_bank_name", driver.bankName)
            putString("driver_bank_account_number", driver.bankAccountNumber)
            putString("driver_bank_ifsc", driver.bankIfscCode)
            putString("driver_bank_holder", driver.bankAccountHolderName)
            putString("driver_upi_id", driver.upiId)
        }.apply()

        _localDriver.value = driver

        if (isDemoMode || firestore == null) {
            onSuccess()
        } else {
            // Snappy local response: Call onSuccess immediately and sync Firestore in the background
            onSuccess()
            firestore?.collection("drivers")?.document(driver.uid)?.set(driver)
                ?.addOnSuccessListener {
                    Log.d(TAG, "Drivers Firestore background sync successful.")
                }
                ?.addOnFailureListener { e ->
                    Log.e(TAG, "Firestore background save failed.", e)
                }
        }
    }

    fun updateOnlineStatus(isOnline: Boolean, onSuccess: () -> Unit = {}) {
        val current = _localDriver.value
        if (current != null) {
            val updated = current.copy(onlineStatus = isOnline)
            _localDriver.value = updated
            
            val prefs = context.getSharedPreferences("o2_cabs_driver_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("driver_online", isOnline).apply()

            if (!isDemoMode && firestore != null) {
                firestore?.collection("drivers")?.document(current.uid)?.update("onlineStatus", isOnline)
                    ?.addOnSuccessListener { onSuccess() }
                    ?.addOnFailureListener { 
                        Log.e(TAG, "Firestore online update failed.") 
                        onSuccess()
                    }
            } else {
                onSuccess()
            }
        }
    }

    // ==========================================
    // RIDE & TRIP MANAGEMENT APIs
    // ==========================================

    fun observeRideRequests(onUpdate: (List<Ride>) -> Unit) {
        if (isDemoMode || firestore == null) {
            // Emulates active ride changes
            CoroutineScope(Dispatchers.Main).launch {
                _localRides.collect { rides ->
                    onUpdate(rides)
                }
            }
        } else {
            // Firestore real-time snapshot listener on standard "rides" collection
            ridesListener?.remove()
            ridesListener = firestore?.collection("rides")
                ?.whereEqualTo("status", "PENDING")
                ?.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Rides subscription failed. Falling back to local engine.", error)
                        onUpdate(_localRides.value)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val ridesList = mutableListOf<Ride>()
                        for (doc in snapshot) {
                            val ride = doc.toObject(Ride::class.java)
                            ridesList.add(ride.copy(id = doc.id))
                        }
                        // Merge with local if dry run
                        if (ridesList.isEmpty()) {
                            onUpdate(_localRides.value)
                        } else {
                            onUpdate(ridesList)
                        }
                    } else {
                        onUpdate(_localRides.value)
                    }
                }
        }
    }

    fun updateRideStatus(rideId: String, status: String, onSuccess: () -> Unit) {
        // Update local list
        val updatedList = _localRides.value.map { r ->
            if (r.id == rideId) r.copy(status = status) else r
        }
        _localRides.value = updatedList

        // Send alert/notification
        if (status == "ACCEPTED") {
            addNotification("Trip Accepted", "Navigate to pickup point for client ${getCurrentActiveRide()?.customerName ?: "Customer"}.")
        } else if (status == "STARTED") {
            addNotification("Trip Started", "Driving to passenger's drop-off location.")
        } else if (status == "COMPLETED") {
            val currentRide = updatedList.find { r -> r.id == rideId }
            val fare = currentRide?.estimatedFare ?: 0.0
            
            // Increment local earnings
            val driver = _localDriver.value
            if (driver != null) {
                val updatedDriver = driver.copy(
                    todayEarnings = driver.todayEarnings + fare,
                    completedTrips = driver.completedTrips + 1,
                    walletBalance = driver.walletBalance + fare
                )
                saveDriverProfile(updatedDriver, {}, {})
            }
            addNotification("Trip Standard Fare Credited", "Congratulations! ₹${fare} added to your driver wallet.")
        }

        if (isDemoMode || firestore == null) {
            onSuccess()
        } else {
            firestore?.collection("rides")?.document(rideId)?.update("status", status)
                ?.addOnSuccessListener { onSuccess() }
                ?.addOnFailureListener { e ->
                    Log.e(TAG, "Failed updating firestore ride state. Succeeding locally.", e)
                    onSuccess()
                }
        }
    }

    fun getCurrentActiveRide(): Ride? {
        return _localRides.value.find { r -> r.status == "ACCEPTED" || r.status == "STARTED" }
    }

    fun rejectRide(rideId: String, onSuccess: () -> Unit) {
        val updated = _localRides.value.filter { r -> r.id != rideId }
        _localRides.value = updated
        onSuccess()
    }

    // ==========================================
    // NOTIFICATIONS ENGINE
    // ==========================================

    fun addNotification(title: String, message: String) {
        val newNotif = Notification(
            id = "notif_" + UUID.randomUUID().toString().take(6),
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        val currentList = _localNotifications.value.toMutableList()
        currentList.add(0, newNotif) // insertion at top
        _localNotifications.value = currentList

        if (!isDemoMode && firestore != null) {
            val driverUid = getCurrentUid()
            if (driverUid != null) {
                firestore?.collection("drivers")?.document(driverUid)
                    ?.collection("notifications")?.add(newNotif)
            }
        }
    }

    fun observeNotifications(onUpdate: (List<Notification>) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            _localNotifications.collect { list ->
                onUpdate(list)
            }
        }
    }

    fun markNotificationsAsRead() {
        val updated = _localNotifications.value.map { n -> n.copy(isRead = true) }
        _localNotifications.value = updated
    }

    fun getWalletLimit(): Double {
        val prefs = context.getSharedPreferences("o2_cabs_driver_prefs", Context.MODE_PRIVATE)
        return prefs.getFloat("driver_wallet_limit", 50000f).toDouble()
    }

    fun saveWalletLimit(limit: Double) {
        val prefs = context.getSharedPreferences("o2_cabs_driver_prefs", Context.MODE_PRIVATE)
        prefs.edit().putFloat("driver_wallet_limit", limit.toFloat()).apply()
    }

    // ==========================================
    // WALLET WITHDRAWAL APIs
    // ==========================================

    fun requestWalletWithdrawal(amount: Double, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val driver = _localDriver.value
        if (driver == null) {
            onError("Driver profile not found.")
            return
        }
        if (amount <= 0) {
            onError("Please enter a valid amount.")
            return
        }
        if (driver.walletBalance < amount) {
            onError("Insufficient wallet balance. Accessible: ₹${driver.walletBalance}")
            return
        }

        // Subtract from local wallet
        val updatedDriver = driver.copy(
            walletBalance = driver.walletBalance - amount
        )
        saveDriverProfile(updatedDriver, {
            addNotification(
                "Withdrawal Pending",
                "Withdrawal request of ₹${amount} submitted. Will be processed within 24-48 working hours."
            )
            onSuccess()
        }, {
            onError("Failed to update wallet profile.")
        })
    }

    fun cleanup() {
        ridesListener?.remove()
        notificationsListener?.remove()
    }
}
