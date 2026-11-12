package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CrmRepository(private val db: FirebaseFirestore) {

  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth = Firebase.auth

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
  }

  // --- REALTIME OBSERVATION FLOWS (ONLY USER-SAVED DATA, NO DEFAULT DATA) ---

  fun observeBookings(userId: String = requireUserId()): Flow<List<Booking>> = flow {
    val path = "users/$userId/bookings"
    emitAll(
      db.collection("users").document(userId).collection("bookings")
        .snapshots()
        .map { snapshot ->
          snapshot.toObjects(Booking::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .filterNot { isSeededOrDefaultBooking(it) }
            .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
        }
        .catch { error ->
          logStreamErrorIfAuthenticated(error, userId, path)
          throw error
        }
    )
  }.flowOn(Dispatchers.Default)

  fun observeDrivers(userId: String = requireUserId()): Flow<List<Driver>> = flow {
    val path = "users/$userId/drivers"
    emitAll(
      db.collection("users").document(userId).collection("drivers")
        .snapshots()
        .map { snapshot ->
          snapshot.toObjects(Driver::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .filterNot { isSeededOrDefaultDriver(it) }
            .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
        }
        .catch { error ->
          logStreamErrorIfAuthenticated(error, userId, path)
          throw error
        }
    )
  }.flowOn(Dispatchers.Default)

  fun observeVehicles(userId: String = requireUserId()): Flow<List<Vehicle>> = flow {
    val path = "users/$userId/vehicles"
    emitAll(
      db.collection("users").document(userId).collection("vehicles")
        .snapshots()
        .map { snapshot ->
          snapshot.toObjects(Vehicle::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .filterNot { isSeededOrDefaultVehicle(it) }
            .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
        }
        .catch { error ->
          logStreamErrorIfAuthenticated(error, userId, path)
          throw error
        }
    )
  }.flowOn(Dispatchers.Default)

  fun observeLeads(userId: String = requireUserId()): Flow<List<Lead>> = flow {
    val path = "users/$userId/leads"
    emitAll(
      db.collection("users").document(userId).collection("leads")
        .snapshots()
        .map { snapshot ->
          snapshot.toObjects(Lead::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .filterNot { isSeededOrDefaultLead(it) }
            .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
        }
        .catch { error ->
          logStreamErrorIfAuthenticated(error, userId, path)
          throw error
        }
    )
  }.flowOn(Dispatchers.Default)

  fun observeCustomers(userId: String = requireUserId()): Flow<List<Customer>> = flow {
    val path = "users/$userId/customers"
    emitAll(
      db.collection("users").document(userId).collection("customers")
        .snapshots()
        .map { snapshot ->
          snapshot.toObjects(Customer::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .filterNot { isSeededOrDefaultCustomer(it) }
            .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
        }
        .catch { error ->
          logStreamErrorIfAuthenticated(error, userId, path)
          throw error
        }
    )
  }.flowOn(Dispatchers.Default)

  private fun logStreamErrorIfAuthenticated(error: Throwable, uid: String, path: String) {
    val firestoreError = generateSequence(error) { it.cause }
      .filterIsInstance<FirebaseFirestoreException>()
      .firstOrNull()
    if (firestoreError == null && error is CancellationException) throw error
    val exceptionToLog = firestoreError ?: (error as? Exception)
    if (exceptionToLog != null && auth.currentUser?.uid == uid) {
      handleFirestoreError(exceptionToLog, OperationType.LIST, path)
    }
  }

  // --- BOOKINGS CRUD ---

  suspend fun addBooking(booking: Booking): Result<String> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("bookings").document()
    val payload = mapOf(
      "userId" to uid,
      "customerName" to booking.customerName.trim(),
      "companyName" to booking.companyName.trim(),
      "customerPhone" to booking.customerPhone.trim(),
      "pickupLocation" to booking.pickupLocation.trim(),
      "dropLocation" to booking.dropLocation.trim(),
      "vehicleCategory" to booking.vehicleCategory,
      "vehicleModel" to booking.vehicleModel.trim(),
      "vehicleNumber" to booking.vehicleNumber.trim(),
      "driverName" to booking.driverName.trim(),
      "driverPhone" to booking.driverPhone.trim(),
      "tripStatus" to booking.tripStatus,
      "paymentStatus" to booking.paymentStatus,
      "paymentChannel" to booking.paymentChannel,
      "baseAmount" to booking.baseAmount,
      "tollCharges" to booking.tollCharges,
      "parkingCharges" to booking.parkingCharges,
      "gstAmount" to booking.gstAmount,
      "totalAmount" to booking.totalAmount,
      "expenseAmount" to booking.expenseAmount,
      "distanceKm" to booking.distanceKm,
      "tripDate" to booking.tripDate,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp(),
    )
    try {
      docRef.set(payload).await()
      docRef.id
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  suspend fun getBookingById(ownerUid: String, bookingId: String): Result<Booking?> = runCatching {
    val docRef = db.collection("users").document(ownerUid).collection("bookings").document(bookingId)
    try {
      docRef.get().await().toObject(Booking::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, docRef.path)
      throw e
    }
  }

  suspend fun updateBooking(booking: Booking): Result<Unit> = runCatching {
    val uid = requireUserId()
    require(booking.id.isNotBlank()) { "Booking ID must not be blank" }
    requireNotNull(booking.createdAt) { "Booking createdAt must not be null" }
    val docRef = db.collection("users").document(uid).collection("bookings").document(booking.id)
    try {
      docRef.set(booking.copy(userId = uid, updatedAt = null)).await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun deleteBooking(bookingId: String): Result<Unit> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("bookings").document(bookingId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }

  // --- DRIVERS CRUD ---

  suspend fun addDriver(driver: Driver): Result<String> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("drivers").document()
    val payload = mapOf(
      "userId" to uid,
      "name" to driver.name.trim(),
      "phone" to driver.phone.trim(),
      "address" to driver.address.trim(),
      "licenseNumber" to driver.licenseNumber.trim(),
      "aadharNumber" to driver.aadharNumber.trim(),
      "selfiePhotoUri" to driver.selfiePhotoUri.trim(),
      "documentsUploaded" to driver.documentsUploaded.trim(),
      "assignedVehicle" to driver.assignedVehicle.trim(),
      "vehicleCategory" to driver.vehicleCategory,
      "status" to driver.status,
      "tripsCompleted" to driver.tripsCompleted,
      "earningsTotal" to driver.earningsTotal,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp(),
    )
    try {
      docRef.set(payload).await()
      docRef.id
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  suspend fun updateDriver(driver: Driver): Result<Unit> = runCatching {
    val uid = requireUserId()
    require(driver.id.isNotBlank()) { "Driver ID must not be blank" }
    requireNotNull(driver.createdAt) { "Driver createdAt must not be null" }
    val docRef = db.collection("users").document(uid).collection("drivers").document(driver.id)
    try {
      docRef.set(driver.copy(userId = uid, updatedAt = null)).await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun deleteDriver(driverId: String): Result<Unit> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("drivers").document(driverId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }

  // --- VEHICLES CRUD ---

  suspend fun addVehicle(vehicle: Vehicle): Result<String> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("vehicles").document()
    val payload = mapOf(
      "userId" to uid,
      "registrationNumber" to vehicle.registrationNumber.trim(),
      "modelName" to vehicle.modelName.trim(),
      "manufacturingYear" to vehicle.manufacturingYear.trim(),
      "category" to vehicle.category,
      "status" to vehicle.status,
      "maintenanceCost" to vehicle.maintenanceCost,
      "serviceDate" to vehicle.serviceDate.trim(),
      "nextServiceDate" to vehicle.nextServiceDate.trim(),
      "serviceKilometers" to vehicle.serviceKilometers,
      "presentRunningKilometers" to vehicle.presentRunningKilometers,
      "nextServiceKilometers" to vehicle.nextServiceKilometers,
      "partsChanged" to vehicle.partsChanged.trim(),
      "insuranceDetails" to vehicle.insuranceDetails.trim(),
      "insuranceExpiryDate" to vehicle.insuranceExpiryDate.trim(),
      "fitnessDetails" to vehicle.fitnessDetails.trim(),
      "fitnessExpiryDate" to vehicle.fitnessExpiryDate.trim(),
      "pollutionDetails" to vehicle.pollutionDetails.trim(),
      "pollutionExpiryDate" to vehicle.pollutionExpiryDate.trim(),
      "serviceNotes" to vehicle.serviceNotes.trim(),
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp(),
    )
    try {
      docRef.set(payload).await()
      docRef.id
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  suspend fun updateVehicle(vehicle: Vehicle): Result<Unit> = runCatching {
    val uid = requireUserId()
    require(vehicle.id.isNotBlank()) { "Vehicle ID must not be blank" }
    requireNotNull(vehicle.createdAt) { "Vehicle createdAt must not be null" }
    val docRef = db.collection("users").document(uid).collection("vehicles").document(vehicle.id)
    try {
      docRef.set(vehicle.copy(userId = uid, updatedAt = null)).await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun deleteVehicle(vehicleId: String): Result<Unit> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("vehicles").document(vehicleId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }

  // --- LEADS CRUD ---

  suspend fun addLead(lead: Lead): Result<String> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("leads").document()
    val payload = mapOf(
      "userId" to uid,
      "clientName" to lead.clientName.trim(),
      "clientPhone" to lead.clientPhone.trim(),
      "routeSummary" to lead.routeSummary.trim(),
      "preferredCategory" to lead.preferredCategory,
      "quotedAmount" to lead.quotedAmount,
      "status" to lead.status,
      "source" to lead.source.trim(),
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp(),
    )
    try {
      docRef.set(payload).await()
      docRef.id
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  suspend fun updateLead(lead: Lead): Result<Unit> = runCatching {
    val uid = requireUserId()
    require(lead.id.isNotBlank()) { "Lead ID must not be blank" }
    requireNotNull(lead.createdAt) { "Lead createdAt must not be null" }
    val docRef = db.collection("users").document(uid).collection("leads").document(lead.id)
    try {
      docRef.set(lead.copy(userId = uid, updatedAt = null)).await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun deleteLead(leadId: String): Result<Unit> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("leads").document(leadId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }

  // --- CUSTOMERS CRUD ---

  suspend fun addCustomer(customer: Customer): Result<String> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("customers").document()
    val payload = mapOf(
      "userId" to uid,
      "name" to customer.name.trim(),
      "address" to customer.address.trim(),
      "phone" to customer.phone.trim(),
      "email" to customer.email.trim(),
      "companyName" to customer.companyName.trim(),
      "gstin" to customer.gstin.trim(),
      "totalBookings" to customer.totalBookings,
      "totalBilled" to customer.totalBilled,
      "pendingBalance" to customer.pendingBalance,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp(),
    )
    try {
      docRef.set(payload).await()
      docRef.id
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }
  }

  suspend fun updateCustomer(customer: Customer): Result<Unit> = runCatching {
    val uid = requireUserId()
    require(customer.id.isNotBlank()) { "Customer ID must not be blank" }
    requireNotNull(customer.createdAt) { "Customer createdAt must not be null" }
    val docRef = db.collection("users").document(uid).collection("customers").document(customer.id)
    try {
      docRef.set(customer.copy(userId = uid, updatedAt = null)).await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      throw e
    }
  }

  suspend fun deleteCustomer(customerId: String): Result<Unit> = runCatching {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid).collection("customers").document(customerId)
    try {
      docRef.delete().await()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.DELETE, docRef.path)
      throw e
    }
  }

  suspend fun ensureDefaultDriversSeeded(): Result<Unit> = Result.success(Unit)

  suspend fun loadLiveDemoData(todayDate: String): Result<Unit> = Result.success(Unit)

  fun isSeededOrDefaultDriver(d: Driver): Boolean {
    val name = d.name.trim()
    val lic = d.licenseNumber.trim()
    val phone = d.phone.trim()
    val veh = d.assignedVehicle.trim()
    if (name.isBlank() && phone.isBlank() && lic.isBlank()) return true
    if (name in DEFAULT_DRIVER_NAMES) return true
    if (lic in DEFAULT_DRIVER_LICENSES) return true
    if (phone in DEFAULT_DRIVER_PHONES) return true
    if (veh in DEFAULT_DRIVER_VEHICLES) return true
    return false
  }

  fun isSeededOrDefaultCustomer(c: Customer): Boolean {
    val name = c.name.trim()
    val phone = c.phone.trim()
    val email = c.email.trim()
    val gstin = c.gstin.trim()
    if (name.isBlank() && phone.isBlank()) return true
    if (name in DEFAULT_CUSTOMER_NAMES) return true
    if (email in DEFAULT_CUSTOMER_EMAILS) return true
    if (gstin in DEFAULT_CUSTOMER_GSTINS) return true
    if (phone in DEFAULT_CUSTOMER_PHONES) return true
    return false
  }

  fun isSeededOrDefaultVehicle(v: Vehicle): Boolean {
    val reg = v.registrationNumber.trim()
    val mod = v.modelName.trim()
    if (reg.isBlank() && mod.isBlank()) return true
    if (reg in DEFAULT_VEHICLE_REGS) return true
    if (mod in DEFAULT_VEHICLE_MODELS) return true
    return false
  }

  fun isSeededOrDefaultLead(l: Lead): Boolean {
    val name = l.clientName.trim()
    val phone = l.clientPhone.trim()
    if (name.isBlank() && phone.isBlank()) return true
    if (name in DEFAULT_LEAD_NAMES) return true
    if (phone in DEFAULT_LEAD_PHONES) return true
    return false
  }

  fun isSeededOrDefaultBooking(b: Booking): Boolean {
    val cust = b.customerName.trim()
    val veh = b.vehicleNumber.trim()
    val drv = b.driverName.trim()
    val phone = b.customerPhone.trim()
    if (cust.isBlank() && b.companyName.isBlank() && b.pickupLocation.isBlank()) return true
    if (cust in DEFAULT_CUSTOMER_NAMES) return true
    if (veh in DEFAULT_VEHICLE_REGS) return true
    if (drv in DEFAULT_DRIVER_NAMES) return true
    if (phone in DEFAULT_CUSTOMER_PHONES) return true
    return false
  }

  /**
   * Automatically removes any previously seeded default/sample documents from Firestore
   * so only genuine user-saved records remain in the database.
   */
  suspend fun purgeLegacyDefaultData(userId: String = requireUserId()) {
    runCatching {
      val userDoc = db.collection("users").document(userId)

      val driversSnap = userDoc.collection("drivers").get().await()
      for (doc in driversSnap.documents) {
        val d = doc.toObject(Driver::class.java)
        if (d != null && isSeededOrDefaultDriver(d)) {
          doc.reference.delete().await()
        }
      }

      val customersSnap = userDoc.collection("customers").get().await()
      for (doc in customersSnap.documents) {
        val c = doc.toObject(Customer::class.java)
        if (c != null && isSeededOrDefaultCustomer(c)) {
          doc.reference.delete().await()
        }
      }

      val vehiclesSnap = userDoc.collection("vehicles").get().await()
      for (doc in vehiclesSnap.documents) {
        val v = doc.toObject(Vehicle::class.java)
        if (v != null && isSeededOrDefaultVehicle(v)) {
          doc.reference.delete().await()
        }
      }

      val leadsSnap = userDoc.collection("leads").get().await()
      for (doc in leadsSnap.documents) {
        val l = doc.toObject(Lead::class.java)
        if (l != null && isSeededOrDefaultLead(l)) {
          doc.reference.delete().await()
        }
      }

      val bookingsSnap = userDoc.collection("bookings").get().await()
      for (doc in bookingsSnap.documents) {
        val b = doc.toObject(Booking::class.java)
        if (b != null && isSeededOrDefaultBooking(b)) {
          doc.reference.delete().await()
        }
      }
    }
  }

  companion object {
    private val DEFAULT_DRIVER_NAMES = setOf(
      "Ramesh Kumar",
      "Suresh Gowda",
      "Mohammed Irfan",
      "Venkatesh Rao",
      "New Fleet Driver",
      "Manjunath Naik"
    )
    private val DEFAULT_DRIVER_LICENSES = setOf(
      "KA01-2016-0089123",
      "KA05-2018-0041290",
      "KA03-2019-0076511",
      "KA04-2015-0011842",
      "KA01-2021-0098123",
      "KA01-2020-0054120"
    )
    private val DEFAULT_DRIVER_PHONES = setOf(
      "+91 98450 11223",
      "+91 99001 44556",
      "+91 97312 77889",
      "+91 94480 33991",
      "+91 98450 12345",
      "+91 98450 55667"
    )
    private val DEFAULT_DRIVER_VEHICLES = setOf(
      "Toyota Innova Crysta (KA-01-MG-4590)",
      "Maruti Swift Dzire (KA-05-AB-2104)",
      "Hyundai Creta (KA-03-NC-8812)",
      "Force Urbania 17S (KA-04-TR-9901)",
      "Toyota Innova (KA-01-AB-1001)"
    )

    private val DEFAULT_CUSTOMER_NAMES = setOf(
      "Vikram Malhotra",
      "Ananya Deshmukh",
      "Karthik Subramanian",
      "Apex Global Tech Retreat",
      "Rohan Kulkarni",
      "Walk-in Corporate Client",
      "Walk-in Customer",
      "Rajesh Sharma"
    )
    private val DEFAULT_CUSTOMER_EMAILS = setOf(
      "vikram@infosys-travel.in",
      "ananya@horizonpharma.co.in",
      "karthik.s@gmail.com",
      "customer@fleetcrm.in"
    )
    private val DEFAULT_CUSTOMER_GSTINS = setOf(
      "29AABCA8912K1Z5",
      "27AAACH4412P1Z2",
      "UNREGISTERED-B2C",
      "UNREGISTERED"
    )
    private val DEFAULT_CUSTOMER_PHONES = setOf(
      "+91 98201 12244",
      "+91 97654 32109",
      "+91 94441 88776",
      "+91 98442 66771",
      "+91 98450 00000",
      "+91 98201 00000",
      "+91 98450 11223"
    )

    private val DEFAULT_VEHICLE_REGS = setOf(
      "KA-05-AB-2104",
      "KA-03-NC-8812",
      "KA-01-MG-4590",
      "KA-04-TR-9901",
      "KA-01-FL-2026",
      "KA-01-AB-7788"
    )
    private val DEFAULT_VEHICLE_MODELS = setOf(
      "Maruti Suzuki Dzire ZXI",
      "Hyundai Creta SX Diesel",
      "Toyota Innova Crysta 2.4Z",
      "Force Tempo Traveller 17-Seater",
      "Toyota Innova Hycross",
      "Toyota Innova Hycross ZX"
    )

    private val DEFAULT_LEAD_NAMES = setOf(
      "Deepak Joshi",
      "Meera Krishnan",
      "Siddharth Verma",
      "Prospective Traveler",
      "Arjun Reddy",
      "Website Visitor"
    )
    private val DEFAULT_LEAD_PHONES = setOf(
      "+91 98801 55443",
      "+91 99160 22334",
      "+91 96112 88990",
      "+91 99000 11223",
      "+91 99801 22334",
      "+91 98450 99001"
    )
  }
}
