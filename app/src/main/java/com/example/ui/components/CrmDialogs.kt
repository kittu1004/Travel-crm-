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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.example.data.formatToDdMmYyyy
import com.example.ui.formatInr
import com.example.ui.theme.CanvasBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val VEHICLE_CATEGORIES = listOf("Sedans", "Compact SUVs", "SUVs", "Tourist Travellers")
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
        // Dark Modal Header (Auto-Fill Sample option removed)
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

/**
 * Reusable Select / Dropdown field (<select> tag equivalent) using Material 3 ExposedDropdownMenuBox.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormSelectDropdown(
  selectedValue: String,
  options: List<String>,
  onValueSelected: (String) -> Unit,
  label: String,
  icon: ImageVector,
  modifier: Modifier = Modifier,
  testTagName: String = "",
) {
  var expanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = { expanded = !expanded },
    modifier = modifier
  ) {
    OutlinedTextField(
      value = selectedValue,
      onValueChange = {},
      readOnly = true,
      singleLine = true,
      label = { Text(label, fontSize = 12.sp) },
      leadingIcon = {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = SidebarActiveBlue,
          modifier = Modifier.size(18.dp)
        )
      },
      trailingIcon = {
        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
      },
      shape = RoundedCornerShape(10.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SidebarActiveBlue,
        unfocusedBorderColor = CardBorder,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = CanvasBackground.copy(alpha = 0.5f)
      ),
      modifier = Modifier
        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        .fillMaxWidth()
        .then(if (testTagName.isNotBlank()) Modifier.testTag(testTagName) else Modifier)
    )

    ExposedDropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      containerColor = Color.White
    ) {
      options.forEach { option ->
        DropdownMenuItem(
          text = {
            Text(
              text = option,
              fontSize = 13.sp,
              fontWeight = if (option == selectedValue) FontWeight.ExtraBold else FontWeight.Medium,
              color = if (option == selectedValue) SidebarActiveBlue else TextPrimaryDark
            )
          },
          onClick = {
            onValueSelected(option)
            expanded = false
          }
        )
      }
    }
  }
}

/**
 * Reusable Date Field with an interactive Calendar Picker popup (DatePickerDialog)
 * formatting dates as DD-MM-YYYY (date-month-year).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormCalendarDateField(
  dateValue: String,
  onDateChange: (String) -> Unit,
  label: String = "Date (DD-MM-YYYY)",
  modifier: Modifier = Modifier,
) {
  var showCalendarDialog by remember { mutableStateOf(false) }

  Box(modifier = modifier) {
    OutlinedTextField(
      value = dateValue,
      onValueChange = { onDateChange(it) },
      label = { Text(label, fontSize = 12.sp) },
      placeholder = { Text("DD-MM-YYYY", fontSize = 12.sp, color = TextSecondarySlate.copy(alpha = 0.65f)) },
      leadingIcon = {
        IconButton(
          onClick = { showCalendarDialog = true },
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "Open Calendar",
            tint = SidebarActiveBlue,
            modifier = Modifier.size(18.dp)
          )
        }
      },
      trailingIcon = {
        TextButton(
          onClick = { showCalendarDialog = true },
          contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "Pick Date from Calendar",
            tint = SidebarActiveBlue,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text("Calendar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SidebarActiveBlue)
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(10.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SidebarActiveBlue,
        unfocusedBorderColor = CardBorder,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = CanvasBackground.copy(alpha = 0.5f)
      ),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("calendar_date_input_field")
    )
  }

  if (showCalendarDialog) {
    val initialMillis = remember(dateValue) {
      runCatching {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US).apply {
          timeZone = TimeZone.getTimeZone("UTC")
        }
        sdf.parse(formatToDdMmYyyy(dateValue))?.time
      }.getOrNull() ?: System.currentTimeMillis()
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    DatePickerDialog(
      onDismissRequest = { showCalendarDialog = false },
      confirmButton = {
        Button(
          onClick = {
            val selectedMillis = datePickerState.selectedDateMillis
            if (selectedMillis != null) {
              val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
              }
              onDateChange(sdf.format(Date(selectedMillis)))
            }
            showCalendarDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue)
        ) {
          Text("Select Date", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showCalendarDialog = false }) {
          Text("Cancel", color = TextSecondarySlate, fontWeight = FontWeight.SemiBold)
        }
      }
    ) {
      DatePicker(state = datePickerState)
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewBookingDialog(
  defaultDate: String,
  initialBooking: Booking? = null,
  isInvoiceForm: Boolean = false,
  availableDrivers: List<Driver> = emptyList(),
  availableVehicles: List<Vehicle> = emptyList(),
  availableCustomers: List<Customer> = emptyList(),
  defaultGstPercent: Int = 0,
  onDismiss: () -> Unit,
  onConfirm: (
    customerName: String,
    companyName: String,
    customerPhone: String,
    pickup: String,
    drop: String,
    category: String,
    vehicleNumber: String,
    driverName: String,
    driverPhone: String,
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
  ) -> Unit,
) {
  val isEditMode = initialBooking != null
  val normalizedDefaultDate = formatToDdMmYyyy(defaultDate)

  var customerName by remember(initialBooking) { mutableStateOf(initialBooking?.customerName ?: "") }
  var companyName by remember(initialBooking) { mutableStateOf(initialBooking?.companyName ?: "") }
  var customerPhone by remember(initialBooking) { mutableStateOf(initialBooking?.customerPhone ?: "") }
  var pickup by remember(initialBooking) { mutableStateOf(initialBooking?.pickupLocation ?: "") }
  var drop by remember(initialBooking) { mutableStateOf(initialBooking?.dropLocation ?: "") }
  var category by remember(initialBooking) { mutableStateOf(initialBooking?.vehicleCategory ?: "SUVs") }
  var vehicleNumber by remember(initialBooking) { mutableStateOf(initialBooking?.vehicleNumber ?: "") }
  var driverName by remember(initialBooking) { mutableStateOf(initialBooking?.driverName ?: "") }
  var driverPhone by remember(initialBooking) { mutableStateOf(initialBooking?.driverPhone ?: "") }
  val tripStatus = initialBooking?.tripStatus ?: "Active"
  var paymentStatus by remember(initialBooking) { mutableStateOf(initialBooking?.paymentStatus ?: "Paid") }
  var paymentChannel by remember(initialBooking) { mutableStateOf(initialBooking?.paymentChannel ?: "UPI") }
  var baseAmountText by remember(initialBooking) {
    mutableStateOf(if (initialBooking != null) initialBooking.baseAmount.toString() else "")
  }
  var tollChargesText by remember(initialBooking) {
    mutableStateOf(if (initialBooking != null && initialBooking.tollCharges > 0L) initialBooking.tollCharges.toString() else "")
  }
  var parkingChargesText by remember(initialBooking) {
    mutableStateOf(if (initialBooking != null && initialBooking.parkingCharges > 0L) initialBooking.parkingCharges.toString() else "")
  }
  var includeOptionalGst by remember(initialBooking) {
    mutableStateOf((initialBooking?.gstAmount ?: 0L) > 0L)
  }
  var selectedGstSlab by remember(initialBooking, defaultGstPercent) {
    val inferredSlab = if (initialBooking != null && initialBooking.baseAmount > 0L && initialBooking.gstAmount > 0L) {
      ((initialBooking.gstAmount * 100L) / initialBooking.baseAmount).toInt()
    } else if (defaultGstPercent > 0) {
      defaultGstPercent
    } else {
      5
    }
    mutableStateOf(if (inferredSlab in listOf(5, 12, 18)) inferredSlab else 5)
  }
  var distanceText by remember(initialBooking) {
    mutableStateOf(if (initialBooking != null) initialBooking.distanceKm.toString() else "")
  }
  var tripDate by remember(initialBooking) {
    val raw = initialBooking?.tripDate?.ifBlank { normalizedDefaultDate } ?: normalizedDefaultDate
    mutableStateOf(formatToDdMmYyyy(raw))
  }

  val liveBase = baseAmountText.toLongOrNull() ?: 0L
  val liveToll = tollChargesText.toLongOrNull() ?: 0L
  val liveParking = parkingChargesText.toLongOrNull() ?: 0L
  val liveGst = if (includeOptionalGst) (liveBase * selectedGstSlab) / 100L else 0L
  val liveTotal = liveBase + liveToll + liveParking + liveGst

  CrmFormModalShell(
    badgeText = when {
      isInvoiceForm && isEditMode -> "EDIT INVOICE DETAILS"
      isInvoiceForm -> "INVOICE FORM"
      isEditMode -> "UPDATE BOOKING DETAILS"
      else -> "BOOKING ENTRY FORM"
    },
    badgeBg = KpiGreenBg,
    badgeFg = NewBookingGreen,
    title = when {
      isInvoiceForm && isEditMode -> "Edit Invoice Details"
      isInvoiceForm -> "Invoice Form"
      isEditMode -> "Edit Booking Details"
      else -> "New Booking Form"
    },
    subtitle = "Enter customer & company name, DD-MM-YYYY calendar date, route, driver details, and billing.",
    primaryButtonText = when {
      isInvoiceForm -> "Save Invoice & Generate PDF"
      isEditMode -> "Update Booking & Invoice"
      else -> "Save Booking & Generate PDF"
    },
    primaryButtonColor = NewBookingGreen,
    onClearFields = {
      customerName = ""
      companyName = ""
      customerPhone = ""
      pickup = ""
      drop = ""
      vehicleNumber = ""
      driverName = ""
      driverPhone = ""
      baseAmountText = ""
      tollChargesText = ""
      parkingChargesText = ""
      includeOptionalGst = false
      distanceText = ""
    },
    onDismiss = onDismiss,
    onConfirm = {
      val resolvedBase = baseAmountText.toLongOrNull() ?: 4500L
      val resolvedToll = tollChargesText.toLongOrNull() ?: 0L
      val resolvedParking = parkingChargesText.toLongOrNull() ?: 0L
      val resolvedGst = if (includeOptionalGst) (resolvedBase * selectedGstSlab) / 100L else 0L
      onConfirm(
        customerName.ifBlank { "Rajesh Sharma" },
        companyName,
        customerPhone.ifBlank { "+91 98450 11223" },
        pickup.ifBlank { "Bengaluru Airport T2" },
        drop.ifBlank { "Whitefield Tech Park" },
        category,
        vehicleNumber.ifBlank { "KA-01-MG-4590" },
        driverName.ifBlank { "Ramesh Kumar" },
        driverPhone.ifBlank { "+91 98450 11223" },
        tripStatus,
        paymentStatus,
        paymentChannel,
        resolvedBase,
        resolvedToll,
        resolvedParking,
        resolvedGst,
        initialBooking?.expenseAmount ?: 0L,
        distanceText.toLongOrNull() ?: 120L,
        formatToDdMmYyyy(tripDate.ifBlank { normalizedDefaultDate })
      )
      onDismiss()
    }
  ) {
    // Section 1: Customer Name, Company Name & Calendar Schedule
    FormSectionCard(
      stepNumber = "1",
      sectionTitle = "Customer Name, Company Name & Date",
      accentColor = SidebarActiveBlue
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = customerName,
          onValueChange = { customerName = it },
          label = "Customer Name",
          placeholder = "e.g., Rajesh Sharma",
          icon = Icons.Default.Person,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = companyName,
          onValueChange = { companyName = it },
          label = "Company Name",
          placeholder = "e.g., Wipro Ltd",
          icon = Icons.Default.Business,
          modifier = Modifier.weight(1f)
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = customerPhone,
          onValueChange = { customerPhone = it },
          label = "Contact Mobile",
          placeholder = "+91 98450 11223",
          icon = Icons.Default.Phone,
          modifier = Modifier.weight(1f)
        )
        FormCalendarDateField(
          dateValue = tripDate,
          onDateChange = { tripDate = it },
          label = "Date (DD-MM-YYYY)",
          modifier = Modifier.weight(1.2f)
        )
      }
    }

    // Section 2: Pickup, Drop Destination & Distance (Trip Dispatch Status Removed)
    FormSectionCard(
      stepNumber = "2",
      sectionTitle = "Pickup, Drop Destination & Distance",
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

      FormFieldInput(
        value = distanceText,
        onValueChange = { distanceText = it },
        label = "Est. Distance (km)",
        placeholder = "e.g., 140",
        icon = Icons.Default.Speed,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // Section 3: Driver Detail Section & Fleet Category Select Dropdown (Quick Assign Removed)
    FormSectionCard(
      stepNumber = "3",
      sectionTitle = "Driver Details & Fleet Category",
      accentColor = KpiPurpleIcon
    ) {
      FormSelectDropdown(
        selectedValue = category,
        options = VEHICLE_CATEGORIES,
        onValueSelected = { category = it },
        label = "Select Fleet Category",
        icon = Icons.Default.DirectionsCar,
        modifier = Modifier.fillMaxWidth(),
        testTagName = "select_fleet_category"
      )

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = driverName,
          onValueChange = { driverName = it },
          label = "Driver Name",
          placeholder = "e.g., Ramesh Kumar",
          icon = Icons.Default.Badge,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = driverPhone,
          onValueChange = { driverPhone = it },
          label = "Driver Phone Number",
          placeholder = "+91 98450 11223",
          icon = Icons.Default.Phone,
          modifier = Modifier.weight(1f)
        )
      }

      FormFieldInput(
        value = vehicleNumber,
        onValueChange = { vehicleNumber = it.uppercase() },
        label = "Vehicle Number Plate",
        placeholder = "e.g., KA-01-MG-4590",
        icon = Icons.Default.DirectionsCar,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // Section 4: Base Fare, Toll & Parking Charges, Optional GST & Payment Select Dropdowns (Internal Trip Expenses Removed)
    FormSectionCard(
      stepNumber = "4",
      sectionTitle = "Fare, Toll & Parking Charges, Optional GST & Payment",
      accentColor = KpiGreenIcon
    ) {
      FormFieldInput(
        value = baseAmountText,
        onValueChange = { baseAmountText = it },
        label = "Base Fare Amount (₹)",
        placeholder = "e.g., 6500",
        icon = Icons.Default.CurrencyRupee,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = tollChargesText,
          onValueChange = { tollChargesText = it },
          label = "Toll / FASTag Charges (₹)",
          placeholder = "0 (Optional)",
          icon = Icons.Default.Route,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = parkingChargesText,
          onValueChange = { parkingChargesText = it },
          label = "Parking / Entry Charges (₹)",
          placeholder = "0 (Optional)",
          icon = Icons.Default.LocationOn,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
      }

      // Optional GST Selection Row (OFF by default!)
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(CanvasBackground)
          .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
          .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = "GST Tax Option (Default: No GST / Optional)",
          fontSize = 11.sp,
          fontWeight = FontWeight.ExtraBold,
          color = TextPrimaryDark
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          FilterChip(
            selected = !includeOptionalGst,
            onClick = { includeOptionalGst = false },
            label = { Text("No GST (0%)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = KpiBlueBg,
              selectedLabelColor = SidebarActiveBlue
            )
          )
          listOf(5, 12, 18).forEach { slab ->
            FilterChip(
              selected = includeOptionalGst && selectedGstSlab == slab,
              onClick = {
                includeOptionalGst = true
                selectedGstSlab = slab
              },
              label = { Text("+ $slab% GST", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = KpiGreenBg,
                selectedLabelColor = KpiGreenIcon
              )
            )
          }
        }
      }

      // Select Dropdowns for Payment Status and Payment Channel
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormSelectDropdown(
          selectedValue = paymentStatus,
          options = PAYMENT_STATUSES,
          onValueSelected = { paymentStatus = it },
          label = "Select Payment Status",
          icon = Icons.Default.ReceiptLong,
          modifier = Modifier.weight(1f),
          testTagName = "select_payment_status"
        )
        FormSelectDropdown(
          selectedValue = paymentChannel,
          options = PAYMENT_CHANNELS,
          onValueSelected = { paymentChannel = it },
          label = "Select Payment Channel",
          icon = Icons.Default.Payment,
          modifier = Modifier.weight(1f),
          testTagName = "select_payment_channel"
        )
      }

      // Live Invoice Breakdown Preview Card (Internal Trip Expenses Removed)
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
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "LIVE INVOICE BREAKDOWN PREVIEW",
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              color = KpiGreenIcon
            )
            Text(
              text = "Base: ${formatInr(liveBase)} • Toll: ${formatInr(liveToll)} • Parking: ${formatInr(liveParking)}",
              fontSize = 12.sp,
              color = TextPrimaryDark
            )
            Text(
              text = if (includeOptionalGst) {
                "Optional GST ($selectedGstSlab%): ${formatInr(liveGst)}"
              } else {
                "GST: ₹0 (Not Applied)"
              },
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
    subtitle = "Enter commercial badge details, driver phone number, assigned fleet vehicle, and duty status.",
    primaryButtonText = if (isEditMode) "Update Driver" else "Save Driver to Roster",
    primaryButtonColor = SidebarActiveBlue,
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
          label = "Driver Phone Number",
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
      FormFieldInput(
        value = vehicle,
        onValueChange = { vehicle = it },
        label = "Assigned Vehicle Make & Registration Plate",
        placeholder = "e.g., Toyota Innova Crysta (KA-01-MG-4590)",
        icon = Icons.Default.DirectionsCar,
        modifier = Modifier.fillMaxWidth()
      )

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormSelectDropdown(
          selectedValue = category,
          options = VEHICLE_CATEGORIES,
          onValueSelected = { category = it },
          label = "Select Fleet Category",
          icon = Icons.Default.DirectionsCar,
          modifier = Modifier.weight(1f)
        )
        FormSelectDropdown(
          selectedValue = status,
          options = DRIVER_STATUSES,
          onValueSelected = { status = it },
          label = "Select Duty Status",
          icon = Icons.Default.Badge,
          modifier = Modifier.weight(1f)
        )
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
  onConfirm: (
    registrationNumber: String,
    modelName: String,
    manufacturingYear: String,
    category: String,
    status: String,
    maintenanceCost: Long,
    serviceDate: String,
    nextServiceDate: String,
    serviceKilometers: Long,
    presentRunningKilometers: Long,
    nextServiceKilometers: Long,
    insuranceDetails: String,
    fitnessDetails: String,
    pollutionDetails: String,
    serviceNotes: String,
  ) -> Unit,
) {
  val isEditMode = initialVehicle != null
  var selectedTab by remember { mutableStateOf(0) } // 0 = Add Vehicle Section, 1 = Workshop Maintenance

  var model by remember(initialVehicle) { mutableStateOf(initialVehicle?.modelName ?: "") }
  var regNo by remember(initialVehicle) { mutableStateOf(initialVehicle?.registrationNumber ?: "") }
  var manufacturingYear by remember(initialVehicle) {
    mutableStateOf(initialVehicle?.manufacturingYear?.ifBlank { "2024" } ?: "2024")
  }
  var category by remember(initialVehicle) { mutableStateOf(initialVehicle?.category ?: "SUVs") }
  val status = initialVehicle?.status ?: "Available"

  var serviceDate by remember(initialVehicle) {
    val raw = initialVehicle?.serviceDate?.ifBlank { "12-11-2026" } ?: "12-11-2026"
    mutableStateOf(formatToDdMmYyyy(raw))
  }
  var nextServiceDate by remember(initialVehicle) {
    val raw = initialVehicle?.nextServiceDate?.ifBlank { "28-12-2026" } ?: "28-12-2026"
    mutableStateOf(formatToDdMmYyyy(raw))
  }
  var serviceKmText by remember(initialVehicle) {
    mutableStateOf(
      if (initialVehicle != null && initialVehicle.serviceKilometers > 0L)
        initialVehicle.serviceKilometers.toString()
      else ""
    )
  }
  var presentRunningKmText by remember(initialVehicle) {
    mutableStateOf(
      if (initialVehicle != null && initialVehicle.presentRunningKilometers > 0L)
        initialVehicle.presentRunningKilometers.toString()
      else ""
    )
  }
  var nextServiceKmText by remember(initialVehicle) {
    mutableStateOf(
      if (initialVehicle != null && initialVehicle.nextServiceKilometers > 0L)
        initialVehicle.nextServiceKilometers.toString()
      else ""
    )
  }
  var costText by remember(initialVehicle) {
    mutableStateOf(if (initialVehicle != null) initialVehicle.maintenanceCost.toString() else "")
  }
  var insuranceDetails by remember(initialVehicle) { mutableStateOf(initialVehicle?.insuranceDetails ?: "") }
  var fitnessDetails by remember(initialVehicle) { mutableStateOf(initialVehicle?.fitnessDetails ?: "") }
  var pollutionDetails by remember(initialVehicle) { mutableStateOf(initialVehicle?.pollutionDetails ?: "") }
  var notes by remember(initialVehicle) { mutableStateOf(initialVehicle?.serviceNotes ?: "") }

  CrmFormModalShell(
    badgeText = if (isEditMode) "UPDATE FLEET VEHICLE" else "VEHICLES MAINTENANCE",
    badgeBg = KpiOrangeBg,
    badgeFg = KpiOrangeIcon,
    title = if (isEditMode) "Edit Vehicle & Workshop Maintenance" else "Add Vehicle & Workshop Maintenance",
    subtitle = "Switch between Add Vehicle Section and Workshop Maintenance tabs below.",
    primaryButtonText = if (isEditMode) "Update Vehicle" else "Save Vehicle & Maintenance",
    primaryButtonColor = SidebarActiveBlue,
    onClearFields = {
      model = ""
      regNo = ""
      manufacturingYear = ""
      serviceKmText = ""
      presentRunningKmText = ""
      nextServiceKmText = ""
      costText = ""
      insuranceDetails = ""
      fitnessDetails = ""
      pollutionDetails = ""
      notes = ""
    },
    onDismiss = onDismiss,
    onConfirm = {
      onConfirm(
        regNo.ifBlank { "KA-01-AB-7788" },
        model.ifBlank { "Toyota Innova Hycross ZX" },
        manufacturingYear.ifBlank { "2024" },
        category,
        status,
        costText.toLongOrNull() ?: 5000L,
        formatToDdMmYyyy(serviceDate.ifBlank { "12-11-2026" }),
        formatToDdMmYyyy(nextServiceDate.ifBlank { "28-12-2026" }),
        serviceKmText.toLongOrNull() ?: 40000L,
        presentRunningKmText.toLongOrNull() ?: 44500L,
        nextServiceKmText.toLongOrNull() ?: 50000L,
        insuranceDetails.ifBlank { "Comprehensive Active" },
        fitnessDetails.ifBlank { "FC Valid" },
        pollutionDetails.ifBlank { "PUC Valid" },
        notes.ifBlank { "Full synthetic service & brake inspection" }
      )
      onDismiss()
    }
  ) {
    // Tabs selector: Add Vehicle Section | Workshop Maintenance
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(CanvasBackground)
        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
        .padding(4.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      val tabs = listOf("Add Vehicle Section", "Workshop Maintenance")
      tabs.forEachIndexed { index, tabTitle ->
        val isSelected = selectedTab == index
        Surface(
          color = if (isSelected) SidebarActiveBlue else Color.Transparent,
          shape = RoundedCornerShape(9.dp),
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(9.dp))
            .clickable { selectedTab = index }
            .testTag("vehicle_dialog_tab_$index")
        ) {
          Box(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = tabTitle,
              fontSize = 12.sp,
              fontWeight = FontWeight.ExtraBold,
              color = if (isSelected) Color.White else TextPrimaryDark
            )
          }
        }
      }
    }

    if (selectedTab == 0) {
      FormSectionCard(
        stepNumber = "1",
        sectionTitle = "Add Vehicle Section",
        accentColor = KpiOrangeIcon
      ) {
        FormFieldInput(
          value = model,
          onValueChange = { model = it },
          label = "Vehicle Name, Make and Model",
          placeholder = "e.g., Toyota Innova Hycross ZX",
          icon = Icons.Default.DirectionsCar,
          modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          FormFieldInput(
            value = regNo,
            onValueChange = { regNo = it.uppercase() },
            label = "Registration Number Plate",
            placeholder = "e.g., KA-01-AB-7788",
            icon = Icons.Default.Badge,
            modifier = Modifier.weight(1f)
          )
          FormFieldInput(
            value = manufacturingYear,
            onValueChange = { manufacturingYear = it },
            label = "Manufacturing Year",
            placeholder = "e.g., 2024",
            icon = Icons.Default.CalendarMonth,
            isNumeric = true,
            modifier = Modifier.weight(1f)
          )
        }

        FormSelectDropdown(
          selectedValue = category,
          options = VEHICLE_CATEGORIES,
          onValueSelected = { category = it },
          label = "Select Fleet Category",
          icon = Icons.Default.DirectionsCar,
          modifier = Modifier.fillMaxWidth(),
          testTagName = "vehicle_select_fleet_category"
        )
      }
    } else {
      FormSectionCard(
        stepNumber = "2",
        sectionTitle = "Workshop Maintenance",
        accentColor = SidebarActiveBlue
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          FormCalendarDateField(
            dateValue = serviceDate,
            onDateChange = { serviceDate = it },
            label = "Service Date (DD-MM-YYYY)",
            modifier = Modifier.weight(1f)
          )
          FormCalendarDateField(
            dateValue = nextServiceDate,
            onDateChange = { nextServiceDate = it },
            label = "Next Service Due (DD-MM-YYYY)",
            modifier = Modifier.weight(1f)
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          FormFieldInput(
            value = serviceKmText,
            onValueChange = { serviceKmText = it },
            label = "Service Kilometers (km)",
            placeholder = "e.g., 40000",
            icon = Icons.Default.Speed,
            isNumeric = true,
            modifier = Modifier.weight(1f)
          )
          FormFieldInput(
            value = presentRunningKmText,
            onValueChange = { presentRunningKmText = it },
            label = "Present Running Kilometers (km)",
            placeholder = "e.g., 44500",
            icon = Icons.Default.Speed,
            isNumeric = true,
            modifier = Modifier.weight(1f)
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          FormFieldInput(
            value = nextServiceKmText,
            onValueChange = { nextServiceKmText = it },
            label = "Next Service Kilometers (km)",
            placeholder = "e.g., 50000",
            icon = Icons.Default.Speed,
            isNumeric = true,
            modifier = Modifier.weight(1f)
          )
          FormFieldInput(
            value = costText,
            onValueChange = { costText = it },
            label = "Maintenance Cost (₹)",
            placeholder = "e.g., 8500",
            icon = Icons.Default.CurrencyRupee,
            isNumeric = true,
            modifier = Modifier.weight(1f)
          )
        }

        FormFieldInput(
          value = insuranceDetails,
          onValueChange = { insuranceDetails = it },
          label = "Insurance Details",
          placeholder = "e.g., ICICI Lombard Comprehensive Valid till 15-11-2027",
          icon = Icons.Default.ReceiptLong,
          modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          FormFieldInput(
            value = fitnessDetails,
            onValueChange = { fitnessDetails = it },
            label = "Fitness Details",
            placeholder = "e.g., RTO FC Valid till 2028",
            icon = Icons.Default.Badge,
            modifier = Modifier.weight(1f)
          )
          FormFieldInput(
            value = pollutionDetails,
            onValueChange = { pollutionDetails = it },
            label = "Pollution Details",
            placeholder = "e.g., PUC Valid till 06-2027",
            icon = Icons.Default.Build,
            modifier = Modifier.weight(1f)
          )
        }

        FormFieldInput(
          value = notes,
          onValueChange = { notes = it },
          label = "Next Service Notes",
          placeholder = "e.g., Full synthetic oil change, AC filter & brake pad inspection",
          icon = Icons.Default.Build,
          singleLine = false,
          modifier = Modifier.fillMaxWidth()
        )
      }
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
  var followUpDate by remember {
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    mutableStateOf(sdf.format(Date()))
  }

  CrmFormModalShell(
    badgeText = if (isEditMode) "UPDATE TRAVEL LEAD" else "TRAVEL LEAD & QUOTATION",
    badgeBg = KpiBlueBg,
    badgeFg = KpiBlueIcon,
    title = if (isEditMode) "Edit Travel Lead & Quote" else "Create Travel Lead & Package Quote",
    subtitle = "Capture prospective customer details, tour itinerary, preferred fleet category, and package quote.",
    primaryButtonText = if (isEditMode) "Update Lead" else "Save Lead to Pipeline",
    primaryButtonColor = SidebarActiveBlue,
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
      sectionTitle = "Prospective Traveler & Inquiry Date",
      accentColor = SidebarActiveBlue
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = name,
          onValueChange = { name = it },
          label = "Customer Name",
          placeholder = "e.g., Arjun Reddy",
          icon = Icons.Default.Person,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = phone,
          onValueChange = { phone = it },
          label = "Customer Mobile Number",
          placeholder = "+91 99801 22334",
          icon = Icons.Default.Phone,
          modifier = Modifier.weight(1f)
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormSelectDropdown(
          selectedValue = source,
          options = LEAD_SOURCES,
          onValueSelected = { source = it },
          label = "Select Lead Source",
          icon = Icons.Default.Business,
          modifier = Modifier.weight(1f)
        )
        FormCalendarDateField(
          dateValue = followUpDate,
          onDateChange = { followUpDate = it },
          label = "Travel Date (DD-MM-YYYY)",
          modifier = Modifier.weight(1.1f)
        )
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

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormSelectDropdown(
          selectedValue = category,
          options = VEHICLE_CATEGORIES,
          onValueSelected = { category = it },
          label = "Select Fleet Category",
          icon = Icons.Default.DirectionsCar,
          modifier = Modifier.weight(1f)
        )
        FormSelectDropdown(
          selectedValue = status,
          options = LEAD_STATUSES,
          onValueSelected = { status = it },
          label = "Select Lead Status",
          icon = Icons.Default.ReceiptLong,
          modifier = Modifier.weight(1f)
        )
      }

      FormFieldInput(
        value = quoteText,
        onValueChange = { quoteText = it },
        label = "Quoted Package Price (₹)",
        placeholder = "e.g., 14500",
        icon = Icons.Default.CurrencyRupee,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@Composable
fun AddCustomerDialog(
  initialCustomer: Customer? = null,
  onDismiss: () -> Unit,
  onConfirm: (
    name: String,
    address: String,
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
  var customerName by remember(initialCustomer) { mutableStateOf(initialCustomer?.name ?: "") }
  var address by remember(initialCustomer) {
    mutableStateOf(
      initialCustomer?.address?.ifBlank { initialCustomer.companyName } ?: ""
    )
  }
  var phone by remember(initialCustomer) { mutableStateOf(initialCustomer?.phone ?: "") }
  var bookingsCountText by remember(initialCustomer) {
    mutableStateOf(if (initialCustomer != null) initialCustomer.totalBookings.toString() else "1")
  }

  CrmFormModalShell(
    badgeText = if (isEditMode) "UPDATE CUSTOMER DATA" else "CUSTOMER DATA ENTRY",
    badgeBg = KpiTealBg,
    badgeFg = KpiTealIcon,
    title = if (isEditMode) "Edit Customer Details" else "Add Customer Details",
    subtitle = "Save customer name, address, mobile number, and total trips.",
    primaryButtonText = if (isEditMode) "Update Customer" else "Save Customer",
    primaryButtonColor = SidebarActiveBlue,
    onClearFields = {
      customerName = ""
      address = ""
      phone = ""
      bookingsCountText = "1"
    },
    onDismiss = onDismiss,
    onConfirm = {
      val cleanName = customerName.ifBlank { "Rajesh Sharma" }
      val cleanAddress = address.ifBlank { "Bengaluru, Karnataka" }
      onConfirm(
        cleanName,
        cleanAddress,
        phone.ifBlank { "+91 98450 11223" },
        initialCustomer?.email ?: "",
        initialCustomer?.companyName?.ifBlank { cleanAddress } ?: cleanAddress,
        initialCustomer?.gstin ?: "",
        bookingsCountText.toLongOrNull() ?: 1L,
        initialCustomer?.totalBilled ?: 0L,
        initialCustomer?.pendingBalance ?: 0L
      )
      onDismiss()
    }
  ) {
    FormSectionCard(
      stepNumber = "1",
      sectionTitle = "Customer Details (Saved from Booking History)",
      accentColor = KpiTealIcon
    ) {
      FormFieldInput(
        value = customerName,
        onValueChange = { customerName = it },
        label = "Customer Name",
        placeholder = "e.g., Rajesh Sharma",
        icon = Icons.Default.Person,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = address,
        onValueChange = { address = it },
        label = "Address",
        placeholder = "e.g., Whitefield Tech Park, Bengaluru",
        icon = Icons.Default.LocationOn,
        modifier = Modifier.fillMaxWidth()
      )

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormFieldInput(
          value = phone,
          onValueChange = { phone = it },
          label = "Mobile Number",
          placeholder = "+91 98450 11223",
          icon = Icons.Default.Phone,
          modifier = Modifier.weight(1.2f)
        )
        FormFieldInput(
          value = bookingsCountText,
          onValueChange = { bookingsCountText = it },
          label = "Total Trips",
          placeholder = "e.g., 3",
          icon = Icons.Default.Route,
          isNumeric = true,
          modifier = Modifier.weight(0.8f)
        )
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
  var expenseDate by remember(booking) { mutableStateOf(formatToDdMmYyyy(booking.tripDate)) }

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
      FormCalendarDateField(
        dateValue = expenseDate,
        onDateChange = { expenseDate = it },
        label = "Expense Date (DD-MM-YYYY)",
        modifier = Modifier.fillMaxWidth()
      )
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
  var travelDate by remember {
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    mutableStateOf(sdf.format(Date()))
  }

  CrmFormModalShell(
    badgeText = "LIVE CUSTOMER BOOKING PORTAL",
    badgeBg = KpiGreenBg,
    badgeFg = NewBookingGreen,
    title = agencyName,
    subtitle = "Customer web inquiries submitted here flow directly into your Dashboard View Leads pipeline.",
    primaryButtonText = "Send Web Booking Inquiry",
    primaryButtonColor = NewBookingGreen,
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
        pickupDrop.ifBlank { "Bengaluru Airport to Mysuru Resort ($travelDate)" },
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
          label = "Customer Name",
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
      FormCalendarDateField(
        dateValue = travelDate,
        onDateChange = { travelDate = it },
        label = "Preferred Travel Date (DD-MM-YYYY)",
        modifier = Modifier.fillMaxWidth()
      )
      FormFieldInput(
        value = pickupDrop,
        onValueChange = { pickupDrop = it },
        label = "Pickup & Destination Route",
        placeholder = "e.g., Bengaluru Airport to Mysuru Resort",
        icon = Icons.Default.Route,
        modifier = Modifier.fillMaxWidth()
      )
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FormSelectDropdown(
          selectedValue = selectedCategory,
          options = VEHICLE_CATEGORIES,
          onValueSelected = { selectedCategory = it },
          label = "Select Fleet Category",
          icon = Icons.Default.DirectionsCar,
          modifier = Modifier.weight(1f)
        )
        FormFieldInput(
          value = budgetText,
          onValueChange = { budgetText = it },
          label = "Estimated Fare (₹)",
          placeholder = "e.g., 8500",
          icon = Icons.Default.CurrencyRupee,
          isNumeric = true,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}
