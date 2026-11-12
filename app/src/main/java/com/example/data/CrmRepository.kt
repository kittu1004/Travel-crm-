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

  // --- REALTIME OBSERVATION FLOWS ---

  fun observeBookings(userId: String = requireUserId()): Flow<List<Booking>> = flow {
    val path = "users/$userId/bookings"
    emitAll(
      db.collection("users").document(userId).collection("bookings")
        .snapshots()
        .map { snapshot ->
          snapshot.toObjects(Booking::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
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
}
