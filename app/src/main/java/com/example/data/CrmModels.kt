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
  INVOICES_BILLING("Invoices & Billing"),
  GST_TAX("GST Tax Details"),
  VEHICLES_MAINTENANCE("Vehicles Maintenance"),
  DRIVER_ROSTER("Driver Roster"),
  TRIP_EXPENSES("Trip Details and Trip Expenses"),
  EARNINGS_HISTORY("Earnings History"),
  CUSTOMERS_ACCOUNTS("Customers & Accounts"),
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

@IgnoreExtraProperties
data class Booking(
  @DocumentId val id: String = "",
  val userId: String = "",
  val customerName: String = "",
  val customerPhone: String = "",
  val pickupLocation: String = "",
  val dropLocation: String = "",
  val vehicleCategory: String = "Sedans",
  val vehicleNumber: String = "",
  val driverName: String = "",
  val tripStatus: String = "Active",
  val paymentStatus: String = "Pending",
  val paymentChannel: String = "UPI",
  val baseAmount: Long = 0L,
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
  val category: String = "Sedans",
  val status: String = "Available",
  val maintenanceCost: Long = 0L,
  val nextServiceDate: String = "",
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
