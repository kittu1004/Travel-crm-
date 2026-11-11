package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CrmSection
import com.example.ui.theme.KpiGreenBg
import com.example.ui.theme.KpiGreenIcon
import com.example.ui.theme.NewBookingGreen
import com.example.ui.theme.SidebarActiveBlue
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondarySlate

private val VEHICLE_CATEGORIES = listOf("Sedans", "Compact SUVs", "SUVs", "Tourist Travellers")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewBookingDialog(
  defaultDate: String,
  onDismiss: () -> Unit,
  onConfirm: (
    customerName: String,
    customerPhone: String,
    pickup: String,
    drop: String,
    category: String,
    vehicleNumber: String,
    driverName: String,
    tripStatus: String,
    paymentStatus: String,
    paymentChannel: String,
    baseAmount: Long,
    expenseAmount: Long,
    distanceKm: Long,
    tripDate: String,
  ) -> Unit,
) {
  var customerName by remember { mutableStateOf("") }
  var customerPhone by remember { mutableStateOf("+91 98450 11223") }
  var pickup by remember { mutableStateOf("Bengaluru Airport T2") }
  var drop by remember { mutableStateOf("Whitefield Tech Hub") }
  var category by remember { mutableStateOf("SUVs") }
  var vehicleNumber by remember { mutableStateOf("KA-01-MG-4590") }
  var driverName by remember { mutableStateOf("Ramesh Kumar") }
  var tripStatus by remember { mutableStateOf("Active") }
  var paymentStatus by remember { mutableStateOf("Paid") }
  var paymentChannel by remember { mutableStateOf("UPI") }
  var baseAmountText by remember { mutableStateOf("6500") }
  var expenseAmountText by remember { mutableStateOf("1400") }
  var distanceText by remember { mutableStateOf("140") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text("Create New Fleet Booking", fontWeight = FontWeight.Bold)
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = customerName,
          onValueChange = { customerName = it },
          label = { Text("Customer / Corporate Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = customerPhone,
          onValueChange = { customerPhone = it },
          label = { Text("Customer Phone") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = pickup,
          onValueChange = { pickup = it },
          label = { Text("Pickup Location") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = drop,
          onValueChange = { drop = it },
          label = { Text("Drop Location") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Text("Vehicle Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          VEHICLE_CATEGORIES.forEach { cat ->
            FilterChip(
              selected = category == cat,
              onClick = { category = cat },
              label = { Text(cat, fontSize = 11.sp) }
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = driverName,
            onValueChange = { driverName = it },
            label = { Text("Driver") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = vehicleNumber,
            onValueChange = { vehicleNumber = it },
            label = { Text("Vehicle Plate") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = baseAmountText,
            onValueChange = { baseAmountText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Base Fare (₹)") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = expenseAmountText,
            onValueChange = { expenseAmountText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Expenses (₹)") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        Text("Payment Status", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf("Paid", "Pending").forEach { status ->
            FilterChip(
              selected = paymentStatus == status,
              onClick = { paymentStatus = status },
              label = { Text(status) }
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirm(
            customerName.ifBlank { "Rajesh Sharma" },
            customerPhone,
            pickup,
            drop,
            category,
            vehicleNumber,
            driverName,
            tripStatus,
            paymentStatus,
            paymentChannel,
            baseAmountText.toLongOrNull() ?: 4500L,
            expenseAmountText.toLongOrNull() ?: 900L,
            distanceText.toLongOrNull() ?: 120L,
            defaultDate
          )
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = NewBookingGreen)
      ) {
        Text("Confirm Booking", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddDriverDialog(
  onDismiss: () -> Unit,
  onConfirm: (String, String, String, String, String, String) -> Unit,
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("+91 98450 55667") }
  var license by remember { mutableStateOf("KA01-2020-0054120") }
  var vehicle by remember { mutableStateOf("Toyota Innova Crysta (KA-01-MG-4590)") }
  var category by remember { mutableStateOf("SUVs") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add Driver to Roster", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Driver Full Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Mobile Number") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = license,
          onValueChange = { license = it },
          label = { Text("Commercial License No.") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = vehicle,
          onValueChange = { vehicle = it },
          label = { Text("Assigned Vehicle") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          VEHICLE_CATEGORIES.forEach { cat ->
            FilterChip(
              selected = category == cat,
              onClick = { category = cat },
              label = { Text(cat, fontSize = 11.sp) }
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirm(name.ifBlank { "Manjunath Naik" }, phone, license, vehicle, category, "Available")
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue)
      ) {
        Text("Save Driver")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddVehicleDialog(
  onDismiss: () -> Unit,
  onConfirm: (String, String, String, String, Long, String, String) -> Unit,
) {
  var regNo by remember { mutableStateOf("KA-01-AB-7788") }
  var model by remember { mutableStateOf("Toyota Innova Hycross ZX") }
  var category by remember { mutableStateOf("SUVs") }
  var costText by remember { mutableStateOf("8500") }
  var notes by remember { mutableStateOf("Full synthetic service & brake check") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add Fleet Vehicle", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = regNo,
          onValueChange = { regNo = it },
          label = { Text("Registration Plate") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = model,
          onValueChange = { model = it },
          label = { Text("Vehicle Make & Model") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          VEHICLE_CATEGORIES.forEach { cat ->
            FilterChip(
              selected = category == cat,
              onClick = { category = cat },
              label = { Text(cat, fontSize = 11.sp) }
            )
          }
        }
        OutlinedTextField(
          value = costText,
          onValueChange = { costText = it.filter { ch -> ch.isDigit() } },
          label = { Text("Maintenance Spend (₹)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Service Notes") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirm(
            regNo,
            model,
            category,
            "Available",
            costText.toLongOrNull() ?: 5000L,
            "2026-12-28",
            notes
          )
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue)
      ) {
        Text("Add Vehicle")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddLeadDialog(
  onDismiss: () -> Unit,
  onConfirm: (String, String, String, String, Long, String) -> Unit,
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("+91 99801 22334") }
  var route by remember { mutableStateOf("Bengaluru to Coorg 3-Day Package") }
  var category by remember { mutableStateOf("Compact SUVs") }
  var quoteText by remember { mutableStateOf("14500") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Create Travel Lead", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Prospective Client Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Client Phone") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = route,
          onValueChange = { route = it },
          label = { Text("Route & Package Details") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          VEHICLE_CATEGORIES.forEach { cat ->
            FilterChip(
              selected = category == cat,
              onClick = { category = cat },
              label = { Text(cat, fontSize = 11.sp) }
            )
          }
        }
        OutlinedTextField(
          value = quoteText,
          onValueChange = { quoteText = it.filter { ch -> ch.isDigit() } },
          label = { Text("Quoted Package Price (₹)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirm(
            name.ifBlank { "Arjun Reddy" },
            phone,
            route,
            category,
            quoteText.toLongOrNull() ?: 12000L,
            "Direct Inquiry"
          )
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue)
      ) {
        Text("Save Lead")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

@Composable
fun AddCustomerDialog(
  onDismiss: () -> Unit,
  onConfirm: (String, String, String, String, String, Long, Long) -> Unit,
) {
  var company by remember { mutableStateOf("") }
  var contact by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("+91 98201 44556") }
  var gstin by remember { mutableStateOf("29AABCC4512N1Z9") }
  var billedText by remember { mutableStateOf("45000") }
  var pendingText by remember { mutableStateOf("12000") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add Corporate / Retail Account", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = company,
          onValueChange = { company = it },
          label = { Text("Company / Account Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = contact,
          onValueChange = { contact = it },
          label = { Text("Primary Contact Person") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Phone Number") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = gstin,
          onValueChange = { gstin = it },
          label = { Text("Customer GSTIN") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = billedText,
            onValueChange = { billedText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Total Billed (₹)") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = pendingText,
            onValueChange = { pendingText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Pending Due (₹)") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirm(
            contact.ifBlank { "Neha Gupta" },
            phone,
            "accounts@corporate.in",
            company.ifBlank { "Synergy Tech India Ltd" },
            gstin,
            billedText.toLongOrNull() ?: 45000L,
            pendingText.toLongOrNull() ?: 0L
          )
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue)
      ) {
        Text("Save Account")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

@Composable
fun KpiDetailsDialog(
  spec: KpiCardSpec,
  onDismiss: () -> Unit,
  onOpenSection: (CrmSection) -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(spec.title.replace("\n", " "), fontWeight = FontWeight.ExtraBold)
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
          color = spec.iconBg,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("CURRENT LIVE METRIC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = spec.iconTint)
            Spacer(modifier = Modifier.height(4.dp))
            Text(spec.valueText, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimaryDark)
          }
        }
        Text(
          text = "This metric is synchronized in real time with Cloud Firestore. Open the dedicated ${spec.targetSection.title} module to inspect individual records, export reports, or update status.",
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondarySlate
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onDismiss()
          onOpenSection(spec.targetSection)
        },
        colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue)
      ) {
        Text("Open ${spec.targetSection.title}", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Close") }
    }
  )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomerWebsitePreviewDialog(
  agencyName: String,
  onDismiss: () -> Unit,
  onSubmitWebLead: (String, String, String, String, Long) -> Unit,
) {
  var visitorName by remember { mutableStateOf("") }
  var visitorPhone by remember { mutableStateOf("+91 98450 99001") }
  var pickupDrop by remember { mutableStateOf("Bengaluru Airport to Mysuru Resort") }
  var selectedCategory by remember { mutableStateOf("SUVs") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column {
        Surface(color = KpiGreenBg, shape = RoundedCornerShape(6.dp)) {
          Text(
            "LIVE CUSTOMER BOOKING PORTAL",
            color = KpiGreenIcon,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(agencyName, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          "Submit a live customer web inquiry below — it immediately flows into your Admin Operations Command Center Leads pipeline!",
          fontSize = 12.sp,
          color = TextSecondarySlate
        )
        OutlinedTextField(
          value = visitorName,
          onValueChange = { visitorName = it },
          label = { Text("Your Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = visitorPhone,
          onValueChange = { visitorPhone = it },
          label = { Text("Mobile Number") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = pickupDrop,
          onValueChange = { pickupDrop = it },
          label = { Text("Pickup & Destination") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          VEHICLE_CATEGORIES.forEach { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              label = { Text(cat, fontSize = 11.sp) }
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSubmitWebLead(
            visitorName.ifBlank { "Website Visitor" },
            visitorPhone,
            pickupDrop,
            selectedCategory,
            8500L
          )
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = NewBookingGreen)
      ) {
        Text("Send Web Booking Inquiry", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Close Preview") }
    }
  )
}
