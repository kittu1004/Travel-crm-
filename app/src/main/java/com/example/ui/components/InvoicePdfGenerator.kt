package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.R
import com.example.data.Booking
import com.example.data.Customer
import com.example.data.Vehicle
import com.example.ui.formatInr
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.KpiAmberBg
import com.example.ui.theme.KpiAmberIcon
import com.example.ui.theme.KpiBlueBg
import com.example.ui.theme.KpiGreenBg
import com.example.ui.theme.KpiGreenIcon
import com.example.ui.theme.NewBookingGreen
import com.example.ui.theme.SidebarActiveBlue
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondarySlate
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.text.NumberFormat
import java.util.Locale

fun buildInvoiceNumber(booking: Booking): String {
  val digits = booking.id.filter { it.isDigit() }.takeLast(2)
  val twoDigit = if (digits.length == 2) {
    digits
  } else {
    (((booking.customerName.hashCode() and 0x7FFF) % 89) + 10).toString()
  }
  return "INV-2026-1001-$twoDigit"
}

fun formatRsDecimal(amount: Long): String {
  val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
  return "Rs. ${formatter.format(amount)}.00"
}

fun amountToIndianWords(amount: Long): String {
  if (amount <= 0L) return "Rupees Zero Only"
  val units = arrayOf(
    "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
    "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
  )
  val tens = arrayOf(
    "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
  )

  fun twoDigitsToWords(n: Int): String {
    return if (n < 20) {
      units[n]
    } else {
      val t = tens[n / 10]
      val u = units[n % 10]
      if (u.isEmpty()) t else "$t $u"
    }
  }

  fun convertNumber(num: Long): String {
    if (num == 0L) return ""
    val parts = mutableListOf<String>()
    var rem = num
    val crore = (rem / 10_000_000L).toInt()
    rem %= 10_000_000L
    val lakh = (rem / 100_000L).toInt()
    rem %= 100_000L
    val thousand = (rem / 1_000L).toInt()
    rem %= 1_000L
    val hundred = (rem / 100L).toInt()
    val lastTwo = (rem % 100L).toInt()

    if (crore > 0) parts.add("${twoDigitsToWords(crore)} Crore")
    if (lakh > 0) parts.add("${twoDigitsToWords(lakh)} Lakh")
    if (thousand > 0) parts.add("${twoDigitsToWords(thousand)} Thousand")
    if (hundred > 0) parts.add("${units[hundred]} Hundred")
    if (lastTwo > 0) parts.add(twoDigitsToWords(lastTwo))

    return parts.joinToString(" ")
  }

  return "Rupees ${convertNumber(amount)} Only"
}

suspend fun generateBookingInvoicePdf(
  context: Context,
  booking: Booking,
  agencyName: String,
  agencyGstin: String,
  gstPercent: Int,
): Result<File> = withContext(Dispatchers.IO) {
  runCatching {
    val invoiceNo = buildInvoiceNumber(booking)
    val invoicesDir = File(context.cacheDir, "invoices").apply { mkdirs() }
    val safeCustomer = booking.customerName.replace(Regex("[^a-zA-Z0-9_-]"), "_").take(24).ifBlank { "Client" }
    val pdfFile = File(invoicesDir, "${invoiceNo}_${safeCustomer}.pdf")

    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val royalBluePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#1D63B8")
      style = Paint.Style.FILL
    }
    val yellowBadgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#F5B014")
      style = Paint.Style.FILL
    }
    val cardBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#F8FAFC")
      style = Paint.Style.FILL
    }
    val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.WHITE
      style = Paint.Style.FILL
    }
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#E2E8F0")
      style = Paint.Style.STROKE
      strokeWidth = 1f
    }
    val titleWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.WHITE
      textSize = 15.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val subWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#DBEAFE")
      textSize = 9.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val blueSectionHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#1D63B8")
      textSize = 8.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val boldDarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#0F172A")
      textSize = 11f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val regularSlatePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#475569")
      textSize = 9.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val mutedSmallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#64748B")
      textSize = 8.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val hasGst = booking.gstAmount > 0L
    val effectiveGstRate = if (hasGst && booking.baseAmount > 0L) {
      ((booking.gstAmount * 100L) / booking.baseAmount).toInt().coerceAtLeast(1)
    } else {
      0
    }

    val displayAgency = agencyName.ifBlank { "APEX CABS" }
    val displayGstin = agencyGstin.ifBlank { "37ABCDE1234F1Z5" }
    val displayPhone = booking.driverPhone.ifBlank { booking.customerPhone.ifBlank { "+91 7670868036" } }
    val displayCity = booking.pickupLocation.ifBlank { "Vijayawada" }
    val formattedDate = com.example.data.formatToDdMmYyyy(booking.tripDate)

    // 1. Top Royal-Blue Rounded Header Card
    val headerRect = RectF(36f, 36f, 559f, 126f)
    canvas.drawRoundRect(headerRect, 10f, 10f, royalBluePaint)

    // Logo Box on Left of Header
    val logoOuterRect = RectF(48f, 48f, 114f, 114f)
    canvas.drawRoundRect(logoOuterRect, 10f, 10f, whitePaint)
    runCatching {
      val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.taxi_invoice_logo)
      if (logoBitmap != null) {
        val logoInnerRect = RectF(50f, 50f, 112f, 112f)
        val clipPath = Path().apply {
          addRoundRect(logoInnerRect, 8f, 8f, Path.Direction.CW)
        }
        canvas.save()
        canvas.clipPath(clipPath)
        canvas.drawBitmap(logoBitmap, null, logoInnerRect, null)
        canvas.restore()
      }
    }

    // Agency Name, Location & Contact/GSTIN
    canvas.drawText(displayAgency.uppercase(), 126f, 64f, titleWhitePaint)
    canvas.drawText(
      if (displayCity.contains(",")) displayCity else "$displayCity, Andhra Pradesh",
      126f,
      82f,
      subWhitePaint
    )
    canvas.drawText(
      "Ph: $displayPhone   |   GSTIN: $displayGstin",
      126f,
      108f,
      subWhitePaint
    )

    // Top-Right Yellow "TAX INVOICE" Badge + Invoice No + Date
    val taxBadgeRect = RectF(452f, 48f, 545f, 67f)
    canvas.drawRoundRect(taxBadgeRect, 5f, 5f, yellowBadgePaint)
    val taxBadgeTextPaint = Paint(boldDarkPaint).apply {
      textSize = 8.5f
      textAlign = Paint.Align.CENTER
    }
    canvas.drawText("TAX INVOICE", taxBadgeRect.centerX(), 61f, taxBadgeTextPaint)

    val rightWhiteBold = Paint(titleWhitePaint).apply {
      textSize = 11f
      textAlign = Paint.Align.RIGHT
    }
    val rightWhiteSub = Paint(subWhitePaint).apply {
      textSize = 9f
      textAlign = Paint.Align.RIGHT
    }
    canvas.drawText(invoiceNo, 545f, 88f, rightWhiteBold)
    canvas.drawText("Date: $formattedDate", 545f, 105f, rightWhiteSub)

    // 2. Two Side-by-Side Rounded Info Boxes
    val leftBox = RectF(36f, 140f, 291f, 224f)
    val rightBox = RectF(304f, 140f, 559f, 224f)
    canvas.drawRoundRect(leftBox, 8f, 8f, cardBgPaint)
    canvas.drawRoundRect(leftBox, 8f, 8f, borderPaint)
    canvas.drawRoundRect(rightBox, 8f, 8f, cardBgPaint)
    canvas.drawRoundRect(rightBox, 8f, 8f, borderPaint)

    // Left Box: BILLED TO (PASSENGER)
    canvas.drawText("BILLED TO (PASSENGER)", 50f, 158f, blueSectionHeaderPaint)
    canvas.drawText(booking.customerName.ifBlank { "Passenger" }, 50f, 176f, boldDarkPaint)
    canvas.drawText("Mobile: ${booking.customerPhone.ifBlank { "N/A" }}", 50f, 193f, regularSlatePaint)
    canvas.drawText(
      booking.companyName.ifBlank { booking.pickupLocation.ifBlank { "N/A" } },
      50f,
      210f,
      regularSlatePaint
    )

    // Right Box: TRIP, VEHICLE & DRIVER DETAILS
    canvas.drawText("TRIP, VEHICLE & DRIVER DETAILS", 318f, 158f, blueSectionHeaderPaint)
    val routeHeader = "${booking.pickupLocation.uppercase()}  -->  ${booking.dropLocation.uppercase()} (${booking.distanceKm} KM)"
    canvas.drawText(routeHeader.take(40), 318f, 176f, Paint(boldDarkPaint).apply { textSize = 10.5f })
    val vehicleLabel = booking.vehicleModel.ifBlank { booking.vehicleCategory.ifBlank { "Sedan" } }
    canvas.drawText(
      "Vehicle: $vehicleLabel   |   Reg No: ${booking.vehicleNumber.ifBlank { "N/A" }}",
      318f,
      193f,
      Paint(boldDarkPaint).apply { textSize = 9.5f }
    )
    canvas.drawText(
      "Driver: ${booking.driverName.ifBlank { "N/A" }}   |   Package: One Way",
      318f,
      210f,
      regularSlatePaint
    )

    // 3. Fare Particulars Table
    val tableTop = 240f
    val rowCount = if (booking.parkingCharges > 0L) 3 else 2
    val tableHeight = 26f + (rowCount * 42f)
    val tableOuterRect = RectF(36f, tableTop, 559f, tableTop + tableHeight)
    canvas.drawRoundRect(tableOuterRect, 6f, 6f, whitePaint)

    // Table Header Bar
    val thRect = RectF(36f, tableTop, 559f, tableTop + 26f)
    val thPath = Path().apply {
      addRoundRect(
        thRect,
        floatArrayOf(6f, 6f, 6f, 6f, 0f, 0f, 0f, 0f),
        Path.Direction.CW
      )
    }
    canvas.drawPath(thPath, royalBluePaint)

    val thTextPaint = Paint(titleWhitePaint).apply { textSize = 8.5f }
    val thCenterPaint = Paint(thTextPaint).apply { textAlign = Paint.Align.CENTER }
    val thRightPaint = Paint(thTextPaint).apply { textAlign = Paint.Align.RIGHT }

    canvas.drawText("S.NO", 56f, tableTop + 16f, thCenterPaint)
    canvas.drawText("FARE PARTICULARS / DESCRIPTION", 88f, tableTop + 16f, thTextPaint)
    canvas.drawText("SAC CODE", 412f, tableTop + 16f, thCenterPaint)
    canvas.drawText("AMOUNT (INR)", 547f, tableTop + 16f, thRightPaint)

    val snoPaint = Paint(regularSlatePaint).apply { textAlign = Paint.Align.CENTER }
    val sacPaint = Paint(regularSlatePaint).apply { textAlign = Paint.Align.CENTER }
    val amtRightPaint = Paint(boldDarkPaint).apply {
      textSize = 10.5f
      textAlign = Paint.Align.RIGHT
    }

    // Row 1: Base Taxi Fare (One Way)
    var rowTop = tableTop + 26f
    canvas.drawText("1", 56f, rowTop + 19f, snoPaint)
    canvas.drawText("Base Taxi Fare (One Way)", 88f, rowTop + 16f, Paint(boldDarkPaint).apply { textSize = 10f })
    canvas.drawText(
      "Route: ${booking.pickupLocation.uppercase()} to ${booking.dropLocation.uppercase()} (${booking.distanceKm} KM)",
      88f,
      rowTop + 30f,
      mutedSmallPaint
    )
    canvas.drawText("996412", 412f, rowTop + 22f, sacPaint)
    canvas.drawText(formatRsDecimal(booking.baseAmount), 547f, rowTop + 22f, amtRightPaint)
    canvas.drawLine(36f, rowTop + 42f, 559f, rowTop + 42f, borderPaint)

    // Row 2: Tollgate & Fastag Charges
    rowTop += 42f
    val row2Rect = RectF(36.5f, rowTop + 0.5f, 558.5f, rowTop + 41.5f)
    canvas.drawRect(row2Rect, cardBgPaint)
    canvas.drawText("2", 56f, rowTop + 19f, snoPaint)
    canvas.drawText("Tollgate & Fastag Charges", 88f, rowTop + 16f, Paint(boldDarkPaint).apply { textSize = 10f })
    canvas.drawText("Highway and expressway toll plaza fee", 88f, rowTop + 30f, mutedSmallPaint)
    canvas.drawText("996412", 412f, rowTop + 22f, sacPaint)
    canvas.drawText(formatRsDecimal(booking.tollCharges), 547f, rowTop + 22f, amtRightPaint)

    if (booking.parkingCharges > 0L) {
      canvas.drawLine(36f, rowTop + 42f, 559f, rowTop + 42f, borderPaint)
      rowTop += 42f
      canvas.drawText("3", 56f, rowTop + 19f, snoPaint)
      canvas.drawText("Parking & Entry Charges", 88f, rowTop + 16f, Paint(boldDarkPaint).apply { textSize = 10f })
      canvas.drawText("Airport and terminal parking fee", 88f, rowTop + 30f, mutedSmallPaint)
      canvas.drawText("996412", 412f, rowTop + 22f, sacPaint)
      canvas.drawText(formatRsDecimal(booking.parkingCharges), 547f, rowTop + 22f, amtRightPaint)
    }

    canvas.drawRoundRect(tableOuterRect, 6f, 6f, borderPaint)

    // 4. Bottom Summary Section (Words Box on Left, SubTotal/GST/GrandTotal on Right)
    val summaryTop = tableTop + tableHeight + 18f
    val wordsRect = RectF(36f, summaryTop, 326f, summaryTop + 74f)
    canvas.drawRoundRect(wordsRect, 8f, 8f, cardBgPaint)
    canvas.drawRoundRect(wordsRect, 8f, 8f, borderPaint)

    val wordsHeaderPaint = Paint(mutedSmallPaint).apply {
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val wordsValuePaint = Paint(boldDarkPaint).apply {
      textSize = 10f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
    }
    val payStatusPaint = Paint(boldDarkPaint).apply {
      textSize = 9f
      color = if (booking.paymentStatus.equals("Paid", ignoreCase = true)) {
        AndroidColor.parseColor("#16A34A")
      } else {
        AndroidColor.parseColor("#D97706")
      }
    }

    canvas.drawText("TOTAL AMOUNT IN WORDS", 50f, summaryTop + 18f, wordsHeaderPaint)
    canvas.drawText(amountToIndianWords(booking.totalAmount).take(48), 50f, summaryTop + 36f, wordsValuePaint)
    canvas.drawText(
      "Payment Status: ${booking.paymentStatus.uppercase()} (${booking.paymentChannel})",
      50f,
      summaryTop + 60f,
      payStatusPaint
    )

    // Right Column: Sub Total, GST, Grand Total
    val subTotal = booking.baseAmount + booking.tollCharges + booking.parkingCharges
    canvas.drawText("Sub Total:", 344f, summaryTop + 15f, regularSlatePaint)
    canvas.drawText(formatRsDecimal(subTotal), 551f, summaryTop + 15f, amtRightPaint)

    val gstLabelPaint = Paint(mutedSmallPaint).apply { textSize = 9.5f }
    val gstRightPaint = Paint(gstLabelPaint).apply { textAlign = Paint.Align.RIGHT }
    canvas.drawText("GST ($effectiveGstRate%):", 344f, summaryTop + 33f, gstLabelPaint)
    canvas.drawText(formatRsDecimal(booking.gstAmount), 551f, summaryTop + 33f, gstRightPaint)

    val grandRect = RectF(340f, summaryTop + 46f, 559f, summaryTop + 74f)
    canvas.drawRoundRect(grandRect, 6f, 6f, royalBluePaint)
    val grandLabelPaint = Paint(titleWhitePaint).apply { textSize = 10.5f }
    val grandAmtPaint = Paint(titleWhitePaint).apply {
      textSize = 11.5f
      textAlign = Paint.Align.RIGHT
    }
    canvas.drawText("GRAND TOTAL", 352f, summaryTop + 64f, grandLabelPaint)
    canvas.drawText(formatRsDecimal(booking.totalAmount), 547f, summaryTop + 64f, grandAmtPaint)

    // 5. Footer Notes & Signatory
    val footerY = summaryTop + 98f
    canvas.drawLine(36f, footerY, 559f, footerY, borderPaint)

    canvas.drawText(
      "Thank you for riding with ${displayAgency}!",
      36f,
      footerY + 24f,
      Paint(boldDarkPaint).apply { textSize = 9.5f }
    )
    canvas.drawText(
      "This is a computer-generated tax invoice and does not require a physical seal.",
      36f,
      footerY + 38f,
      mutedSmallPaint
    )

    val sigCenterX = 494f
    val sigNamePaint = Paint(boldDarkPaint).apply {
      textSize = 9.5f
      textAlign = Paint.Align.CENTER
    }
    val sigSubPaint = Paint(mutedSmallPaint).apply {
      textAlign = Paint.Align.CENTER
    }
    canvas.drawText(
      booking.driverName.ifBlank { displayAgency },
      sigCenterX,
      footerY + 28f,
      sigNamePaint
    )
    canvas.drawLine(430f, footerY + 33f, 559f, footerY + 33f, Paint(borderPaint).apply {
      color = AndroidColor.parseColor("#94A3B8")
    })
    canvas.drawText("Authorized Signatory", sigCenterX, footerY + 45f, sigSubPaint)

    pdfDocument.finishPage(page)
    FileOutputStream(pdfFile).use { out ->
      pdfDocument.writeTo(out)
    }
    pdfDocument.close()
    pdfFile
  }
}

suspend fun generateGstTaxDetailsPdf(
  context: Context,
  bookings: List<Booking>,
  agencyName: String,
  agencyGstin: String,
  defaultGstPercent: Int,
): Result<File> = withContext(Dispatchers.IO) {
  runCatching {
    val activeBookings = bookings.filter { it.tripStatus != "Cancelled" }
    val taxableValue = activeBookings.sumOf { it.baseAmount }
    val totalGst = activeBookings.sumOf { it.gstAmount }
    val cgst = totalGst / 2L
    val sgst = totalGst - cgst

    val invoicesDir = File(context.cacheDir, "invoices").apply { mkdirs() }
    val pdfFile = File(invoicesDir, "GST_Tax_Details_Statement.pdf")

    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val navyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#0B1120")
      style = Paint.Style.FILL
    }
    val cardBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#F8FAFC")
      style = Paint.Style.FILL
    }
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#CBD5E1")
      style = Paint.Style.STROKE
      strokeWidth = 1f
    }
    val tealAccentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#CCFBF1")
      style = Paint.Style.FILL
    }
    val titleWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.WHITE
      textSize = 20f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val subWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#94A3B8")
      textSize = 11f
    }
    val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#0F172A")
      textSize = 13f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#1E293B")
      textSize = 11.5f
    }
    val boldBodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#0F172A")
      textSize = 12f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val rightBoldPaint = Paint(boldBodyPaint).apply {
      textAlign = Paint.Align.RIGHT
    }

    // Header
    canvas.drawRect(0f, 0f, 595f, 110f, navyPaint)
    canvas.drawText("GST Tax Details Statement", 36f, 44f, titleWhitePaint)
    canvas.drawText("$agencyName  •  GSTIN: $agencyGstin", 36f, 68f, subWhitePaint)
    canvas.drawText("SAC Code: 996411 (Passenger Transport Services)", 36f, 88f, subWhitePaint)

    // Summary Box
    val summaryRect = RectF(36f, 134f, 559f, 360f)
    canvas.drawRoundRect(summaryRect, 10f, 10f, cardBgPaint)
    canvas.drawRoundRect(summaryRect, 10f, 10f, borderPaint)

    var y = 166f
    canvas.drawText("GST TAX SUMMARY", 52f, y, headingPaint)
    y += 28f
    canvas.drawText("Total Taxable Passenger Fare Value", 52f, y, bodyPaint)
    canvas.drawText(formatInr(taxableValue), 543f, y, rightBoldPaint)
    canvas.drawLine(52f, y + 10f, 543f, y + 10f, borderPaint)

    y += 32f
    canvas.drawText("Output CGST (${defaultGstPercent / 2.0}%)", 52f, y, bodyPaint)
    canvas.drawText(formatInr(cgst), 543f, y, rightBoldPaint)
    canvas.drawLine(52f, y + 10f, 543f, y + 10f, borderPaint)

    y += 32f
    canvas.drawText("Output SGST (${defaultGstPercent / 2.0}%)", 52f, y, bodyPaint)
    canvas.drawText(formatInr(sgst), 543f, y, rightBoldPaint)
    canvas.drawLine(52f, y + 10f, 543f, y + 10f, borderPaint)

    y += 32f
    canvas.drawText("Gross Output GST Collected", 52f, y, boldBodyPaint)
    canvas.drawText(formatInr(totalGst), 543f, y, rightBoldPaint)

    // Collected Net GST Highlight Box
    y += 24f
    val netBox = RectF(48f, y, 547f, y + 42f)
    canvas.drawRoundRect(netBox, 8f, 8f, tealAccentPaint)
    canvas.drawRoundRect(netBox, 8f, 8f, borderPaint)
    canvas.drawText("Collected Net GST", 64f, y + 26f, headingPaint)
    canvas.drawText(formatInr(totalGst), 535f, y + 26f, rightBoldPaint)

    // Recent GST Invoices Table
    var tableY = 390f
    canvas.drawText("RECENT GST BOOKINGS & INVOICES", 36f, tableY, headingPaint)
    tableY += 16f
    val thRect = RectF(36f, tableY, 559f, tableY + 28f)
    canvas.drawRoundRect(thRect, 6f, 6f, navyPaint)
    val thWhite = Paint(titleWhitePaint).apply { textSize = 10.5f }
    canvas.drawText("CUSTOMER", 48f, tableY + 18f, thWhite)
    canvas.drawText("DATE", 230f, tableY + 18f, thWhite)
    canvas.drawText("BASE FARE", 340f, tableY + 18f, thWhite)
    canvas.drawText("GST COLLECTED", 455f, tableY + 18f, thWhite)

    var rowY = tableY + 48f
    activeBookings.take(10).forEach { b ->
      canvas.drawText(b.customerName.take(22), 48f, rowY, bodyPaint)
      canvas.drawText(com.example.data.formatToDdMmYyyy(b.tripDate), 230f, rowY, bodyPaint)
      canvas.drawText(formatInr(b.baseAmount), 340f, rowY, bodyPaint)
      canvas.drawText(formatInr(b.gstAmount), 455f, rowY, boldBodyPaint)
      canvas.drawLine(36f, rowY + 8f, 559f, rowY + 8f, borderPaint)
      rowY += 26f
    }

    pdfDocument.finishPage(page)
    FileOutputStream(pdfFile).use { out ->
      pdfDocument.writeTo(out)
    }
    pdfDocument.close()
    pdfFile
  }
}

suspend fun copyInvoicePdfToUri(sourceFile: File, destUri: Uri, context: Context): Result<Long> =
  withContext(Dispatchers.IO) {
    runCatching {
      var copied = 0L
      context.contentResolver.openOutputStream(destUri)?.use { out ->
        sourceFile.inputStream().use { input ->
          copied = input.copyTo(out)
        }
      } ?: error("Unable to open destination file stream")
      copied
    }
  }

fun shareOrOpenInvoicePdf(context: Context, pdfFile: File, invoiceNo: String, openDirectly: Boolean) {
  val uri = FileProvider.getUriForFile(
    context,
    "${context.packageName}.fileprovider",
    pdfFile
  )
  if (openDirectly) {
    val viewIntent = Intent(Intent.ACTION_VIEW).apply {
      setDataAndType(uri, "application/pdf")
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    runCatching {
      context.startActivity(Intent.createChooser(viewIntent, "Open $invoiceNo"))
    }
  } else {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
      type = "application/pdf"
      putExtra(Intent.EXTRA_STREAM, uri)
      putExtra(Intent.EXTRA_SUBJECT, "$invoiceNo - ${pdfFile.name}")
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    runCatching {
      context.startActivity(Intent.createChooser(shareIntent, "Share PDF ($invoiceNo)"))
    }
  }
}

@Composable
fun GstTaxPdfDownloadDialog(
  bookings: List<Booking>,
  agencyName: String,
  agencyGstin: String,
  defaultGstPercent: Int,
  onDismiss: () -> Unit,
  onStatusMessage: (String) -> Unit,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var generatedFile by remember { mutableStateOf<File?>(null) }
  var errorText by remember { mutableStateOf<String?>(null) }

  val activeBookings = remember(bookings) { bookings.filter { it.tripStatus != "Cancelled" } }
  val taxableValue = remember(activeBookings) { activeBookings.sumOf { it.baseAmount } }
  val totalGst = remember(activeBookings) { activeBookings.sumOf { it.gstAmount } }
  val cgst = totalGst / 2L
  val sgst = totalGst - cgst

  LaunchedEffect(bookings, agencyName, agencyGstin, defaultGstPercent) {
    val result = generateGstTaxDetailsPdf(
      context = context,
      bookings = bookings,
      agencyName = agencyName,
      agencyGstin = agencyGstin,
      defaultGstPercent = defaultGstPercent
    )
    result.fold(
      onSuccess = { file -> generatedFile = file },
      onFailure = { err -> errorText = err.localizedMessage ?: "Failed to generate GST PDF" }
    )
  }

  val saveGstPdfLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("application/pdf")
  ) { destUri: Uri? ->
    val file = generatedFile
    if (destUri != null && file != null) {
      scope.launch {
        copyInvoicePdfToUri(file, destUri, context).fold(
          onSuccess = {
            onStatusMessage("Saved GST Tax Details PDF to your device!")
            onDismiss()
          },
          onFailure = { e ->
            onStatusMessage("Failed to save GST PDF: ${e.localizedMessage}")
          }
        )
      }
    }
  }

  CrmFormModalShell(
    badgeText = "GST TAX DETAILS PDF",
    badgeBg = KpiGreenBg,
    badgeFg = NewBookingGreen,
    title = "Download GST Tax Details",
    subtitle = "Export SAC 9964 Passenger Transport GST summary and Collected Net GST as PDF.",
    primaryButtonText = "Save GST PDF",
    primaryButtonColor = NewBookingGreen,
    onDismiss = onDismiss,
    onConfirm = {
      saveGstPdfLauncher.launch("GST_Tax_Details_${agencyName.replace(" ", "_")}.pdf")
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(CardSurface)
        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Text("GSTIN: $agencyGstin ($agencyName)", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = SidebarActiveBlue)
      HorizontalDivider(color = CardBorder)
      InvoiceLineRow("Total Taxable Passenger Fare Value", formatInr(taxableValue))
      InvoiceLineRow("Output CGST (${defaultGstPercent / 2.0}%)", formatInr(cgst))
      InvoiceLineRow("Output SGST (${defaultGstPercent / 2.0}%)", formatInr(sgst))
      InvoiceLineRow("Gross Output GST Collected", formatInr(totalGst))
      HorizontalDivider(color = CardBorder)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Collected Net GST", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = TextPrimaryDark)
        Text(formatInr(totalGst), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = KpiGreenIcon)
      }
    }

    if (errorText != null) {
      Text(errorText ?: "", color = Color.Red, fontSize = 12.sp)
    }

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = {
          saveGstPdfLauncher.launch("GST_Tax_Details_${agencyName.replace(" ", "_")}.pdf")
        },
        enabled = generatedFile != null,
        colors = ButtonDefaults.buttonColors(containerColor = NewBookingGreen),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("save_gst_pdf_button")
      ) {
        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Download / Save GST Details PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      OutlinedButton(
        onClick = {
          generatedFile?.let { file ->
            shareOrOpenInvoicePdf(context, file, "GST_Tax_Details", openDirectly = false)
          }
        },
        enabled = generatedFile != null,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Share GST Details PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun InvoicePdfGeneratedDialog(
  booking: Booking,
  agencyName: String,
  agencyGstin: String,
  gstPercent: Int,
  onEditInvoice: () -> Unit = {},
  onDismiss: () -> Unit,
  onStatusMessage: (String) -> Unit,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val invoiceNo = remember(booking) { buildInvoiceNumber(booking) }
  var generatedFile by remember { mutableStateOf<File?>(null) }
  var errorText by remember { mutableStateOf<String?>(null) }
  val hasGst = booking.gstAmount > 0L

  LaunchedEffect(booking) {
    val result = generateBookingInvoicePdf(
      context = context,
      booking = booking,
      agencyName = agencyName,
      agencyGstin = agencyGstin,
      gstPercent = gstPercent
    )
    result.fold(
      onSuccess = { file -> generatedFile = file },
      onFailure = { err -> errorText = err.localizedMessage ?: "Failed to generate PDF" }
    )
  }

  val savePdfLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("application/pdf")
  ) { destUri: Uri? ->
    val file = generatedFile
    if (destUri != null && file != null) {
      scope.launch {
        copyInvoicePdfToUri(file, destUri, context).fold(
          onSuccess = {
            onStatusMessage("Saved Invoice PDF ($invoiceNo.pdf) to your device!")
            onDismiss()
          },
          onFailure = { e ->
            onStatusMessage("Failed to save PDF: ${e.localizedMessage}")
          }
        )
      }
    }
  }

  CrmFormModalShell(
    badgeText = if (hasGst) "GST TAX INVOICE PDF READY" else "TRIP INVOICE PDF READY",
    badgeBg = KpiGreenBg,
    badgeFg = NewBookingGreen,
    title = "Invoice $invoiceNo Ready (PDF)",
    subtitle = "Includes Base Fare, Toll Charges, Parking Charges, and Optional GST.",
    primaryButtonText = "Download / Save PDF",
    primaryButtonColor = NewBookingGreen,
    onDismiss = onDismiss,
    onConfirm = {
      savePdfLauncher.launch("${invoiceNo}_${booking.customerName.replace(" ", "_")}.pdf")
    }
  ) {
    // Visual Invoice Preview Card matching the uploaded Taxi Invoice layout
    val displayAgency = agencyName.ifBlank { "APEX CABS" }
    val displayGstin = agencyGstin.ifBlank { "37ABCDE1234F1Z5" }
    val displayPhone = booking.driverPhone.ifBlank { booking.customerPhone.ifBlank { "+91 7670868036" } }
    val displayCity = booking.pickupLocation.ifBlank { "Vijayawada" }
    val formattedDate = com.example.data.formatToDdMmYyyy(booking.tripDate)
    val subTotal = booking.baseAmount + booking.tollCharges + booking.parkingCharges
    val effectiveGstRate = if (hasGst && booking.baseAmount > 0L) {
      ((booking.gstAmount * 100L) / booking.baseAmount).toInt().coerceAtLeast(1)
    } else {
      0
    }
    val royalBlue = Color(0xFF1D63B8)
    val badgeYellow = Color(0xFFF5B014)

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(Color.White)
        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // 1. Top Royal-Blue Header Banner
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(royalBlue)
          .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color.White)
              .padding(2.dp)
          ) {
            Image(
              painter = painterResource(id = R.drawable.taxi_invoice_logo),
              contentDescription = "Taxi Invoice Logo",
              modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp))
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
              text = displayAgency.uppercase(),
              fontWeight = FontWeight.ExtraBold,
              fontSize = 15.sp,
              color = Color.White
            )
            Text(
              text = if (displayCity.contains(",")) displayCity else "$displayCity, Andhra Pradesh",
              fontSize = 10.sp,
              color = Color(0xFFDBEAFE)
            )
            Text(
              text = "Ph: $displayPhone  |  GSTIN: $displayGstin",
              fontSize = 9.sp,
              color = Color(0xFFDBEAFE)
            )
          }
        }

        Column(
          horizontalAlignment = Alignment.End,
          verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
          Surface(
            color = badgeYellow,
            shape = RoundedCornerShape(5.dp)
          ) {
            Text(
              text = "TAX INVOICE",
              color = TextPrimaryDark,
              fontSize = 9.sp,
              fontWeight = FontWeight.ExtraBold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
          Text(
            text = invoiceNo,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp,
            color = Color.White
          )
          Text(
            text = "Date: $formattedDate",
            fontSize = 9.sp,
            color = Color(0xFFDBEAFE)
          )
        }
      }

      // 2. Two Side-by-Side Info Boxes
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Column(
          modifier = Modifier
            .weight(0.95f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          Text(
            text = "BILLED TO (PASSENGER)",
            fontSize = 8.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = royalBlue
          )
          Text(
            text = booking.customerName.ifBlank { "Passenger" },
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = TextPrimaryDark
          )
          Text(
            text = "Mobile: ${booking.customerPhone.ifBlank { "N/A" }}",
            fontSize = 10.sp,
            color = TextSecondarySlate
          )
          Text(
            text = booking.companyName.ifBlank { booking.pickupLocation.ifBlank { "N/A" } },
            fontSize = 10.sp,
            color = TextSecondarySlate
          )
        }

        Column(
          modifier = Modifier
            .weight(1.15f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          Text(
            text = "TRIP, VEHICLE & DRIVER DETAILS",
            fontSize = 8.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = royalBlue
          )
          Text(
            text = "${booking.pickupLocation.uppercase()} --> ${booking.dropLocation.uppercase()} (${booking.distanceKm} KM)",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 10.5.sp,
            color = TextPrimaryDark
          )
          val vehicleLabel = booking.vehicleModel.ifBlank { booking.vehicleCategory.ifBlank { "Sedan" } }
          Text(
            text = "Vehicle: $vehicleLabel | Reg No: ${booking.vehicleNumber.ifBlank { "N/A" }}",
            fontWeight = FontWeight.Bold,
            fontSize = 9.5.sp,
            color = TextPrimaryDark
          )
          Text(
            text = "Driver: ${booking.driverName.ifBlank { "N/A" }} | Package: One Way",
            fontSize = 9.5.sp,
            color = TextSecondarySlate
          )
        }
      }

      // 3. Fare Particulars Table
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(6.dp))
          .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(royalBlue)
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("S.NO", color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
          Text("FARE PARTICULARS / DESCRIPTION", color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
          Text("SAC CODE", color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(52.dp), textAlign = TextAlign.Center)
          Text("AMOUNT (INR)", color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(78.dp), textAlign = TextAlign.End)
        }

        // Row 1: Base Taxi Fare
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("1", fontSize = 10.sp, color = TextSecondarySlate, modifier = Modifier.width(28.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text("Base Taxi Fare (One Way)", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = TextPrimaryDark)
            Text(
              "Route: ${booking.pickupLocation.uppercase()} to ${booking.dropLocation.uppercase()} (${booking.distanceKm} KM)",
              fontSize = 8.5.sp,
              color = TextSecondarySlate
            )
          }
          Text("996412", fontSize = 9.5.sp, color = TextSecondarySlate, modifier = Modifier.width(52.dp), textAlign = TextAlign.Center)
          Text(formatRsDecimal(booking.baseAmount), fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = TextPrimaryDark, modifier = Modifier.width(78.dp), textAlign = TextAlign.End)
        }

        HorizontalDivider(color = CardBorder)

        // Row 2: Tollgate & Fastag Charges
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 8.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("2", fontSize = 10.sp, color = TextSecondarySlate, modifier = Modifier.width(28.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text("Tollgate & Fastag Charges", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = TextPrimaryDark)
            Text("Highway and expressway toll plaza fee", fontSize = 8.5.sp, color = TextSecondarySlate)
          }
          Text("996412", fontSize = 9.5.sp, color = TextSecondarySlate, modifier = Modifier.width(52.dp), textAlign = TextAlign.Center)
          Text(formatRsDecimal(booking.tollCharges), fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = TextPrimaryDark, modifier = Modifier.width(78.dp), textAlign = TextAlign.End)
        }

        if (booking.parkingCharges > 0L) {
          HorizontalDivider(color = CardBorder)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color.White)
              .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("3", fontSize = 10.sp, color = TextSecondarySlate, modifier = Modifier.width(28.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text("Parking & Entry Charges", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = TextPrimaryDark)
              Text("Airport and terminal parking fee", fontSize = 8.5.sp, color = TextSecondarySlate)
            }
            Text("996412", fontSize = 9.5.sp, color = TextSecondarySlate, modifier = Modifier.width(52.dp), textAlign = TextAlign.Center)
            Text(formatRsDecimal(booking.parkingCharges), fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = TextPrimaryDark, modifier = Modifier.width(78.dp), textAlign = TextAlign.End)
          }
        }
      }

      // 4. Bottom Summary Section
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
      ) {
        Column(
          modifier = Modifier
            .weight(1.1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = "TOTAL AMOUNT IN WORDS",
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondarySlate
          )
          Text(
            text = amountToIndianWords(booking.totalAmount),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Payment Status: ${booking.paymentStatus.uppercase()} (${booking.paymentChannel})",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = if (booking.paymentStatus.equals("Paid", ignoreCase = true)) KpiGreenIcon else KpiAmberIcon
          )
        }

        Column(
          modifier = Modifier.weight(0.95f),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Sub Total:", fontSize = 10.sp, color = TextSecondarySlate)
            Text(formatRsDecimal(subTotal), fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("GST ($effectiveGstRate%):", fontSize = 9.5.sp, color = TextSecondarySlate.copy(alpha = 0.75f))
            Text(formatRsDecimal(booking.gstAmount), fontSize = 9.5.sp, color = TextSecondarySlate.copy(alpha = 0.75f))
          }
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(royalBlue)
              .padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("GRAND TOTAL", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text(formatRsDecimal(booking.totalAmount), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
          }
        }
      }

      HorizontalDivider(color = CardBorder)

      // 5. Footer & Authorized Signatory
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Thank you for riding with $displayAgency!",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          Text(
            text = "This is a computer-generated tax invoice and does not require a physical seal.",
            fontSize = 8.sp,
            color = TextSecondarySlate
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = booking.driverName.ifBlank { displayAgency },
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )
          HorizontalDivider(
            color = TextSecondarySlate.copy(alpha = 0.5f),
            modifier = Modifier
              .width(105.dp)
              .padding(vertical = 2.dp)
          )
          Text(
            text = "Authorized Signatory",
            fontSize = 8.sp,
            color = TextSecondarySlate
          )
        }
      }
    }

    if (errorText != null) {
      Text(errorText ?: "", color = Color.Red, fontSize = 12.sp)
    }

    // Quick Action Column (up and down)
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedButton(
        onClick = {
          onDismiss()
          onEditInvoice()
        },
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Edit Invoice / Toll / GST", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = {
          savePdfLauncher.launch("${invoiceNo}_${booking.customerName.replace(" ", "_")}.pdf")
        },
        enabled = generatedFile != null,
        colors = ButtonDefaults.buttonColors(containerColor = NewBookingGreen),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("save_invoice_pdf_button")
      ) {
        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Save Invoice PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      OutlinedButton(
        onClick = {
          generatedFile?.let { file ->
            shareOrOpenInvoicePdf(context, file, invoiceNo, openDirectly = false)
          }
        },
        enabled = generatedFile != null,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("share_invoice_pdf_button")
      ) {
        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Share Invoice PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      OutlinedButton(
        onClick = {
          generatedFile?.let { file ->
            shareOrOpenInvoicePdf(context, file, invoiceNo, openDirectly = true)
          }
        },
        enabled = generatedFile != null,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("open_invoice_pdf_button")
      ) {
        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("View Invoice PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun InvoiceLineRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(label, fontSize = 12.sp, color = TextSecondarySlate)
    Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
  }
}

suspend fun generateCustomersDataPdf(
  context: Context,
  customers: List<Customer>,
  bookings: List<Booking>,
  agencyName: String,
  agencyGstin: String,
  selectedCustomer: Customer? = null,
): Result<File> = withContext(Dispatchers.IO) {
  runCatching {
    val targetCustomers = if (selectedCustomer != null) listOf(selectedCustomer) else customers
    val invoicesDir = File(context.cacheDir, "invoices").apply { mkdirs() }
    val safeName = selectedCustomer?.name?.replace(Regex("[^a-zA-Z0-9_-]"), "_")?.take(24)?.ifBlank { "Customer" }
    val fileName = if (safeName != null) "Customer_Data_${safeName}.pdf" else "Customers_Data_Report.pdf"
    val pdfFile = File(invoicesDir, fileName)

    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val navyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#0B1120")
      style = Paint.Style.FILL
    }
    val cardBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#F8FAFC")
      style = Paint.Style.FILL
    }
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#CBD5E1")
      style = Paint.Style.STROKE
      strokeWidth = 1f
    }
    val titleWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.WHITE
      textSize = 20f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val subWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#94A3B8")
      textSize = 11f
    }
    val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#0F172A")
      textSize = 13f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#1E293B")
      textSize = 11f
    }
    val boldBodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#0F172A")
      textSize = 11.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    // Header
    canvas.drawRect(0f, 0f, 595f, 108f, navyPaint)
    canvas.drawText(
      if (selectedCustomer != null) "Customer Data Profile: ${selectedCustomer.name}" else "Customers Data Report",
      36f,
      44f,
      titleWhitePaint
    )
    val headerSubtitle = buildString {
      if (agencyName.isNotBlank()) append(agencyName)
      if (agencyGstin.isNotBlank()) {
        if (isNotEmpty()) append("  •  ")
        append("GSTIN: $agencyGstin")
      }
      if (isEmpty()) append("Saved Customer Records & Trip Summary")
    }
    canvas.drawText(headerSubtitle, 36f, 68f, subWhitePaint)
    canvas.drawText(
      "Total Saved Customers in Report: ${targetCustomers.size}  •  Total Trips: ${targetCustomers.sumOf { it.totalBookings }}",
      36f,
      88f,
      subWhitePaint
    )

    var y = 132f
    if (selectedCustomer != null) {
      val boxRect = RectF(36f, y, 559f, y + 180f)
      canvas.drawRoundRect(boxRect, 10f, 10f, cardBgPaint)
      canvas.drawRoundRect(boxRect, 10f, 10f, borderPaint)

      var detailY = y + 26f
      canvas.drawText("CUSTOMER DETAILS", 52f, detailY, headingPaint)
      detailY += 24f
      canvas.drawText("Customer Name: ${selectedCustomer.name}", 52f, detailY, boldBodyPaint)
      detailY += 20f
      canvas.drawText("Company Name: ${selectedCustomer.companyName.ifBlank { "N/A" }}", 52f, detailY, bodyPaint)
      detailY += 20f
      canvas.drawText("Address: ${selectedCustomer.address.ifBlank { "N/A" }}", 52f, detailY, bodyPaint)
      detailY += 20f
      canvas.drawText("Mobile Number: ${selectedCustomer.phone.ifBlank { "N/A" }}", 52f, detailY, bodyPaint)
      detailY += 20f
      canvas.drawText("Total Trips: ${selectedCustomer.totalBookings}   |   Total Billed: ${formatInr(selectedCustomer.totalBilled)}", 52f, detailY, boldBodyPaint)

      y += 204f
      val custBookings = bookings.filter {
        it.customerName.equals(selectedCustomer.name, ignoreCase = true) ||
          (selectedCustomer.phone.isNotBlank() && it.customerPhone == selectedCustomer.phone)
      }
      canvas.drawText("ASSOCIATED BOOKINGS / TRIPS (${custBookings.size})", 36f, y, headingPaint)
      y += 14f
      val thRect = RectF(36f, y, 559f, y + 26f)
      canvas.drawRoundRect(thRect, 6f, 6f, navyPaint)
      val thWhite = Paint(titleWhitePaint).apply { textSize = 10f }
      canvas.drawText("DATE", 48f, y + 17f, thWhite)
      canvas.drawText("ROUTE", 130f, y + 17f, thWhite)
      canvas.drawText("VEHICLE", 345f, y + 17f, thWhite)
      canvas.drawText("AMOUNT", 475f, y + 17f, thWhite)
      y += 44f
      custBookings.take(14).forEach { b ->
        canvas.drawText(com.example.data.formatToDdMmYyyy(b.tripDate), 48f, y, bodyPaint)
        canvas.drawText("${b.pickupLocation} -> ${b.dropLocation}".take(32), 130f, y, bodyPaint)
        canvas.drawText("${b.vehicleCategory} ${b.vehicleModel}".trim().take(18), 345f, y, bodyPaint)
        canvas.drawText(formatInr(b.totalAmount), 475f, y, boldBodyPaint)
        canvas.drawLine(36f, y + 8f, 559f, y + 8f, borderPaint)
        y += 24f
      }
    } else {
      val thRect = RectF(36f, y, 559f, y + 28f)
      canvas.drawRoundRect(thRect, 6f, 6f, navyPaint)
      val thWhite = Paint(titleWhitePaint).apply { textSize = 10f }
      canvas.drawText("CUSTOMER NAME", 48f, y + 18f, thWhite)
      canvas.drawText("MOBILE NUMBER", 190f, y + 18f, thWhite)
      canvas.drawText("ADDRESS / COMPANY", 300f, y + 18f, thWhite)
      canvas.drawText("TOTAL TRIPS", 480f, y + 18f, thWhite)

      var rowY = y + 48f
      targetCustomers.take(22).forEach { c ->
        canvas.drawText(c.name.take(22), 48f, rowY, boldBodyPaint)
        canvas.drawText(c.phone.ifBlank { "N/A" }.take(15), 190f, rowY, bodyPaint)
        canvas.drawText(c.address.ifBlank { c.companyName.ifBlank { "N/A" } }.take(26), 300f, rowY, bodyPaint)
        canvas.drawText(c.totalBookings.toString(), 505f, rowY, boldBodyPaint)
        canvas.drawLine(36f, rowY + 8f, 559f, rowY + 8f, borderPaint)
        rowY += 26f
      }
    }

    pdfDocument.finishPage(page)
    FileOutputStream(pdfFile).use { out ->
      pdfDocument.writeTo(out)
    }
    pdfDocument.close()
    pdfFile
  }
}

@Composable
fun CustomersDataViewDialog(
  customers: List<Customer>,
  bookings: List<Booking>,
  selectedCustomer: Customer? = null,
  onDownloadPdf: (Customer?) -> Unit,
  onDismiss: () -> Unit,
) {
  val listToShow = if (selectedCustomer != null) listOf(selectedCustomer) else customers

  CrmFormModalShell(
    badgeText = "CUSTOMERS DATA VIEW",
    badgeBg = KpiBlueBg,
    badgeFg = SidebarActiveBlue,
    title = if (selectedCustomer != null) "Customer Details: ${selectedCustomer.name}" else "Customers Data View (${customers.size})",
    subtitle = "Complete view of saved Customer Name, Address, Mobile Number, Company, and Total Trips.",
    primaryButtonText = "Download PDF",
    primaryButtonColor = NewBookingGreen,
    onDismiss = onDismiss,
    onConfirm = {
      onDismiss()
      onDownloadPdf(selectedCustomer)
    }
  ) {
    if (listToShow.isEmpty()) {
      Text(
        text = "No saved customer records available.",
        fontSize = 13.sp,
        color = TextSecondarySlate
      )
    } else {
      listToShow.forEachIndexed { index, c ->
        val matchingBookings = remember(c, bookings) {
          bookings.filter {
            it.customerName.equals(c.name, ignoreCase = true) ||
              (c.phone.isNotBlank() && it.customerPhone == c.phone)
          }
        }
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardSurface)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "${index + 1}. ${c.name}",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = TextPrimaryDark
          )
          HorizontalDivider(color = CardBorder)
          InvoiceLineRow("Customer Name", c.name)
          if (c.companyName.isNotBlank()) {
            InvoiceLineRow("Company Name", c.companyName)
          }
          InvoiceLineRow("Address", c.address.ifBlank { c.companyName.ifBlank { "N/A" } })
          InvoiceLineRow("Mobile Number", c.phone.ifBlank { "N/A" })
          if (c.email.isNotBlank()) {
            InvoiceLineRow("Email", c.email)
          }
          if (c.gstin.isNotBlank()) {
            InvoiceLineRow("Customer GSTIN", c.gstin)
          }
          InvoiceLineRow("Total Trips", c.totalBookings.toString())
          if (c.totalBilled > 0L) {
            InvoiceLineRow("Total Billed Amount", formatInr(c.totalBilled))
          }
          if (matchingBookings.isNotEmpty()) {
            HorizontalDivider(color = CardBorder)
            Text(
              text = "Recent Trips (${matchingBookings.size}):",
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              color = SidebarActiveBlue
            )
            matchingBookings.take(5).forEach { b ->
              Text(
                text = "• ${com.example.data.formatToDdMmYyyy(b.tripDate)}: ${b.pickupLocation} → ${b.dropLocation} (${b.vehicleCategory} ${b.vehicleModel}) • ${formatInr(b.totalAmount)}",
                fontSize = 11.sp,
                color = TextSecondarySlate
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun CustomerDataPdfDownloadDialog(
  customers: List<Customer>,
  bookings: List<Booking>,
  agencyName: String,
  agencyGstin: String,
  selectedCustomer: Customer? = null,
  onDismiss: () -> Unit,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var generatedFile by remember { mutableStateOf<File?>(null) }
  var errorText by remember { mutableStateOf<String?>(null) }
  var statusFeedback by remember { mutableStateOf<String?>(null) }

  val targetCustomers = if (selectedCustomer != null) listOf(selectedCustomer) else customers
  val defaultPdfName = remember(selectedCustomer) {
    if (selectedCustomer != null) {
      "Customer_Data_${selectedCustomer.name.replace(" ", "_")}.pdf"
    } else {
      "Customers_Data_Report.pdf"
    }
  }

  LaunchedEffect(customers, bookings, selectedCustomer, agencyName, agencyGstin) {
    val result = generateCustomersDataPdf(
      context = context,
      customers = customers,
      bookings = bookings,
      agencyName = agencyName,
      agencyGstin = agencyGstin,
      selectedCustomer = selectedCustomer
    )
    result.fold(
      onSuccess = { file -> generatedFile = file },
      onFailure = { err -> errorText = err.localizedMessage ?: "Failed to generate Customer Data PDF" }
    )
  }

  val saveCustomerPdfLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("application/pdf")
  ) { destUri: Uri? ->
    val file = generatedFile
    if (destUri != null && file != null) {
      scope.launch {
        copyInvoicePdfToUri(file, destUri, context).fold(
          onSuccess = {
            statusFeedback = "Saved Customers Data PDF to your device!"
            onDismiss()
          },
          onFailure = { e ->
            errorText = "Failed to save PDF: ${e.localizedMessage}"
          }
        )
      }
    }
  }

  CrmFormModalShell(
    badgeText = "CUSTOMER DATA PDF",
    badgeBg = KpiGreenBg,
    badgeFg = NewBookingGreen,
    title = if (selectedCustomer != null) "Download Customer PDF: ${selectedCustomer.name}" else "Download Customers Data PDF",
    subtitle = "Export saved Customer Name, Address, Mobile Number, and Total Trips as PDF.",
    primaryButtonText = "Download / Save PDF",
    primaryButtonColor = NewBookingGreen,
    onDismiss = onDismiss,
    onConfirm = {
      saveCustomerPdfLauncher.launch(defaultPdfName)
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(CardSurface)
        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = if (selectedCustomer != null) "Customer Profile Summary" else "Customers Data Summary",
        fontWeight = FontWeight.ExtraBold,
        fontSize = 14.sp,
        color = SidebarActiveBlue
      )
      HorizontalDivider(color = CardBorder)
      InvoiceLineRow("Customers Included", targetCustomers.size.toString())
      InvoiceLineRow("Total Combined Trips", targetCustomers.sumOf { it.totalBookings }.toString())
      if (selectedCustomer != null) {
        InvoiceLineRow("Customer Name", selectedCustomer.name)
        InvoiceLineRow("Address", selectedCustomer.address.ifBlank { selectedCustomer.companyName.ifBlank { "N/A" } })
        InvoiceLineRow("Mobile Number", selectedCustomer.phone.ifBlank { "N/A" })
      }
    }

    if (errorText != null) {
      Text(errorText ?: "", color = Color.Red, fontSize = 12.sp)
    }
    if (statusFeedback != null) {
      Text(statusFeedback ?: "", color = KpiGreenIcon, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = {
          saveCustomerPdfLauncher.launch(defaultPdfName)
        },
        enabled = generatedFile != null,
        colors = ButtonDefaults.buttonColors(containerColor = NewBookingGreen),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("save_customer_data_pdf_button")
      ) {
        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Download / Save Customers Data PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      OutlinedButton(
        onClick = {
          generatedFile?.let { file ->
            shareOrOpenInvoicePdf(context, file, "Customer_Data", openDirectly = false)
          }
        },
        enabled = generatedFile != null,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Share Customers Data PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      OutlinedButton(
        onClick = {
          generatedFile?.let { file ->
            shareOrOpenInvoicePdf(context, file, "Customer_Data", openDirectly = true)
          }
        },
        enabled = generatedFile != null,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("View Customers Data PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

suspend fun generateFinancialHistoryPdf(
  context: Context,
  bookings: List<Booking>,
  vehicles: List<Vehicle>,
  agencyName: String,
  agencyGstin: String,
  reportType: String, // "ALL", "REVENUE", "EXPENSES", "PROFITS"
): Result<File> = withContext(Dispatchers.IO) {
  runCatching {
    val validBookings = bookings.filter { it.tripStatus != "Cancelled" }
    val totalRevenue = validBookings.sumOf { it.totalAmount }
    val paidRevenue = validBookings.filter { it.paymentStatus == "Paid" }.sumOf { it.totalAmount }
    val pendingRevenue = validBookings.filter { it.paymentStatus != "Paid" }.sumOf { it.totalAmount }
    val tripExpenses = validBookings.sumOf { it.expenseAmount }
    val maintenanceExpenses = vehicles.sumOf { it.maintenanceCost }
    val totalExpenses = tripExpenses + maintenanceExpenses
    val totalGst = validBookings.sumOf { it.gstAmount }
    val netProfit = totalRevenue - totalExpenses - totalGst

    val invoicesDir = File(context.cacheDir, "invoices").apply { mkdirs() }
    val fileName = when (reportType) {
      "REVENUE" -> "Revenue_History_Report.pdf"
      "EXPENSES" -> "Expenses_History_Report.pdf"
      "PROFITS" -> "Profits_History_Report.pdf"
      else -> "Revenue_Expenses_Profits_Report.pdf"
    }
    val pdfFile = File(invoicesDir, fileName)

    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val royalBluePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#1D63B8")
      style = Paint.Style.FILL
    }
    val cardBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#F8FAFC")
      style = Paint.Style.FILL
    }
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#CBD5E1")
      style = Paint.Style.STROKE
      strokeWidth = 1f
    }
    val titleWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.WHITE
      textSize = 18f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val subWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#DBEAFE")
      textSize = 10.5f
    }
    val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#0F172A")
      textSize = 12.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#1E293B")
      textSize = 10.5f
    }
    val boldBodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#0F172A")
      textSize = 11f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val rightBoldPaint = Paint(boldBodyPaint).apply {
      textAlign = Paint.Align.RIGHT
    }

    val headerTitle = when (reportType) {
      "REVENUE" -> "Revenue History Report"
      "EXPENSES" -> "Expenses History Report"
      "PROFITS" -> "Profits History Report"
      else -> "Revenue, Expenses & Profits History Report"
    }

    canvas.drawRect(0f, 0f, 595f, 102f, royalBluePaint)
    canvas.drawText(headerTitle, 36f, 44f, titleWhitePaint)
    val displayAgency = agencyName.ifBlank { "APEX CABS" }
    val displayGstin = agencyGstin.ifBlank { "37ABCDE1234F1Z5" }
    canvas.drawText("$displayAgency  |  GSTIN: $displayGstin", 36f, 66f, subWhitePaint)
    canvas.drawText("Saved Trips: ${validBookings.size}  |  Saved Vehicles: ${vehicles.size}", 36f, 86f, subWhitePaint)

    // Summary Box
    val summaryRect = RectF(36f, 120f, 559f, 290f)
    canvas.drawRoundRect(summaryRect, 10f, 10f, cardBgPaint)
    canvas.drawRoundRect(summaryRect, 10f, 10f, borderPaint)

    var y = 146f
    canvas.drawText("FINANCIAL HISTORY SUMMARY", 50f, y, headingPaint)
    y += 24f
    canvas.drawText("Total Gross Revenue (Paid: ${formatRsDecimal(paidRevenue)} | Pending: ${formatRsDecimal(pendingRevenue)})", 50f, y, bodyPaint)
    canvas.drawText(formatRsDecimal(totalRevenue), 543f, y, rightBoldPaint)
    canvas.drawLine(50f, y + 8f, 543f, y + 8f, borderPaint)

    y += 26f
    canvas.drawText("Total Trip Operating Expenses (${validBookings.size} Trips)", 50f, y, bodyPaint)
    canvas.drawText(formatRsDecimal(tripExpenses), 543f, y, rightBoldPaint)
    canvas.drawLine(50f, y + 8f, 543f, y + 8f, borderPaint)

    y += 26f
    canvas.drawText("Total Vehicle Maintenance Expenses (${vehicles.size} Vehicles)", 50f, y, bodyPaint)
    canvas.drawText(formatRsDecimal(maintenanceExpenses), 543f, y, rightBoldPaint)
    canvas.drawLine(50f, y + 8f, 543f, y + 8f, borderPaint)

    y += 26f
    canvas.drawText("Combined Total Expenses (Trip + Maintenance)", 50f, y, boldBodyPaint)
    canvas.drawText(formatRsDecimal(totalExpenses), 543f, y, rightBoldPaint)
    canvas.drawLine(50f, y + 8f, 543f, y + 8f, borderPaint)

    y += 26f
    canvas.drawText("Net Profit (Revenue - Combined Expenses - GST)", 50f, y, headingPaint)
    canvas.drawText(formatRsDecimal(netProfit), 543f, y, rightBoldPaint)

    // Ledger Table
    var tableY = 316f
    canvas.drawText("SAVED REVENUE, EXPENSES & PROFITS LEDGER", 36f, tableY, headingPaint)
    tableY += 14f
    val thRect = RectF(36f, tableY, 559f, tableY + 26f)
    canvas.drawRoundRect(thRect, 6f, 6f, royalBluePaint)
    val thWhite = Paint(titleWhitePaint).apply { textSize = 9.5f }
    canvas.drawText("DATE", 46f, tableY + 17f, thWhite)
    canvas.drawText("CUSTOMER / ROUTE", 118f, tableY + 17f, thWhite)
    canvas.drawText("REVENUE", 315f, tableY + 17f, thWhite)
    canvas.drawText("EXPENSE", 405f, tableY + 17f, thWhite)
    canvas.drawText("PROFIT", 490f, tableY + 17f, thWhite)

    var rowY = tableY + 44f
    validBookings.take(16).forEach { b ->
      val tripProfit = b.totalAmount - b.expenseAmount - b.gstAmount
      canvas.drawText(com.example.data.formatToDdMmYyyy(b.tripDate), 46f, rowY, bodyPaint)
      canvas.drawText("${b.customerName} (${b.pickupLocation}->${b.dropLocation})".take(28), 118f, rowY, bodyPaint)
      canvas.drawText(formatInr(b.totalAmount), 315f, rowY, boldBodyPaint)
      canvas.drawText(formatInr(b.expenseAmount), 405f, rowY, bodyPaint)
      canvas.drawText(formatInr(tripProfit), 490f, rowY, boldBodyPaint)
      canvas.drawLine(36f, rowY + 7f, 559f, rowY + 7f, borderPaint)
      rowY += 24f
    }

    pdfDocument.finishPage(page)
    FileOutputStream(pdfFile).use { out ->
      pdfDocument.writeTo(out)
    }
    pdfDocument.close()
    pdfFile
  }
}

@Composable
fun FinancialReportPdfDownloadDialog(
  bookings: List<Booking>,
  vehicles: List<Vehicle>,
  agencyName: String,
  agencyGstin: String,
  reportType: String,
  onDismiss: () -> Unit,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var generatedFile by remember { mutableStateOf<File?>(null) }
  var errorText by remember { mutableStateOf<String?>(null) }

  val validBookings = remember(bookings) { bookings.filter { it.tripStatus != "Cancelled" } }
  val totalRevenue = remember(validBookings) { validBookings.sumOf { it.totalAmount } }
  val tripExpenses = remember(validBookings) { validBookings.sumOf { it.expenseAmount } }
  val maintenanceExpenses = remember(vehicles) { vehicles.sumOf { it.maintenanceCost } }
  val totalExpenses = tripExpenses + maintenanceExpenses
  val totalGst = remember(validBookings) { validBookings.sumOf { it.gstAmount } }
  val netProfit = totalRevenue - totalExpenses - totalGst

  val reportTitle = when (reportType) {
    "REVENUE" -> "Save Revenue History PDF"
    "EXPENSES" -> "Save Expenses History PDF"
    "PROFITS" -> "Save Profits History PDF"
    else -> "Save Revenue, Expenses & Profits PDF"
  }
  val defaultPdfFileName = when (reportType) {
    "REVENUE" -> "Revenue_History_Report.pdf"
    "EXPENSES" -> "Expenses_History_Report.pdf"
    "PROFITS" -> "Profits_History_Report.pdf"
    else -> "Revenue_Expenses_Profits_Report.pdf"
  }

  LaunchedEffect(bookings, vehicles, agencyName, agencyGstin, reportType) {
    val result = generateFinancialHistoryPdf(
      context = context,
      bookings = bookings,
      vehicles = vehicles,
      agencyName = agencyName,
      agencyGstin = agencyGstin,
      reportType = reportType
    )
    result.fold(
      onSuccess = { file -> generatedFile = file },
      onFailure = { err -> errorText = err.localizedMessage ?: "Failed to generate report PDF" }
    )
  }

  val saveReportPdfLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("application/pdf")
  ) { destUri: Uri? ->
    val file = generatedFile
    if (destUri != null && file != null) {
      scope.launch {
        copyInvoicePdfToUri(file, destUri, context).onSuccess {
          onDismiss()
        }
      }
    }
  }

  CrmFormModalShell(
    badgeText = "REPORTS & EXPORTS PDF",
    badgeBg = KpiGreenBg,
    badgeFg = NewBookingGreen,
    title = reportTitle,
    subtitle = "",
    primaryButtonText = "Download / Save PDF",
    primaryButtonColor = NewBookingGreen,
    onDismiss = onDismiss,
    onConfirm = {
      saveReportPdfLauncher.launch(defaultPdfFileName)
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(CardSurface)
        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      InvoiceLineRow("Total Gross Revenue", formatInr(totalRevenue))
      InvoiceLineRow("Total Trip Operating Expenses", formatInr(tripExpenses))
      InvoiceLineRow("Total Vehicle Maintenance Expenses", formatInr(maintenanceExpenses))
      InvoiceLineRow("Combined Expenses", formatInr(totalExpenses))
      HorizontalDivider(color = CardBorder)
      InvoiceLineRow("Net Operating Profit", formatInr(netProfit))
    }

    if (errorText != null) {
      Text(errorText ?: "", color = Color.Red, fontSize = 12.sp)
    }

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = { saveReportPdfLauncher.launch(defaultPdfFileName) },
        enabled = generatedFile != null,
        colors = ButtonDefaults.buttonColors(containerColor = NewBookingGreen),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Download / Save PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      OutlinedButton(
        onClick = {
          generatedFile?.let { file ->
            shareOrOpenInvoicePdf(context, file, defaultPdfFileName.removeSuffix(".pdf"), openDirectly = false)
          }
        },
        enabled = generatedFile != null,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Share Report PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      OutlinedButton(
        onClick = {
          generatedFile?.let { file ->
            shareOrOpenInvoicePdf(context, file, defaultPdfFileName.removeSuffix(".pdf"), openDirectly = true)
          }
        },
        enabled = generatedFile != null,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("View Report PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}


