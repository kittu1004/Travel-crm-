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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.formatToDdMmYyyy
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
      .padding(bottom = 18.dp),
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
          .size(38.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(Color.White)
          .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
          .testTag("back_to_dashboard_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
          color = Color.White,
          fontWeight = FontWeight.ExtraBold
        )
      }
    }

    if (actionLabel != null && onActionClick != null) {
      Spacer(modifier = Modifier.width(8.dp))
      Button(
        onClick = onActionClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = SidebarActiveBlue,
          contentColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 9.dp),
        modifier = Modifier.testTag("module_primary_action_button")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(5.dp))
        Text(actionLabel, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
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
  onOpenNewInvoice: () -> Unit = onOpenNewBooking,
  onDownloadGstDetails: () -> Unit = {},
  onOpenAddLead: () -> Unit,
  onOpenAddDriver: () -> Unit,
  onOpenAddVehicle: () -> Unit,
  onOpenAddVehicleMaintenance: () -> Unit = onOpenAddVehicle,
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
  onLoadDemoData: () -> Unit = {},
  onSignOut: () -> Unit,
  onOpenApkDownloadCenter: () -> Unit = {},
  onEditBooking: (Booking) -> Unit = {},
  onEditInvoice: (Booking) -> Unit = onEditBooking,
  onEditLead: (Lead) -> Unit = {},
  onEditDriver: (Driver) -> Unit = {},
  onEditVehicle: (Vehicle) -> Unit = {},
  onEditVehicleMaintenance: (Vehicle) -> Unit = onEditVehicle,
  onEditCustomer: (Customer) -> Unit = {},
  onDeleteCustomer: (Customer) -> Unit = {},
  onEditTripExpenses: (Booking) -> Unit = {},
  onDownloadInvoicePdf: (Booking) -> Unit = {},
) {
  when (section) {
    CrmSection.LEADS -> {
      CrmModuleSectionHeader(
        title = "Leads & Inquiries Pipeline",
        subtitle = "Track saved inquiries and convert to bookings in one tap.",
        actionLabel = "Add Lead",
        onActionClick = onOpenAddLead,
        onBackToDashboard = onBackToDashboard
      )
      if (leads.isEmpty()) {
        EmptyModuleCard(
          message = "No saved travel leads yet. Tap 'Add Lead' to create an inquiry."
        )
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          leads.forEach { lead ->
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = CardSurface),
              border = BorderStroke(1.dp, CardBorder),
              elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SidebarActiveBlue),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                      Text(lead.clientName, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = TextPrimaryDark)
                      Text("${lead.clientPhone} • Source: ${lead.source}", fontSize = 12.sp, color = TextSecondarySlate)
                    }
                  }
                  StatusBadge(
                    text = lead.status,
                    bg = if (lead.status == "Converted") KpiGreenBg else KpiBlueBg,
                    fg = if (lead.status == "Converted") KpiGreenIcon else KpiBlueIcon
                  )
                }
                Surface(
                  color = Color(0xFFECF3FF),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "Route: ${lead.routeSummary} (${lead.preferredCategory})",
                      fontSize = 12.5.sp,
                      color = TextPrimaryDark,
                      modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = formatInr(lead.quotedAmount),
                      fontWeight = FontWeight.ExtraBold,
                      color = SidebarActiveBlue,
                      fontSize = 15.sp
                    )
                  }
                }
                FlowRow(
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  OutlinedButton(
                    onClick = { onEditLead(lead) },
                    border = BorderStroke(1.dp, Color(0xFFBFD6F6)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                  ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Edit Details", fontSize = 12.sp, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                  }
                  if (lead.status != "Converted") {
                    Button(
                      onClick = { onConvertLead(lead) },
                      colors = ButtonDefaults.buttonColors(containerColor = NewBookingGreen),
                      shape = RoundedCornerShape(10.dp),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                      Text("Convert to Booking", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                  IconButton(onClick = { onDeleteLead(lead.id) }, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Lead", tint = KpiRedIcon)
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
        title = "Booking History",
        subtitle = "Manage saved fleet trips, customer & company records, vehicle model, driver details, and invoices.",
        actionLabel = "New Booking",
        onActionClick = onOpenNewBooking,
        onBackToDashboard = onBackToDashboard
      )
      if (bookings.isEmpty()) {
        EmptyModuleCard(
          message = "No saved bookings yet. Tap 'New Booking' to create a booking."
        )
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          bookings.forEach { booking ->
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = CardSurface),
              border = BorderStroke(1.dp, CardBorder),
              elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SidebarActiveBlue),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                      val companySuffix = if (booking.companyName.isNotBlank()) " • ${booking.companyName}" else ""
                      val modelSuffix = if (booking.vehicleModel.isNotBlank()) " • ${booking.vehicleModel}" else ""
                      Text(
                        "${booking.customerName}$companySuffix (${booking.vehicleCategory}$modelSuffix)",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = TextPrimaryDark
                      )
                      Text(
                        "${booking.pickupLocation} → ${booking.dropLocation}",
                        fontSize = 13.sp,
                        color = TextSecondarySlate
                      )
                    }
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  StatusBadge(
                    text = booking.tripStatus,
                    bg = when (booking.tripStatus) {
                      "Completed" -> KpiGreenBg
                      "Cancelled" -> KpiRedBg
                      else -> Color(0xFFDCFCE7)
                    },
                    fg = when (booking.tripStatus) {
                      "Completed" -> KpiGreenIcon
                      "Cancelled" -> KpiRedIcon
                      else -> Color(0xFF15803D)
                    }
                  )
                }

                val driverPhoneInfo = if (booking.driverPhone.isNotBlank()) " (${booking.driverPhone})" else ""
                val modelInfo = if (booking.vehicleModel.isNotBlank()) "${booking.vehicleModel} - " else ""
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(15.dp))
                    Text("Driver: ${booking.driverName}$driverPhoneInfo", fontSize = 12.5.sp, color = TextPrimaryDark)
                  }
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(15.dp))
                    Text("Vehicle: $modelInfo${booking.vehicleNumber}", fontSize = 12.5.sp, color = TextPrimaryDark)
                  }
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(15.dp))
                    Text("Date: ${formatToDdMmYyyy(booking.tripDate)}", fontSize = 12.5.sp, color = TextPrimaryDark)
                  }
                }

                // Soft icy-blue Total highlight container matching Screenshot 1
                Surface(
                  color = Color(0xFFECF3FF),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Payments,
                      contentDescription = null,
                      tint = SidebarActiveBlue,
                      modifier = Modifier.size(22.dp)
                    )
                    Column {
                      Text(
                        "Total: ${formatInr(booking.totalAmount)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = TextPrimaryDark
                      )
                      Text(
                        text = buildString {
                          append("Base: ${formatInr(booking.baseAmount)}")
                          if (booking.tollCharges > 0) append(" • Toll: ${formatInr(booking.tollCharges)}")
                          if (booking.parkingCharges > 0) append(" • Parking: ${formatInr(booking.parkingCharges)}")
                          if (booking.gstAmount > 0) append(" • GST: ${formatInr(booking.gstAmount)}") else append(" • No GST")
                        },
                        fontSize = 11.5.sp,
                        color = TextSecondarySlate
                      )
                    }
                  }
                }

                FlowRow(
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Button(
                    onClick = { onDownloadInvoicePdf(booking) },
                    colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Invoice PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                  OutlinedButton(
                    onClick = { onEditBooking(booking) },
                    border = BorderStroke(1.dp, Color(0xFFBFD6F6)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Edit Details", fontSize = 12.sp, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                  }
                  if (booking.tripStatus != "Completed") {
                    OutlinedButton(
                      onClick = { onUpdateBookingStatus(booking, "Completed") },
                      border = BorderStroke(1.dp, Color(0xFFBFD6F6)),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                      shape = RoundedCornerShape(10.dp)
                    ) {
                      Icon(Icons.Default.Check, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Complete", fontSize = 12.sp, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                    }
                  }
                  if (booking.tripStatus != "Cancelled") {
                    OutlinedButton(
                      onClick = { onUpdateBookingStatus(booking, "Cancelled") },
                      border = BorderStroke(1.dp, KpiRedIcon.copy(alpha = 0.35f)),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                      shape = RoundedCornerShape(10.dp)
                    ) {
                      Text("Cancel", fontSize = 12.sp, color = KpiRedIcon, fontWeight = FontWeight.Bold)
                    }
                  }
                  IconButton(
                    onClick = { onDeleteBooking(booking.id) },
                    modifier = Modifier.size(34.dp)
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

    CrmSection.INVOICES_BILLING -> {
      CrmModuleSectionHeader(
        title = "Invoices History",
        subtitle = "Generate new invoices, view saved trip & tax invoices, and download PDF invoices.",
        actionLabel = "Generate New Invoice",
        onActionClick = onOpenNewInvoice,
        onBackToDashboard = onBackToDashboard
      )
      if (bookings.isEmpty()) {
        EmptyModuleCard("No saved invoices yet. Tap 'Generate New Invoice' above to create an invoice.")
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          bookings.forEachIndexed { idx, b ->
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = CardSurface),
              border = BorderStroke(1.dp, CardBorder),
              elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SidebarActiveBlue),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                      val companyLabel = if (b.companyName.isNotBlank()) " (${b.companyName})" else ""
                      Text(
                        "INV-2026-${1001 + idx} • ${b.customerName}$companyLabel",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = TextPrimaryDark
                      )
                      val modelLabel = if (b.vehicleModel.isNotBlank()) " • ${b.vehicleModel}" else ""
                      Text(
                        text = "Date: ${formatToDdMmYyyy(b.tripDate)} • ${b.vehicleCategory}$modelLabel • Driver: ${b.driverName}${if (b.driverPhone.isNotBlank()) " (${b.driverPhone})" else ""}",
                        fontSize = 12.sp,
                        color = TextSecondarySlate
                      )
                    }
                  }
                  Spacer(modifier = Modifier.width(8.dp))
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
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                  }
                }

                Surface(
                  color = Color(0xFFECF3FF),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(22.dp))
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        "Total: ${formatInr(b.totalAmount)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = TextPrimaryDark
                      )
                      Text(
                        text = buildString {
                          append("Base: ${formatInr(b.baseAmount)}")
                          append(" • Toll: ${formatInr(b.tollCharges)}")
                          append(" • Parking: ${formatInr(b.parkingCharges)}")
                          if (b.gstAmount > 0L) append(" • GST: ${formatInr(b.gstAmount)}") else append(" • No GST")
                        },
                        fontSize = 11.5.sp,
                        color = TextSecondarySlate
                      )
                    }
                  }
                }

                FlowRow(
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Button(
                    onClick = { onDownloadInvoicePdf(b) },
                    colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Download PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                  OutlinedButton(
                    onClick = { onEditInvoice(b) },
                    border = BorderStroke(1.dp, Color(0xFFBFD6F6)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Edit Invoice", fontSize = 12.sp, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
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

      CrmModuleSectionHeader(
        title = "GST Tax Details",
        subtitle = "SAC 9964 Passenger Transport GST details, CGST/SGST split, Collected Net GST, and PDF download.",
        actionLabel = "Download GST Details",
        onActionClick = onDownloadGstDetails,
        onBackToDashboard = onBackToDashboard
      )
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SidebarActiveBlue),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Percent, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Text("GSTIN: $agencyGstin ($agencyName)", fontWeight = FontWeight.ExtraBold, color = TextPrimaryDark, fontSize = 14.sp)
          }
          HorizontalDivider(color = CardBorder)
          TaxRow("Total Taxable Passenger Fare Value", formatInr(taxableValue))
          TaxRow("Output CGST (${defaultGstPercent / 2.0}%)", formatInr(cgst))
          TaxRow("Output SGST (${defaultGstPercent / 2.0}%)", formatInr(sgst))
          TaxRow("Gross Output GST Collected", formatInr(totalGst), isBold = true)
          Surface(
            color = Color(0xFFECF3FF),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
              TaxRow("Collected Net GST", formatInr(totalGst), isBold = true, color = SidebarActiveBlue)
            }
          }

          Button(
            onClick = onDownloadGstDetails,
            colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("download_gst_details_button")
          ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Download GST Tax Details (PDF)", fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }

    CrmSection.VEHICLES_MAINTENANCE -> {
      // 0 = Vehicle Details & Vehicle Documents, 1 = Vehicle Maintenance Section
      var selectedVehicleTab by remember { mutableStateOf(0) }

      CrmModuleSectionHeader(
        title = "Vehicles Maintenance",
        subtitle = "",
        actionLabel = if (selectedVehicleTab == 0) "Add Vehicle & Documents" else "Add Vehicle Maintenance",
        onActionClick = if (selectedVehicleTab == 0) onOpenAddVehicle else onOpenAddVehicleMaintenance,
        onBackToDashboard = onBackToDashboard
      )

      // Vertical Tabs (Up and Down, not side-by-side)
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          val tabs = listOf(
            "1. Vehicle Details & Vehicle Documents (with Document Expiry Dates)",
            "2. Vehicle Maintenance Section (Odometer, Service Dates & Next Service Note)"
          )
          tabs.forEachIndexed { index, tabTitle ->
            val isSelected = selectedVehicleTab == index
            Surface(
              color = if (isSelected) SidebarActiveBlue else Color(0xFFECF3FF),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { selectedVehicleTab = index }
                .testTag("vehicles_module_tab_$index")
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
              ) {
                Text(
                  text = tabTitle,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (isSelected) Color.White else TextPrimaryDark
                )
              }
            }
          }
        }
      }

      if (vehicles.isEmpty()) {
        EmptyModuleCard(
          if (selectedVehicleTab == 0) {
            "No saved vehicles yet. Tap 'Add Vehicle & Documents' to add a vehicle."
          } else {
            "No saved vehicle maintenance records yet. Tap 'Add Vehicle Maintenance' to add a maintenance record."
          }
        )
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          vehicles.forEach { v ->
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = CardSurface),
              border = BorderStroke(1.dp, CardBorder),
              elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              if (selectedVehicleTab == 0) {
                // Tab 1: Vehicle Details & Vehicle Documents (with Expiry Dates)
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      modifier = Modifier.weight(1f),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(38.dp)
                          .clip(RoundedCornerShape(10.dp))
                          .background(SidebarActiveBlue),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                      }
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = v.modelName.ifBlank { "Unnamed Vehicle" },
                          fontWeight = FontWeight.ExtraBold,
                          fontSize = 15.sp,
                          color = TextPrimaryDark
                        )
                        Text(
                          text = "Reg No: ${v.registrationNumber.ifBlank { "N/A" }} • Year: ${v.manufacturingYear.ifBlank { "N/A" }}",
                          fontSize = 12.sp,
                          color = TextSecondarySlate
                        )
                      }
                    }
                    Surface(
                      color = Color(0xFFECF3FF),
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Text(
                        text = v.category.ifBlank { "Sedan" },
                        color = SidebarActiveBlue,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                      )
                    }
                  }

                  Surface(
                    color = Color(0xFFECF3FF),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                      verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Text(
                        text = "Insurance: ${v.insuranceDetails.ifBlank { "Not specified" }} • Expiry: ${formatToDdMmYyyy(v.insuranceExpiryDate).ifBlank { "Not set" }}",
                        fontSize = 12.sp,
                        color = TextPrimaryDark
                      )
                      Text(
                        text = "Fitness (FC): ${v.fitnessDetails.ifBlank { "Not specified" }} • Expiry: ${formatToDdMmYyyy(v.fitnessExpiryDate).ifBlank { "Not set" }}",
                        fontSize = 12.sp,
                        color = TextPrimaryDark
                      )
                      Text(
                        text = "Pollution (PUC): ${v.pollutionDetails.ifBlank { "Not specified" }} • Expiry: ${formatToDdMmYyyy(v.pollutionExpiryDate).ifBlank { "Not set" }}",
                        fontSize = 12.sp,
                        color = TextPrimaryDark
                      )
                    }
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    OutlinedButton(
                      onClick = { onEditVehicle(v) },
                      border = BorderStroke(1.dp, Color(0xFFBFD6F6)),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                      shape = RoundedCornerShape(10.dp)
                    ) {
                      Icon(Icons.Default.Edit, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(5.dp))
                      Text("Edit Vehicle & Documents", fontSize = 12.sp, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = { onDeleteVehicle(v.id) }, modifier = Modifier.size(34.dp)) {
                      Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Vehicle", tint = KpiRedIcon)
                    }
                  }
                }
              } else {
                // Tab 2: Vehicle Maintenance Section (Present Odometer KM, Present Service Date & KM, Next Service Date & KM, Next Service Note)
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      modifier = Modifier.weight(1f),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(38.dp)
                          .clip(RoundedCornerShape(10.dp))
                          .background(SidebarActiveBlue),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                      }
                      Text(
                        text = "${v.modelName.ifBlank { "Vehicle" }} (${v.registrationNumber.ifBlank { "N/A" }})",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = TextPrimaryDark
                      )
                    }
                    StatusBadge(
                      text = "Cost: ${formatInr(v.maintenanceCost)}",
                      bg = KpiOrangeBg,
                      fg = KpiOrangeIcon
                    )
                  }

                  Surface(
                    color = Color(0xFFECF3FF),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                      verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Text(
                        text = "Present Odometer KM: ${v.presentRunningKilometers} km",
                        fontSize = 13.sp,
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.ExtraBold
                      )
                      Text(
                        text = "Present Service Date: ${formatToDdMmYyyy(v.serviceDate).ifBlank { "Not set" }} • Present Service KM: ${v.serviceKilometers} km",
                        fontSize = 12.sp,
                        color = SidebarActiveBlue,
                        fontWeight = FontWeight.Bold
                      )
                      Text(
                        text = "Next Service Date: ${formatToDdMmYyyy(v.nextServiceDate).ifBlank { "Not set" }} • Next Service Kilometers: ${v.nextServiceKilometers} km",
                        fontSize = 12.sp,
                        color = KpiGreenIcon,
                        fontWeight = FontWeight.Bold
                      )
                      Text(
                        text = "Next Service Note: ${v.partsChanged.ifBlank { v.serviceNotes }.ifBlank { "Not specified" }}",
                        fontSize = 12.sp,
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    OutlinedButton(
                      onClick = { onEditVehicleMaintenance(v) },
                      border = BorderStroke(1.dp, Color(0xFFBFD6F6)),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                      shape = RoundedCornerShape(10.dp)
                    ) {
                      Icon(Icons.Default.Edit, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(5.dp))
                      Text("Edit Vehicle Maintenance", fontSize = 12.sp, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = { onDeleteVehicle(v.id) }, modifier = Modifier.size(34.dp)) {
                      Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Vehicle", tint = KpiRedIcon)
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
        title = "Driver Roster (${drivers.size} Saved)",
        subtitle = "Driver name, phone, address, driving license, Aadhar number, selfie photo, and uploaded documents.",
        actionLabel = "Add Driver",
        onActionClick = onOpenAddDriver,
        onBackToDashboard = onBackToDashboard
      )
      if (drivers.isEmpty()) {
        EmptyModuleCard("No saved drivers yet. Tap 'Add Driver' to add a driver.")
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          drivers.forEach { d ->
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = CardSurface),
              border = BorderStroke(1.dp, CardBorder),
              elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SidebarActiveBlue),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Badge, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = d.name.ifBlank { "Unnamed Driver" },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = TextPrimaryDark
                      )
                      Text(
                        text = "Phone: ${d.phone.ifBlank { "N/A" }} • ${d.address.ifBlank { "Address not set" }}",
                        fontSize = 12.sp,
                        color = TextSecondarySlate
                      )
                    }
                  }
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
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                  }
                }

                Surface(
                  color = Color(0xFFECF3FF),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Text(
                      text = "DL No: ${d.licenseNumber.ifBlank { "N/A" }} • Aadhar: ${d.aadharNumber.ifBlank { "Not specified" }}",
                      fontSize = 12.sp,
                      color = TextPrimaryDark,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "Selfie: ${d.selfiePhotoUri.ifBlank { "Not uploaded" }}",
                      fontSize = 12.sp,
                      color = KpiTealIcon,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = "Documents: ${d.documentsUploaded.ifBlank { "No documents uploaded" }}",
                      fontSize = 12.sp,
                      color = SidebarActiveBlue
                    )
                    if (d.assignedVehicle.isNotBlank()) {
                      Text(
                        text = "Assigned Vehicle: ${d.assignedVehicle} (${d.vehicleCategory.ifBlank { "Sedan" }})",
                        fontSize = 12.sp,
                        color = TextSecondarySlate
                      )
                    }
                  }
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  OutlinedButton(
                    onClick = { onEditDriver(d) },
                    border = BorderStroke(1.dp, Color(0xFFBFD6F6)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Edit Driver", fontSize = 12.sp, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                  }
                  Spacer(modifier = Modifier.width(6.dp))
                  IconButton(onClick = { onDeleteDriver(d.id) }, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Driver", tint = KpiRedIcon)
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
        EmptyModuleCard("No saved trips logged yet.")
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          bookings.forEach { b ->
            val netMargin = b.totalAmount - b.expenseAmount - b.gstAmount
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = CardSurface),
              border = BorderStroke(1.dp, CardBorder),
              elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SidebarActiveBlue),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                      Text("${b.pickupLocation} → ${b.dropLocation}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = TextPrimaryDark)
                      Text(
                        "Driver: ${b.driverName} • ${b.vehicleNumber} • ${b.distanceKm} km",
                        fontSize = 12.sp,
                        color = TextSecondarySlate
                      )
                    }
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  OutlinedButton(
                    onClick = { onEditTripExpenses(b) },
                    border = BorderStroke(1.dp, Color(0xFFBFD6F6)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Expenses", fontSize = 11.5.sp, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                  }
                }
                Surface(
                  color = Color(0xFFECF3FF),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text("Billed: ${formatInr(b.totalAmount)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    Text("Expenses: ${formatInr(b.expenseAmount)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KpiOrangeIcon)
                    Text(
                      "Net: ${formatInr(netMargin)}",
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
    }

    CrmSection.CUSTOMERS_ACCOUNTS -> {
      // Combine saved Customer Data with any customers present in Booking History
      val mergedCustomers = remember(customers, bookings) {
        val result = mutableListOf<Customer>()
        val seenKeys = mutableSetOf<String>()

        customers.forEach { c ->
          val matchingBookings = bookings.filter {
            it.customerName.equals(c.name, ignoreCase = true) ||
              (c.phone.isNotBlank() && it.customerPhone == c.phone)
          }
          val resolvedAddress = c.address.ifBlank {
            matchingBookings.firstOrNull()?.let { "${it.pickupLocation} → ${it.dropLocation}" }
              ?: c.companyName
          }
          val resolvedTrips = maxOf(c.totalBookings, matchingBookings.size.toLong())
          result.add(
            c.copy(
              address = resolvedAddress,
              totalBookings = resolvedTrips
            )
          )
          seenKeys.add(c.name.lowercase().trim())
          if (c.phone.isNotBlank()) seenKeys.add(c.phone.trim())
        }

        bookings
          .groupBy { it.customerName.lowercase().trim() }
          .forEach { (key, customerBookings) ->
            val first = customerBookings.first()
            val phoneKey = first.customerPhone.trim()
            if (key.isNotBlank() && key !in seenKeys && (phoneKey.isBlank() || phoneKey !in seenKeys)) {
              result.add(
                Customer(
                  id = "booking_cust_${first.id}",
                  name = first.customerName,
                  address = "${first.pickupLocation} → ${first.dropLocation}",
                  phone = first.customerPhone,
                  email = "",
                  companyName = first.companyName.ifBlank { first.pickupLocation },
                  gstin = "",
                  totalBookings = customerBookings.size.toLong(),
                  totalBilled = customerBookings.sumOf { it.totalAmount },
                  pendingBalance = customerBookings.filter { it.paymentStatus != "Paid" }.sumOf { it.totalAmount }
                )
              )
              seenKeys.add(key)
              if (phoneKey.isNotBlank()) seenKeys.add(phoneKey)
            }
          }
        result
      }

      var showCustomersViewDialog by remember { mutableStateOf(false) }
      var showCustomerPdfDialog by remember { mutableStateOf(false) }
      var activeCustomerForDialog by remember { mutableStateOf<Customer?>(null) }
      var showInlineCustomersView by remember { mutableStateOf(false) }

      CrmModuleSectionHeader(
        title = "Customer Data",
        subtitle = "Saved customer details: Customer Name, Address, Mobile Number, and Total Trips.",
        actionLabel = "Add Customer",
        onActionClick = onOpenAddCustomer,
        onBackToDashboard = onBackToDashboard
      )

      // Customer Data Options Card: Show Customers Data View & Download PDF (Stacked Up and Down)
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = {
              activeCustomerForDialog = null
              showInlineCustomersView = !showInlineCustomersView
              showCustomersViewDialog = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("show_customers_data_view_button")
          ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Show Customers Data View", fontWeight = FontWeight.Bold, color = Color.White)
          }

          Button(
            onClick = {
              activeCustomerForDialog = null
              showCustomerPdfDialog = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = NewBookingGreen),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("download_customers_pdf_button")
          ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Download PDF", fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }

      if (showInlineCustomersView && mergedCustomers.isNotEmpty()) {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFECF3FF)),
          border = BorderStroke(1.dp, SidebarActiveBlue.copy(alpha = 0.35f)),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "Customers Data View Summary (${mergedCustomers.size} Saved Records)",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 14.sp,
              color = SidebarActiveBlue
            )
            HorizontalDivider(color = CardBorder)
            mergedCustomers.forEachIndexed { idx, cust ->
              Text(
                text = "${idx + 1}. ${cust.name} • Mobile: ${cust.phone.ifBlank { "N/A" }} • Address: ${cust.address.ifBlank { cust.companyName.ifBlank { "N/A" } }} • Total Trips: ${cust.totalBookings}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
            }
          }
        }
      }

      if (mergedCustomers.isEmpty()) {
        EmptyModuleCard("No saved customer records yet. Save a booking or tap 'Add Customer'.")
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          mergedCustomers.forEach { c ->
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = CardSurface),
              border = BorderStroke(1.dp, CardBorder),
              elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(38.dp)
                      .clip(RoundedCornerShape(10.dp))
                      .background(SidebarActiveBlue),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                  }
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = c.name,
                      fontWeight = FontWeight.ExtraBold,
                      fontSize = 15.5.sp,
                      color = TextPrimaryDark
                    )
                    if (c.companyName.isNotBlank()) {
                      Text(
                        text = c.companyName,
                        fontSize = 12.sp,
                        color = TextSecondarySlate
                      )
                    }
                  }
                  StatusBadge(
                    text = "${c.totalBookings} Trips",
                    bg = Color(0xFFECF3FF),
                    fg = SidebarActiveBlue
                  )
                }

                Surface(
                  color = Color(0xFFECF3FF),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                  ) {
                    Text(
                      text = "Address: ${c.address.ifBlank { c.companyName.ifBlank { "N/A" } }}",
                      fontSize = 12.sp,
                      color = TextSecondarySlate
                    )
                    Text(
                      text = "Mobile Number: ${c.phone.ifBlank { "N/A" }}",
                      fontSize = 12.5.sp,
                      color = TextPrimaryDark,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedButton(
                    onClick = {
                      activeCustomerForDialog = c
                      showCustomersViewDialog = true
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFBFD6F6)),
                    modifier = Modifier.testTag("view_customer_${c.id}")
                  ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Customers Data View", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SidebarActiveBlue)
                  }
                  OutlinedButton(
                    onClick = {
                      activeCustomerForDialog = c
                      showCustomerPdfDialog = true
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, KpiGreenIcon.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("pdf_customer_${c.id}")
                  ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = KpiGreenIcon, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Download PDF", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = KpiGreenIcon)
                  }
                  OutlinedButton(
                    onClick = { onEditCustomer(c) },
                    border = BorderStroke(1.dp, Color(0xFFBFD6F6)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 11.5.sp, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                  }
                  OutlinedButton(
                    onClick = { onDeleteCustomer(c) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, KpiRedIcon.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("delete_customer_${c.id}")
                  ) {
                    Icon(
                      Icons.Default.DeleteOutline,
                      contentDescription = "Delete Customer",
                      tint = KpiRedIcon,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = KpiRedIcon)
                  }
                }
              }
            }
          }
        }
      }

      if (showCustomersViewDialog) {
        CustomersDataViewDialog(
          customers = mergedCustomers,
          bookings = bookings,
          selectedCustomer = activeCustomerForDialog,
          onDownloadPdf = { targetCust ->
            activeCustomerForDialog = targetCust
            showCustomerPdfDialog = true
          },
          onDismiss = { showCustomersViewDialog = false }
        )
      }

      if (showCustomerPdfDialog) {
        CustomerDataPdfDownloadDialog(
          customers = mergedCustomers,
          bookings = bookings,
          agencyName = agencyName,
          agencyGstin = agencyGstin,
          selectedCustomer = activeCustomerForDialog,
          onDismiss = { showCustomerPdfDialog = false }
        )
      }
    }

    CrmSection.USER_ROLES -> {
      CrmModuleSectionHeader(
        title = "User Accounts & Access Roles",
        subtitle = "Signed-in operator account.",
        onBackToDashboard = onBackToDashboard
      )
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
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
              .background(SidebarActiveBlue),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
          }
          Column(modifier = Modifier.weight(1f)) {
            Text("Administrator", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = TextPrimaryDark)
            Text(userEmail.ifBlank { "Signed-In User" }, fontSize = 12.sp, color = SidebarActiveBlue)
            Text("Full Access: Dashboard View, Bookings, Invoices, GST & Fleet", fontSize = 12.sp, color = TextSecondarySlate)
          }
          StatusBadge("Active", KpiGreenBg, KpiGreenIcon)
        }
      }
    }

    CrmSection.REPORTS_EXPORT, CrmSection.EARNINGS_HISTORY -> {
      val context = LocalContext.current
      // 0 = All (Revenue, Expenses & Profits), 1 = Revenue History, 2 = Expenses History, 3 = Profits History
      var selectedReportTab by remember { mutableStateOf(0) }
      var activePdfReportType by remember { mutableStateOf<String?>(null) }

      val valid = bookings.filter { it.tripStatus != "Cancelled" }
      val totalRevenue = valid.sumOf { it.totalAmount }
      val realizedRevenue = valid.filter { it.paymentStatus == "Paid" }.sumOf { it.totalAmount }
      val pendingRevenue = valid.filter { it.paymentStatus != "Paid" }.sumOf { it.totalAmount }
      val totalTripExpenses = valid.sumOf { it.expenseAmount }
      val totalVehicleMaintenanceExpenses = vehicles.sumOf { it.maintenanceCost }
      val combinedExpenses = totalTripExpenses + totalVehicleMaintenanceExpenses
      val totalGstCollected = valid.sumOf { it.gstAmount }
      val totalNetProfit = totalRevenue - combinedExpenses - totalGstCollected

      val sedanSum = valid.filter {
        it.vehicleCategory.equals("Sedan", ignoreCase = true) ||
          it.vehicleCategory.equals("Sedans", ignoreCase = true)
      }.sumOf { it.totalAmount }
      val byCategory = listOf(
        "Sedan" to sedanSum,
        "Compact SUVs" to valid.filter { it.vehicleCategory == "Compact SUVs" }.sumOf { it.totalAmount },
        "SUVs" to valid.filter { it.vehicleCategory == "SUVs" }.sumOf { it.totalAmount },
        "Tourist Travellers" to valid.filter { it.vehicleCategory == "Tourist Travellers" }.sumOf { it.totalAmount }
      )

      CrmModuleSectionHeader(
        title = "Reports & Export",
        subtitle = "",
        actionLabel = "Save Report PDF",
        onActionClick = { activePdfReportType = "ALL" },
        onBackToDashboard = onBackToDashboard
      )

      // Vertical Tabs (Up and Down) for Revenue History, Expenses History, Profits History
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          val reportTabs = listOf(
            "1. All Financial History (Revenue, Expenses & Profits)",
            "2. Revenue History (${formatInr(totalRevenue)})",
            "3. Expenses History (${formatInr(combinedExpenses)})",
            "4. Profits History (${formatInr(totalNetProfit)})"
          )
          reportTabs.forEachIndexed { idx, tabLabel ->
            val isSelected = selectedReportTab == idx
            Surface(
              color = if (isSelected) SidebarActiveBlue else Color(0xFFECF3FF),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { selectedReportTab = idx }
                .testTag("reports_tab_$idx")
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
              ) {
                Text(
                  text = tabLabel,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (isSelected) Color.White else TextPrimaryDark
                )
              }
            }
          }
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // 1. REVENUE HISTORY SECTION
        if (selectedReportTab == 0 || selectedReportTab == 1) {
          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = BorderStroke(1.dp, CardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SidebarActiveBlue),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.BarChart, contentDescription = null, tint = Color.White, modifier = Modifier.size(19.dp))
                }
                Text("Revenue History", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextPrimaryDark)
              }
              HorizontalDivider(color = CardBorder)
              TaxRow("Total Gross Revenue", formatInr(totalRevenue), isBold = true, color = SidebarActiveBlue)
              TaxRow("Realized Paid Revenue", formatInr(realizedRevenue), isBold = true, color = KpiGreenIcon)
              TaxRow("Pending Receivables", formatInr(pendingRevenue), isBold = true, color = KpiAmberIcon)
              HorizontalDivider(color = CardBorder)
              Text("Revenue by Vehicle Category", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSecondarySlate)
              byCategory.forEach { (cat, sum) ->
                TaxRow(cat, formatInr(sum))
              }

              if (valid.isNotEmpty()) {
                HorizontalDivider(color = CardBorder)
                Text("Saved Trip Revenue Records (${valid.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SidebarActiveBlue)
                valid.forEachIndexed { idx, b ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(12.dp))
                      .background(Color(0xFFECF3FF))
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "${idx + 1}. ${b.customerName} (${formatToDdMmYyyy(b.tripDate)})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimaryDark
                      )
                      Text(
                        text = "${b.pickupLocation} → ${b.dropLocation} • ${b.vehicleCategory} ${b.vehicleModel}".trim(),
                        fontSize = 11.sp,
                        color = TextSecondarySlate
                      )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                      Text(formatInr(b.totalAmount), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = KpiGreenIcon)
                      Text("${b.paymentStatus} (${b.paymentChannel})", fontSize = 10.sp, color = TextSecondarySlate)
                    }
                  }
                }
              }

              Button(
                onClick = { activePdfReportType = "REVENUE" },
                colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("save_revenue_history_pdf_button")
              ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Revenue History (PDF)", fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }

        // 2. EXPENSES HISTORY SECTION
        if (selectedReportTab == 0 || selectedReportTab == 2) {
          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = BorderStroke(1.dp, CardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(KpiOrangeIcon),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Payments, contentDescription = null, tint = Color.White, modifier = Modifier.size(19.dp))
                }
                Text("Expenses History", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextPrimaryDark)
              }
              HorizontalDivider(color = CardBorder)
              TaxRow("Total Trip Operating Expenses", formatInr(totalTripExpenses), isBold = true, color = KpiOrangeIcon)
              TaxRow("Total Vehicle Maintenance Expenses", formatInr(totalVehicleMaintenanceExpenses), isBold = true, color = KpiOrangeIcon)
              HorizontalDivider(color = CardBorder)
              TaxRow("Combined Total Expenses", formatInr(combinedExpenses), isBold = true, color = KpiRedIcon)

              if (valid.isNotEmpty()) {
                HorizontalDivider(color = CardBorder)
                Text("Saved Trip Expenses Records (${valid.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KpiOrangeIcon)
                valid.forEachIndexed { idx, b ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(12.dp))
                      .background(Color(0xFFECF3FF))
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "${idx + 1}. ${b.pickupLocation} → ${b.dropLocation} (${formatToDdMmYyyy(b.tripDate)})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimaryDark
                      )
                      Text(
                        text = "Driver: ${b.driverName.ifBlank { "N/A" }} • Vehicle: ${b.vehicleNumber.ifBlank { "N/A" }}",
                        fontSize = 11.sp,
                        color = TextSecondarySlate
                      )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                      Text(
                        text = formatInr(b.expenseAmount),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = KpiOrangeIcon
                      )
                      OutlinedButton(
                        onClick = { onEditTripExpenses(b) },
                        border = BorderStroke(1.dp, Color(0xFFBFD6F6)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                      ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Save Expense", fontSize = 10.sp, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }
              }

              if (vehicles.any { it.maintenanceCost > 0L }) {
                HorizontalDivider(color = CardBorder)
                Text("Saved Vehicle Maintenance Expenses", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KpiOrangeIcon)
                vehicles.filter { it.maintenanceCost > 0L }.forEach { v ->
                  TaxRow(
                    "${v.modelName.ifBlank { "Vehicle" }} (${v.registrationNumber.ifBlank { "N/A" }}) - ${formatToDdMmYyyy(v.serviceDate).ifBlank { "Service" }}",
                    formatInr(v.maintenanceCost),
                    color = KpiOrangeIcon
                  )
                }
              }

              Button(
                onClick = { activePdfReportType = "EXPENSES" },
                colors = ButtonDefaults.buttonColors(containerColor = KpiOrangeIcon),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("save_expenses_history_pdf_button")
              ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Expenses History (PDF)", fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }

        // 3. PROFITS HISTORY SECTION
        if (selectedReportTab == 0 || selectedReportTab == 3) {
          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = BorderStroke(1.dp, CardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NewBookingGreen),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Payments, contentDescription = null, tint = Color.White, modifier = Modifier.size(19.dp))
                }
                Text("Profits History", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextPrimaryDark)
              }
              HorizontalDivider(color = CardBorder)
              TaxRow("Total Gross Revenue", formatInr(totalRevenue), isBold = true, color = SidebarActiveBlue)
              TaxRow("Less: Combined Expenses (Trips + Maintenance)", formatInr(combinedExpenses), color = KpiOrangeIcon)
              TaxRow("Less: Output GST Collected", formatInr(totalGstCollected), color = TextSecondarySlate)
              HorizontalDivider(color = CardBorder)
              TaxRow(
                "Total Net Operating Profit",
                formatInr(totalNetProfit),
                isBold = true,
                color = if (totalNetProfit >= 0L) KpiGreenIcon else KpiRedIcon
              )

              if (valid.isNotEmpty()) {
                HorizontalDivider(color = CardBorder)
                Text("Saved Trip-by-Trip Profits History (${valid.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KpiGreenIcon)
                valid.forEachIndexed { idx, b ->
                  val tripProfit = b.totalAmount - b.expenseAmount - b.gstAmount
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(12.dp))
                      .background(Color(0xFFECF3FF))
                      .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "${idx + 1}. ${b.customerName} • ${b.pickupLocation} → ${b.dropLocation}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimaryDark
                      )
                      Text(
                        text = "Date: ${formatToDdMmYyyy(b.tripDate)} • Revenue: ${formatInr(b.totalAmount)} • Expense: ${formatInr(b.expenseAmount)}",
                        fontSize = 11.sp,
                        color = TextSecondarySlate
                      )
                    }
                    Text(
                      text = "Profit: ${formatInr(tripProfit)}",
                      fontWeight = FontWeight.ExtraBold,
                      fontSize = 13.sp,
                      color = if (tripProfit >= 0L) KpiGreenIcon else KpiRedIcon
                    )
                  }
                }
              }

              Button(
                onClick = { activePdfReportType = "PROFITS" },
                colors = ButtonDefaults.buttonColors(containerColor = NewBookingGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("save_profits_history_pdf_button")
              ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Profits History (PDF)", fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }

        // 4. COMBINED EXPORT & SAVE CARD (PDF & CSV)
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = CardSurface),
          border = BorderStroke(1.dp, CardBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Save & Export Complete Financial Statement", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextPrimaryDark)
            Text(
              "Saved Bookings: ${bookings.size} | Saved Drivers: ${drivers.size} | Saved Vehicles: ${vehicles.size} | Saved Customers: ${customers.size}",
              fontSize = 13.sp,
              color = TextSecondarySlate
            )
            Button(
              onClick = { activePdfReportType = "ALL" },
              colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Save Revenue, Expenses & Profits Report (PDF)", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Button(
              onClick = {
                val csvBuilder = StringBuilder()
                csvBuilder.append("Date,Customer,Route,Category,VehicleModel,VehicleNo,Driver,PaymentStatus,RevenueINR,ExpenseINR,GSTINR,NetProfitINR\n")
                valid.forEach { b ->
                  val netProfit = b.totalAmount - b.expenseAmount - b.gstAmount
                  csvBuilder.append(
                    "${formatToDdMmYyyy(b.tripDate)},${b.customerName},${b.pickupLocation} to ${b.dropLocation},${b.vehicleCategory},${b.vehicleModel},${b.vehicleNumber},${b.driverName},${b.paymentStatus},${b.totalAmount},${b.expenseAmount},${b.gstAmount},$netProfit\n"
                  )
                }
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                  type = "text/plain"
                  putExtra(Intent.EXTRA_SUBJECT, "Revenue, Expenses & Profits History CSV")
                  putExtra(Intent.EXTRA_TEXT, csvBuilder.toString())
                }
                context.startActivity(Intent.createChooser(sendIntent, "Export Revenue, Expenses & Profits CSV"))
              },
              colors = ButtonDefaults.buttonColors(containerColor = NewBookingGreen),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Export & Share Revenue, Expenses & Profits CSV", fontWeight = FontWeight.Bold, color = Color.White)
            }
          }
        }
      }

      if (activePdfReportType != null) {
        FinancialReportPdfDownloadDialog(
          bookings = bookings,
          vehicles = vehicles,
          agencyName = agencyName,
          agencyGstin = agencyGstin,
          reportType = activePdfReportType ?: "ALL",
          onDismiss = { activePdfReportType = null }
        )
      }
    }

    CrmSection.SYSTEM_SETTINGS -> {
      var nameInput by remember(agencyName) { mutableStateOf(agencyName) }
      var gstinInput by remember(agencyGstin) { mutableStateOf(agencyGstin) }
      var gstSlabInput by remember(defaultGstPercent) { mutableStateOf(defaultGstPercent.toString()) }

      CrmModuleSectionHeader(
        title = "System Settings & Tariff Control",
        subtitle = "",
        onBackToDashboard = onBackToDashboard
      )
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SidebarActiveBlue),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Text("Agency & GST Tariff Configuration", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = TextPrimaryDark)
          }
          HorizontalDivider(color = CardBorder)
          val fieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SidebarActiveBlue,
            unfocusedBorderColor = Color(0xFFCBD9F3),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF6F9FF)
          )
          OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Travel Agency / Company Name") },
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = gstinInput,
            onValueChange = { gstinInput = it },
            label = { Text("Company GSTIN") },
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = gstSlabInput,
            onValueChange = { gstSlabInput = it.filter { ch -> ch.isDigit() } },
            label = { Text("Optional Passenger Transport GST % (0, 5, 12, or 18)") },
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
          )
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = {
                onSaveSettings(nameInput, gstinInput, gstSlabInput.toIntOrNull() ?: 0)
              },
              colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Save Configuration", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
              onClick = onSignOut,
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, KpiRedIcon),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("sign_out_button")
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
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = CardSurface),
    border = BorderStroke(1.dp, CardBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(58.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0xFFECF3FF)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.DirectionsCar,
          contentDescription = null,
          tint = SidebarActiveBlue,
          modifier = Modifier.size(32.dp)
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
      Text(message, style = MaterialTheme.typography.bodyMedium, color = TextSecondarySlate)
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
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      color = TextPrimaryDark
    )
    Text(
      text = value,
      fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold,
      fontSize = 15.sp,
      color = color
    )
  }
}
