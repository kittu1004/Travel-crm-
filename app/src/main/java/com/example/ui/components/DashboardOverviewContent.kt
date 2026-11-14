package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.TimeToLeave
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Booking
import com.example.data.CrmSection
import com.example.data.Customer
import com.example.data.Driver
import com.example.data.InflowCategory
import com.example.data.Vehicle
import com.example.ui.formatInr
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.FilterBarBorder
import com.example.ui.theme.FilterBarWarmBg
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
import com.example.ui.theme.TextMutedUppercase
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondarySlate
import com.example.ui.theme.ViewDetailsPeachBg
import com.example.ui.theme.ViewDetailsPeachBorder
import com.example.ui.theme.ViewDetailsPeachText

import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path

data class KpiCardSpec(
  val id: String,
  val title: String,
  val valueText: String,
  val icon: ImageVector,
  val iconTint: Color,
  val iconBg: Color,
  val gradientColors: List<Color>,
  val targetSection: CrmSection,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardOverviewContent(
  bookings: List<Booking>,
  drivers: List<Driver>,
  vehicles: List<Vehicle>,
  customers: List<Customer>,
  todayDate: String,
  selectedCategory: InflowCategory,
  columnsPerRow: Int,
  onSelectCategory: (InflowCategory) -> Unit,
  onRefreshRevenue: () -> Unit,
  onNavigateToSection: (CrmSection) -> Unit,
  onInspectKpiDetails: (KpiCardSpec) -> Unit,
  onToggleBookingPayment: (Booking) -> Unit,
) {
  val totalBookings = bookings.size
  val completedTrips = bookings.count { it.tripStatus == "Completed" }
  val cancelledTrips = bookings.count { it.tripStatus == "Cancelled" }

  val totalDrivers = drivers.size
  val totalVehicles = vehicles.size
  val totalCustomers = customers.size

  val validBookings = bookings.filter { it.tripStatus != "Cancelled" }
  val todaysRevenue = validBookings
    .filter { com.example.data.formatToDdMmYyyy(it.tripDate) == com.example.data.formatToDdMmYyyy(todayDate) && it.paymentStatus == "Paid" }
    .sumOf { it.totalAmount }

  val monthlyRevenue = validBookings
    .filter { it.paymentStatus == "Paid" }
    .sumOf { it.totalAmount }

  val pendingPayments = validBookings
    .filter { it.paymentStatus == "Pending" }
    .sumOf { it.totalAmount }

  val totalGst = validBookings
    .sumOf { it.gstAmount }

  val kpiCards = listOf(
    KpiCardSpec(
      id = "total_bookings",
      title = "TOTAL\nBOOKINGS",
      valueText = totalBookings.toString(),
      icon = Icons.Default.Receipt,
      iconTint = Color(0xFF0062E6),
      iconBg = Color(0xFFDCF2FF),
      gradientColors = listOf(Color(0xFF0058DF), Color(0xFF0099F7), Color(0xFF12BEFF)),
      targetSection = CrmSection.BOOKING_HISTORY
    ),
    KpiCardSpec(
      id = "completed_trips",
      title = "COMPLETED\nTRIPS",
      valueText = completedTrips.toString(),
      icon = Icons.Default.CheckCircle,
      iconTint = Color(0xFF1B9B28),
      iconBg = Color(0xFFE2FCE3),
      gradientColors = listOf(Color(0xFF128822), Color(0xFF28BC29), Color(0xFF3BD52A)),
      targetSection = CrmSection.BOOKING_HISTORY
    ),
    KpiCardSpec(
      id = "cancelled_trips",
      title = "CANCELLED\nTRIPS",
      valueText = cancelledTrips.toString(),
      icon = Icons.Default.Cancel,
      iconTint = Color(0xFFD91522),
      iconBg = Color(0xFFFFE8EE),
      gradientColors = listOf(Color(0xFFC8194E), Color(0xFFEE426B), Color(0xFFFF6E8E)),
      targetSection = CrmSection.BOOKING_HISTORY
    ),
    KpiCardSpec(
      id = "total_drivers",
      title = "TOTAL\nDRIVERS",
      valueText = totalDrivers.toString(),
      icon = Icons.Default.HowToReg,
      iconTint = Color(0xFF6B20E0),
      iconBg = Color(0xFFEFE6FF),
      gradientColors = listOf(Color(0xFF5A14D1), Color(0xFF833CE2), Color(0xFFA265F2)),
      targetSection = CrmSection.DRIVER_ROSTER
    ),
    KpiCardSpec(
      id = "total_vehicles",
      title = "TOTAL\nVEHICLES",
      valueText = totalVehicles.toString(),
      icon = Icons.Default.DirectionsCar,
      iconTint = Color(0xFFE06800),
      iconBg = Color(0xFFFFF4C4),
      gradientColors = listOf(Color(0xFFE56700), Color(0xFFF69608), Color(0xFFFFC115)),
      targetSection = CrmSection.VEHICLES_MAINTENANCE
    ),
    KpiCardSpec(
      id = "total_customers",
      title = "TOTAL\nCUSTOMERS",
      valueText = totalCustomers.toString(),
      icon = Icons.Default.People,
      iconTint = Color(0xFF0083A1),
      iconBg = Color(0xFFDBF9FD),
      gradientColors = listOf(Color(0xFF007590), Color(0xFF00A8C6), Color(0xFF05D2E8)),
      targetSection = CrmSection.CUSTOMERS_ACCOUNTS
    ),
    KpiCardSpec(
      id = "todays_revenue",
      title = "TODAY'S\nREVENUE",
      valueText = formatInr(todaysRevenue),
      icon = Icons.Default.CurrencyRupee,
      iconTint = Color(0xFF0068E8),
      iconBg = Color(0xFFDCF2FF),
      gradientColors = listOf(Color(0xFF005AE0), Color(0xFF00A0FA), Color(0xFF0EC3FF)),
      targetSection = CrmSection.REPORTS_EXPORT
    ),
    KpiCardSpec(
      id = "monthly_revenue",
      title = "MONTHLY\nREVENUE",
      valueText = formatInr(monthlyRevenue),
      icon = Icons.Default.TrendingUp,
      iconTint = Color(0xFF701BE0),
      iconBg = Color(0xFFF1E6FF),
      gradientColors = listOf(Color(0xFF6512D6), Color(0xFF9338EA), Color(0xFFB661F8)),
      targetSection = CrmSection.REPORTS_EXPORT
    ),
    KpiCardSpec(
      id = "pending_payments",
      title = "PENDING\nPAYMENTS",
      valueText = formatInr(pendingPayments),
      icon = Icons.Default.NotificationsActive,
      iconTint = Color(0xFFDF5900),
      iconBg = Color(0xFFFFEEAE),
      gradientColors = listOf(Color(0xFFE53A24), Color(0xFFF55D3B), Color(0xFFFF8258)),
      targetSection = CrmSection.INVOICES_BILLING
    ),
    KpiCardSpec(
      id = "gst_details",
      title = "GST\nDETAILS",
      valueText = formatInr(totalGst),
      icon = Icons.Default.Percent,
      iconTint = Color(0xFF0E8A45),
      iconBg = Color(0xFFE3FCE5),
      gradientColors = listOf(Color(0xFF158228), Color(0xFF2AB034), Color(0xFF42CF47)),
      targetSection = CrmSection.GST_TAX
    ),
  )

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Render KPI grid in rows of columnsPerRow (4 on wide screens, 2 on compact phones)
    kpiCards.chunked(columnsPerRow).forEach { rowSpecs ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        rowSpecs.forEach { spec ->
          KpiMetricCard(
            spec = spec,
            onViewDetails = { onInspectKpiDetails(spec) },
            onCardClick = { onNavigateToSection(spec.targetSection) },
            modifier = Modifier.weight(1f)
          )
        }
        val emptySlots = columnsPerRow - rowSpecs.size
        repeat(emptySlots) {
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
fun KpiMetricCard(
  spec: KpiCardSpec,
  onViewDetails: () -> Unit,
  onCardClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable { onCardClick() }
      .testTag("kpi_card_${spec.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          brush = Brush.linearGradient(
            colors = spec.gradientColors,
            start = Offset(0f, 0f),
            end = Offset(600f, 450f)
          )
        )
    ) {
      // Subtle layered wave curves matching the uploaded dashboard screenshot
      Canvas(modifier = Modifier.matchParentSize()) {
        val w = size.width
        val h = size.height

        val wave1 = Path().apply {
          moveTo(0f, h * 0.85f)
          cubicTo(
            w * 0.35f, h * 0.62f,
            w * 0.65f, h * 0.30f,
            w, h * 0.22f
          )
          lineTo(w, h)
          lineTo(0f, h)
          close()
        }
        drawPath(path = wave1, color = Color.White.copy(alpha = 0.11f))

        val wave2 = Path().apply {
          moveTo(w * 0.18f, h)
          cubicTo(
            w * 0.50f, h * 0.68f,
            w * 0.78f, h * 0.52f,
            w, h * 0.46f
          )
          lineTo(w, h)
          close()
        }
        drawPath(path = wave2, color = Color.White.copy(alpha = 0.13f))
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = spec.title,
          fontSize = 13.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Color.White,
          lineHeight = 16.sp,
          letterSpacing = 0.3.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Text(
            text = spec.valueText,
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(spec.iconBg),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = spec.icon,
              contentDescription = null,
              tint = spec.iconTint,
              modifier = Modifier.size(21.dp)
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RevenueCashflowPerformanceCard(
  bookings: List<Booking>,
  selectedCategory: InflowCategory,
  onSelectCategory: (InflowCategory) -> Unit,
  onRefreshRevenue: () -> Unit,
  onToggleBookingPayment: (Booking) -> Unit,
) {
  val filteredBookings = when (selectedCategory) {
    InflowCategory.ALL_INFLOWS -> bookings.filter { it.tripStatus != "Cancelled" }
    InflowCategory.GST_TAX_TAB -> bookings.filter { it.tripStatus != "Cancelled" && it.gstAmount > 0 }
    InflowCategory.REALIZED_PAID -> bookings.filter { it.tripStatus != "Cancelled" && it.paymentStatus == "Paid" }
    InflowCategory.PENDING_RECEIVABLES -> bookings.filter { it.tripStatus != "Cancelled" && it.paymentStatus == "Pending" }
    InflowCategory.SEDANS -> bookings.filter { it.tripStatus != "Cancelled" && it.vehicleCategory == "Sedans" }
    InflowCategory.COMPACT_SUVS -> bookings.filter { it.tripStatus != "Cancelled" && it.vehicleCategory == "Compact SUVs" }
    InflowCategory.SUVS -> bookings.filter { it.tripStatus != "Cancelled" && it.vehicleCategory == "SUVs" }
    InflowCategory.TOURIST_TRAVELLERS -> bookings.filter { it.tripStatus != "Cancelled" && it.vehicleCategory == "Tourist Travellers" }
  }

  val grossBilled = filteredBookings.sumOf { it.totalAmount }
  val realizedPaid = filteredBookings.filter { it.paymentStatus == "Paid" }.sumOf { it.totalAmount }
  val pendingReceivable = filteredBookings.filter { it.paymentStatus == "Pending" }.sumOf { it.totalAmount }
  val gstTotal = filteredBookings.sumOf { it.gstAmount }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("revenue_cashflow_card"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = CardSurface),
    border = BorderStroke(1.dp, CardBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp)
    ) {
      // Header Row with "LIVE REALIZED INFLOWS" badge and "Refresh Revenue" button
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Revenue Details & Cashflow Performance",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimaryDark,
            fontWeight = FontWeight.ExtraBold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Real-time breakdown of gross billed revenue, realized payments, outstanding receivables, payment channels, and recent transaction inflows.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondarySlate
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            color = KpiTealBg,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, KpiTealIcon.copy(alpha = 0.4f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = KpiTealIcon,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "LIVE REALIZED\nINFLOWS",
                color = KpiTealIcon,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.ExtraBold
              )
            }
          }

          TextButton(
            onClick = onRefreshRevenue,
            modifier = Modifier.testTag("refresh_revenue_button")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Refresh Revenue",
              tint = TextPrimaryDark,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Refresh\nRevenue",
              color = TextPrimaryDark,
              fontSize = 11.sp,
              lineHeight = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Warm filter bar container matching the screenshot
      Surface(
        color = FilterBarWarmBg,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, FilterBarBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        FlowRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 4.dp, top = 6.dp, bottom = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.FilterList,
              contentDescription = null,
              tint = TextMutedUppercase,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "SELECT CATEGORY TO VIEW DETAILS:",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedUppercase
            )
          }

          CategoryFilterPill(
            category = InflowCategory.ALL_INFLOWS,
            selected = selectedCategory == InflowCategory.ALL_INFLOWS,
            icon = Icons.Default.GridView,
            chipBg = Color.White,
            chipText = TextPrimaryDark,
            onClick = { onSelectCategory(InflowCategory.ALL_INFLOWS) }
          )
          CategoryFilterPill(
            category = InflowCategory.GST_TAX_TAB,
            selected = selectedCategory == InflowCategory.GST_TAX_TAB,
            icon = Icons.Default.Percent,
            chipBg = KpiTealBg,
            chipText = KpiTealIcon,
            onClick = { onSelectCategory(InflowCategory.GST_TAX_TAB) }
          )
          CategoryFilterPill(
            category = InflowCategory.REALIZED_PAID,
            selected = selectedCategory == InflowCategory.REALIZED_PAID,
            icon = Icons.Default.CheckCircle,
            chipBg = KpiGreenBg,
            chipText = KpiGreenIcon,
            onClick = { onSelectCategory(InflowCategory.REALIZED_PAID) }
          )
          CategoryFilterPill(
            category = InflowCategory.PENDING_RECEIVABLES,
            selected = selectedCategory == InflowCategory.PENDING_RECEIVABLES,
            icon = Icons.Default.Schedule,
            chipBg = KpiAmberBg,
            chipText = KpiAmberIcon,
            onClick = { onSelectCategory(InflowCategory.PENDING_RECEIVABLES) }
          )
          CategoryFilterPill(
            category = InflowCategory.SEDANS,
            selected = selectedCategory == InflowCategory.SEDANS,
            icon = Icons.Default.DirectionsCar,
            chipBg = KpiBlueBg,
            chipText = KpiBlueIcon,
            onClick = { onSelectCategory(InflowCategory.SEDANS) }
          )
          CategoryFilterPill(
            category = InflowCategory.COMPACT_SUVS,
            selected = selectedCategory == InflowCategory.COMPACT_SUVS,
            icon = Icons.Default.TimeToLeave,
            chipBg = KpiTealBg,
            chipText = KpiTealIcon,
            onClick = { onSelectCategory(InflowCategory.COMPACT_SUVS) }
          )
          CategoryFilterPill(
            category = InflowCategory.SUVS,
            selected = selectedCategory == InflowCategory.SUVS,
            icon = Icons.Default.AirportShuttle,
            chipBg = KpiOrangeBg,
            chipText = KpiOrangeIcon,
            onClick = { onSelectCategory(InflowCategory.SUVS) }
          )
          CategoryFilterPill(
            category = InflowCategory.TOURIST_TRAVELLERS,
            selected = selectedCategory == InflowCategory.TOURIST_TRAVELLERS,
            icon = Icons.Default.DirectionsBus,
            chipBg = KpiGreenBg,
            chipText = KpiGreenIcon,
            onClick = { onSelectCategory(InflowCategory.TOURIST_TRAVELLERS) }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Summary Cashflow Sub-Cards Row (matching the bottom pastel cards peeking in the screenshot)
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        CashflowSummaryMiniCard(
          label = "GROSS BILLED (${selectedCategory.label.uppercase()})",
          amount = formatInr(grossBilled),
          subtext = "${filteredBookings.size} matching trips",
          bg = Color(0xFFECF3FF),
          border = CardBorder,
          accent = Color(0xFF0A66EE),
          modifier = Modifier.weight(1f)
        )
        CashflowSummaryMiniCard(
          label = "REALIZED INFLOWS (PAID)",
          amount = formatInr(realizedPaid),
          subtext = "Settled via UPI / Bank / Cash",
          bg = KpiGreenBg,
          border = KpiGreenIcon.copy(alpha = 0.3f),
          accent = KpiGreenIcon,
          modifier = Modifier.weight(1f)
        )
        CashflowSummaryMiniCard(
          label = "OUTSTANDING RECEIVABLES",
          amount = formatInr(pendingReceivable),
          subtext = " include 5% GST (${formatInr(gstTotal)})",
          bg = KpiAmberBg,
          border = KpiAmberIcon.copy(alpha = 0.3f),
          accent = KpiAmberIcon,
          modifier = Modifier.weight(1f)
        )
      }

      if (filteredBookings.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = CardBorder)
        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Recent Inflow Transactions (${selectedCategory.label})",
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark
        )
        Spacer(modifier = Modifier.height(8.dp))

        filteredBookings.take(5).forEach { booking ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "${booking.customerName} • ${booking.vehicleCategory}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimaryDark
              )
              Text(
                text = "${booking.pickupLocation} → ${booking.dropLocation} (${booking.paymentChannel})",
                fontSize = 12.sp,
                color = TextSecondarySlate
              )
            }
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = formatInr(booking.totalAmount),
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = TextPrimaryDark
                )
                Text(
                  text = "GST: ${formatInr(booking.gstAmount)}",
                  fontSize = 11.sp,
                  color = TextSecondarySlate
                )
              }
              Surface(
                color = if (booking.paymentStatus == "Paid") KpiGreenBg else KpiAmberBg,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { onToggleBookingPayment(booking) }
              ) {
                Text(
                  text = booking.paymentStatus,
                  color = if (booking.paymentStatus == "Paid") KpiGreenIcon else KpiAmberIcon,
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

@Composable
private fun CategoryFilterPill(
  category: InflowCategory,
  selected: Boolean,
  icon: ImageVector,
  chipBg: Color,
  chipText: Color,
  onClick: () -> Unit,
) {
  Surface(
    color = chipBg,
    shape = RoundedCornerShape(8.dp),
    border = BorderStroke(
      width = if (selected) 2.dp else 1.dp,
      color = if (selected) TextPrimaryDark else chipText.copy(alpha = 0.28f)
    ),
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable { onClick() }
      .testTag("filter_pill_${category.name.lowercase()}")
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = chipText,
        modifier = Modifier.size(13.dp)
      )
      Spacer(modifier = Modifier.width(5.dp))
      Text(
        text = category.label,
        color = chipText,
        fontSize = 12.sp,
        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold
      )
    }
  }
}

@Composable
private fun CashflowSummaryMiniCard(
  label: String,
  amount: String,
  subtext: String,
  bg: Color,
  border: Color,
  accent: Color,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(bg)
      .border(1.dp, border, RoundedCornerShape(14.dp))
      .padding(14.dp)
  ) {
    Column {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = accent.copy(alpha = 0.85f)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = amount,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = TextPrimaryDark
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtext,
        fontSize = 11.sp,
        color = TextSecondarySlate
      )
    }
  }
}
