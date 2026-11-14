package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.UploadFile
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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

private val VEHICLE_CATEGORIES = listOf("Sedan", "Compact SUVs", "SUVs", "Tourist Travellers")
private val PAYMENT_STATUSES = listOf("Paid", "Pending")
private val PAYMENT_CHANNELS = listOf("UPI", "Bank NEFT", "Cash", "Corporate Card")
private val DRIVER_STATUSES = listOf("Available", "On Trip", "Off Duty")
private val LEAD_SOURCES = listOf("Direct Inquiry", "Website", "WhatsApp", "Corporate Desk", "Phone Call")
private val LEAD_STATUSES = listOf("New", "Quoted", "Converted", "Lost")

private fun normalizeFleetCategory(raw: String?): String {
  return when {
    raw.isNullOrBlank() -> "Sedan"
    raw.equals("Sedans", ignoreCase = true) -> "Sedan"
    else -> raw
  }
}

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
      color = Color(0xFFEFF4FF),
      tonalElevation = 8.dp,
      shadowElevation = 18.dp,
      border = BorderStroke(1.dp, CardBorder),
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .widthIn(max = 640.dp)
        .heightIn(max = 760.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Vibrant Navy-to-Royal-Blue & Cyan-Purple Wave Modal Header matching Screenshot 2
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  Color(0xFF0A1D66),
                  Color(0xFF0052D4),
                  Color(0xFF0B8CF5),
                  Color(0xFF7C3AED)
                )
              )
            )
        ) {
          Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val headerWave = Path().apply {
              moveTo(0f, h * 0.58f)
              cubicTo(w * 0.34f, h * 0.92f, w * 0.68f, h * 0.25f, w, h * 0.52f)
              lineTo(w, h)
              lineTo(0f, h)
              close()
            }
            drawPath(
              path = headerWave,
              brush = Brush.horizontalGradient(
                colors = listOf(
                  Color(0xFF38BDF8).copy(alpha = 0.24f),
                  Color(0xFF60A5FA).copy(alpha = 0.16f),
                  Color(0xFFC084FC).copy(alpha = 0.32f)
                )
              )
            )
          }

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                color = Color(0xFF0B4CC9).copy(alpha = 0.72f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
              ) {
                Text(
                  text = badgeText,
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.ExtraBold,
                  letterSpacing = 0.7.sp,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
              }

              IconButton(
                onClick = onDismiss,
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.18f))
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Close Modal",
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
              )
              Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.32f),
                modifier = Modifier.size(34.dp)
              )
            }
          }
        }

        // Scrollable Structured Form Content on soft lavender-blue wave canvas
        Box(
          modifier = Modifier
            .weight(1f, fill = false)
            .fillMaxWidth()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color(0xFFEEF3FF),
                  Color(0xFFE7EFFD),
                  Color(0xFFF3EEFF)
                )
              )
            )
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .verticalScroll(rememberScrollState())
              .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            content()
          }
        }

        HorizontalDivider(color = CardBorder)

        // Sticky Action Footer with soft bottom wave tint
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color(0xFFF8FAFF),
                  Color(0xFFEDF2FF)
                )
              )
            )
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = onConfirm,
              colors = ButtonDefaults.buttonColors(containerColor = primaryButtonColor),
              shape = RoundedCornerShape(12.dp),
              contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("modal_confirm_submit_button")
            ) {
              Text(primaryButtonText, fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
            }

            OutlinedButton(
              onClick = onDismiss,
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, Color(0xFFC2D4F2)),
              colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Cancel", color = TextPrimaryDark, fontWeight = FontWeight.Bold)
            }

            if (onClearFields != null) {
              TextButton(
                onClick = onClearFields,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("Clear Form", color = SidebarActiveBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }
        }
      }
    }
  }
}

private fun sectionHeaderGradient(accentColor: Color): Brush {
  return when (accentColor) {
    KpiPurpleIcon -> Brush.horizontalGradient(
      colors = listOf(Color(0xFF5B21B6), Color(0xFF7C3AED), Color(0xFFA855F7))
    )
    KpiTealIcon -> Brush.horizontalGradient(
      colors = listOf(Color(0xFF0F766E), Color(0xFF0891B2), Color(0xFF06B6D4))
    )
    NewBookingGreen, KpiGreenIcon -> Brush.horizontalGradient(
      colors = listOf(Color(0xFF15803D), Color(0xFF16A34A), Color(0xFF22C55E))
    )
    KpiOrangeIcon -> Brush.horizontalGradient(
      colors = listOf(Color(0xFFC2410C), Color(0xFFEA580C), Color(0xFFF59E0B))
    )
    else -> Brush.horizontalGradient(
      colors = listOf(Color(0xFF0048C4), Color(0xFF0066EE), Color(0xFF2585FF))
    )
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
      .clip(RoundedCornerShape(16.dp))
      .background(CardSurface)
      .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
  ) {
    // Full-width colorful gradient header banner matching Screenshot 2
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(sectionHeaderGradient(accentColor))
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color.White),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = stepNumber,
            color = accentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = sectionTitle,
          fontSize = 13.5.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Color.White
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      content()
    }
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
    shape = RoundedCornerShape(12.dp),
    colors = OutlinedTextFieldDefaults.colors(
      focusedBorderColor = SidebarActiveBlue,
      unfocusedBorderColor = Color(0xFFCBD9F3),
      focusedContainerColor = Color.White,
      unfocusedContainerColor = Color(0xFFF6F9FF)
    ),
    modifier = modifier
  )
}

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
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SidebarActiveBlue,
        unfocusedBorderColor = Color(0xFFCBD9F3),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color(0xFFF6F9FF)
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
      singleLine = true,
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SidebarActiveBlue,
        unfocusedBorderColor = Color(0xFFCBD9F3),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color(0xFFF6F9FF)
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
    vehicleModel: String,
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
  var category by remember(initialBooking) {
    mutableStateOf(normalizeFleetCategory(initialBooking?.vehicleCategory))
  }
  var vehicleModel by remember(initialBooking) { mutableStateOf(initialBooking?.vehicleModel ?: "") }
  var vehicleNumber by remember(initialBooking) { mutableStateOf(initialBooking?.vehicleNumber ?: "") }
  var driverName by remember(initialBooking) { mutableStateOf(initialBooking?.driverName ?: "") }
  var driverPhone by remember(initialBooking) { mutableStateOf(initialBooking?.driverPhone ?: "") }
  val tripStatus = initialBooking?.tripStatus ?: "Active"
  var paymentStatus by remember(initialBooking) { mutableStateOf(initialBooking?.paymentStatus ?: "Paid") }
  var paymentChannel by remember(initialBooking) { mutableStateOf(initialBooking?.paymentChannel ?: "UPI") }
  var baseAmountText by remember(initialBooking) {
    mutableStateOf(if (initialBooking != null && initialBooking.baseAmount > 0L) initialBooking.baseAmount.toString() else "")
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
    mutableStateOf(if (initialBooking != null && initialBooking.distanceKm > 0L) initialBooking.distanceKm.toString() else "")
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
      isInvoiceForm -> "NEW INVOICE GENERATION"
      isEditMode -> "UPDATE BOOKING DETAILS"
      else -> "BOOKING ENTRY FORM"
    },
    badgeBg = KpiGreenBg,
    badgeFg = NewBookingGreen,
    title = when {
      isInvoiceForm && isEditMode -> "Edit Invoice Details"
      isInvoiceForm -> "Generate New Invoice"
      isEditMode -> "Edit Booking Details"
      else -> "New Booking Form"
    },
    subtitle = "All fields stacked vertically: customer, date, route, fleet category (default Sedan), vehicle model, driver, and billing.",
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
      category = "Sedan"
      vehicleModel = ""
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
      val resolvedBase = baseAmountText.toLongOrNull() ?: 0L
      val resolvedToll = tollChargesText.toLongOrNull() ?: 0L
      val resolvedParking = parkingChargesText.toLongOrNull() ?: 0L
      val resolvedGst = if (includeOptionalGst) (resolvedBase * selectedGstSlab) / 100L else 0L
      onConfirm(
        customerName.trim(),
        companyName.trim(),
        customerPhone.trim(),
        pickup.trim(),
        drop.trim(),
        category,
        vehicleModel.trim(),
        vehicleNumber.trim(),
        driverName.trim(),
        driverPhone.trim(),
        tripStatus,
        paymentStatus,
        paymentChannel,
        resolvedBase,
        resolvedToll,
        resolvedParking,
        resolvedGst,
        initialBooking?.expenseAmount ?: 0L,
        distanceText.toLongOrNull() ?: 0L,
        formatToDdMmYyyy(tripDate.ifBlank { normalizedDefaultDate })
      )
      onDismiss()
    }
  ) {
    // Section 1: Customer Name & Company Name (Stacked Up and Down)
    FormSectionCard(
      stepNumber = "1",
      sectionTitle = "Customer Name & Company Name",
      accentColor = SidebarActiveBlue
    ) {
      FormFieldInput(
        value = customerName,
        onValueChange = { customerName = it },
        label = "Customer Name",
        placeholder = "Enter customer name",
        icon = Icons.Default.Person,
        modifier = Modifier.fillMaxWidth()
      )
      FormFieldInput(
        value = companyName,
        onValueChange = { companyName = it },
        label = "Company Name",
        placeholder = "Enter company name",
        icon = Icons.Default.Business,
        modifier = Modifier.fillMaxWidth()
      )
      FormFieldInput(
        value = customerPhone,
        onValueChange = { customerPhone = it },
        label = "Contact Mobile",
        placeholder = "Enter customer mobile number",
        icon = Icons.Default.Phone,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // Section 2: Pickup & Drop Details (with Date, Pickup, Drop & Distance)
    FormSectionCard(
      stepNumber = "2",
      sectionTitle = "Pickup & Drop Details",
      accentColor = KpiTealIcon
    ) {
      FormCalendarDateField(
        dateValue = tripDate,
        onDateChange = { tripDate = it },
        label = "Date (DD-MM-YYYY)",
        modifier = Modifier.fillMaxWidth()
      )
      FormFieldInput(
        value = pickup,
        onValueChange = { pickup = it },
        label = "Pickup Location / Terminal / Hotel",
        placeholder = "Enter pickup location",
        icon = Icons.Default.LocationOn,
        modifier = Modifier.fillMaxWidth()
      )
      FormFieldInput(
        value = drop,
        onValueChange = { drop = it },
        label = "Drop Location / Outstation Destination",
        placeholder = "Enter drop location",
        icon = Icons.Default.Route,
        modifier = Modifier.fillMaxWidth()
      )
      FormFieldInput(
        value = distanceText,
        onValueChange = { distanceText = it },
        label = "Est. Distance (km)",
        placeholder = "Enter distance in km",
        icon = Icons.Default.Speed,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // Section 3: Fleet Category (Default Sedan), Vehicle Model & Driver Details (Stacked Up and Down)
    FormSectionCard(
      stepNumber = "3",
      sectionTitle = "Fleet Category, Vehicle Model & Driver Details",
      accentColor = KpiPurpleIcon
    ) {
      FormSelectDropdown(
        selectedValue = category,
        options = VEHICLE_CATEGORIES,
        onValueSelected = { category = it },
        label = "Select Fleet Category (Default: Sedan)",
        icon = Icons.Default.DirectionsCar,
        modifier = Modifier.fillMaxWidth(),
        testTagName = "select_fleet_category"
      )

      FormFieldInput(
        value = vehicleModel,
        onValueChange = { vehicleModel = it },
        label = "Vehicle Model",
        placeholder = "Enter vehicle model (e.g., Maruti Dzire / Toyota Innova)",
        icon = Icons.Default.DirectionsCar,
        modifier = Modifier.fillMaxWidth().testTag("booking_vehicle_model_input")
      )

      FormFieldInput(
        value = vehicleNumber,
        onValueChange = { vehicleNumber = it.uppercase() },
        label = "Vehicle Number Plate",
        placeholder = "Enter vehicle registration number",
        icon = Icons.Default.DirectionsCar,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = driverName,
        onValueChange = { driverName = it },
        label = "Driver Name",
        placeholder = "Enter driver name",
        icon = Icons.Default.Badge,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = driverPhone,
        onValueChange = { driverPhone = it },
        label = "Driver Phone Number",
        placeholder = "Enter driver phone number",
        icon = Icons.Default.Phone,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // Section 4: Base Fare, Toll & Parking Charges, Optional GST & Payment (Stacked Up and Down)
    FormSectionCard(
      stepNumber = "4",
      sectionTitle = "Fare, Toll & Parking Charges, Optional GST & Payment",
      accentColor = KpiGreenIcon
    ) {
      FormFieldInput(
        value = baseAmountText,
        onValueChange = { baseAmountText = it },
        label = "Base Fare Amount (₹)",
        placeholder = "Enter base fare amount",
        icon = Icons.Default.CurrencyRupee,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = tollChargesText,
        onValueChange = { tollChargesText = it },
        label = "Toll / FASTag Charges (₹)",
        placeholder = "0 (Optional)",
        icon = Icons.Default.Route,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = parkingChargesText,
        onValueChange = { parkingChargesText = it },
        label = "Parking / Entry Charges (₹)",
        placeholder = "0 (Optional)",
        icon = Icons.Default.LocationOn,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )

      // Optional GST Selection (Stacked Vertically Up and Down)
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
        FilterChip(
          selected = !includeOptionalGst,
          onClick = { includeOptionalGst = false },
          label = { Text("No GST (0%)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = KpiBlueBg,
            selectedLabelColor = SidebarActiveBlue
          ),
          modifier = Modifier.fillMaxWidth()
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
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      FormSelectDropdown(
        selectedValue = paymentStatus,
        options = PAYMENT_STATUSES,
        onValueSelected = { paymentStatus = it },
        label = "Select Payment Status",
        icon = Icons.Default.ReceiptLong,
        modifier = Modifier.fillMaxWidth(),
        testTagName = "select_payment_status"
      )

      FormSelectDropdown(
        selectedValue = paymentChannel,
        options = PAYMENT_CHANNELS,
        onValueSelected = { paymentChannel = it },
        label = "Select Payment Channel",
        icon = Icons.Default.Payment,
        modifier = Modifier.fillMaxWidth(),
        testTagName = "select_payment_channel"
      )

      // Live Invoice Breakdown Preview Card
      Surface(
        color = KpiGreenBg,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, KpiGreenIcon.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
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
          Text(
            text = "Total Amount: ${formatInr(liveTotal)}",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimaryDark
          )
        }
      }
    }
  }
}

@Composable
fun AddDriverDialog(
  initialDriver: Driver? = null,
  availableVehicles: List<Vehicle> = emptyList(),
  onDismiss: () -> Unit,
  onConfirm: (
    name: String,
    phone: String,
    address: String,
    licenseNumber: String,
    aadharNumber: String,
    selfiePhotoUri: String,
    documentsUploaded: String,
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
  var address by remember(initialDriver) { mutableStateOf(initialDriver?.address ?: "") }
  var license by remember(initialDriver) { mutableStateOf(initialDriver?.licenseNumber ?: "") }
  var aadharNumber by remember(initialDriver) { mutableStateOf(initialDriver?.aadharNumber ?: "") }
  var selfiePhotoUri by remember(initialDriver) { mutableStateOf(initialDriver?.selfiePhotoUri ?: "") }
  var documentsUploaded by remember(initialDriver) { mutableStateOf(initialDriver?.documentsUploaded ?: "") }
  var vehicle by remember(initialDriver) { mutableStateOf(initialDriver?.assignedVehicle ?: "") }
  var category by remember(initialDriver) {
    mutableStateOf(normalizeFleetCategory(initialDriver?.vehicleCategory))
  }
  var status by remember(initialDriver) { mutableStateOf(initialDriver?.status ?: "Available") }
  var tripsText by remember(initialDriver) {
    mutableStateOf(if (initialDriver != null && initialDriver.tripsCompleted > 0L) initialDriver.tripsCompleted.toString() else "0")
  }
  var earningsText by remember(initialDriver) {
    mutableStateOf(if (initialDriver != null && initialDriver.earningsTotal > 0L) initialDriver.earningsTotal.toString() else "0")
  }

  // Zero-permission Android Photo Picker for Driver Selfie Photo
  val selfiePhotoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      selfiePhotoUri = "Selfie Photo Uploaded (${uri.lastPathSegment ?: "photo.jpg"})"
    }
  }

  // Camera preview capture option for Driver Selfie Photo
  val selfieCameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap ->
    if (bitmap != null) {
      selfiePhotoUri = "Driver Selfie Captured (${bitmap.width}x${bitmap.height})"
    }
  }

  // Document Upload Launchers (Driving License, Aadhar Card, Additional Documents)
  val licenseDocLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
  ) { uri ->
    if (uri != null) {
      val item = "DL Document (${uri.lastPathSegment ?: "license.pdf"})"
      documentsUploaded = listOf(documentsUploaded, item).filter { it.isNotBlank() }.joinToString(", ")
    }
  }

  val aadharDocLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
  ) { uri ->
    if (uri != null) {
      val item = "Aadhar Document (${uri.lastPathSegment ?: "aadhar.pdf"})"
      documentsUploaded = listOf(documentsUploaded, item).filter { it.isNotBlank() }.joinToString(", ")
    }
  }

  val otherDocLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
  ) { uri ->
    if (uri != null) {
      val item = "Driver Document (${uri.lastPathSegment ?: "document.pdf"})"
      documentsUploaded = listOf(documentsUploaded, item).filter { it.isNotBlank() }.joinToString(", ")
    }
  }

  CrmFormModalShell(
    badgeText = if (isEditMode) "UPDATE DRIVER RECORD" else "DRIVER FORM",
    badgeBg = KpiPurpleBg,
    badgeFg = KpiPurpleIcon,
    title = if (isEditMode) "Edit Driver Details" else "Add Driver Details",
    subtitle = "Driver name, phone, address, DL number, Aadhar number, selfie photo, and document uploads.",
    primaryButtonText = if (isEditMode) "Update Driver" else "Save Driver",
    primaryButtonColor = SidebarActiveBlue,
    onClearFields = {
      name = ""
      phone = ""
      address = ""
      license = ""
      aadharNumber = ""
      selfiePhotoUri = ""
      documentsUploaded = ""
      vehicle = ""
      category = "Sedan"
      tripsText = "0"
      earningsText = "0"
    },
    onDismiss = onDismiss,
    onConfirm = {
      onConfirm(
        name.trim(),
        phone.trim(),
        address.trim(),
        license.trim(),
        aadharNumber.trim(),
        selfiePhotoUri.trim(),
        documentsUploaded.trim(),
        vehicle.trim(),
        category,
        status,
        tripsText.toLongOrNull() ?: 0L,
        earningsText.toLongOrNull() ?: 0L
      )
      onDismiss()
    }
  ) {
    // Section 1: Driver Personal & Identity Details (Stacked Vertically)
    FormSectionCard(
      stepNumber = "1",
      sectionTitle = "Driver Name, Phone, Address, License & Aadhar",
      accentColor = KpiPurpleIcon
    ) {
      FormFieldInput(
        value = name,
        onValueChange = { name = it },
        label = "Driver Name",
        placeholder = "Enter driver full name",
        icon = Icons.Default.Person,
        modifier = Modifier.fillMaxWidth().testTag("driver_name_input")
      )

      FormFieldInput(
        value = phone,
        onValueChange = { phone = it },
        label = "Driver Phone Number",
        placeholder = "Enter driver phone number",
        icon = Icons.Default.Phone,
        modifier = Modifier.fillMaxWidth().testTag("driver_phone_input")
      )

      FormFieldInput(
        value = address,
        onValueChange = { address = it },
        label = "Driver Address",
        placeholder = "Enter driver residential address",
        icon = Icons.Default.LocationOn,
        singleLine = false,
        modifier = Modifier.fillMaxWidth().testTag("driver_address_input")
      )

      FormFieldInput(
        value = license,
        onValueChange = { license = it.uppercase() },
        label = "Driver Driving License Number",
        placeholder = "Enter driving license number",
        icon = Icons.Default.Badge,
        modifier = Modifier.fillMaxWidth().testTag("driver_license_input")
      )

      FormFieldInput(
        value = aadharNumber,
        onValueChange = { aadharNumber = it },
        label = "Driver Aadhar Card Number",
        placeholder = "Enter 12-digit Aadhar card number",
        icon = Icons.Default.Badge,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth().testTag("driver_aadhar_input")
      )
    }

    // Section 2: Driver Selfie Photo & Driver Documents Upload Options (Stacked Vertically)
    FormSectionCard(
      stepNumber = "2",
      sectionTitle = "Driver Selfie Photo & Documents Upload Options",
      accentColor = KpiTealIcon
    ) {
      Text(
        text = "Driver Selfie Photo",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )

      Button(
        onClick = {
          selfiePhotoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().testTag("upload_driver_selfie_button")
      ) {
        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Select Driver Selfie Photo from Gallery", fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }

      OutlinedButton(
        onClick = { selfieCameraLauncher.launch(null) },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SidebarActiveBlue),
        modifier = Modifier.fillMaxWidth().testTag("capture_driver_selfie_button")
      ) {
        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = SidebarActiveBlue, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Take Driver Selfie Photo", color = SidebarActiveBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }

      FormFieldInput(
        value = selfiePhotoUri,
        onValueChange = { selfiePhotoUri = it },
        label = "Driver Selfie Photo Status / File Reference",
        placeholder = "No selfie photo selected yet",
        icon = Icons.Default.CameraAlt,
        modifier = Modifier.fillMaxWidth()
      )

      HorizontalDivider(color = CardBorder)

      Text(
        text = "Driver Documents Upload Options",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )

      OutlinedButton(
        onClick = { licenseDocLauncher.launch(arrayOf("application/pdf", "image/*")) },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, KpiTealIcon),
        modifier = Modifier.fillMaxWidth().testTag("upload_driver_dl_doc_button")
      ) {
        Icon(Icons.Default.UploadFile, contentDescription = null, tint = KpiTealIcon, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Upload Driving License Document (PDF / Image)", color = KpiTealIcon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }

      OutlinedButton(
        onClick = { aadharDocLauncher.launch(arrayOf("application/pdf", "image/*")) },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, KpiTealIcon),
        modifier = Modifier.fillMaxWidth().testTag("upload_driver_aadhar_doc_button")
      ) {
        Icon(Icons.Default.UploadFile, contentDescription = null, tint = KpiTealIcon, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Upload Aadhar Card Document (PDF / Image)", color = KpiTealIcon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }

      OutlinedButton(
        onClick = { otherDocLauncher.launch(arrayOf("application/pdf", "image/*")) },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, KpiTealIcon),
        modifier = Modifier.fillMaxWidth().testTag("upload_driver_other_doc_button")
      ) {
        Icon(Icons.Default.UploadFile, contentDescription = null, tint = KpiTealIcon, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Upload Police Verification / Badge Document", color = KpiTealIcon, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }

      FormFieldInput(
        value = documentsUploaded,
        onValueChange = { documentsUploaded = it },
        label = "Uploaded Driver Documents Summary",
        placeholder = "Uploaded DL, Aadhar, or Badge files appear here",
        icon = Icons.Default.UploadFile,
        singleLine = false,
        modifier = Modifier.fillMaxWidth()
      )
    }

    // Section 3: Fleet Vehicle Assignment & Duty Status (Stacked Vertically)
    FormSectionCard(
      stepNumber = "3",
      sectionTitle = "Fleet Vehicle Assignment & Duty Status",
      accentColor = SidebarActiveBlue
    ) {
      FormFieldInput(
        value = vehicle,
        onValueChange = { vehicle = it },
        label = "Assigned Vehicle Make & Registration Plate",
        placeholder = "Enter assigned vehicle",
        icon = Icons.Default.DirectionsCar,
        modifier = Modifier.fillMaxWidth()
      )

      FormSelectDropdown(
        selectedValue = category,
        options = VEHICLE_CATEGORIES,
        onValueSelected = { category = it },
        label = "Select Fleet Category (Default: Sedan)",
        icon = Icons.Default.DirectionsCar,
        modifier = Modifier.fillMaxWidth()
      )

      FormSelectDropdown(
        selectedValue = status,
        options = DRIVER_STATUSES,
        onValueSelected = { status = it },
        label = "Select Duty Status",
        icon = Icons.Default.Badge,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = tripsText,
        onValueChange = { tripsText = it },
        label = "Completed Trips Count",
        placeholder = "0",
        icon = Icons.Default.Route,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = earningsText,
        onValueChange = { earningsText = it },
        label = "Total Driver Earnings / Bata (₹)",
        placeholder = "0",
        icon = Icons.Default.CurrencyRupee,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

/**
 * FORM 1: Vehicle Details & Vehicle Documents (with Document Expiry Dates) in one form.
 */
@Composable
fun AddVehicleDetailsDialog(
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
    partsChanged: String,
    insuranceDetails: String,
    insuranceExpiryDate: String,
    fitnessDetails: String,
    fitnessExpiryDate: String,
    pollutionDetails: String,
    pollutionExpiryDate: String,
    serviceNotes: String,
  ) -> Unit,
) {
  val isEditMode = initialVehicle != null

  var model by remember(initialVehicle) { mutableStateOf(initialVehicle?.modelName ?: "") }
  var regNo by remember(initialVehicle) { mutableStateOf(initialVehicle?.registrationNumber ?: "") }
  var manufacturingYear by remember(initialVehicle) {
    mutableStateOf(initialVehicle?.manufacturingYear ?: "")
  }
  var category by remember(initialVehicle) {
    mutableStateOf(normalizeFleetCategory(initialVehicle?.category))
  }
  val status = initialVehicle?.status ?: "Available"

  var insuranceDetails by remember(initialVehicle) { mutableStateOf(initialVehicle?.insuranceDetails ?: "") }
  var insuranceExpiryDate by remember(initialVehicle) {
    mutableStateOf(formatToDdMmYyyy(initialVehicle?.insuranceExpiryDate ?: ""))
  }
  var fitnessDetails by remember(initialVehicle) { mutableStateOf(initialVehicle?.fitnessDetails ?: "") }
  var fitnessExpiryDate by remember(initialVehicle) {
    mutableStateOf(formatToDdMmYyyy(initialVehicle?.fitnessExpiryDate ?: ""))
  }
  var pollutionDetails by remember(initialVehicle) { mutableStateOf(initialVehicle?.pollutionDetails ?: "") }
  var pollutionExpiryDate by remember(initialVehicle) {
    mutableStateOf(formatToDdMmYyyy(initialVehicle?.pollutionExpiryDate ?: ""))
  }

  CrmFormModalShell(
    badgeText = if (isEditMode) "UPDATE VEHICLE & DOCUMENTS" else "VEHICLE DETAILS & DOCUMENTS FORM",
    badgeBg = KpiOrangeBg,
    badgeFg = KpiOrangeIcon,
    title = if (isEditMode) "Edit Vehicle Details & Documents" else "Add Vehicle Details & Documents",
    subtitle = "Vehicle make/model, registration number, fleet category (default Sedan), and document expiry dates.",
    primaryButtonText = if (isEditMode) "Update Vehicle & Documents" else "Save Vehicle & Documents",
    primaryButtonColor = SidebarActiveBlue,
    onClearFields = {
      model = ""
      regNo = ""
      manufacturingYear = ""
      category = "Sedan"
      insuranceDetails = ""
      insuranceExpiryDate = ""
      fitnessDetails = ""
      fitnessExpiryDate = ""
      pollutionDetails = ""
      pollutionExpiryDate = ""
    },
    onDismiss = onDismiss,
    onConfirm = {
      onConfirm(
        regNo.trim(),
        model.trim(),
        manufacturingYear.trim(),
        category,
        status,
        initialVehicle?.maintenanceCost ?: 0L,
        initialVehicle?.serviceDate ?: "",
        initialVehicle?.nextServiceDate ?: "",
        initialVehicle?.serviceKilometers ?: 0L,
        initialVehicle?.presentRunningKilometers ?: 0L,
        initialVehicle?.nextServiceKilometers ?: 0L,
        initialVehicle?.partsChanged ?: "",
        insuranceDetails.trim(),
        formatToDdMmYyyy(insuranceExpiryDate),
        fitnessDetails.trim(),
        formatToDdMmYyyy(fitnessExpiryDate),
        pollutionDetails.trim(),
        formatToDdMmYyyy(pollutionExpiryDate),
        initialVehicle?.serviceNotes ?: ""
      )
      onDismiss()
    }
  ) {
    FormSectionCard(
      stepNumber = "1",
      sectionTitle = "Vehicle Details (Default Fleet Category: Sedan)",
      accentColor = KpiOrangeIcon
    ) {
      FormFieldInput(
        value = model,
        onValueChange = { model = it },
        label = "Vehicle Name, Make and Model",
        placeholder = "Enter vehicle name, make and model",
        icon = Icons.Default.DirectionsCar,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = regNo,
        onValueChange = { regNo = it.uppercase() },
        label = "Registration Number Plate",
        placeholder = "Enter registration number plate",
        icon = Icons.Default.Badge,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = manufacturingYear,
        onValueChange = { manufacturingYear = it },
        label = "Manufacturing Year",
        placeholder = "Enter manufacturing year",
        icon = Icons.Default.CalendarMonth,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )

      FormSelectDropdown(
        selectedValue = category,
        options = VEHICLE_CATEGORIES,
        onValueSelected = { category = it },
        label = "Select Fleet Category (Default: Sedan)",
        icon = Icons.Default.DirectionsCar,
        modifier = Modifier.fillMaxWidth(),
        testTagName = "vehicle_select_fleet_category"
      )
    }

    FormSectionCard(
      stepNumber = "2",
      sectionTitle = "Vehicle Documents & Document Expiry Dates",
      accentColor = SidebarActiveBlue
    ) {
      FormFieldInput(
        value = insuranceDetails,
        onValueChange = { insuranceDetails = it },
        label = "Insurance Details",
        placeholder = "Enter insurance policy details",
        icon = Icons.Default.ReceiptLong,
        modifier = Modifier.fillMaxWidth()
      )
      FormCalendarDateField(
        dateValue = insuranceExpiryDate,
        onDateChange = { insuranceExpiryDate = it },
        label = "Insurance Document Expiry Date (DD-MM-YYYY)",
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = fitnessDetails,
        onValueChange = { fitnessDetails = it },
        label = "Fitness Certificate (FC) Details",
        placeholder = "Enter fitness certificate details",
        icon = Icons.Default.Badge,
        modifier = Modifier.fillMaxWidth()
      )
      FormCalendarDateField(
        dateValue = fitnessExpiryDate,
        onDateChange = { fitnessExpiryDate = it },
        label = "Fitness Document Expiry Date (DD-MM-YYYY)",
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = pollutionDetails,
        onValueChange = { pollutionDetails = it },
        label = "Pollution (PUC) Details",
        placeholder = "Enter pollution certificate details",
        icon = Icons.Default.Build,
        modifier = Modifier.fillMaxWidth()
      )
      FormCalendarDateField(
        dateValue = pollutionExpiryDate,
        onDateChange = { pollutionExpiryDate = it },
        label = "Pollution Document Expiry Date (DD-MM-YYYY)",
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

/**
 * FORM 2: Separate Vehicle Maintenance Form (with Odometer/Service Dates and "What are the parts changed in the present service" tab).
 */
@Composable
fun AddVehicleMaintenanceDialog(
  initialVehicle: Vehicle? = null,
  availableVehicles: List<Vehicle> = emptyList(),
  onDismiss: () -> Unit,
  onConfirm: (
    targetVehicle: Vehicle?,
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
    partsChanged: String,
    insuranceDetails: String,
    insuranceExpiryDate: String,
    fitnessDetails: String,
    fitnessExpiryDate: String,
    pollutionDetails: String,
    pollutionExpiryDate: String,
    serviceNotes: String,
  ) -> Unit,
) {
  var selectedVehicle by remember(initialVehicle) {
    mutableStateOf(initialVehicle ?: availableVehicles.firstOrNull())
  }
  var activeMaintenanceTab by remember { mutableStateOf(0) } // 0 = Odometer & Service Schedule, 1 = What are the parts changed in the present service

  var model by remember(selectedVehicle) { mutableStateOf(selectedVehicle?.modelName ?: "") }
  var regNo by remember(selectedVehicle) { mutableStateOf(selectedVehicle?.registrationNumber ?: "") }

  var presentRunningKmText by remember(selectedVehicle) {
    mutableStateOf(
      if (selectedVehicle != null && selectedVehicle!!.presentRunningKilometers > 0L)
        selectedVehicle!!.presentRunningKilometers.toString()
      else ""
    )
  }
  var serviceDate by remember(selectedVehicle) {
    mutableStateOf(formatToDdMmYyyy(selectedVehicle?.serviceDate ?: ""))
  }
  var serviceKmText by remember(selectedVehicle) {
    mutableStateOf(
      if (selectedVehicle != null && selectedVehicle!!.serviceKilometers > 0L)
        selectedVehicle!!.serviceKilometers.toString()
      else ""
    )
  }
  var nextServiceDate by remember(selectedVehicle) {
    mutableStateOf(formatToDdMmYyyy(selectedVehicle?.nextServiceDate ?: ""))
  }
  var nextServiceKmText by remember(selectedVehicle) {
    mutableStateOf(
      if (selectedVehicle != null && selectedVehicle!!.nextServiceKilometers > 0L)
        selectedVehicle!!.nextServiceKilometers.toString()
      else ""
    )
  }
  var partsChanged by remember(selectedVehicle) {
    mutableStateOf(selectedVehicle?.partsChanged ?: "")
  }
  var costText by remember(selectedVehicle) {
    mutableStateOf(
      if (selectedVehicle != null && selectedVehicle!!.maintenanceCost > 0L)
        selectedVehicle!!.maintenanceCost.toString()
      else ""
    )
  }
  var notes by remember(selectedVehicle) { mutableStateOf(selectedVehicle?.serviceNotes ?: "") }

  CrmFormModalShell(
    badgeText = "VEHICLE MAINTENANCE FORM",
    badgeBg = KpiBlueBg,
    badgeFg = SidebarActiveBlue,
    title = if (selectedVehicle != null) "Edit Vehicle Maintenance" else "Add Vehicle Maintenance",
    subtitle = "Present odometer km, present service date & km, next service date & km, and parts changed in present service.",
    primaryButtonText = "Save Vehicle Maintenance",
    primaryButtonColor = SidebarActiveBlue,
    onClearFields = {
      presentRunningKmText = ""
      serviceDate = ""
      serviceKmText = ""
      nextServiceDate = ""
      nextServiceKmText = ""
      partsChanged = ""
      costText = ""
      notes = ""
    },
    onDismiss = onDismiss,
    onConfirm = {
      val baseVehicle = selectedVehicle
      onConfirm(
        baseVehicle,
        regNo.trim(),
        model.trim(),
        baseVehicle?.manufacturingYear ?: "",
        normalizeFleetCategory(baseVehicle?.category),
        baseVehicle?.status ?: "Available",
        costText.toLongOrNull() ?: 0L,
        formatToDdMmYyyy(serviceDate),
        formatToDdMmYyyy(nextServiceDate),
        serviceKmText.toLongOrNull() ?: 0L,
        presentRunningKmText.toLongOrNull() ?: 0L,
        nextServiceKmText.toLongOrNull() ?: 0L,
        partsChanged.trim(),
        baseVehicle?.insuranceDetails ?: "",
        baseVehicle?.insuranceExpiryDate ?: "",
        baseVehicle?.fitnessDetails ?: "",
        baseVehicle?.fitnessExpiryDate ?: "",
        baseVehicle?.pollutionDetails ?: "",
        baseVehicle?.pollutionExpiryDate ?: "",
        notes.trim()
      )
      onDismiss()
    }
  ) {
    if (initialVehicle == null && availableVehicles.isNotEmpty()) {
      val vehicleOptions = availableVehicles.map { "${it.modelName} (${it.registrationNumber})" }
      val currentLabel = selectedVehicle?.let { "${it.modelName} (${it.registrationNumber})" } ?: vehicleOptions.first()
      FormSelectDropdown(
        selectedValue = currentLabel,
        options = vehicleOptions,
        onValueSelected = { label ->
          val match = availableVehicles.firstOrNull { "${it.modelName} (${it.registrationNumber})" == label }
          if (match != null) {
            selectedVehicle = match
          }
        },
        label = "Select Saved Vehicle for Maintenance",
        icon = Icons.Default.DirectionsCar,
        modifier = Modifier.fillMaxWidth()
      )
    }

    FormSectionCard(
      stepNumber = "1",
      sectionTitle = "Vehicle Identification",
      accentColor = KpiOrangeIcon
    ) {
      FormFieldInput(
        value = model,
        onValueChange = { model = it },
        label = "Vehicle Name, Make and Model",
        placeholder = "Enter vehicle make and model",
        icon = Icons.Default.DirectionsCar,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = regNo,
        onValueChange = { regNo = it.uppercase() },
        label = "Registration Number Plate",
        placeholder = "Enter registration number",
        icon = Icons.Default.Badge,
        modifier = Modifier.fillMaxWidth()
      )
    }

    FormSectionCard(
      stepNumber = "2",
      sectionTitle = "Odometer & Service Schedule Details",
      accentColor = SidebarActiveBlue
    ) {
      FormFieldInput(
        value = presentRunningKmText,
        onValueChange = { presentRunningKmText = it },
        label = "Present Odometer KM",
        placeholder = "Enter present odometer reading (km)",
        icon = Icons.Default.Speed,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )

      FormCalendarDateField(
        dateValue = serviceDate,
        onDateChange = { serviceDate = it },
        label = "Present Service Date (DD-MM-YYYY)",
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = serviceKmText,
        onValueChange = { serviceKmText = it },
        label = "Present Service KM",
        placeholder = "Enter kilometers at present service",
        icon = Icons.Default.Speed,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )

      FormCalendarDateField(
        dateValue = nextServiceDate,
        onDateChange = { nextServiceDate = it },
        label = "Next Service Date (DD-MM-YYYY)",
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = nextServiceKmText,
        onValueChange = { nextServiceKmText = it },
        label = "Next Service Kilometers",
        placeholder = "Enter next service due kilometers",
        icon = Icons.Default.Speed,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = costText,
        onValueChange = { costText = it },
        label = "Service / Maintenance Cost (₹)",
        placeholder = "Enter service cost",
        icon = Icons.Default.CurrencyRupee,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = partsChanged,
        onValueChange = {
          partsChanged = it
          notes = it
        },
        label = "Next Service Note",
        placeholder = "Enter next service note",
        icon = Icons.Default.Build,
        singleLine = false,
        modifier = Modifier.fillMaxWidth().testTag("next_service_note_input")
      )
    }
  }
}

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
  var category by remember(initialLead) {
    mutableStateOf(normalizeFleetCategory(initialLead?.preferredCategory))
  }
  var quoteText by remember(initialLead) {
    mutableStateOf(if (initialLead != null && initialLead.quotedAmount > 0L) initialLead.quotedAmount.toString() else "")
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
    subtitle = "Capture prospective customer details, tour itinerary, preferred fleet category (default Sedan), and quote.",
    primaryButtonText = if (isEditMode) "Update Lead" else "Save Lead to Pipeline",
    primaryButtonColor = SidebarActiveBlue,
    onClearFields = {
      name = ""
      phone = ""
      route = ""
      category = "Sedan"
      quoteText = ""
    },
    onDismiss = onDismiss,
    onConfirm = {
      onConfirm(
        name.trim(),
        phone.trim(),
        route.trim(),
        category,
        quoteText.toLongOrNull() ?: 0L,
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
      FormFieldInput(
        value = name,
        onValueChange = { name = it },
        label = "Customer Name",
        placeholder = "Enter customer name",
        icon = Icons.Default.Person,
        modifier = Modifier.fillMaxWidth()
      )
      FormFieldInput(
        value = phone,
        onValueChange = { phone = it },
        label = "Customer Mobile Number",
        placeholder = "Enter customer mobile number",
        icon = Icons.Default.Phone,
        modifier = Modifier.fillMaxWidth()
      )
      FormSelectDropdown(
        selectedValue = source,
        options = LEAD_SOURCES,
        onValueSelected = { source = it },
        label = "Select Lead Source",
        icon = Icons.Default.Business,
        modifier = Modifier.fillMaxWidth()
      )
      FormCalendarDateField(
        dateValue = followUpDate,
        onDateChange = { followUpDate = it },
        label = "Travel Date (DD-MM-YYYY)",
        modifier = Modifier.fillMaxWidth()
      )
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
        placeholder = "Enter pickup and destination route",
        icon = Icons.Default.Route,
        modifier = Modifier.fillMaxWidth()
      )

      FormSelectDropdown(
        selectedValue = category,
        options = VEHICLE_CATEGORIES,
        onValueSelected = { category = it },
        label = "Select Fleet Category (Default: Sedan)",
        icon = Icons.Default.DirectionsCar,
        modifier = Modifier.fillMaxWidth()
      )

      FormSelectDropdown(
        selectedValue = status,
        options = LEAD_STATUSES,
        onValueSelected = { status = it },
        label = "Select Lead Status",
        icon = Icons.Default.ReceiptLong,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = quoteText,
        onValueChange = { quoteText = it },
        label = "Quoted Package Price (₹)",
        placeholder = "Enter quoted amount",
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
      val cleanName = customerName.trim()
      val cleanAddress = address.trim()
      onConfirm(
        cleanName,
        cleanAddress,
        phone.trim(),
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
      sectionTitle = "Customer Details (Stacked Vertically)",
      accentColor = KpiTealIcon
    ) {
      FormFieldInput(
        value = customerName,
        onValueChange = { customerName = it },
        label = "Customer Name",
        placeholder = "Enter customer name",
        icon = Icons.Default.Person,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = address,
        onValueChange = { address = it },
        label = "Address",
        placeholder = "Enter customer address",
        icon = Icons.Default.LocationOn,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = phone,
        onValueChange = { phone = it },
        label = "Mobile Number",
        placeholder = "Enter customer mobile number",
        icon = Icons.Default.Phone,
        modifier = Modifier.fillMaxWidth()
      )

      FormFieldInput(
        value = bookingsCountText,
        onValueChange = { bookingsCountText = it },
        label = "Total Trips",
        placeholder = "1",
        icon = Icons.Default.Route,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )
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
      FormFieldInput(
        value = fuelText,
        onValueChange = { fuelText = it },
        label = "Diesel / Fuel Cost (₹)",
        placeholder = "0",
        icon = Icons.Default.LocalGasStation,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )
      FormFieldInput(
        value = tollText,
        onValueChange = { tollText = it },
        label = "FASTag / Highway Toll (₹)",
        placeholder = "0",
        icon = Icons.Default.Route,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )
      FormFieldInput(
        value = driverBataText,
        onValueChange = { driverBataText = it },
        label = "Driver Bata / Allowance (₹)",
        placeholder = "0",
        icon = Icons.Default.Badge,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )
      FormFieldInput(
        value = permitParkingText,
        onValueChange = { permitParkingText = it },
        label = "State Permit & Parking (₹)",
        placeholder = "0",
        icon = Icons.Default.ReceiptLong,
        isNumeric = true,
        modifier = Modifier.fillMaxWidth()
      )
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
        placeholder = "0",
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
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text("Total Billed: ${formatInr(booking.totalAmount)}", fontSize = 12.sp, color = TextPrimaryDark)
          Text("Total Trip Expenses: ${formatInr(totalExpense)}", fontSize = 12.sp, color = KpiOrangeIcon, fontWeight = FontWeight.Bold)
          Text("NET PROFIT: ${formatInr(netProfit)}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = KpiGreenIcon)
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
