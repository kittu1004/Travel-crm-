package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Booking
import com.example.data.CrmRepository
import com.example.data.CrmSection
import com.example.data.Customer
import com.example.data.Driver
import com.example.data.InflowCategory
import com.example.data.Lead
import com.example.data.UiState
import com.example.data.Vehicle
import com.google.firebase.firestore.FirebaseFirestoreException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CrmViewModel(
  private val repository: CrmRepository,
  private val currentUserId: String,
) : ViewModel() {

  private val _selectedSection = MutableStateFlow(CrmSection.DASHBOARD)
  val selectedSection: StateFlow<CrmSection> = _selectedSection.asStateFlow()

  private val _selectedInflowCategory = MutableStateFlow(InflowCategory.ALL_INFLOWS)
  val selectedInflowCategory: StateFlow<InflowCategory> = _selectedInflowCategory.asStateFlow()

  private val _statusBanner = MutableStateFlow<String?>(null)
  val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

  private val _latestInvoiceBooking = MutableStateFlow<Booking?>(null)
  val latestInvoiceBooking: StateFlow<Booking?> = _latestInvoiceBooking.asStateFlow()

  private val _isActionBusy = MutableStateFlow(false)
  val isActionBusy: StateFlow<Boolean> = _isActionBusy.asStateFlow()

  private val _agencyName = MutableStateFlow("Apex Fleet & Travel Operations")
  val agencyName: StateFlow<String> = _agencyName.asStateFlow()

  private val _agencyGstin = MutableStateFlow("29AABCF9901K1Z8")
  val agencyGstin: StateFlow<String> = _agencyGstin.asStateFlow()

  private val _defaultGstPercent = MutableStateFlow(0)
  val defaultGstPercent: StateFlow<Int> = _defaultGstPercent.asStateFlow()

  val bookingsState: StateFlow<UiState<List<Booking>>> =
    repository.observeBookings(currentUserId)
      .map<List<Booking>, UiState<List<Booking>>> { UiState.Success(it) }
      .catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
          .filterIsInstance<FirebaseFirestoreException>()
          .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Error observing bookings", rootError)
        emit(UiState.Error(rootError.message ?: "Failed to load bookings"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Loading
      )

  val driversState: StateFlow<UiState<List<Driver>>> =
    repository.observeDrivers(currentUserId)
      .map<List<Driver>, UiState<List<Driver>>> { UiState.Success(it) }
      .catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
          .filterIsInstance<FirebaseFirestoreException>()
          .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Error observing drivers", rootError)
        emit(UiState.Error(rootError.message ?: "Failed to load drivers"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Loading
      )

  val vehiclesState: StateFlow<UiState<List<Vehicle>>> =
    repository.observeVehicles(currentUserId)
      .map<List<Vehicle>, UiState<List<Vehicle>>> { UiState.Success(it) }
      .catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
          .filterIsInstance<FirebaseFirestoreException>()
          .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Error observing vehicles", rootError)
        emit(UiState.Error(rootError.message ?: "Failed to load vehicles"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Loading
      )

  val leadsState: StateFlow<UiState<List<Lead>>> =
    repository.observeLeads(currentUserId)
      .map<List<Lead>, UiState<List<Lead>>> { UiState.Success(it) }
      .catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
          .filterIsInstance<FirebaseFirestoreException>()
          .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Error observing leads", rootError)
        emit(UiState.Error(rootError.message ?: "Failed to load leads"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Loading
      )

  val customersState: StateFlow<UiState<List<Customer>>> =
    repository.observeCustomers(currentUserId)
      .map<List<Customer>, UiState<List<Customer>>> { UiState.Success(it) }
      .catch { error ->
        val firestoreError = generateSequence(error) { it.cause }
          .filterIsInstance<FirebaseFirestoreException>()
          .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val rootError = firestoreError ?: error
        Log.w(TAG, "Error observing customers", rootError)
        emit(UiState.Error(rootError.message ?: "Failed to load customers"))
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Loading
      )

  fun selectSection(section: CrmSection) {
    _selectedSection.value = section
  }

  fun selectInflowCategory(category: InflowCategory) {
    _selectedInflowCategory.value = category
  }

  fun clearBanner() {
    _statusBanner.value = null
  }

  fun showStatusMessage(message: String) {
    _statusBanner.value = message
  }

  fun openInvoicePdfForBooking(booking: Booking) {
    _latestInvoiceBooking.value = booking
  }

  fun dismissInvoicePdfDialog() {
    _latestInvoiceBooking.value = null
  }

  fun todayDateString(): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    return sdf.format(Date())
  }

  fun loadLiveDemoData() {
    // Default/demo data seeding disabled per user requirement: keep only saved data.
  }

  fun refreshRevenueMetrics() {
    _statusBanner.value = "Live realized inflows & GST receivables synchronized with Cloud Firestore."
  }

  fun saveBookingDetails(
    existingBooking: Booking?,
    customerName: String,
    companyName: String = "",
    customerPhone: String,
    pickupLocation: String,
    dropLocation: String,
    vehicleCategory: String,
    vehicleModel: String = "",
    vehicleNumber: String,
    driverName: String,
    driverPhone: String = "",
    tripStatus: String,
    paymentStatus: String,
    paymentChannel: String,
    baseAmount: Long,
    tollCharges: Long,
    parkingCharges: Long,
    gstAmount: Long,
    expenseAmount: Long,
    distanceKm: Long,
    tripDate: String,
  ) {
    val safeBase = baseAmount.coerceAtLeast(0L)
    val safeToll = tollCharges.coerceAtLeast(0L)
    val safeParking = parkingCharges.coerceAtLeast(0L)
    val safeGst = gstAmount.coerceAtLeast(0L)
    val totalAmount = safeBase + safeToll + safeParking + safeGst
    val resolvedCustomer = customerName.trim()

    viewModelScope.launch {
      _isActionBusy.value = true
      val bookingPayload = (existingBooking ?: Booking()).copy(
        customerName = resolvedCustomer,
        companyName = companyName.trim(),
        customerPhone = customerPhone.trim(),
        pickupLocation = pickupLocation.trim(),
        dropLocation = dropLocation.trim(),
        vehicleCategory = vehicleCategory.ifBlank { "Sedan" },
        vehicleModel = vehicleModel.trim(),
        vehicleNumber = vehicleNumber.trim(),
        driverName = driverName.trim(),
        driverPhone = driverPhone.trim(),
        tripStatus = tripStatus.ifBlank { "Active" },
        paymentStatus = paymentStatus.ifBlank { "Paid" },
        paymentChannel = paymentChannel.trim().ifBlank { "UPI" },
        baseAmount = safeBase,
        tollCharges = safeToll,
        parkingCharges = safeParking,
        gstAmount = safeGst,
        totalAmount = totalAmount.coerceAtLeast(0L),
        expenseAmount = expenseAmount.coerceAtLeast(0L),
        distanceKm = distanceKm.coerceAtLeast(0L),
        tripDate = tripDate.trim().ifBlank { todayDateString() },
      )

      val result = if (existingBooking != null && existingBooking.id.isNotBlank() && existingBooking.createdAt != null) {
        repository.updateBooking(bookingPayload)
      } else {
        repository.addBooking(bookingPayload)
      }
      _isActionBusy.value = false
      result.fold(
        onSuccess = {
          // Automatically sync customer details (name, address, mobile number, total trips) to Customer Data
          if (resolvedCustomer.isNotBlank()) {
            val currentCustomers = (customersState.value as? UiState.Success<List<Customer>>)?.data ?: emptyList()
            val currentBookings = (bookingsState.value as? UiState.Success<List<Booking>>)?.data ?: emptyList()
            val resolvedPhone = bookingPayload.customerPhone
            val resolvedAddress = if (bookingPayload.pickupLocation.isNotBlank() || bookingPayload.dropLocation.isNotBlank()) {
              "${bookingPayload.pickupLocation} → ${bookingPayload.dropLocation}"
            } else {
              bookingPayload.companyName
            }
            val existingCust = currentCustomers.firstOrNull {
              it.name.equals(resolvedCustomer, ignoreCase = true) ||
                (resolvedPhone.isNotBlank() && it.phone == resolvedPhone)
            }
            val matchingTripCount = (currentBookings.count {
              it.customerName.equals(resolvedCustomer, ignoreCase = true) ||
                (resolvedPhone.isNotBlank() && it.customerPhone == resolvedPhone)
            } + if (existingBooking == null) 1 else 0).toLong().coerceAtLeast(1L)

            if (existingCust != null && existingCust.id.isNotBlank() && existingCust.createdAt != null) {
              repository.updateCustomer(
                existingCust.copy(
                  name = resolvedCustomer,
                  address = existingCust.address.ifBlank { resolvedAddress },
                  phone = resolvedPhone.ifBlank { existingCust.phone },
                  companyName = bookingPayload.companyName.ifBlank { existingCust.companyName },
                  totalBookings = matchingTripCount.coerceAtLeast(existingCust.totalBookings)
                )
              )
            } else if (existingCust == null) {
              repository.addCustomer(
                Customer(
                  name = resolvedCustomer,
                  address = resolvedAddress,
                  phone = resolvedPhone,
                  email = "",
                  companyName = bookingPayload.companyName,
                  gstin = "",
                  totalBookings = matchingTripCount,
                  totalBilled = totalAmount,
                  pendingBalance = if (bookingPayload.paymentStatus == "Pending") totalAmount else 0L
                )
              )
            }
          }

          val actionWord = if (existingBooking != null) "Updated" else "Saved"
          _statusBanner.value = "$actionWord booking for ${resolvedCustomer.ifBlank { "Customer" }} (${formatInr(totalAmount)}) — Invoice PDF generated!"
          _latestInvoiceBooking.value = bookingPayload
        },
        onFailure = { e ->
          _statusBanner.value = "Error saving booking: ${e.localizedMessage}"
        }
      )
    }
  }

  fun createBooking(
    customerName: String,
    companyName: String = "",
    customerPhone: String,
    pickupLocation: String,
    dropLocation: String,
    vehicleCategory: String,
    vehicleModel: String = "",
    vehicleNumber: String,
    driverName: String,
    driverPhone: String = "",
    tripStatus: String,
    paymentStatus: String,
    paymentChannel: String,
    baseAmount: Long,
    tollCharges: Long = 0L,
    parkingCharges: Long = 0L,
    gstAmount: Long = 0L,
    expenseAmount: Long,
    distanceKm: Long,
    tripDate: String,
  ) {
    saveBookingDetails(
      existingBooking = null,
      customerName = customerName,
      companyName = companyName,
      customerPhone = customerPhone,
      pickupLocation = pickupLocation,
      dropLocation = dropLocation,
      vehicleCategory = vehicleCategory,
      vehicleModel = vehicleModel,
      vehicleNumber = vehicleNumber,
      driverName = driverName,
      driverPhone = driverPhone,
      tripStatus = tripStatus,
      paymentStatus = paymentStatus,
      paymentChannel = paymentChannel,
      baseAmount = baseAmount,
      tollCharges = tollCharges,
      parkingCharges = parkingCharges,
      gstAmount = gstAmount,
      expenseAmount = expenseAmount,
      distanceKm = distanceKm,
      tripDate = tripDate,
    )
  }

  fun updateBookingStatus(booking: Booking, newTripStatus: String) {
    viewModelScope.launch {
      repository.updateBooking(booking.copy(tripStatus = newTripStatus))
        .onSuccess { _statusBanner.value = "Booking status updated to $newTripStatus." }
    }
  }

  fun toggleBookingPaymentStatus(booking: Booking) {
    val nextPayment = if (booking.paymentStatus == "Paid") "Pending" else "Paid"
    viewModelScope.launch {
      repository.updateBooking(booking.copy(paymentStatus = nextPayment))
        .onSuccess { _statusBanner.value = "Invoice payment marked as $nextPayment." }
    }
  }

  fun updateBookingExpenses(booking: Booking, newExpense: Long, newDistanceKm: Long) {
    viewModelScope.launch {
      repository.updateBooking(
        booking.copy(
          expenseAmount = newExpense.coerceAtLeast(0L),
          distanceKm = newDistanceKm.coerceAtLeast(0L)
        )
      ).onSuccess {
        _statusBanner.value = "Updated trip expenses (${formatInr(newExpense)}) & odometer (${newDistanceKm} km) for ${booking.customerName}."
      }
    }
  }

  fun deleteBooking(bookingId: String) {
    viewModelScope.launch {
      repository.deleteBooking(bookingId)
        .onSuccess { _statusBanner.value = "Booking removed." }
    }
  }

  fun saveDriverDetails(
    existingDriver: Driver?,
    name: String,
    phone: String,
    address: String = "",
    licenseNumber: String,
    aadharNumber: String = "",
    selfiePhotoUri: String = "",
    documentsUploaded: String = "",
    assignedVehicle: String,
    vehicleCategory: String,
    status: String,
    tripsCompleted: Long = existingDriver?.tripsCompleted ?: 0L,
    earningsTotal: Long = existingDriver?.earningsTotal ?: 0L,
  ) {
    val resolvedName = name.trim()
    viewModelScope.launch {
      val payload = (existingDriver ?: Driver()).copy(
        name = resolvedName,
        phone = phone.trim(),
        address = address.trim(),
        licenseNumber = licenseNumber.trim(),
        aadharNumber = aadharNumber.trim(),
        selfiePhotoUri = selfiePhotoUri.trim(),
        documentsUploaded = documentsUploaded.trim(),
        assignedVehicle = assignedVehicle.trim(),
        vehicleCategory = vehicleCategory.ifBlank { "Sedan" },
        status = status.ifBlank { "Available" },
        tripsCompleted = tripsCompleted.coerceAtLeast(0L),
        earningsTotal = earningsTotal.coerceAtLeast(0L),
      )
      val result = if (existingDriver != null && existingDriver.id.isNotBlank() && existingDriver.createdAt != null) {
        repository.updateDriver(payload)
      } else {
        repository.addDriver(payload)
      }
      result.onSuccess {
        val actionWord = if (existingDriver != null) "Updated" else "Saved"
        _statusBanner.value = "$actionWord driver $resolvedName in Driver Roster."
      }
    }
  }

  fun createDriver(
    name: String,
    phone: String,
    licenseNumber: String,
    assignedVehicle: String,
    vehicleCategory: String,
    status: String,
  ) {
    saveDriverDetails(
      existingDriver = null,
      name = name,
      phone = phone,
      licenseNumber = licenseNumber,
      assignedVehicle = assignedVehicle,
      vehicleCategory = vehicleCategory,
      status = status
    )
  }

  fun cycleDriverStatus(driver: Driver) {
    val nextStatus = when (driver.status) {
      "Available" -> "On Trip"
      "On Trip" -> "Off Duty"
      else -> "Available"
    }
    viewModelScope.launch {
      repository.updateDriver(driver.copy(status = nextStatus))
        .onSuccess { _statusBanner.value = "${driver.name} duty status set to $nextStatus." }
    }
  }

  fun deleteDriver(driverId: String) {
    viewModelScope.launch {
      repository.deleteDriver(driverId)
    }
  }

  fun saveVehicleDetails(
    existingVehicle: Vehicle?,
    registrationNumber: String,
    modelName: String,
    manufacturingYear: String = "",
    category: String,
    status: String = "Available",
    maintenanceCost: Long = 0L,
    serviceDate: String = "",
    nextServiceDate: String = "",
    serviceKilometers: Long = 0L,
    presentRunningKilometers: Long = 0L,
    nextServiceKilometers: Long = 0L,
    partsChanged: String = "",
    insuranceDetails: String = "",
    insuranceExpiryDate: String = "",
    fitnessDetails: String = "",
    fitnessExpiryDate: String = "",
    pollutionDetails: String = "",
    pollutionExpiryDate: String = "",
    serviceNotes: String = "",
  ) {
    val resolvedReg = registrationNumber.trim()
    viewModelScope.launch {
      val payload = (existingVehicle ?: Vehicle()).copy(
        registrationNumber = resolvedReg,
        modelName = modelName.trim(),
        manufacturingYear = manufacturingYear.trim(),
        category = category.ifBlank { "Sedan" },
        status = status.ifBlank { "Available" },
        maintenanceCost = maintenanceCost.coerceAtLeast(0L),
        serviceDate = serviceDate.trim(),
        nextServiceDate = nextServiceDate.trim(),
        serviceKilometers = serviceKilometers.coerceAtLeast(0L),
        presentRunningKilometers = presentRunningKilometers.coerceAtLeast(0L),
        nextServiceKilometers = nextServiceKilometers.coerceAtLeast(0L),
        partsChanged = partsChanged.trim(),
        insuranceDetails = insuranceDetails.trim(),
        insuranceExpiryDate = insuranceExpiryDate.trim(),
        fitnessDetails = fitnessDetails.trim(),
        fitnessExpiryDate = fitnessExpiryDate.trim(),
        pollutionDetails = pollutionDetails.trim(),
        pollutionExpiryDate = pollutionExpiryDate.trim(),
        serviceNotes = serviceNotes.trim(),
      )
      val result = if (existingVehicle != null && existingVehicle.id.isNotBlank() && existingVehicle.createdAt != null) {
        repository.updateVehicle(payload)
      } else {
        repository.addVehicle(payload)
      }
      result.onSuccess {
        val actionWord = if (existingVehicle != null) "Updated" else "Saved"
        _statusBanner.value = "$actionWord vehicle ${resolvedReg.ifBlank { modelName }} in fleet registry."
      }
    }
  }

  fun createVehicle(
    registrationNumber: String,
    modelName: String,
    category: String,
    status: String,
    maintenanceCost: Long,
    nextServiceDate: String,
    serviceNotes: String,
  ) {
    saveVehicleDetails(
      existingVehicle = null,
      registrationNumber = registrationNumber,
      modelName = modelName,
      category = category,
      status = status,
      maintenanceCost = maintenanceCost,
      nextServiceDate = nextServiceDate,
      serviceNotes = serviceNotes
    )
  }

  fun cycleVehicleStatus(vehicle: Vehicle) {
    val nextStatus = when (vehicle.status) {
      "Available" -> "On Trip"
      "On Trip" -> "Maintenance"
      else -> "Available"
    }
    viewModelScope.launch {
      repository.updateVehicle(vehicle.copy(status = nextStatus))
        .onSuccess { _statusBanner.value = "Vehicle ${vehicle.registrationNumber} marked as $nextStatus." }
    }
  }

  fun deleteVehicle(vehicleId: String) {
    viewModelScope.launch {
      repository.deleteVehicle(vehicleId)
    }
  }

  fun saveLeadDetails(
    existingLead: Lead?,
    clientName: String,
    clientPhone: String,
    routeSummary: String,
    preferredCategory: String,
    quotedAmount: Long,
    source: String,
    status: String = existingLead?.status ?: "New",
  ) {
    val resolvedClient = clientName.trim()
    viewModelScope.launch {
      val payload = (existingLead ?: Lead()).copy(
        clientName = resolvedClient,
        clientPhone = clientPhone.trim(),
        routeSummary = routeSummary.trim(),
        preferredCategory = preferredCategory.ifBlank { "Sedan" },
        quotedAmount = quotedAmount.coerceAtLeast(0L),
        status = status.ifBlank { "New" },
        source = source.trim().ifBlank { "Website" },
      )
      val result = if (existingLead != null && existingLead.id.isNotBlank() && existingLead.createdAt != null) {
        repository.updateLead(payload)
      } else {
        repository.addLead(payload)
      }
      result.onSuccess {
        val actionWord = if (existingLead != null) "Updated" else "Saved"
        _statusBanner.value = "$actionWord lead for $resolvedClient in pipeline."
      }
    }
  }

  fun createLead(
    clientName: String,
    clientPhone: String,
    routeSummary: String,
    preferredCategory: String,
    quotedAmount: Long,
    source: String,
  ) {
    saveLeadDetails(null, clientName, clientPhone, routeSummary, preferredCategory, quotedAmount, source, "New")
  }

  fun convertLeadToBooking(lead: Lead) {
    viewModelScope.launch {
      if (lead.createdAt != null) {
        repository.updateLead(lead.copy(status = "Converted"))
      }
      val base = lead.quotedAmount.coerceAtLeast(0L)
      val gst = (base * _defaultGstPercent.value) / 100L
      val convertedBooking = Booking(
        customerName = lead.clientName,
        customerPhone = lead.clientPhone,
        pickupLocation = lead.routeSummary.substringBefore(" to "),
        dropLocation = lead.routeSummary.substringAfter(" to ", lead.routeSummary),
        vehicleCategory = lead.preferredCategory.ifBlank { "Sedan" },
        vehicleNumber = "",
        driverName = "",
        tripStatus = "Active",
        paymentStatus = "Pending",
        paymentChannel = "UPI",
        baseAmount = base,
        tollCharges = 0L,
        parkingCharges = 0L,
        gstAmount = gst,
        totalAmount = base + gst,
        expenseAmount = 0L,
        distanceKm = 0L,
        tripDate = todayDateString(),
      )
      repository.addBooking(convertedBooking).onSuccess {
        _statusBanner.value = "Converted lead '${lead.clientName}' into Booking & generated Invoice PDF!"
        _latestInvoiceBooking.value = convertedBooking
      }
    }
  }

  fun deleteLead(leadId: String) {
    viewModelScope.launch {
      repository.deleteLead(leadId)
    }
  }

  fun saveCustomerDetails(
    existingCustomer: Customer?,
    name: String,
    address: String = "",
    phone: String,
    email: String = "",
    companyName: String = "",
    gstin: String = "",
    totalBookings: Long = existingCustomer?.totalBookings ?: 1L,
    totalBilled: Long = existingCustomer?.totalBilled ?: 0L,
    pendingBalance: Long = existingCustomer?.pendingBalance ?: 0L,
  ) {
    val resolvedName = name.trim()
    val resolvedCompany = companyName.trim()
    viewModelScope.launch {
      val payload = (existingCustomer ?: Customer()).copy(
        name = resolvedName,
        address = address.trim(),
        phone = phone.trim(),
        email = email.trim(),
        companyName = resolvedCompany,
        gstin = gstin.trim(),
        totalBookings = totalBookings.coerceAtLeast(0L),
        totalBilled = totalBilled.coerceAtLeast(0L),
        pendingBalance = pendingBalance.coerceAtLeast(0L),
      )
      val result = if (existingCustomer != null && existingCustomer.id.isNotBlank() && existingCustomer.createdAt != null) {
        repository.updateCustomer(payload)
      } else {
        repository.addCustomer(payload)
      }
      result.onSuccess {
        val actionWord = if (existingCustomer != null) "Updated" else "Saved"
        _statusBanner.value = "$actionWord customer '$resolvedName' in Customer Data."
      }
    }
  }

  fun createCustomer(
    name: String,
    phone: String,
    email: String,
    companyName: String,
    gstin: String,
    totalBilled: Long,
    pendingBalance: Long,
  ) {
    saveCustomerDetails(null, name, "", phone, email, companyName, gstin, 1L, totalBilled, pendingBalance)
  }

  fun deleteCustomer(customerId: String) {
    viewModelScope.launch {
      if (customerId.isNotBlank()) {
        repository.deleteCustomer(customerId)
          .onSuccess { _statusBanner.value = "Customer deleted from Customer Data." }
      }
    }
  }

  fun settleCustomerBalance(customer: Customer) {
    viewModelScope.launch {
      if (customer.createdAt != null) {
        repository.updateCustomer(customer.copy(pendingBalance = 0L))
          .onSuccess { _statusBanner.value = "Cleared pending receivables for ${customer.companyName}." }
      }
    }
  }

  fun updateSystemSettings(agency: String, gstin: String, gstSlab: Int) {
    _agencyName.value = agency.ifBlank { "Apex Fleet & Travel Operations" }
    _agencyGstin.value = gstin.ifBlank { "29AABCF9901K1Z8" }
    _defaultGstPercent.value = gstSlab.coerceIn(0, 18)
    _statusBanner.value = "System settings & optional GST rate (${_defaultGstPercent.value}%) saved."
  }

  companion object {
    private const val TAG = "CrmViewModel"
    private const val STOP_TIMEOUT_MILLIS = 5000L
  }
}

fun formatInr(amount: Long): String {
  val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
  return "₹${formatter.format(amount)}"
}
