package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

sealed interface UiState<out T> {
  data object Loading : UiState<Nothing>
  data class Success<T>(val data: T) : UiState<T>
  data class Error(val message: String) : UiState<Nothing>
}

enum class CrmSection(val title: String, val isSubheader: Boolean = false) {
  DASHBOARD("Dashboard Overview"),
  LEADS("Leads"),
  BOOKING_HISTORY("Booking History"),
  INVOICES_BILLING("Invoices History"),
  GST_TAX("GST Tax Details"),
  VEHICLES_MAINTENANCE("Vehicles Maintenance"),
  DRIVER_ROSTER("Driver Roster"),
  TRIP_EXPENSES("Trip Details and Trip Expenses"),
  EARNINGS_HISTORY("Earnings History"),
  CUSTOMERS_ACCOUNTS("Customer Data"),
  USER_ROLES("User Accounts & Roles"),
  REPORTS_EXPORT("Reports & Export"),
  SYSTEM_SETTINGS("System Settings"),
}

enum class InflowCategory(val label: String) {
  ALL_INFLOWS("All Inflows"),
  GST_TAX_TAB("GST Tax Tab"),
  REALIZED_PAID("Realized (Paid)"),
  PENDING_RECEIVABLES("Pending Receivables"),
  SEDANS("Sedans"),
  COMPACT_SUVS("Compact SUVs"),
  SUVS("SUVs"),
  TOURIST_TRAVELLERS("Tourist Travellers"),
}

/**
 * Normalizes any YYYY-MM-DD date string to DD-MM-YYYY (date-month-year) format.
 */
fun formatToDdMmYyyy(rawDate: String): String {
  val trimmed = rawDate.trim()
  val parts = trimmed.split("-")
  if (parts.size == 3 && parts[0].length == 4 && parts[2].length <= 2) {
    return "${parts[2].padStart(2, '0')}-${parts[1].padStart(2, '0')}-${parts[0]}"
  }
  return trimmed
}

@IgnoreExtraProperties
data class Booking(
  @DocumentId val id: String = "",
  val userId: String = "",
  val customerName: String = "",
  val companyName: String = "",
  val customerPhone: String = "",
  val pickupLocation: String = "",
  val dropLocation: String = "",
  val vehicleCategory: String = "Sedans",
  val vehicleNumber: String = "",
  val driverName: String = "",
  val driverPhone: String = "",
  val tripStatus: String = "Active",
  val paymentStatus: String = "Pending",
  val paymentChannel: String = "UPI",
  val baseAmount: Long = 0L,
  val tollCharges: Long = 0L,
  val parkingCharges: Long = 0L,
  val gstAmount: Long = 0L,
  val totalAmount: Long = 0L,
  val expenseAmount: Long = 0L,
  val distanceKm: Long = 0L,
  val tripDate: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class Driver(
  @DocumentId val id: String = "",
  val userId: String = "",
  val name: String = "",
  val phone: String = "",
  val licenseNumber: String = "",
  val assignedVehicle: String = "",
  val vehicleCategory: String = "Sedans",
  val status: String = "Available",
  val tripsCompleted: Long = 0L,
  val earningsTotal: Long = 0L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class Vehicle(
  @DocumentId val id: String = "",
  val userId: String = "",
  val registrationNumber: String = "",
  val modelName: String = "",
  val manufacturingYear: String = "",
  val category: String = "Sedans",
  val status: String = "Available",
  val maintenanceCost: Long = 0L,
  val serviceDate: String = "",
  val nextServiceDate: String = "",
  val serviceKilometers: Long = 0L,
  val presentRunningKilometers: Long = 0L,
  val nextServiceKilometers: Long = 0L,
  val insuranceDetails: String = "",
  val fitnessDetails: String = "",
  val pollutionDetails: String = "",
  val serviceNotes: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class Lead(
  @DocumentId val id: String = "",
  val userId: String = "",
  val clientName: String = "",
  val clientPhone: String = "",
  val routeSummary: String = "",
  val preferredCategory: String = "Sedans",
  val quotedAmount: Long = 0L,
  val status: String = "New",
  val source: String = "Website",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)

@IgnoreExtraProperties
data class Customer(
  @DocumentId val id: String = "",
  val userId: String = "",
  val name: String = "",
  val address: String = "",
  val phone: String = "",
  val email: String = "",
  val companyName: String = "",
  val gstin: String = "",
  val totalBookings: Long = 0L,
  val totalBilled: Long = 0L,
  val pendingBalance: Long = 0L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null,
)
