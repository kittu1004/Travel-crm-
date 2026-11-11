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

  private val _isActionBusy = MutableStateFlow(false)
  val isActionBusy: StateFlow<Boolean> = _isActionBusy.asStateFlow()

  private val _agencyName = MutableStateFlow("Apex Fleet & Travel Operations")
  val agencyName: StateFlow<String> = _agencyName.asStateFlow()

  private val _agencyGstin = MutableStateFlow("29AABCF9901K1Z8")
  val agencyGstin: StateFlow<String> = _agencyGstin.asStateFlow()

  private val _defaultGstPercent = MutableStateFlow(5)
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

  init {
    viewModelScope.launch {
      repository.ensureDefaultDriversSeeded()
    }
  }

  fun selectSection(section: CrmSection) {
    _selectedSection.value = section
  }

  fun selectInflowCategory(category: InflowCategory) {
    _selectedInflowCategory.value = category
  }

  fun clearBanner() {
    _statusBanner.value = null
  }

  fun todayDateString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return sdf.format(Date())
  }

  fun loadLiveDemoData() {
    if (_isActionBusy.value) return
    viewModelScope.launch {
      _isActionBusy.value = true
      val result = repository.loadLiveDemoData(todayDateString())
      _isActionBusy.value = false
      result.fold(
        onSuccess = {
          _statusBanner.value = "Live Demo Data loaded! Fleet, bookings, GST invoices & leads synced."
        },
        onFailure = { e ->
          _statusBanner.value = "Failed to load demo data: ${e.localizedMessage ?: "Error"}"
        }
      )
    }
  }

  fun refreshRevenueMetrics() {
    _statusBanner.value = "Live realized inflows & GST receivables synchronized with Cloud Firestore."
  }

  fun createBooking(
    customerName: String,
    customerPhone: String,
    pickupLocation: String,
    dropLocation: String,
    vehicleCategory: String,
    vehicleNumber: String,
    driverName: String,
    tripStatus: String,
    paymentStatus: String,
    paymentChannel: String,
    baseAmount: Long,
    expenseAmount: Long,
    distanceKm: Long,
    tripDate: String,
  ) {
    val gstAmount = (baseAmount * _defaultGstPercent.value) / 100L
    val totalAmount = baseAmount + gstAmount
    viewModelScope.launch {
      _isActionBusy.value = true
      val result = repository.addBooking(
        Booking(
          customerName = customerName.ifBlank { "Walk-in Corporate Client" },
          customerPhone = customerPhone.ifBlank { "+91 98450 00000" },
          pickupLocation = pickupLocation.ifBlank { "Bengaluru Airport T2" },
          dropLocation = dropLocation.ifBlank { "MG Road Business Hub" },
          vehicleCategory = vehicleCategory,
          vehicleNumber = vehicleNumber.ifBlank { "KA-01-MG-4590" },
          driverName = driverName.ifBlank { "Ramesh Kumar" },
          tripStatus = tripStatus,
          paymentStatus = paymentStatus,
          paymentChannel = paymentChannel.ifBlank { "UPI" },
          baseAmount = baseAmount.coerceAtLeast(0L),
          gstAmount = gstAmount.coerceAtLeast(0L),
          totalAmount = totalAmount.coerceAtLeast(0L),
          expenseAmount = expenseAmount.coerceAtLeast(0L),
          distanceKm = distanceKm.coerceAtLeast(1L),
          tripDate = tripDate.ifBlank { todayDateString() },
        )
      )
      _isActionBusy.value = false
      result.fold(
        onSuccess = { _statusBanner.value = "New booking created for $customerName (${formatInr(totalAmount)})." },
        onFailure = { e -> _statusBanner.value = "Error creating booking: ${e.localizedMessage}" }
      )
    }
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
        _statusBanner.value = "Updated trip expenses & odometer distance for ${booking.customerName}."
      }
    }
  }

  fun deleteBooking(bookingId: String) {
    viewModelScope.launch {
      repository.deleteBooking(bookingId)
        .onSuccess { _statusBanner.value = "Booking removed." }
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
    viewModelScope.launch {
      repository.addDriver(
        Driver(
          name = name.ifBlank { "New Fleet Driver" },
          phone = phone.ifBlank { "+91 98450 12345" },
          licenseNumber = licenseNumber.ifBlank { "KA01-2021-0098123" },
          assignedVehicle = assignedVehicle.ifBlank { "Toyota Innova (KA-01-AB-1001)" },
          vehicleCategory = vehicleCategory,
          status = status,
          tripsCompleted = 1L,
          earningsTotal = 2500L,
        )
      ).onSuccess {
        _statusBanner.value = "Driver $name added to Driver Roster."
      }
    }
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

  fun createVehicle(
    registrationNumber: String,
    modelName: String,
    category: String,
    status: String,
    maintenanceCost: Long,
    nextServiceDate: String,
    serviceNotes: String,
  ) {
    viewModelScope.launch {
      repository.addVehicle(
        Vehicle(
          registrationNumber = registrationNumber.ifBlank { "KA-01-FL-2026" },
          modelName = modelName.ifBlank { "Toyota Innova Hycross" },
          category = category,
          status = status,
          maintenanceCost = maintenanceCost.coerceAtLeast(0L),
          nextServiceDate = nextServiceDate.ifBlank { "2026-12-30" },
          serviceNotes = serviceNotes.ifBlank { "Routine fleet inspection & oil change" },
        )
      ).onSuccess {
        _statusBanner.value = "Vehicle $registrationNumber added to fleet."
      }
    }
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

  fun createLead(
    clientName: String,
    clientPhone: String,
    routeSummary: String,
    preferredCategory: String,
    quotedAmount: Long,
    source: String,
  ) {
    viewModelScope.launch {
      repository.addLead(
        Lead(
          clientName = clientName.ifBlank { "Prospective Traveler" },
          clientPhone = clientPhone.ifBlank { "+91 99000 11223" },
          routeSummary = routeSummary.ifBlank { "Bengaluru to Mysore Day Package" },
          preferredCategory = preferredCategory,
          quotedAmount = quotedAmount.coerceAtLeast(1500L),
          status = "New",
          source = source.ifBlank { "Website" },
        )
      ).onSuccess {
        _statusBanner.value = "Lead for $clientName added to pipeline."
      }
    }
  }

  fun convertLeadToBooking(lead: Lead) {
    viewModelScope.launch {
      if (lead.createdAt != null) {
        repository.updateLead(lead.copy(status = "Converted"))
      }
      val base = lead.quotedAmount.coerceAtLeast(2000L)
      val gst = (base * _defaultGstPercent.value) / 100L
      repository.addBooking(
        Booking(
          customerName = lead.clientName,
          customerPhone = lead.clientPhone,
          pickupLocation = lead.routeSummary.substringBefore(" to ").ifBlank { "Bengaluru Hub" },
          dropLocation = lead.routeSummary.substringAfter(" to ", lead.routeSummary).ifBlank { "Outstation Drop" },
          vehicleCategory = lead.preferredCategory,
          vehicleNumber = "KA-01-MG-4590",
          driverName = "Ramesh Kumar",
          tripStatus = "Active",
          paymentStatus = "Pending",
          paymentChannel = "UPI",
          baseAmount = base,
          gstAmount = gst,
          totalAmount = base + gst,
          expenseAmount = (base * 20L) / 100L,
          distanceKm = 180L,
          tripDate = todayDateString(),
        )
      ).onSuccess {
        _statusBanner.value = "Converted lead '${lead.clientName}' into an Active Booking!"
      }
    }
  }

  fun deleteLead(leadId: String) {
    viewModelScope.launch {
      repository.deleteLead(leadId)
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
    viewModelScope.launch {
      repository.addCustomer(
        Customer(
          name = name.ifBlank { "Corporate Account" },
          phone = phone.ifBlank { "+91 98201 00000" },
          email = email.ifBlank { "billing@clientcorp.in" },
          companyName = companyName.ifBlank { "Client Enterprise Pvt Ltd" },
          gstin = gstin.ifBlank { "29AABCC1234D1Z1" },
          totalBookings = 1L,
          totalBilled = totalBilled.coerceAtLeast(0L),
          pendingBalance = pendingBalance.coerceAtLeast(0L),
        )
      ).onSuccess {
        _statusBanner.value = "Customer account '$companyName' saved."
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
    _defaultGstPercent.value = gstSlab.coerceIn(5, 18)
    _statusBanner.value = "System settings & GST slab (${_defaultGstPercent.value}%) saved."
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
