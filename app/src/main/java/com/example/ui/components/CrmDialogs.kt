package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Booking
import com.example.data.CrmSection
import com.example.data.Customer
import com.example.data.Driver
import com.example.data.Lead
import com.example.data.Vehicle
import com.example.ui.formatInr
import com.example.ui.theme.CanvasBackground
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
import com.example.ui.theme.KpiTealBg
import com.example.ui.theme.KpiTealIcon
import com.example.ui.theme.NewBookingGreen
import com.example.ui.theme.SidebarActiveBlue
import com.example.ui.theme.SidebarNavy
import com.example.ui.theme.SidebarTextMuted
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondarySlate

private val VEHICLE_CATEGORIES = listOf("Sedans", "Compact SUVs", "SUVs", "Tourist Travellers")
private val TRIP_STATUSES = listOf("Active", "Completed", "Cancelled")
private val PAYMENT_STATUSES = listOf("Paid", "Pending")
private val PAYMENT_CHANNELS = listOf("UPI", "Bank NEFT", "Cash", "Corporate Card")
private val DRIVER_STATUSES = listOf("Available", "On Trip", "Off Duty")
private val VEHICLE_STATUSES = listOf("Available", "On Trip", "Maintenance")
private val LEAD_SOURCES = listOf("Direct Inquiry", "Website", "WhatsApp", "Corporate Desk", "Phone Call")
private val LEAD_STATUSES = listOf("New", "Quoted", "Converted", "Lost")

@Composable
fun CrmFormModalShell(
  badgeText: String,
  badgeBg: Color = KpiBlueBg,
  badgeFg: Color = SidebarActiveBlue,
  title: String,
  subtitle: String,
  primaryButtonText: String,
  primaryButtonColor: Color = SidebarActiveBlue,
  onQuickFillSample: (() -> Unit)? = null,
  onClearFields: (() -> Unit)? = null,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
  content: @Composable ColumnScope.() -> Unit,
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(22.dp),
      color = CanvasBackground,
      tonalElevation = 8.dp,
      shadowElevation = 18.dp,
      border = BorderStroke(1.dp, CardBorder),
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .widthIn(max = 640.dp)
        .heightIn(max = 760.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Dark Command Center Modal Header
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(SidebarNavy)
            .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              color = badgeBg.copy(alpha = 0.18f),
              shape = RoundedCornerShape(6.dp),
              border = BorderStroke(1.dp, badgeFg.copy(alpha = 0.45f))
            ) {
              Text(
                text = badgeText,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.7.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              if (onQuickFillSample != null) {
                Surface(
                  color = SidebarActiveBlue.copy(alpha = 0.22f),
                  shape = RoundedCornerShape(8.dp),
                  border = BorderStroke(1.dp, SidebarActiveBlue.copy(alpha = 0.6f)),
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onQuickFillSample() }
                    .testTag("modal_quick_fill_button")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.AutoAwesome,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "Auto-Fill Sample",
                      color = Color.White,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }

              IconButton(
                onClick = onDismiss,
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.1f))
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Close Modal",
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = title,
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.ExtraBold
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = subtitle,
            color = SidebarTextMuted,
            fontSize = 12.sp
          )
        }

        // Scrollable Structured Form Content
        Column(
          modifier = Modifier
            .weight(1f, fill = false)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          content()
        }

        HorizontalDivider(color = CardBorder)

        // Sticky Action Footer
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(CardSurface)
            .padding(horizontal = 18.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (onClearFields != null) {
            TextButton(onClick = onClearFields) {
              Text("Clear Form", color = TextSecondarySlate, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
          } else {
            Spacer(modifier = Modifier.width(4.dp))
          }

          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
              onClick = onDismiss,
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, CardBorder)
            ) {
              Text("Cancel", color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
            }

            Button(
              onClick = onConfirm,
              colors = ButtonDefaults.buttonColors(containerColor = primaryButtonColor),
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
              modifier = Modifier.testTag("modal_confirm_submit_button")
            ) {
              Text(primaryButtonText, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun FormSectionCard(
  stepNumber: String,
  sectionTitle: String,
  accentColor: Color = SidebarActiveBlue,
  content: @Composable ColumnScope.() -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(CardSurface)
      .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
      .padding(14.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(22.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(accentColor.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = stepNumber,
          color = accentColor,
          fontSize = 11.sp,
          fontWeight = FontWeight.ExtraBold
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = sectionTitle,
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold,
        color = TextPrimaryDark
      )
    }
    content()
  }
}

@Composable
private fun FormFieldInput(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  placeholder: String,
  icon: ImageVector,
  modifier: Modifier = Modifier,
  isNumeric: Boolean = false,
  singleLine: Boolean = true,
) {
  OutlinedTextField(
    value = value,
    onValueChange = { next ->
      if (isNumeric) {
        onValueChange(next.filter { ch -> ch.isDigit() })
      } else {
        onValueChange(next)
      }
    },
    label = { Text(label, fontSize = 12.sp) },
    placeholder = { Text(placeholder, fontSize = 12.sp, color = TextSecondarySlate.copy(alpha = 0.65f)) },
    leadingIcon = {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = SidebarActiveBlue,
        modifier = Modifier.size(18.dp)
      )
    },
    singleLine = singleLine,
    keyboardOptions = KeyboardOptions(
      keyboardType = if (isNumeric) KeyboardType.Number else KeyboardType.Text
    ),
    shape = RoundedCornerShape(10.dp),
    colors = OutlinedTextFieldDefaults.colors(
      focusedBorderColor = SidebarActiveBlue,
      unfocusedBorderColor = CardBorder,
      focusedContainerColor = Color.White,
      unfocusedContainerColor = CanvasBackground.copy(alpha = 0.5f)
    ),
    modifier = modifier
  )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewBookingDialog(
  defaultDate: String,
  initialBooking: Booking? = null,
  availableDrivers: List<Driver> = emptyList(),
  availableVehicles: List<Vehicle> = emptyList(),
  availableCustomers: List<Customer> = emptyList(),
  defaultGstPercent: Int = 5,
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
  val isEditMode = initialBooking != null
  var customerName by remember(initialBooking) { mutableStateOf(initialBooking?.customerName ?: "") }
  var customerPhone by remember(initialBooking) { mutableStateOf(initialBooking?.customerPhone ?: "") }
  var pickup by remember(initialBooking) { mutableStateOf(initialBooking?.pickupLocation ?: "") }
  var drop by remember(initialBooking) { mutableStateOf(initialBooking?.dropLocation ?: "") }
  var category by remember(initialBooking) { mutableStateOf(initialBooking?.vehicleCategory ?: "SUVs") }
  var vehicleNumber by remember(initialBooking) { mutableStateOf(initialBooking?.vehicleNumber ?: "") }
  var driverName by remember(initialBooking) { mutableStateOf(initialBooking?.driverName ?: "") }
  var tripStatus by remember(initialBooking) { mutableStateOf(initialBooking?.tripStatus ?: "Active") }
  var paymentStatus by remember(initialBooking) { mutableStateOf(initialBooking?.paymentStatus ?: "Paid") }
  var paymentChannel by remember(initialBooking) { mutableStateOf(initialBooking?.paymentChannel ?: "UPI") }
  var baseAmountText by remember(initialBooking) {
    mutableStateOf(if (initialBooking != null) initialBooking.baseAmount.toString() else "")
  }
  var expenseAmountText by remember(initialBooking) {
    mutableStateOf(if (initialBooking != null) initialBooking.expenseAmount.toString() else "")
  }
  var distanceText by remember(initialBooking) {
    mutableStateOf(if (initialBooking != null) initialBooking.distanceKm.toString() else "")
  }
  var tripDate by remember(initialBooking) {
    mutableStateOf(initialBooking?.tripDate?.ifBlank { defaultDate } ?: defaultDate)
  }

  val liveBase = baseAmountText.toLongOrNull() ?: 0L
  val liveGst = (liveBase * defaultGstPercent) / 100L
  val liveTotal = liveBase + liveGst
  val liveExpense = expenseAmountText.toLongOrNull() ?: 0L
  val liveNetMargin = liveTotal - liveExpense - liveGst

  CrmFormModalShell(
    badgeText = if (isEditMode) "UPDATE BOOKING & INVOICE" else "DISPATCH & BILLING ENTRY",
    badgeBg = KpiGreenBg,
    badgeFg = NewBookingGreen,
    title = if (isEditMode) "Edit Booking & Dispatch Details" else "New Fleet Booking & GST Invoice",
    subtitle = "Enter passenger details, pickup/drop route, fleet vehicle & driver assignment, and fare details.",
    primaryButtonText = if (isEditMode) "Update Booking" else "Confirm & Dispatch Booking",
    primaryButtonColor = NewBookingGreen,
    onQuickFillSample = {
      customerName = "Infosys Executive Travel Desk"
      customerPhone = "+91 98450 11223"
      pickup = "Bengaluru Airport Terminal 2"
      drop = "Electronic City Phase 1 Campus"
      category = "SUVs"
      vehicleNumber = availableVehicles.firstOrNull()?.registrationNumber ?: "KA-01-MG-4590"
      driverName = availableDrivers.firstOrNull()?.name ?: "Ramesh Kumar"
      baseAmountText = "6500"
      expenseAmountText = "1250"
      distanceText = "145"
      tripStatus = "Active"
      paymentStatus = "Paid"
      paymentChannel = "UPI"
    },
    onClearFields = {
      customerName = ""
      customerPhone = ""
      pickup = ""
      drop = ""
      vehicleNumber = ""
      driverName = ""
      baseAmountText = ""
      expenseAmountText = ""
      distanceText = ""
    },
    onDismiss = onDismiss,
    onConfirm = {
      onConfirm(
        customerName.ifBlank { "Rajesh Sharma" },
        customerPhone.ifBlank { "+91 98450 11223" },
        pickup.ifBlank { "Bengaluru Airport T2" },
        drop.ifBlank { "Whitefield Tech Park" },
        category,
        vehicleNumber.ifBlank { "KA-01-MG-4590" },
        driverName.ifBlank { "Ramesh Kumar" },
        tripStatus,
        paymentStatus,
        paymentChannel,
        baseAmountText.toLongOrNull() ?: 4500L,
        expenseAmountText.toLongOrNull() ?: 900L,
        distanceText.toLongOrNull() ?: 120L,
        tripDate.ifBlank { defaultDate }
      )
      onDismiss()
    }
  ) {
    // Section 1: Customer & Schedule
    FormSectionCard(
      stepNumber = "1",
      sectionTitle = "Passenger / Corporate Client & Schedule",
      accentColor = SidebarActiveBlue
    ) {
      if (availableCustomers.isNotEmpty()) {
        Text(
          text = "Quick-Select Saved Corporate Client:",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = TextSecondarySlate
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          availableCustomers.take(4).forEach { client ->
            FilterChip(
              selected = customerName == client.companyName,
              onClick = {
                customerName = client.companyName
                customerPhone = client.phone
              },
              label = { Text(client.companyName, fontSize = 11.sp) }
            )
          }
        }
      }

      FormFieldInput(
        value = customerName,
        onValueChange = { customerName = it },
        label = "Customer or Corporate Company Name",
        placeholder = "e.g., Rajesh Sharma / Wipro Ltd",
        icon = Icons.Default.Person,
        modifier = Modifier.fillMaxWidth()
      )

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = customerPhone,
          onValueChange = { customerPhone = it },
          label = "Contact Mobile",
          placeholder = "+91 98450 11223",
          icon = Icons.Default.Phone,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = tripDate,
          onValueChange = { tripDate = it },
          label = "Trip Date (YYYY-MM-DD)",
          placeholder = defaultDate,
          icon = Icons.Default.CalendarToday,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Section 2: Route & Odometer
    FormSectionCard(
      stepNumber = "2",
      sectionTitle = "Pickup, Drop Destination & Trip Status",
      accentColor = KpiTealIcon
    ) {
      FormFieldInput(
        value = pickup,
        onValueChange = { pickup = it },
        label = "Pickup Location / Terminal / Hotel",
        placeholder = "e.g., Kempegowda International Airport T2",
        icon = Icons.Default.LocationOn,
        modifier = Modifier.fillMaxWidth()
      )
      FormFieldInput(
        value = drop,
        onValueChange = { drop = it },
        label = "Drop Location / Outstation Destination",
        placeholder = "e.g., Mysuru Palace Resort / MG Road",
        icon = Icons.Default.Route,
        modifier = Modifier.fillMaxWidth()
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FormFieldInput(
          value = distanceText,
          onValueChange = { distanceText = it },
          label = "Est. Distance (km)",
          placeholder = "e.g., 140",
          icon = Icons.Default.Speed,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
        Column(modifier = Modifier.weight(1.3f)) {
          Text("Trip Dispatch Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
          FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TRIP_STATUSES.forEach { st ->
              FilterChip(
                selected = tripStatus == st,
                onClick = { tripStatus = st },
                label = { Text(st, fontSize = 11.sp) }
              )
            }
          }
        }
      }
    }

    // Section 3: Fleet Vehicle & Driver Assignment
    FormSectionCard(
      stepNumber = "3",
      sectionTitle = "Fleet Category, Vehicle & Driver Assignment",
      accentColor = KpiPurpleIcon
    ) {
      Text("Select Fleet Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
      FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        VEHICLE_CATEGORIES.forEach { cat ->
          FilterChip(
            selected = category == cat,
            onClick = { category = cat },
            label = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = KpiPurpleBg,
              selectedLabelColor = KpiPurpleIcon
            )
          )
        }
      }

      if (availableDrivers.isNotEmpty()) {
        Text("Quick-Assign Driver from Roster:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          availableDrivers.take(5).forEach { drv ->
            FilterChip(
              selected = driverName == drv.name,
              onClick = {
                driverName = drv.name
                category = drv.vehicleCategory
                val plate = drv.assignedVehicle.substringAfter("(", "").substringBefore(")")
                if (plate.isNotBlank()) {
                  vehicleNumber = plate
                }
              },
              label = { Text(drv.name, fontSize = 11.sp) }
            )
          }
        }
      }

      if (availableVehicles.isNotEmpty()) {
        Text("Quick-Assign Fleet Vehicle:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          availableVehicles.take(4).forEach { veh ->
            FilterChip(
              selected = vehicleNumber == veh.registrationNumber,
              onClick = {
                vehicleNumber = veh.registrationNumber
                category = veh.category
              },
              label = { Text("${veh.registrationNumber} (${veh.modelName.take(12)})", fontSize = 11.sp) }
            )
          }
        }
      }

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = driverName,
          onValueChange = { driverName = it },
          label = "Assigned Driver Name",
          placeholder = "e.g., Ramesh Kumar",
          icon = Icons.Default.Badge,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = vehicleNumber,
          onValueChange = { vehicleNumber = it.uppercase() },
          label = "Vehicle Number Plate",
          placeholder = "e.g., KA-01-MG-4590",
          icon = Icons.Default.DirectionsCar,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Section 4: Fare, GST & Payment Channel
    FormSectionCard(
      stepNumber = "4",
      sectionTitle = "Tariff, GST ($defaultGstPercent%) & Trip Expenses",
      accentColor = KpiGreenIcon
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = baseAmountText,
          onValueChange = { baseAmountText = it },
          label = "Base Fare Amount (₹)",
          placeholder = "e.g., 6500",
          icon = Icons.Default.CurrencyRupee,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = expenseAmountText,
          onValueChange = { expenseAmountText = it },
          label = "Fuel / Toll Expenses (₹)",
          placeholder = "e.g., 1200",
          icon = Icons.Default.LocalGasStation,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = Modifier.weight(1f)) {
          Text("Payment Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            PAYMENT_STATUSES.forEach { st ->
              FilterChip(
                selected = paymentStatus == st,
                onClick = { paymentStatus = st },
                label = { Text(st, fontSize = 11.sp) }
              )
            }
          }
        }
        Column(modifier = Modifier.weight(1.4f)) {
          Text("Payment Channel", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
          FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            PAYMENT_CHANNELS.forEach { ch ->
              FilterChip(
                selected = paymentChannel == ch,
                onClick = { paymentChannel = ch },
                label = { Text(ch, fontSize = 11.sp) }
              )
            }
          }
        }
      }

      // Live Invoice & Margin Summary Card
      Surface(
        color = KpiGreenBg,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, KpiGreenIcon.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "LIVE GST INVOICE PREVIEW",
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              color = KpiGreenIcon
            )
            Text(
              text = "Base: ${formatInr(liveBase)} + GST ($defaultGstPercent%): ${formatInr(liveGst)}",
              fontSize = 12.sp,
              color = TextPrimaryDark
            )
            Text(
              text = "Est. Net Profit (after expenses): ${formatInr(liveNetMargin)}",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = KpiGreenIcon
            )
          }
          Text(
            text = formatInr(liveTotal),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimaryDark
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddDriverDialog(
  initialDriver: Driver? = null,
  availableVehicles: List<Vehicle> = emptyList(),
  onDismiss: () -> Unit,
  onConfirm: (
    name: String,
    phone: String,
    licenseNumber: String,
    assignedVehicle: String,
    vehicleCategory: String,
    status: String,
    tripsCompleted: Long,
    earningsTotal: Long,
  ) -> Unit,
) {
  val isEditMode = initialDriver != null
  var name by remember(initialDriver) { mutableStateOf(initialDriver?.name ?: "") }
  var phone by remember(initialDriver) { mutableStateOf(initialDriver?.phone ?: "") }
  var license by remember(initialDriver) { mutableStateOf(initialDriver?.licenseNumber ?: "") }
  var vehicle by remember(initialDriver) { mutableStateOf(initialDriver?.assignedVehicle ?: "") }
  var category by remember(initialDriver) { mutableStateOf(initialDriver?.vehicleCategory ?: "SUVs") }
  var status by remember(initialDriver) { mutableStateOf(initialDriver?.status ?: "Available") }
  var tripsText by remember(initialDriver) {
    mutableStateOf(if (initialDriver != null) initialDriver.tripsCompleted.toString() else "1")
  }
  var earningsText by remember(initialDriver) {
    mutableStateOf(if (initialDriver != null) initialDriver.earningsTotal.toString() else "2500")
  }

  CrmFormModalShell(
    badgeText = if (isEditMode) "UPDATE DRIVER RECORD" else "DRIVER ROSTER ONBOARDING",
    badgeBg = KpiPurpleBg,
    badgeFg = KpiPurpleIcon,
    title = if (isEditMode) "Edit Driver Roster Profile" else "Add Commercial Driver to Roster",
    subtitle = "Enter commercial badge details, mobile contact, assigned fleet vehicle, and duty status.",
    primaryButtonText = if (isEditMode) "Update Driver" else "Save Driver to Roster",
    primaryButtonColor = SidebarActiveBlue,
    onQuickFillSample = {
      name = "Manjunath Naik"
      phone = "+91 98450 55667"
      license = "KA01-2020-0054120"
      vehicle = "Toyota Innova Crysta (KA-01-MG-4590)"
      category = "SUVs"
      status = "Available"
      tripsText = "24"
      earningsText = "38500"
    },
    onClearFields = {
      name = ""
      phone = ""
      license = ""
      vehicle = ""
      tripsText = "0"
      earningsText = "0"
    },
    onDismiss = onDismiss,
    onConfirm = {
      onConfirm(
        name.ifBlank { "Manjunath Naik" },
        phone.ifBlank { "+91 98450 55667" },
        license.ifBlank { "KA01-2020-0054120" },
        vehicle.ifBlank { "Toyota Innova Crysta (KA-01-MG-4590)" },
        category,
        status,
        tripsText.toLongOrNull() ?: 1L,
        earningsText.toLongOrNull() ?: 2500L
      )
      onDismiss()
    }
  ) {
    FormSectionCard(
      stepNumber = "1",
      sectionTitle = "Driver Identity & Commercial Badge",
      accentColor = KpiPurpleIcon
    ) {
      FormFieldInput(
        value = name,
        onValueChange = { name = it },
        label = "Driver Full Name",
        placeholder = "e.g., Manjunath Naik",
        icon = Icons.Default.Person,
        modifier = Modifier.fillMaxWidth()
      )
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = phone,
          onValueChange = { phone = it },
          label = "Driver Mobile Number",
          placeholder = "+91 98450 55667",
          icon = Icons.Default.Phone,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = license,
          onValueChange = { license = it.uppercase() },
          label = "Commercial DL / Badge No.",
          placeholder = "KA01-2020-0054120",
          icon = Icons.Default.Badge,
          modifier = Modifier.weight(1f)
        )
      }
    }

    FormSectionCard(
      stepNumber = "2",
      sectionTitle = "Fleet Vehicle Assignment & Duty Status",
      accentColor = SidebarActiveBlue
    ) {
      if (availableVehicles.isNotEmpty()) {
        Text("Quick-Pick from Registered Fleet Vehicles:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          availableVehicles.take(4).forEach { v ->
            val label = "${v.modelName} (${v.registrationNumber})"
            FilterChip(
              selected = vehicle == label,
              onClick = {
                vehicle = label
                category = v.category
              },
              label = { Text(v.registrationNumber, fontSize = 11.sp) }
            )
          }
        }
      }

      FormFieldInput(
        value = vehicle,
        onValueChange = { vehicle = it },
        label = "Assigned Vehicle Make & Registration Plate",
        placeholder = "e.g., Toyota Innova Crysta (KA-01-MG-4590)",
        icon = Icons.Default.DirectionsCar,
        modifier = Modifier.fillMaxWidth()
      )

      Text("Vehicle Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
      FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        VEHICLE_CATEGORIES.forEach { cat ->
          FilterChip(
            selected = category == cat,
            onClick = { category = cat },
            label = { Text(cat, fontSize = 11.sp) }
          )
        }
      }

      Text("Current Duty Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DRIVER_STATUSES.forEach { st ->
          FilterChip(
            selected = status == st,
            onClick = { status = st },
            label = { Text(st, fontSize = 11.sp) }
          )
        }
      }
    }

    FormSectionCard(
      stepNumber = "3",
      sectionTitle = "Completed Trips & Cumulative Driver Earnings",
      accentColor = KpiGreenIcon
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = tripsText,
          onValueChange = { tripsText = it },
          label = "Completed Trips Count",
          placeholder = "e.g., 18",
          icon = Icons.Default.Route,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = earningsText,
          onValueChange = { earningsText = it },
          label = "Total Driver Earnings / Bata (₹)",
          placeholder = "e.g., 28500",
          icon = Icons.Default.CurrencyRupee,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddVehicleDialog(
  initialVehicle: Vehicle? = null,
  onDismiss: () -> Unit,
  onConfirm: (String, String, String, String, Long, String, String) -> Unit,
) {
  val isEditMode = initialVehicle != null
  var regNo by remember(initialVehicle) { mutableStateOf(initialVehicle?.registrationNumber ?: "") }
  var model by remember(initialVehicle) { mutableStateOf(initialVehicle?.modelName ?: "") }
  var category by remember(initialVehicle) { mutableStateOf(initialVehicle?.category ?: "SUVs") }
  var status by remember(initialVehicle) { mutableStateOf(initialVehicle?.status ?: "Available") }
  var costText by remember(initialVehicle) {
    mutableStateOf(if (initialVehicle != null) initialVehicle.maintenanceCost.toString() else "")
  }
  var nextServiceDate by remember(initialVehicle) {
    mutableStateOf(initialVehicle?.nextServiceDate?.ifBlank { "2026-12-28" } ?: "2026-12-28")
  }
  var notes by remember(initialVehicle) { mutableStateOf(initialVehicle?.serviceNotes ?: "") }

  CrmFormModalShell(
    badgeText = if (isEditMode) "UPDATE FLEET VEHICLE" else "FLEET & MAINTENANCE ENTRY",
    badgeBg = KpiOrangeBg,
    badgeFg = KpiOrangeIcon,
    title = if (isEditMode) "Edit Vehicle & Maintenance Log" else "Register Fleet Vehicle & Maintenance",
    subtitle = "Record vehicle registration plate, fleet category, workshop spend (for 18% GST ITC), and service schedule.",
    primaryButtonText = if (isEditMode) "Update Vehicle" else "Add Vehicle to Fleet",
    primaryButtonColor = SidebarActiveBlue,
    onQuickFillSample = {
      regNo = "KA-01-AB-7788"
      model = "Toyota Innova Hycross ZX Hybrid"
      category = "SUVs"
      status = "Available"
      costText = "8500"
      nextServiceDate = "2026-12-28"
      notes = "40,000 km synthetic oil service, brake pad replacement & wheel alignment"
    },
    onClearFields = {
      regNo = ""
      model = ""
      costText = ""
      notes = ""
    },
    onDismiss = onDismiss,
    onConfirm = {
      onConfirm(
        regNo.ifBlank { "KA-01-AB-7788" },
        model.ifBlank { "Toyota Innova Hycross ZX" },
        category,
        status,
        costText.toLongOrNull() ?: 5000L,
        nextServiceDate.ifBlank { "2026-12-28" },
        notes.ifBlank { "Full synthetic service & brake inspection" }
      )
      onDismiss()
    }
  ) {
    FormSectionCard(
      stepNumber = "1",
      sectionTitle = "Vehicle Registration & Fleet Classification",
      accentColor = KpiOrangeIcon
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = regNo,
          onValueChange = { regNo = it.uppercase() },
          label = "Registration Number Plate",
          placeholder = "e.g., KA-01-AB-7788",
          icon = Icons.Default.DirectionsCar,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = model,
          onValueChange = { model = it },
          label = "Vehicle Make & Model",
          placeholder = "e.g., Toyota Innova Hycross",
          icon = Icons.Default.Badge,
          modifier = Modifier.weight(1.2f)
        )
      }

      Text("Fleet Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
      FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        VEHICLE_CATEGORIES.forEach { cat ->
          FilterChip(
            selected = category == cat,
            onClick = { category = cat },
            label = { Text(cat, fontSize = 11.sp) }
          )
        }
      }

      Text("Operational Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        VEHICLE_STATUSES.forEach { st ->
          FilterChip(
            selected = status == st,
            onClick = { status = st },
            label = { Text(st, fontSize = 11.sp) }
          )
        }
      }
    }

    FormSectionCard(
      stepNumber = "2",
      sectionTitle = "Workshop Maintenance Spend & Next Service Schedule",
      accentColor = SidebarActiveBlue
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = costText,
          onValueChange = { costText = it },
          label = "Maintenance Cost (₹)",
          placeholder = "e.g., 8500",
          icon = Icons.Default.CurrencyRupee,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = nextServiceDate,
          onValueChange = { nextServiceDate = it },
          label = "Next Service Due (YYYY-MM-DD)",
          placeholder = "2026-12-28",
          icon = Icons.Default.CalendarToday,
          modifier = Modifier.weight(1f)
        )
      }
      FormFieldInput(
        value = notes,
        onValueChange = { notes = it },
        label = "Workshop Service & Inspection Notes",
        placeholder = "e.g., Full synthetic oil change, AC filter & brake inspection",
        icon = Icons.Default.Build,
        singleLine = false,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddLeadDialog(
  initialLead: Lead? = null,
  onDismiss: () -> Unit,
  onConfirm: (
    clientName: String,
    clientPhone: String,
    routeSummary: String,
    preferredCategory: String,
    quotedAmount: Long,
    source: String,
    status: String,
  ) -> Unit,
) {
  val isEditMode = initialLead != null
  var name by remember(initialLead) { mutableStateOf(initialLead?.clientName ?: "") }
  var phone by remember(initialLead) { mutableStateOf(initialLead?.clientPhone ?: "") }
  var route by remember(initialLead) { mutableStateOf(initialLead?.routeSummary ?: "") }
  var category by remember(initialLead) { mutableStateOf(initialLead?.preferredCategory ?: "Compact SUVs") }
  var quoteText by remember(initialLead) {
    mutableStateOf(if (initialLead != null) initialLead.quotedAmount.toString() else "")
  }
  var source by remember(initialLead) { mutableStateOf(initialLead?.source ?: "Direct Inquiry") }
  var status by remember(initialLead) { mutableStateOf(initialLead?.status ?: "New") }

  CrmFormModalShell(
    badgeText = if (isEditMode) "UPDATE TRAVEL LEAD" else "TRAVEL LEAD & QUOTATION",
    badgeBg = KpiBlueBg,
    badgeFg = KpiBlueIcon,
    title = if (isEditMode) "Edit Travel Lead & Quote" else "Create Travel Lead & Package Quote",
    subtitle = "Capture prospective customer details, tour itinerary, preferred fleet category, and package quote.",
    primaryButtonText = if (isEditMode) "Update Lead" else "Save Lead to Pipeline",
    primaryButtonColor = SidebarActiveBlue,
    onQuickFillSample = {
      name = "Arjun Reddy"
      phone = "+91 99801 22334"
      route = "Bengaluru to Coorg 3-Day Outstation Package"
      category = "Compact SUVs"
      quoteText = "14500"
      source = "WhatsApp"
      status = "Quoted"
    },
    onClearFields = {
      name = ""
      phone = ""
      route = ""
      quoteText = ""
    },
    onDismiss = onDismiss,
    onConfirm = {
      onConfirm(
        name.ifBlank { "Arjun Reddy" },
        phone.ifBlank { "+91 99801 22334" },
        route.ifBlank { "Bengaluru to Coorg 3-Day Package" },
        category,
        quoteText.toLongOrNull() ?: 12000L,
        source,
        status
      )
      onDismiss()
    }
  ) {
    FormSectionCard(
      stepNumber = "1",
      sectionTitle = "Prospective Traveler & Inquiry Source",
      accentColor = SidebarActiveBlue
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = name,
          onValueChange = { name = it },
          label = "Prospective Client Name",
          placeholder = "e.g., Arjun Reddy",
          icon = Icons.Default.Person,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = phone,
          onValueChange = { phone = it },
          label = "Client Mobile Number",
          placeholder = "+91 99801 22334",
          icon = Icons.Default.Phone,
          modifier = Modifier.weight(1f)
        )
      }

      Text("Lead Acquisition Channel", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
      FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        LEAD_SOURCES.forEach { src ->
          FilterChip(
            selected = source == src,
            onClick = { source = src },
            label = { Text(src, fontSize = 11.sp) }
          )
        }
      }
    }

    FormSectionCard(
      stepNumber = "2",
      sectionTitle = "Itinerary Route, Fleet Category & Quoted Tariff",
      accentColor = KpiGreenIcon
    ) {
      FormFieldInput(
        value = route,
        onValueChange = { route = it },
        label = "Pickup to Destination / Tour Package Summary",
        placeholder = "e.g., Bengaluru to Coorg 3-Day Package",
        icon = Icons.Default.Route,
        modifier = Modifier.fillMaxWidth()
      )

      Text("Preferred Fleet Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
      FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        VEHICLE_CATEGORIES.forEach { cat ->
          FilterChip(
            selected = category == cat,
            onClick = { category = cat },
            label = { Text(cat, fontSize = 11.sp) }
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        FormFieldInput(
          value = quoteText,
          onValueChange = { quoteText = it },
          label = "Quoted Package Price (₹)",
          placeholder = "e.g., 14500",
          icon = Icons.Default.CurrencyRupee,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
        Column(modifier = Modifier.weight(1.2f)) {
          Text("Pipeline Stage", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
          FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            LEAD_STATUSES.forEach { st ->
              FilterChip(
                selected = status == st,
                onClick = { status = st },
                label = { Text(st, fontSize = 11.sp) }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun AddCustomerDialog(
  initialCustomer: Customer? = null,
  onDismiss: () -> Unit,
  onConfirm: (
    name: String,
    phone: String,
    email: String,
    companyName: String,
    gstin: String,
    totalBookings: Long,
    totalBilled: Long,
    pendingBalance: Long,
  ) -> Unit,
) {
  val isEditMode = initialCustomer != null
  var company by remember(initialCustomer) { mutableStateOf(initialCustomer?.companyName ?: "") }
  var contact by remember(initialCustomer) { mutableStateOf(initialCustomer?.name ?: "") }
  var phone by remember(initialCustomer) { mutableStateOf(initialCustomer?.phone ?: "") }
  var email by remember(initialCustomer) { mutableStateOf(initialCustomer?.email ?: "") }
  var gstin by remember(initialCustomer) { mutableStateOf(initialCustomer?.gstin ?: "") }
  var bookingsCountText by remember(initialCustomer) {
    mutableStateOf(if (initialCustomer != null) initialCustomer.totalBookings.toString() else "1")
  }
  var billedText by remember(initialCustomer) {
    mutableStateOf(if (initialCustomer != null) initialCustomer.totalBilled.toString() else "")
  }
  var pendingText by remember(initialCustomer) {
    mutableStateOf(if (initialCustomer != null) initialCustomer.pendingBalance.toString() else "")
  }

  val liveBilled = billedText.toLongOrNull() ?: 0L
  val livePending = pendingText.toLongOrNull() ?: 0L
  val liveRealized = (liveBilled - livePending).coerceAtLeast(0L)

  CrmFormModalShell(
    badgeText = if (isEditMode) "UPDATE CORPORATE ACCOUNT" else "CORPORATE & RETAIL ACCOUNT",
    badgeBg = KpiTealBg,
    badgeFg = KpiTealIcon,
    title = if (isEditMode) "Edit Customer & GST Account" else "Register Corporate / Retail Account",
    subtitle = "Maintain B2B corporate travel accounts, GSTIN tax identifiers, and credit receivable balances.",
    primaryButtonText = if (isEditMode) "Update Account" else "Save Customer Account",
    primaryButtonColor = SidebarActiveBlue,
    onQuickFillSample = {
      company = "Synergy Tech India Pvt Ltd"
      contact = "Neha Gupta (Travel Desk)"
      phone = "+91 98201 44556"
      email = "traveldesk@synergytech.in"
      gstin = "29AABCC4512N1Z9"
      bookingsCountText = "6"
      billedText = "68000"
      pendingText = "12000"
    },
    onClearFields = {
      company = ""
      contact = ""
      phone = ""
      email = ""
      gstin = ""
      bookingsCountText = "1"
      billedText = ""
      pendingText = ""
    },
    onDismiss = onDismiss,
    onConfirm = {
      onConfirm(
        contact.ifBlank { "Neha Gupta" },
        phone.ifBlank { "+91 98201 44556" },
        email.ifBlank { "accounts@corporate.in" },
        company.ifBlank { "Synergy Tech India Ltd" },
        gstin.ifBlank { "29AABCC4512N1Z9" },
        bookingsCountText.toLongOrNull() ?: 1L,
        billedText.toLongOrNull() ?: 45000L,
        pendingText.toLongOrNull() ?: 0L
      )
      onDismiss()
    }
  ) {
    FormSectionCard(
      stepNumber = "1",
      sectionTitle = "Company Identity & GSTIN Tax Registration",
      accentColor = KpiTealIcon
    ) {
      FormFieldInput(
        value = company,
        onValueChange = { company = it },
        label = "Corporate / Company / Account Name",
        placeholder = "e.g., Synergy Tech India Pvt Ltd",
        icon = Icons.Default.Business,
        modifier = Modifier.fillMaxWidth()
      )
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = gstin,
          onValueChange = { gstin = it.uppercase() },
          label = "15-Digit Customer GSTIN",
          placeholder = "29AABCC4512N1Z9",
          icon = Icons.Default.ReceiptLong,
          modifier = Modifier.weight(1.2f)
        )
        FormFieldInput(
          value = bookingsCountText,
          onValueChange = { bookingsCountText = it },
          label = "Total Trips",
          placeholder = "e.g., 5",
          icon = Icons.Default.Route,
          isNumeric = true,
          modifier = Modifier.weight(0.8f)
        )
      }
    }

    FormSectionCard(
      stepNumber = "2",
      sectionTitle = "Primary Contact Person & Billing Email",
      accentColor = SidebarActiveBlue
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = contact,
          onValueChange = { contact = it },
          label = "Contact Person Name",
          placeholder = "e.g., Neha Gupta",
          icon = Icons.Default.Person,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = phone,
          onValueChange = { phone = it },
          label = "Mobile / Desk Phone",
          placeholder = "+91 98201 44556",
          icon = Icons.Default.Phone,
          modifier = Modifier.weight(1f)
        )
      }
      FormFieldInput(
        value = email,
        onValueChange = { email = it },
        label = "Billing & E-Invoice Email",
        placeholder = "e.g., billing@synergytech.in",
        icon = Icons.Default.Email,
        modifier = Modifier.fillMaxWidth()
      )
    }

    FormSectionCard(
      stepNumber = "3",
      sectionTitle = "Account Ledger & Credit Receivables",
      accentColor = KpiAmberIcon
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = billedText,
          onValueChange = { billedText = it },
          label = "Total Billed Amount (₹)",
          placeholder = "e.g., 68000",
          icon = Icons.Default.CurrencyRupee,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = pendingText,
          onValueChange = { pendingText = it },
          label = "Pending Receivable Due (₹)",
          placeholder = "e.g., 12000",
          icon = Icons.Default.AccountBalanceWallet,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
      }

      Surface(
        color = KpiBlueBg,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Realized Paid Amount:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
          Text(formatInr(liveRealized), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = KpiGreenIcon)
        }
      }
    }
  }
}

@Composable
fun TripExpenseEntryDialog(
  booking: Booking,
  onDismiss: () -> Unit,
  onConfirm: (newTotalExpense: Long, newDistanceKm: Long) -> Unit,
) {
  val initialFuel = (booking.expenseAmount * 55L) / 100L
  val initialToll = (booking.expenseAmount * 20L) / 100L
  val initialBata = (booking.expenseAmount * 15L) / 100L
  val initialPermit = (booking.expenseAmount - initialFuel - initialToll - initialBata).coerceAtLeast(0L)

  var fuelText by remember(booking) { mutableStateOf(initialFuel.toString()) }
  var tollText by remember(booking) { mutableStateOf(initialToll.toString()) }
  var driverBataText by remember(booking) { mutableStateOf(initialBata.toString()) }
  var permitParkingText by remember(booking) { mutableStateOf(initialPermit.toString()) }
  var odometerKmText by remember(booking) { mutableStateOf(booking.distanceKm.toString()) }

  val totalExpense = (fuelText.toLongOrNull() ?: 0L) +
    (tollText.toLongOrNull() ?: 0L) +
    (driverBataText.toLongOrNull() ?: 0L) +
    (permitParkingText.toLongOrNull() ?: 0L)
  val netProfit = booking.totalAmount - totalExpense - booking.gstAmount

  CrmFormModalShell(
    badgeText = "TRIP EXPENSE & ODOMETER LOG",
    badgeBg = KpiOrangeBg,
    badgeFg = KpiOrangeIcon,
    title = "Update Trip Expenses & Odometer",
    subtitle = "${booking.customerName} • ${booking.pickupLocation} → ${booking.dropLocation}",
    primaryButtonText = "Save Expense Breakdown",
    primaryButtonColor = NewBookingGreen,
    onQuickFillSample = {
      fuelText = "950"
      tollText = "320"
      driverBataText = "400"
      permitParkingText = "150"
      odometerKmText = "165"
    },
    onClearFields = {
      fuelText = "0"
      tollText = "0"
      driverBataText = "0"
      permitParkingText = "0"
    },
    onDismiss = onDismiss,
    onConfirm = {
      onConfirm(
        totalExpense,
        odometerKmText.toLongOrNull() ?: booking.distanceKm
      )
      onDismiss()
    }
  ) {
    FormSectionCard(
      stepNumber = "1",
      sectionTitle = "Itemized Trip Operating Expenses (₹)",
      accentColor = KpiOrangeIcon
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = fuelText,
          onValueChange = { fuelText = it },
          label = "Diesel / Fuel Cost (₹)",
          placeholder = "e.g., 950",
          icon = Icons.Default.LocalGasStation,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = tollText,
          onValueChange = { tollText = it },
          label = "FASTag / Highway Toll (₹)",
          placeholder = "e.g., 320",
          icon = Icons.Default.Route,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
      }
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = driverBataText,
          onValueChange = { driverBataText = it },
          label = "Driver Bata / Allowance (₹)",
          placeholder = "e.g., 400",
          icon = Icons.Default.Badge,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = permitParkingText,
          onValueChange = { permitParkingText = it },
          label = "State Permit & Parking (₹)",
          placeholder = "e.g., 150",
          icon = Icons.Default.ReceiptLong,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
      }
    }

    FormSectionCard(
      stepNumber = "2",
      sectionTitle = "Logged Odometer Distance & Profitability",
      accentColor = KpiGreenIcon
    ) {
      FormFieldInput(
        value = odometerKmText,
        onValueChange = { odometerKmText = it },
        label = "Total Logged Odometer Distance (km)",
        placeholder = "e.g., 165",
        icon = Icons.Default.Speed,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )

      Surface(
        color = KpiGreenBg,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, KpiGreenIcon.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Total Billed: ${formatInr(booking.totalAmount)}", fontSize = 12.sp, color = TextPrimaryDark)
            Text("Total Trip Expenses: ${formatInr(totalExpense)}", fontSize = 12.sp, color = KpiOrangeIcon, fontWeight = FontWeight.Bold)
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("NET PROFIT", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = KpiGreenIcon)
            Text(formatInr(netProfit), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = KpiGreenIcon)
          }
        }
      }
    }
  }
}

@Composable
fun KpiDetailsDialog(
  spec: KpiCardSpec,
  onDismiss: () -> Unit,
  onOpenSection: (CrmSection) -> Unit,
) {
  CrmFormModalShell(
    badgeText = "LIVE TELEMETRY BREAKDOWN",
    badgeBg = spec.iconBg,
    badgeFg = spec.iconTint,
    title = spec.title.replace("\n", " "),
    subtitle = "Synchronized in real time with Cloud Firestore.",
    primaryButtonText = "Open ${spec.targetSection.title}",
    primaryButtonColor = SidebarActiveBlue,
    onDismiss = onDismiss,
    onConfirm = {
      onDismiss()
      onOpenSection(spec.targetSection)
    }
  ) {
    Surface(
      color = spec.iconBg,
      shape = RoundedCornerShape(14.dp),
      border = BorderStroke(1.dp, spec.iconTint.copy(alpha = 0.3f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Text("CURRENT LIVE METRIC", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = spec.iconTint)
        Spacer(modifier = Modifier.height(6.dp))
        Text(spec.valueText, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimaryDark)
      }
    }
    Text(
      text = "Tap 'Open ${spec.targetSection.title}' below to inspect individual records, create or edit entries, or export audit-ready reports.",
      style = MaterialTheme.typography.bodyMedium,
      color = TextSecondarySlate
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomerWebsitePreviewDialog(
  agencyName: String,
  onDismiss: () -> Unit,
  onSubmitWebLead: (String, String, String, String, Long) -> Unit,
) {
  var visitorName by remember { mutableStateOf("") }
  var visitorPhone by remember { mutableStateOf("") }
  var pickupDrop by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("SUVs") }
  var budgetText by remember { mutableStateOf("8500") }

  CrmFormModalShell(
    badgeText = "LIVE CUSTOMER BOOKING PORTAL",
    badgeBg = KpiGreenBg,
    badgeFg = NewBookingGreen,
    title = agencyName,
    subtitle = "Customer web inquiries submitted here flow directly into your Admin Command Center Leads pipeline.",
    primaryButtonText = "Send Web Booking Inquiry",
    primaryButtonColor = NewBookingGreen,
    onQuickFillSample = {
      visitorName = "Vikramaditya Rao"
      visitorPhone = "+91 98450 99001"
      pickupDrop = "Bengaluru Airport T2 to Mysuru Luxury Resort"
      selectedCategory = "SUVs"
      budgetText = "9500"
    },
    onClearFields = {
      visitorName = ""
      visitorPhone = ""
      pickupDrop = ""
      budgetText = ""
    },
    onDismiss = onDismiss,
    onConfirm = {
      onSubmitWebLead(
        visitorName.ifBlank { "Website Visitor" },
        visitorPhone.ifBlank { "+91 98450 99001" },
        pickupDrop.ifBlank { "Bengaluru Airport to Mysuru Resort" },
        selectedCategory,
        budgetText.toLongOrNull() ?: 8500L
      )
      onDismiss()
    }
  ) {
    FormSectionCard(
      stepNumber = "1",
      sectionTitle = "Online Traveler Inquiry Form",
      accentColor = NewBookingGreen
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = visitorName,
          onValueChange = { visitorName = it },
          label = "Your Full Name",
          placeholder = "e.g., Vikramaditya Rao",
          icon = Icons.Default.Person,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = visitorPhone,
          onValueChange = { visitorPhone = it },
          label = "Mobile Number",
          placeholder = "+91 98450 99001",
          icon = Icons.Default.Phone,
          modifier = Modifier.weight(1f)
        )
      }
      FormFieldInput(
        value = pickupDrop,
        onValueChange = { pickupDrop = it },
        label = "Pickup & Destination Route",
        placeholder = "e.g., Bengaluru Airport to Mysuru Resort",
        icon = Icons.Default.Route,
        modifier = Modifier.fillMaxWidth()
      )
      FormFieldInput(
        value = budgetText,
        onValueChange = { budgetText = it },
        label = "Estimated Fare / Package Quote (₹)",
        placeholder = "e.g., 8500",
        icon = Icons.Default.CurrencyRupee,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )
      Text("Select Preferred Vehicle Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
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
  }
}
