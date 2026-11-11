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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
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
  }

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
  }

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
  }

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
  }

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
  }

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
      "customerPhone" to booking.customerPhone.trim(),
      "pickupLocation" to booking.pickupLocation.trim(),
      "dropLocation" to booking.dropLocation.trim(),
      "vehicleCategory" to booking.vehicleCategory,
      "vehicleNumber" to booking.vehicleNumber.trim(),
      "driverName" to booking.driverName.trim(),
      "tripStatus" to booking.tripStatus,
      "paymentStatus" to booking.paymentStatus,
      "paymentChannel" to booking.paymentChannel,
      "baseAmount" to booking.baseAmount,
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
      "licenseNumber" to driver.licenseNumber.trim(),
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
      "category" to vehicle.category,
      "status" to vehicle.status,
      "maintenanceCost" to vehicle.maintenanceCost,
      "nextServiceDate" to vehicle.nextServiceDate.trim(),
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

  // --- INITIAL DRIVER ROSTER (4 DRIVERS MATCHING REFERENCE UI) & LIVE DEMO DATA ---

  suspend fun ensureDefaultDriversSeeded(): Result<Unit> = runCatching {
    val uid = requireUserId()
    val driversCol = db.collection("users").document(uid).collection("drivers")
    val existing = try {
      driversCol.get().await()
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.LIST, driversCol.path)
      throw e
    }
    if (existing.isEmpty) {
      val initialDrivers = listOf(
        Driver(
          name = "Ramesh Kumar",
          phone = "+91 98450 11223",
          licenseNumber = "KA01-2016-0089123",
          assignedVehicle = "Toyota Innova Crysta (KA-01-MG-4590)",
          vehicleCategory = "SUVs",
          status = "Available",
          tripsCompleted = 148L,
          earningsTotal = 264000L,
        ),
        Driver(
          name = "Suresh Gowda",
          phone = "+91 99001 44556",
          licenseNumber = "KA05-2018-0041290",
          assignedVehicle = "Maruti Swift Dzire (KA-05-AB-2104)",
          vehicleCategory = "Sedans",
          status = "On Trip",
          tripsCompleted = 212L,
          earningsTotal = 318000L,
        ),
        Driver(
          name = "Mohammed Irfan",
          phone = "+91 97312 77889",
          licenseNumber = "KA03-2019-0076511",
          assignedVehicle = "Hyundai Creta (KA-03-NC-8812)",
          vehicleCategory = "Compact SUVs",
          status = "Available",
          tripsCompleted = 96L,
          earningsTotal = 172000L,
        ),
        Driver(
          name = "Venkatesh Rao",
          phone = "+91 94480 33991",
          licenseNumber = "KA04-2015-0011842",
          assignedVehicle = "Force Urbania 17S (KA-04-TR-9901)",
          vehicleCategory = "Tourist Travellers",
          status = "Available",
          tripsCompleted = 184L,
          earningsTotal = 410000L,
        ),
      )
      for (driver in initialDrivers) {
        addDriver(driver).getOrThrow()
      }
    }
  }

  suspend fun loadLiveDemoData(todayDate: String): Result<Unit> = runCatching {
    ensureDefaultDriversSeeded().getOrThrow()

    val demoVehicles = listOf(
      Vehicle(
        registrationNumber = "KA-05-AB-2104",
        modelName = "Maruti Suzuki Dzire ZXI",
        category = "Sedans",
        status = "On Trip",
        maintenanceCost = 6400L,
        nextServiceDate = "2026-12-10",
        serviceNotes = "40,000 km periodic service & wheel alignment complete",
      ),
      Vehicle(
        registrationNumber = "KA-03-NC-8812",
        modelName = "Hyundai Creta SX Diesel",
        category = "Compact SUVs",
        status = "Available",
        maintenanceCost = 9200L,
        nextServiceDate = "2026-12-22",
        serviceNotes = "Brake pads & synthetic oil replaced",
      ),
      Vehicle(
        registrationNumber = "KA-01-MG-4590",
        modelName = "Toyota Innova Crysta 2.4Z",
        category = "SUVs",
        status = "Available",
        maintenanceCost = 14800L,
        nextServiceDate = "2027-01-05",
        serviceNotes = "Full AC compressor & highway tire inspection",
      ),
      Vehicle(
        registrationNumber = "KA-04-TR-9901",
        modelName = "Force Tempo Traveller 17-Seater",
        category = "Tourist Travellers",
        status = "Maintenance",
        maintenanceCost = 22500L,
        nextServiceDate = "2026-11-18",
        serviceNotes = "Suspension bush kit & commercial fitness renewal",
      ),
    )
    for (v in demoVehicles) {
      addVehicle(v).getOrThrow()
    }

    val demoCustomers = listOf(
      Customer(
        name = "Vikram Malhotra",
        phone = "+91 98201 12244",
        email = "vikram@infosys-travel.in",
        companyName = "Apex Global Tech Pvt Ltd",
        gstin = "29AABCA8912K1Z5",
        totalBookings = 14L,
        totalBilled = 186500L,
        pendingBalance = 18900L,
      ),
      Customer(
        name = "Ananya Deshmukh",
        phone = "+91 97654 32109",
        email = "ananya@horizonpharma.co.in",
        companyName = "Horizon BioLabs India",
        gstin = "27AAACH4412P1Z2",
        totalBookings = 9L,
        totalBilled = 112400L,
        pendingBalance = 0L,
      ),
      Customer(
        name = "Karthik Subramanian",
        phone = "+91 94441 88776",
        email = "karthik.s@gmail.com",
        companyName = "Subramanian Family Tours",
        gstin = "UNREGISTERED-B2C",
        totalBookings = 4L,
        totalBilled = 64200L,
        pendingBalance = 14700L,
      ),
    )
    for (c in demoCustomers) {
      addCustomer(c).getOrThrow()
    }

    val demoLeads = listOf(
      Lead(
        clientName = "Deepak Joshi",
        clientPhone = "+91 98801 55443",
        routeSummary = "Bengaluru to Ooty & Coonoor (4 Days Family Package)",
        preferredCategory = "SUVs",
        quotedAmount = 24500L,
        status = "New",
        source = "Website",
      ),
      Lead(
        clientName = "Meera Krishnan",
        clientPhone = "+91 99160 22334",
        routeSummary = "Corporate Airport Transfers (12 Executives)",
        preferredCategory = "Tourist Travellers",
        quotedAmount = 18000L,
        status = "Quoted",
        source = "Corporate Desk",
      ),
      Lead(
        clientName = "Siddharth Verma",
        clientPhone = "+91 96112 88990",
        routeSummary = "Kempegowda Airport T2 to Electronic City Phase 1",
        preferredCategory = "Sedans",
        quotedAmount = 2200L,
        status = "Converted",
        source = "Phone Inquiry",
      ),
    )
    for (l in demoLeads) {
      addLead(l).getOrThrow()
    }

    val demoBookings = listOf(
      Booking(
        customerName = "Vikram Malhotra",
        customerPhone = "+91 98201 12244",
        pickupLocation = "Kempegowda Intl Airport T2",
        dropLocation = "Whitefield IT Park, Bengaluru",
        vehicleCategory = "Sedans",
        vehicleNumber = "KA-05-AB-2104",
        driverName = "Suresh Gowda",
        tripStatus = "Active",
        paymentStatus = "Paid",
        paymentChannel = "UPI",
        baseAmount = 3200L,
        gstAmount = 160L,
        totalAmount = 3360L,
        expenseAmount = 750L,
        distanceKm = 54L,
        tripDate = todayDate,
      ),
      Booking(
        customerName = "Ananya Deshmukh",
        customerPhone = "+91 97654 32109",
        pickupLocation = "MG Road Metro Hub",
        dropLocation = "Mysore Infosys Campus (Round Trip)",
        vehicleCategory = "SUVs",
        vehicleNumber = "KA-01-MG-4590",
        driverName = "Ramesh Kumar",
        tripStatus = "Completed",
        paymentStatus = "Paid",
        paymentChannel = "Bank Transfer",
        baseAmount = 12000L,
        gstAmount = 600L,
        totalAmount = 12600L,
        expenseAmount = 3100L,
        distanceKm = 310L,
        tripDate = todayDate,
      ),
      Booking(
        customerName = "Karthik Subramanian",
        customerPhone = "+91 94441 88776",
        pickupLocation = "Indiranagar 100ft Road",
        dropLocation = "Coorg Madikeri Resort (3 Days)",
        vehicleCategory = "Compact SUVs",
        vehicleNumber = "KA-03-NC-8812",
        driverName = "Mohammed Irfan",
        tripStatus = "Active",
        paymentStatus = "Pending",
        paymentChannel = "Corporate Credit",
        baseAmount = 14000L,
        gstAmount = 700L,
        totalAmount = 14700L,
        expenseAmount = 3800L,
        distanceKm = 540L,
        tripDate = todayDate,
      ),
      Booking(
        customerName = "Apex Global Tech Retreat",
        customerPhone = "+91 98201 12244",
        pickupLocation = "Manyata Tech Park, Hebbal",
        dropLocation = "Chikmagalur Hills Outing (17 Pax)",
        vehicleCategory = "Tourist Travellers",
        vehicleNumber = "KA-04-TR-9901",
        driverName = "Venkatesh Rao",
        tripStatus = "Completed",
        paymentStatus = "Pending",
        paymentChannel = "Corporate Credit",
        baseAmount = 18000L,
        gstAmount = 900L,
        totalAmount = 18900L,
        expenseAmount = 5200L,
        distanceKm = 510L,
        tripDate = "2026-11-08",
      ),
      Booking(
        customerName = "Rohan Kulkarni",
        customerPhone = "+91 98442 66771",
        pickupLocation = "Koramangala 4th Block",
        dropLocation = "Bengaluru City Railway Station",
        vehicleCategory = "Sedans",
        vehicleNumber = "KA-05-AB-2104",
        driverName = "Suresh Gowda",
        tripStatus = "Cancelled",
        paymentStatus = "Paid",
        paymentChannel = "UPI",
        baseAmount = 800L,
        gstAmount = 40L,
        totalAmount = 840L,
        expenseAmount = 0L,
        distanceKm = 14L,
        tripDate = "2026-11-05",
      ),
    )
    for (b in demoBookings) {
      addBooking(b).getOrThrow()
    }
  }
}
