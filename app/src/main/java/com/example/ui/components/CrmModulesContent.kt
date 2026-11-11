package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Booking
import com.example.data.CrmSection
import com.example.data.Customer
import com.example.data.Driver
import com.example.data.Lead
import com.example.data.Vehicle
import com.example.ui.formatInr
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.KpiAmberBg
import com.example.ui.theme.KpiAmberIcon
import com.example.ui.theme.KpiBlueBg
import com.example.ui.theme.KpiBlueIcon
import com.example.ui.theme.KpiGreenBg
import com.example.ui.theme.KpiGreenIcon
import com.example.ui.theme.KpiOrangeBg
import com.example.ui.theme.KpiOrangeIcon
import com.example.ui.theme.KpiPurpleBg
import com.example.ui.theme.KpiPurpleIcon
import com.example.ui.theme.KpiRedBg
import com.example.ui.theme.KpiRedIcon
import com.example.ui.theme.KpiTealBg
import com.example.ui.theme.KpiTealIcon
import com.example.ui.theme.NewBookingGreen
import com.example.ui.theme.SidebarActiveBlue
import com.example.ui.theme.TextMutedUppercase
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondarySlate

@Composable
fun CrmModuleSectionHeader(
  title: String,
  subtitle: String,
  actionLabel: String? = null,
  onActionClick: (() -> Unit)? = null,
  onBackToDashboard: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 14.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      IconButton(
        onClick = onBackToDashboard,
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(Color.White)
          .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
          .testTag("back_to_dashboard_button")
      ) {
        Icon(
          imageVector = Icons.Default.ArrowBack,
          contentDescription = "Back to Dashboard Overview",
          tint = TextPrimaryDark,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimaryDark,
          fontWeight = FontWeight.ExtraBold
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondarySlate
        )
      }
    }

    if (actionLabel != null && onActionClick != null) {
      Button(
        onClick = onActionClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        modifier = Modifier.testTag("module_primary_action_button")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(actionLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CrmModuleDispatcher(
  section: CrmSection,
  bookings: List<Booking>,
  drivers: List<Driver>,
  vehicles: List<Vehicle>,
  leads: List<Lead>,
  customers: List<Customer>,
  agencyName: String,
  agencyGstin: String,
  defaultGstPercent: Int,
  userEmail: String,
  onBackToDashboard: () -> Unit,
  onOpenNewBooking: () -> Unit,
  onOpenAddLead: () -> Unit,
  onOpenAddDriver: () -> Unit,
  onOpenAddVehicle: () -> Unit,
  onOpenAddCustomer: () -> Unit,
  onConvertLead: (Lead) -> Unit,
  onDeleteLead: (String) -> Unit,
  onUpdateBookingStatus: (Booking, String) -> Unit,
  onToggleBookingPayment: (Booking) -> Unit,
  onDeleteBooking: (String) -> Unit,
  onCycleDriverStatus: (Driver) -> Unit,
  onDeleteDriver: (String) -> Unit,
  onCycleVehicleStatus: (Vehicle) -> Unit,
  onDeleteVehicle: (String) -> Unit,
  onSettleCustomer: (Customer) -> Unit,
  onSaveSettings: (String, String, Int) -> Unit,
  onLoadDemoData: () -> Unit,
  onSignOut: () -> Unit,
  onOpenApkDownloadCenter: () -> Unit = {},
  onEditBooking: (Booking) -> Unit = {},
  onEditLead: (Lead) -> Unit = {},
  onEditDriver: (Driver) -> Unit = {},
  onEditVehicle: (Vehicle) -> Unit = {},
  onEditCustomer: (Customer) -> Unit = {},
  onEditTripExpenses: (Booking) -> Unit = {},
  onDownloadInvoicePdf: (Booking) -> Unit = {},
) {
  when (section) {
    CrmSection.LEADS -> {
      CrmModuleSectionHeader(
        title = "Leads & Inquiries Pipeline",
        subtitle = "Track website, phone, and corporate inquiries and convert to bookings in one tap.",
        actionLabel = "Add Lead",
        onActionClick = onOpenAddLead,
        onBackToDashboard = onBackToDashboard
      )
      if (leads.isEmpty()) {
        EmptyModuleCard(
          message = "No travel leads yet. Tap 'Add Lead' or 'Load Live Demo Data' to populate inquiries.",
          onLoadDemo = onLoadDemoData
        )
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          leads.forEach { lead ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = CardSurface),
              border = BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(lead.clientName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimaryDark)
                    Text("${lead.clientPhone} • Source: ${lead.source}", fontSize = 12.sp, color = TextSecondarySlate)
                  }
                  StatusBadge(
                    text = lead.status,
                    bg = if (lead.status == "Converted") KpiGreenBg else KpiBlueBg,
                    fg = if (lead.status == "Converted") KpiGreenIcon else KpiBlueIcon
                  )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "Route: ${lead.routeSummary} (${lead.preferredCategory})",
                  fontSize = 13.sp,
                  color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Quoted: ${formatInr(lead.quotedAmount)}",
                    fontWeight = FontWeight.ExtraBold,
                    color = SidebarActiveBlue,
                    fontSize = 15.sp
                  )
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                      onClick = { onEditLead(lead) },
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                      Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Edit Details", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    if (lead.status != "Converted") {
                      Button(
                        onClick = { onConvertLead(lead) },
                        colors = ButtonDefaults.buttonColors(containerColor = NewBookingGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                      ) {
                        Text("Convert to Booking", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                      }
                    }
                    IconButton(onClick = { onDeleteLead(lead.id) }) {
                      Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Lead", tint = KpiRedIcon)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    CrmSection.BOOKING_HISTORY -> {
      CrmModuleSectionHeader(
        title = "Booking History & Dispatch Log",
        subtitle = "Manage active, completed, and cancelled fleet trips across all vehicle categories.",
        actionLabel = "New Booking",
        onActionClick = onOpenNewBooking,
        onBackToDashboard = onBackToDashboard
      )
      if (bookings.isEmpty()) {
        EmptyModuleCard(
          message = "No bookings recorded yet. Create a new booking or load live demo data.",
          onLoadDemo = onLoadDemoData
        )
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          bookings.forEach { booking ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = CardSurface),
              border = BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      "${booking.customerName} (${booking.vehicleCategory})",
                      fontWeight = FontWeight.Bold,
                      fontSize = 16.sp,
                      color = TextPrimaryDark
                    )
                    Text(
                      "${booking.pickupLocation} → ${booking.dropLocation}",
                      fontSize = 13.sp,
                      color = TextSecondarySlate
                    )
                  }
                  StatusBadge(
                    text = booking.tripStatus,
                    bg = when (booking.tripStatus) {
                      "Completed" -> KpiGreenBg
                      "Cancelled" -> KpiRedBg
                      else -> KpiTealBg
                    },
                    fg = when (booking.tripStatus) {
                      "Completed" -> KpiGreenIcon
                      "Cancelled" -> KpiRedIcon
                      else -> KpiTealIcon
                    }
                  )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  "Driver: ${booking.driverName} • Vehicle: ${booking.vehicleNumber} • Date: ${booking.tripDate}",
                  fontSize = 12.sp,
                  color = TextSecondarySlate
                )
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Column {
                    Text(
                      "Total: ${formatInr(booking.totalAmount)}",
                      fontWeight = FontWeight.ExtraBold,
                      fontSize = 14.sp,
                      color = TextPrimaryDark
                    )
                    Text(
                      text = buildString {
                        append("Base: ${formatInr(booking.baseAmount)}")
                        if (booking.tollCharges > 0) append(" • Toll: ${formatInr(booking.tollCharges)}")
                        if (booking.parkingCharges > 0) append(" • Parking: ${formatInr(booking.parkingCharges)}")
                        if (booking.gstAmount > 0) append(" • GST: ${formatInr(booking.gstAmount)}") else append(" • No GST")
                      },
                      fontSize = 11.sp,
                      color = TextSecondarySlate
                    )
                  }
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(
                      onClick = { onDownloadInvoicePdf(booking) },
                      colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue),
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Invoice PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                      onClick = { onEditBooking(booking) },
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Edit Details", fontSize = 11.sp)
                    }
                    if (booking.tripStatus != "Completed") {
                      OutlinedButton(
                        onClick = { onUpdateBookingStatus(booking, "Completed") },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                      ) {
                        Text("Complete", fontSize = 11.sp)
                      }
                    }
                    if (booking.tripStatus != "Cancelled") {
                      OutlinedButton(
                        onClick = { onUpdateBookingStatus(booking, "Cancelled") },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                      ) {
                        Text("Cancel", fontSize = 11.sp, color = KpiRedIcon)
                      }
                    }
                    IconButton(
                      onClick = { onDeleteBooking(booking.id) },
                      modifier = Modifier.size(32.dp)
                    ) {
                      Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextSecondarySlate)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    CrmSection.INVOICES_BILLING -> {
      CrmModuleSectionHeader(
        title = "Invoices & Billing Ledger",
        subtitle = "Trip & tax invoices with base fare, toll/parking charges, optional GST, and PDF export.",
        actionLabel = "New Invoice",
        onActionClick = onOpenNewBooking,
        onBackToDashboard = onBackToDashboard
      )
      if (bookings.isEmpty()) {
        EmptyModuleCard("No invoices generated yet.", onLoadDemo = onLoadDemoData)
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          bookings.forEachIndexed { idx, b ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = CardSurface),
              border = BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    "INV-2026-${1001 + idx} • ${b.customerName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimaryDark
                  )
                  Text(
                    text = buildString {
                      append("Base: ${formatInr(b.baseAmount)}")
                      append(" • Toll: ${formatInr(b.tollCharges)}")
                      append(" • Parking: ${formatInr(b.parkingCharges)}")
                      if (b.gstAmount > 0L) append(" • GST: ${formatInr(b.gstAmount)}") else append(" • GST: Optional (₹0)")
                    },
                    fontSize = 12.sp,
                    color = TextSecondarySlate
                  )
                }
                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    formatInr(b.totalAmount),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = TextPrimaryDark
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                      onClick = { onEditBooking(b) },
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(3.dp))
                      Text("Edit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                      onClick = { onDownloadInvoicePdf(b) },
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SidebarActiveBlue)
                    }
                    Surface(
                      color = if (b.paymentStatus == "Paid") KpiGreenBg else KpiAmberBg,
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleBookingPayment(b) }
                    ) {
                      Text(
                        text = if (b.paymentStatus == "Paid") "PAID ↻" else "PENDING ↻",
                        color = if (b.paymentStatus == "Paid") KpiGreenIcon else KpiAmberIcon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    CrmSection.GST_TAX -> {
      val activeBookings = bookings.filter { it.tripStatus != "Cancelled" }
      val taxableValue = activeBookings.sumOf { it.baseAmount }
      val totalGst = activeBookings.sumOf { it.gstAmount }
      val cgst = totalGst / 2L
      val sgst = totalGst - cgst
      val maintenanceItc = (vehicles.sumOf { it.maintenanceCost } * 18L) / 100L
      val netGstPayable = (totalGst - maintenanceItc).coerceAtLeast(0L)

      CrmModuleSectionHeader(
        title = "GST Tax Details & Return Summary",
        subtitle = "SAC 9964 Passenger Transport GST liability, CGST/SGST split, and Input Tax Credit.",
        onBackToDashboard = onBackToDashboard
      )
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("GSTIN: $agencyGstin ($agencyName)", fontWeight = FontWeight.Bold, color = SidebarActiveBlue)
          HorizontalDivider(color = CardBorder)
          TaxRow("Total Taxable Passenger Fare Value", formatInr(taxableValue))
          TaxRow("Output CGST (${defaultGstPercent / 2.0}%)", formatInr(cgst))
          TaxRow("Output SGST (${defaultGstPercent / 2.0}%)", formatInr(sgst))
          TaxRow("Gross Output GST Collected", formatInr(totalGst), isBold = true)
          TaxRow("Less: Input Tax Credit (Fleet Maintenance 18%)", "- ${formatInr(maintenanceItc)}", color = KpiGreenIcon)
          HorizontalDivider(color = CardBorder)
          TaxRow("Net GST Payable in Electronic Cash Ledger", formatInr(netGstPayable), isBold = true, color = KpiTealIcon)
        }
      }
    }

    CrmSection.VEHICLES_MAINTENANCE -> {
      CrmModuleSectionHeader(
        title = "Vehicles Maintenance & Fleet Registry",
        subtitle = "Monitor Sedans, Compact SUVs, SUVs, and Tourist Travellers health and service costs.",
        actionLabel = "Add Vehicle",
        onActionClick = onOpenAddVehicle,
        onBackToDashboard = onBackToDashboard
      )
      if (vehicles.isEmpty()) {
        EmptyModuleCard("No fleet vehicles registered yet.", onLoadDemo = onLoadDemoData)
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          vehicles.forEach { v ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = CardSurface),
              border = BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text("${v.modelName} (${v.registrationNumber})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Category: ${v.category} • Next Service: ${v.nextServiceDate}", fontSize = 12.sp, color = TextSecondarySlate)
                  }
                  Surface(
                    color = when (v.status) {
                      "Available" -> KpiGreenBg
                      "Maintenance" -> KpiRedBg
                      else -> KpiBlueBg
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { onCycleVehicleStatus(v) }
                  ) {
                    Text(
                      text = "${v.status} ↻",
                      color = when (v.status) {
                        "Available" -> KpiGreenIcon
                        "Maintenance" -> KpiRedIcon
                        else -> KpiBlueIcon
                      },
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("Notes: ${v.serviceNotes}", fontSize = 13.sp, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    "Maintenance Spend: ${formatInr(v.maintenanceCost)}",
                    fontWeight = FontWeight.Bold,
                    color = KpiOrangeIcon
                  )
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                      onClick = { onEditVehicle(v) },
                      contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Edit Details", fontSize = 11.sp)
                    }
                    IconButton(onClick = { onDeleteVehicle(v.id) }, modifier = Modifier.size(30.dp)) {
                      Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Vehicle", tint = TextSecondarySlate)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    CrmSection.DRIVER_ROSTER -> {
      CrmModuleSectionHeader(
        title = "Driver Roster (${drivers.size} Active)",
        subtitle = "Commercial badge verification, vehicle assignment, duty status, and trip counts.",
        actionLabel = "Add Driver",
        onActionClick = onOpenAddDriver,
        onBackToDashboard = onBackToDashboard
      )
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        drivers.forEach { d ->
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(d.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimaryDark)
                Text("${d.phone} • License: ${d.licenseNumber}", fontSize = 12.sp, color = TextSecondarySlate)
                Text("Vehicle: ${d.assignedVehicle} (${d.vehicleCategory})", fontSize = 12.sp, color = SidebarActiveBlue)
                Text(
                  "Completed Trips: ${d.tripsCompleted} • Earnings: ${formatInr(d.earningsTotal)}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextPrimaryDark
                )
              }
              Column(horizontalAlignment = Alignment.End) {
                Surface(
                  color = when (d.status) {
                    "Available" -> KpiGreenBg
                    "On Trip" -> KpiBlueBg
                    else -> KpiAmberBg
                  },
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.clickable { onCycleDriverStatus(d) }
                ) {
                  Text(
                    text = "${d.status} ↻",
                    color = when (d.status) {
                      "Available" -> KpiGreenIcon
                      "On Trip" -> KpiBlueIcon
                      else -> KpiAmberIcon
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                  )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                  IconButton(onClick = { onEditDriver(d) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Driver", tint = SidebarActiveBlue)
                  }
                  IconButton(onClick = { onDeleteDriver(d.id) }) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Driver", tint = TextSecondarySlate)
                  }
                }
              }
            }
          }
        }
      }
    }

    CrmSection.TRIP_EXPENSES -> {
      CrmModuleSectionHeader(
        title = "Trip Details and Trip Expenses",
        subtitle = "Fuel, highway tolls, state permits, driver bata, and net operating profit per trip.",
        actionLabel = "Log New Trip",
        onActionClick = onOpenNewBooking,
        onBackToDashboard = onBackToDashboard
      )
      if (bookings.isEmpty()) {
        EmptyModuleCard("No trips logged yet.", onLoadDemo = onLoadDemoData)
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          bookings.forEach { b ->
            val netMargin = b.totalAmount - b.expenseAmount - b.gstAmount
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = CardSurface),
              border = BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text("${b.pickupLocation} → ${b.dropLocation}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                      "Driver: ${b.driverName} • ${b.vehicleNumber} • Distance: ${b.distanceKm} km",
                      fontSize = 12.sp,
                      color = TextSecondarySlate
                    )
                  }
                  OutlinedButton(
                    onClick = { onEditTripExpenses(b) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                  ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Expenses", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Billed: ${formatInr(b.totalAmount)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                  Text("Trip Expenses: ${formatInr(b.expenseAmount)}", fontSize = 13.sp, color = KpiOrangeIcon)
                  Text(
                    "Net Profit: ${formatInr(netMargin)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = KpiGreenIcon
                  )
                }
              }
            }
          }
        }
      }
    }

    CrmSection.EARNINGS_HISTORY -> {
      val valid = bookings.filter { it.tripStatus != "Cancelled" }
      val byCategory = listOf("Sedans", "Compact SUVs", "SUVs", "Tourist Travellers").map { cat ->
        cat to valid.filter { it.vehicleCategory == cat }.sumOf { it.totalAmount }
      }
      CrmModuleSectionHeader(
        title = "Earnings History & Fleet Profitability",
        subtitle = "Breakdown of gross revenue across fleet categories and payment channels.",
        onBackToDashboard = onBackToDashboard
      )
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Revenue by Vehicle Category", fontWeight = FontWeight.Bold, fontSize = 16.sp)
          byCategory.forEach { (cat, sum) ->
            TaxRow(cat, formatInr(sum), isBold = true)
          }
          HorizontalDivider(color = CardBorder)
          TaxRow(
            "Total Realized Earnings (Paid)",
            formatInr(valid.filter { it.paymentStatus == "Paid" }.sumOf { it.totalAmount }),
            isBold = true,
            color = KpiGreenIcon
          )
          TaxRow(
            "Total Trip Operating Expenses",
            formatInr(valid.sumOf { it.expenseAmount }),
            color = KpiOrangeIcon
          )
        }
      }
    }

    CrmSection.CUSTOMERS_ACCOUNTS -> {
      CrmModuleSectionHeader(
        title = "Customers & Corporate Accounts",
        subtitle = "Manage B2B corporate travel accounts, GSTIN records, and credit balances.",
        actionLabel = "Add Customer",
        onActionClick = onOpenAddCustomer,
        onBackToDashboard = onBackToDashboard
      )
      if (customers.isEmpty()) {
        EmptyModuleCard("No customer accounts added yet.", onLoadDemo = onLoadDemoData)
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          customers.forEach { c ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = CardSurface),
              border = BorderStroke(1.dp, CardBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(c.companyName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                  Text("Contact: ${c.name} (${c.phone})", fontSize = 12.sp, color = TextSecondarySlate)
                  Text("GSTIN: ${c.gstin} • Bookings: ${c.totalBookings}", fontSize = 12.sp, color = SidebarActiveBlue)
                }
                Column(horizontalAlignment = Alignment.End) {
                  Text("Billed: ${formatInr(c.totalBilled)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick = { onEditCustomer(c) }) {
                      Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(3.dp))
                      Text("Edit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    if (c.pendingBalance > 0) {
                      TextButton(onClick = { onSettleCustomer(c) }) {
                        Text("Settle ${formatInr(c.pendingBalance)}", color = KpiAmberIcon, fontWeight = FontWeight.Bold)
                      }
                    } else {
                      Text("No Dues ✓", color = KpiGreenIcon, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    CrmSection.USER_ROLES -> {
      CrmModuleSectionHeader(
        title = "User Accounts & Access Roles",
        subtitle = "Role-based access control for command center operators and dispatch staff.",
        onBackToDashboard = onBackToDashboard
      )
      val roles = listOf(
        Triple(userEmail.ifBlank { "Signed-In Admin" }, "Super Admin (Command Center)", "Full Access: Revenue, GST, Fleet & Settings"),
        Triple("dispatch@fleetcrm.in", "Fleet Dispatcher", "Bookings, Driver Roster & Trip Assignment"),
        Triple("accounts@fleetcrm.in", "GST & Billing Executive", "Invoices, Receivables & Tax Filing"),
        Triple("maintenance@fleetcrm.in", "Fleet Workshop Manager", "Vehicles Maintenance & Service Logs"),
      )
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        roles.forEach { (email, role, perms) ->
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(KpiPurpleBg),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = KpiPurpleIcon)
              }
              Column(modifier = Modifier.weight(1f)) {
                Text(role, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(email, fontSize = 12.sp, color = SidebarActiveBlue)
                Text(perms, fontSize = 12.sp, color = TextSecondarySlate)
              }
              StatusBadge("Active", KpiGreenBg, KpiGreenIcon)
            }
          }
        }
      }
    }

    CrmSection.REPORTS_EXPORT -> {
      val context = LocalContext.current
      val directApkUrl = "https://github.com/kittu1004/Travel-crm-/releases/latest/download/FleetCRM-debug.apk"
      val releasesUrl = "https://github.com/kittu1004/Travel-crm-/releases"
      CrmModuleSectionHeader(
        title = "Reports, CSV Export & GitHub APK Release",
        subtitle = "Generate audit-ready CSV reports or download the latest Debug APK from kittu1004/Travel-crm-.",
        onBackToDashboard = onBackToDashboard
      )
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CardSurface),
          border = BorderStroke(1.dp, CardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Executive Fleet & Revenue Statement", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
              "Total Bookings: ${bookings.size} | Drivers: ${drivers.size} | Fleet Vehicles: ${vehicles.size} | Corporate Clients: ${customers.size}",
              fontSize = 13.sp,
              color = TextSecondarySlate
            )
            Button(
              onClick = {
                val csvBuilder = StringBuilder()
                csvBuilder.append("Customer,Category,Vehicle,Driver,Status,Payment,BaseINR,GSTINR,TotalINR\n")
                bookings.forEach { b ->
                  csvBuilder.append(
                    "${b.customerName},${b.vehicleCategory},${b.vehicleNumber},${b.driverName},${b.tripStatus},${b.paymentStatus},${b.baseAmount},${b.gstAmount},${b.totalAmount}\n"
                  )
                }
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                  type = "text/plain"
                  putExtra(Intent.EXTRA_SUBJECT, "FleetCRM Revenue & GST Export")
                  putExtra(Intent.EXTRA_TEXT, csvBuilder.toString())
                }
                context.startActivity(Intent.createChooser(sendIntent, "Export FleetCRM CSV Report"))
              },
              colors = ButtonDefaults.buttonColors(containerColor = NewBookingGreen),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Export & Share CSV Ledger", fontWeight = FontWeight.Bold)
            }
          }
        }

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CardSurface),
          border = BorderStroke(1.dp, CardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
              "GitHub Actions Direct APK Download (kittu1004/Travel-crm-)",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = TextPrimaryDark
            )
            Text(
              "Direct Release Asset URL: $directApkUrl",
              fontSize = 12.sp,
              color = SidebarActiveBlue
            )
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = onOpenApkDownloadCenter,
                colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("download_github_apk_button")
              ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save / Download FleetCRM-debug.apk", fontWeight = FontWeight.Bold)
              }
              OutlinedButton(
                onClick = {
                  val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(releasesUrl))
                  context.startActivity(intent)
                },
                shape = RoundedCornerShape(10.dp)
              ) {
                Text("Open GitHub Releases", fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }
    }

    CrmSection.SYSTEM_SETTINGS -> {
      var nameInput by remember(agencyName) { mutableStateOf(agencyName) }
      var gstinInput by remember(agencyGstin) { mutableStateOf(agencyGstin) }
      var gstSlabInput by remember(defaultGstPercent) { mutableStateOf(defaultGstPercent.toString()) }

      CrmModuleSectionHeader(
        title = "System Settings & Tariff Control",
        subtitle = "Configure agency identity, GST slab percentage, demo data, and session security.",
        onBackToDashboard = onBackToDashboard
      )
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Travel Agency / Company Name") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = gstinInput,
            onValueChange = { gstinInput = it },
            label = { Text("Company GSTIN") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = gstSlabInput,
            onValueChange = { gstSlabInput = it.filter { ch -> ch.isDigit() } },
            label = { Text("Default Passenger Transport GST % (5, 12, or 18)") },
            modifier = Modifier.fillMaxWidth()
          )
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
              onClick = {
                onSaveSettings(nameInput, gstinInput, gstSlabInput.toIntOrNull() ?: 5)
              },
              colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Save Configuration", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
              onClick = onSignOut,
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, KpiRedIcon),
              modifier = Modifier.testTag("sign_out_button")
            ) {
              Icon(Icons.Default.Logout, contentDescription = null, tint = KpiRedIcon, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Sign Out", color = KpiRedIcon, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    CrmSection.DASHBOARD -> Unit
  }
}

@Composable
private fun EmptyModuleCard(
  message: String,
  onLoadDemo: () -> Unit,
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = CardSurface),
    border = BorderStroke(1.dp, CardBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = Icons.Default.DirectionsCar,
        contentDescription = null,
        tint = SidebarActiveBlue,
        modifier = Modifier.size(36.dp)
      )
      Spacer(modifier = Modifier.height(10.dp))
      Text(message, style = MaterialTheme.typography.bodyMedium, color = TextSecondarySlate)
      Spacer(modifier = Modifier.height(14.dp))
      Button(
        onClick = onLoadDemo,
        colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue),
        shape = RoundedCornerShape(10.dp)
      ) {
        Text("Load Live Demo Data", fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun StatusBadge(
  text: String,
  bg: Color,
  fg: Color,
) {
  Surface(color = bg, shape = RoundedCornerShape(8.dp)) {
    Text(
      text = text,
      color = fg,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
    )
  }
}

@Composable
private fun TaxRow(
  label: String,
  value: String,
  isBold: Boolean = false,
  color: Color = TextPrimaryDark,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
      fontSize = 14.sp,
      color = TextPrimaryDark
    )
    Text(
      text = value,
      fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold,
      fontSize = 15.sp,
      color = color
    )
  }
}
