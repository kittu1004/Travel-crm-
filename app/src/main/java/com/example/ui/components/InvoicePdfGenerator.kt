package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.Booking
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

fun buildInvoiceNumber(booking: Booking): String {
  val suffix = booking.id.takeLast(4).uppercase().ifBlank {
    ((booking.customerName.hashCode() and 0xFFFF) % 9000 + 1000).toString()
  }
  return "INV-2026-$suffix"
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
    val greenAccentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#DCFCE7")
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
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }
    val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#0F172A")
      textSize = 13f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#64748B")
      textSize = 10.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#1E293B")
      textSize = 11.5f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }
    val boldBodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#0F172A")
      textSize = 12f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val totalValuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = AndroidColor.parseColor("#15803D")
      textSize = 16f
      typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val hasGst = booking.gstAmount > 0L
    val effectiveGstRate = if (hasGst && booking.baseAmount > 0L) {
      ((booking.gstAmount * 100L) / booking.baseAmount).toInt().coerceAtLeast(1)
    } else {
      gstPercent
    }

    // 1. Top Dark Navy Header Banner
    canvas.drawRect(0f, 0f, 595f, 118f, navyPaint)
    canvas.drawText(agencyName, 36f, 44f, titleWhitePaint)
    canvas.drawText(
      if (hasGst) "GSTIN: $agencyGstin  •  SAC Code: 996411 (Passenger Fleet Transport)"
      else "Passenger Fleet & Travel Operations  •  SAC Code: 996411",
      36f,
      66f,
      subWhitePaint
    )
    canvas.drawText(
      if (hasGst) "GST TAX INVOICE" else "TRIP INVOICE",
      36f,
      92f,
      Paint(titleWhitePaint).apply {
        textSize = 12f
        color = AndroidColor.parseColor("#38BDF8")
      }
    )

    val rightAlignPaint = Paint(titleWhitePaint).apply {
      textSize = 14f
      textAlign = Paint.Align.RIGHT
    }
    val rightSubPaint = Paint(subWhitePaint).apply {
      textAlign = Paint.Align.RIGHT
    }
    val formattedDate = com.example.data.formatToDdMmYyyy(booking.tripDate)
    canvas.drawText("Invoice #: $invoiceNo", 559f, 44f, rightAlignPaint)
    canvas.drawText("Date: $formattedDate", 559f, 66f, rightSubPaint)
    canvas.drawText("Status: ${booking.paymentStatus.uppercase()} (${booking.paymentChannel})", 559f, 88f, rightSubPaint)

    // 2. Customer & Driver Info Boxes
    val leftBox = RectF(36f, 140f, 288f, 275f)
    val rightBox = RectF(306f, 140f, 559f, 275f)
    canvas.drawRoundRect(leftBox, 8f, 8f, cardBgPaint)
    canvas.drawRoundRect(leftBox, 8f, 8f, borderPaint)
    canvas.drawRoundRect(rightBox, 8f, 8f, cardBgPaint)
    canvas.drawRoundRect(rightBox, 8f, 8f, borderPaint)

    canvas.drawText("CUSTOMER & COMPANY DETAILS", 50f, 160f, labelPaint)
    canvas.drawText("Customer: ${booking.customerName}", 50f, 180f, headingPaint)
    if (booking.companyName.isNotBlank()) {
      canvas.drawText("Company: ${booking.companyName}", 50f, 198f, bodyPaint)
      canvas.drawText("Phone: ${booking.customerPhone}", 50f, 216f, bodyPaint)
      canvas.drawText("Payment: ${booking.paymentStatus} (${booking.paymentChannel})", 50f, 234f, boldBodyPaint)
    } else {
      canvas.drawText("Phone: ${booking.customerPhone}", 50f, 202f, bodyPaint)
      canvas.drawText("Payment Mode: ${booking.paymentChannel}", 50f, 222f, bodyPaint)
      canvas.drawText("Payment Status: ${booking.paymentStatus}", 50f, 242f, boldBodyPaint)
    }

    canvas.drawText("DRIVER & FLEET DETAILS", 320f, 160f, labelPaint)
    canvas.drawText("Fleet Category: ${booking.vehicleCategory}", 320f, 180f, boldBodyPaint)
    canvas.drawText("Vehicle Plate: ${booking.vehicleNumber}", 320f, 198f, bodyPaint)
    canvas.drawText("Driver Name: ${booking.driverName}", 320f, 216f, bodyPaint)
    canvas.drawText(
      "Driver Phone: ${booking.driverPhone.ifBlank { booking.customerPhone }}",
      320f,
      234f,
      bodyPaint
    )
    canvas.drawText("Distance: ${booking.distanceKm} km", 320f, 252f, bodyPaint)

    // 3. Route Summary Box
    val routeBox = RectF(36f, 292f, 559f, 355f)
    canvas.drawRoundRect(routeBox, 8f, 8f, cardBgPaint)
    canvas.drawRoundRect(routeBox, 8f, 8f, borderPaint)
    canvas.drawText("ITINERARY / PICKUP & DROP ROUTE", 50f, 314f, labelPaint)
    canvas.drawText("Pickup: ${booking.pickupLocation}   →   Drop: ${booking.dropLocation}", 50f, 338f, boldBodyPaint)

    // 4. Itemized Tariff, Toll, Parking & Optional GST Table
    val tableTop = 376f
    val headerRect = RectF(36f, tableTop, 559f, tableTop + 32f)
    canvas.drawRoundRect(headerRect, 6f, 6f, navyPaint)

    val thPaint = Paint(titleWhitePaint).apply { textSize = 11f }
    val thRightPaint = Paint(thPaint).apply { textAlign = Paint.Align.RIGHT }
    canvas.drawText("DESCRIPTION OF CHARGES", 50f, tableTop + 20f, thPaint)
    canvas.drawText("SAC", 350f, tableTop + 20f, thPaint)
    canvas.drawText("AMOUNT (INR)", 545f, tableTop + 20f, thRightPaint)

    val rowRightPaint = Paint(boldBodyPaint).apply { textAlign = Paint.Align.RIGHT }
    var currentY = tableTop + 56f

    // Row 1: Base Fare
    canvas.drawText(
      "Base Fleet Charter Tariff (${booking.vehicleCategory} - ${booking.distanceKm} km)",
      50f,
      currentY,
      boldBodyPaint
    )
    canvas.drawText("996411", 350f, currentY, bodyPaint)
    canvas.drawText(formatInr(booking.baseAmount), 545f, currentY, rowRightPaint)
    canvas.drawLine(36f, currentY + 13f, 559f, currentY + 13f, borderPaint)

    // Row 2: Toll / FASTag Charges
    currentY += 34f
    canvas.drawText("Highway / FASTag Toll Charges", 50f, currentY, bodyPaint)
    canvas.drawText("996411", 350f, currentY, bodyPaint)
    canvas.drawText(formatInr(booking.tollCharges), 545f, currentY, rowRightPaint)
    canvas.drawLine(36f, currentY + 13f, 559f, currentY + 13f, borderPaint)

    // Row 3: Parking & Airport Entry Charges
    currentY += 34f
    canvas.drawText("Parking & Airport / Terminal Entry Charges", 50f, currentY, bodyPaint)
    canvas.drawText("996411", 350f, currentY, bodyPaint)
    canvas.drawText(formatInr(booking.parkingCharges), 545f, currentY, rowRightPaint)
    canvas.drawLine(36f, currentY + 13f, 559f, currentY + 13f, borderPaint)

    // Row 4+: Optional GST
    if (hasGst) {
      val halfGst = booking.gstAmount / 2L
      val otherHalfGst = booking.gstAmount - halfGst
      val halfRate = effectiveGstRate / 2.0

      currentY += 34f
      canvas.drawText("Output CGST @ $halfRate% (Optional GST)", 50f, currentY, bodyPaint)
      canvas.drawText("996411", 350f, currentY, bodyPaint)
      canvas.drawText(formatInr(halfGst), 545f, currentY, rowRightPaint)
      canvas.drawLine(36f, currentY + 13f, 559f, currentY + 13f, borderPaint)

      currentY += 34f
      canvas.drawText("Output SGST @ $halfRate% (Optional GST)", 50f, currentY, bodyPaint)
      canvas.drawText("996411", 350f, currentY, bodyPaint)
      canvas.drawText(formatInr(otherHalfGst), 545f, currentY, rowRightPaint)
      canvas.drawLine(36f, currentY + 13f, 559f, currentY + 13f, borderPaint)
    } else {
      currentY += 34f
      canvas.drawText("GST Tax (Optional - Not Applied)", 50f, currentY, bodyPaint)
      canvas.drawText("—", 350f, currentY, bodyPaint)
      canvas.drawText(formatInr(0L), 545f, currentY, rowRightPaint)
      canvas.drawLine(36f, currentY + 13f, 559f, currentY + 13f, borderPaint)
    }

    // Grand Total Banner
    currentY += 28f
    val totalRect = RectF(36f, currentY, 559f, currentY + 54f)
    canvas.drawRoundRect(totalRect, 8f, 8f, greenAccentPaint)
    canvas.drawRoundRect(totalRect, 8f, 8f, borderPaint)
    canvas.drawText(
      if (hasGst) "GRAND TOTAL INVOICE AMOUNT (INCL. TOLL, PARKING & GST)"
      else "GRAND TOTAL INVOICE AMOUNT (BASE + TOLL + PARKING)",
      50f,
      currentY + 32f,
      headingPaint
    )
    val grandRightPaint = Paint(totalValuePaint).apply { textAlign = Paint.Align.RIGHT }
    canvas.drawText(formatInr(booking.totalAmount), 545f, currentY + 33f, grandRightPaint)

    // Footer Notes & Signatory
    val footerY = 742f
    canvas.drawLine(36f, footerY, 559f, footerY, borderPaint)
    canvas.drawText(
      "Terms: Computer-generated Invoice issued by $agencyName.",
      36f,
      footerY + 24f,
      bodyPaint
    )
    canvas.drawText(
      "All disputes subject to local jurisdiction. Thank you for traveling with $agencyName!",
      36f,
      footerY + 42f,
      labelPaint
    )
    canvas.drawText(
      "Authorized Signatory",
      559f,
      footerY + 42f,
      Paint(boldBodyPaint).apply { textAlign = Paint.Align.RIGHT }
    )

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
      context.startActivity(Intent.createChooser(viewIntent, "Open Invoice $invoiceNo"))
    }
  } else {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
      type = "application/pdf"
      putExtra(Intent.EXTRA_STREAM, uri)
      putExtra(Intent.EXTRA_SUBJECT, "Invoice $invoiceNo - ${pdfFile.name}")
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    runCatching {
      context.startActivity(Intent.createChooser(shareIntent, "Share Invoice PDF ($invoiceNo)"))
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
    // Visual Invoice Preview Card inside Modal
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(CardSurface)
        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          Icon(
            imageVector = Icons.Default.PictureAsPdf,
            contentDescription = null,
            tint = KpiGreenIcon,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(agencyName, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = TextPrimaryDark)
            Text(
              if (hasGst) "GSTIN: $agencyGstin • SAC 996411" else "SAC 996411 • GST Optional (Not Applied)",
              fontSize = 11.sp,
              color = TextSecondarySlate
            )
          }
        }
        Surface(
          color = if (booking.paymentStatus == "Paid") KpiGreenBg else KpiAmberBg,
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = booking.paymentStatus.uppercase(),
            color = if (booking.paymentStatus == "Paid") KpiGreenIcon else KpiAmberIcon,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }

      HorizontalDivider(color = CardBorder)

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
          Text("CUSTOMER & COMPANY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
          Text(booking.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
          if (booking.companyName.isNotBlank()) {
            Text("Company: ${booking.companyName}", fontSize = 12.sp, color = TextPrimaryDark)
          }
          Text(booking.customerPhone, fontSize = 12.sp, color = TextSecondarySlate)
        }
        Column(horizontalAlignment = Alignment.End) {
          Text("INVOICE NO & DATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondarySlate)
          Text(invoiceNo, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = SidebarActiveBlue)
          Text(com.example.data.formatToDdMmYyyy(booking.tripDate), fontSize = 12.sp, color = TextSecondarySlate)
        }
      }

      Surface(
        color = KpiBlueBg.copy(alpha = 0.55f),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "${booking.pickupLocation} → ${booking.dropLocation}",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = TextPrimaryDark
          )
          val driverPhoneText = if (booking.driverPhone.isNotBlank()) " (${booking.driverPhone})" else ""
          Text(
            text = "Fleet: ${booking.vehicleCategory} (${booking.vehicleNumber}) • Driver: ${booking.driverName}$driverPhoneText • ${booking.distanceKm} km",
            fontSize = 11.sp,
            color = TextSecondarySlate
          )
        }
      }

      InvoiceLineRow("Base Fleet Charter Fare", formatInr(booking.baseAmount))
      InvoiceLineRow("Highway / FASTag Toll Charges", formatInr(booking.tollCharges))
      InvoiceLineRow("Parking & Entry Charges", formatInr(booking.parkingCharges))

      if (hasGst) {
        val halfGst = booking.gstAmount / 2L
        val otherHalfGst = booking.gstAmount - halfGst
        InvoiceLineRow("Output CGST (Optional GST)", formatInr(halfGst))
        InvoiceLineRow("Output SGST (Optional GST)", formatInr(otherHalfGst))
      } else {
        InvoiceLineRow("GST Tax (Optional)", "₹0 (Not Applied)")
      }

      HorizontalDivider(color = CardBorder)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("GRAND TOTAL INVOICE", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = TextPrimaryDark)
          Text("Payment Mode: ${booking.paymentChannel}", fontSize = 11.sp, color = TextSecondarySlate)
        }
        Text(
          text = formatInr(booking.totalAmount),
          fontWeight = FontWeight.ExtraBold,
          fontSize = 20.sp,
          color = KpiGreenIcon
        )
      }
    }

    if (errorText != null) {
      Text(errorText ?: "", color = Color.Red, fontSize = 12.sp)
    }

    // Quick Action Row: Edit Invoice (Toll/Parking/GST), Save PDF, Share PDF, View PDF
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedButton(
        onClick = {
          onDismiss()
          onEditInvoice()
        },
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
        modifier = Modifier.weight(1.1f)
      ) {
        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Edit Toll/GST", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
          .weight(1f)
          .testTag("save_invoice_pdf_button")
      ) {
        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Save PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
          .weight(1f)
          .testTag("share_invoice_pdf_button")
      ) {
        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Share", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
          .weight(1f)
          .testTag("open_invoice_pdf_button")
      ) {
        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
    Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
  }
}
