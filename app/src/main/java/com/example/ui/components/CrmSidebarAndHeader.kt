package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CrmSection
import com.example.ui.theme.CardBorder
import com.example.ui.theme.SidebarActiveBlue
import com.example.ui.theme.TextPrimaryDark

data class SidebarNavItem(
  val section: CrmSection,
  val icon: ImageVector?,
  val iconTint: Color,
  val badgeCount: Int? = null,
)

@Composable
fun CrmDarkSidebar(
  selectedSection: CrmSection,
  driverCount: Int,
  onSelectSection: (CrmSection) -> Unit,
  modifier: Modifier = Modifier,
) {
  val navItems = listOf(
    SidebarNavItem(CrmSection.DASHBOARD, Icons.Default.Explore, Color(0xFF38BDF8)),
    SidebarNavItem(CrmSection.LEADS, Icons.Default.PersonAdd, Color(0xFFFBBF24)),
    SidebarNavItem(CrmSection.BOOKING_HISTORY, Icons.Default.CalendarMonth, Color(0xFF38BDF8)),
    SidebarNavItem(CrmSection.INVOICES_BILLING, Icons.Default.ReceiptLong, Color(0xFF34D399)),
    SidebarNavItem(CrmSection.GST_TAX, Icons.Default.Percent, Color(0xFF2DD4BF)),
    SidebarNavItem(CrmSection.VEHICLES_MAINTENANCE, Icons.Default.DirectionsCar, Color(0xFFFACC15)),
    SidebarNavItem(
      CrmSection.DRIVER_ROSTER,
      Icons.Default.Badge,
      Color(0xFFF472B6),
      badgeCount = driverCount
    ),
    SidebarNavItem(CrmSection.TRIP_EXPENSES, Icons.Default.SwapVert, Color(0xFFFB7185)),
    SidebarNavItem(CrmSection.CUSTOMERS_ACCOUNTS, Icons.Default.Groups, Color(0xFF38BDF8)),
    SidebarNavItem(CrmSection.USER_ROLES, Icons.Default.ManageAccounts, Color(0xFFC4B5FD)),
    SidebarNavItem(CrmSection.REPORTS_EXPORT, Icons.Default.BarChart, Color(0xFF4ADE80)),
    SidebarNavItem(CrmSection.SYSTEM_SETTINGS, Icons.Default.Settings, Color(0xFFE2E8F0)),
  )

  val sidebarBackgroundBrush = Brush.verticalGradient(
    colors = listOf(
      Color(0xFF091B5E),
      Color(0xFF102A83),
      Color(0xFF1E329A),
      Color(0xFF392393),
      Color(0xFF4A1382)
    )
  )

  val activeItemBrush = Brush.horizontalGradient(
    colors = listOf(
      Color(0xFF24D3FF),
      Color(0xFF0BA5EC),
      Color(0xFF0284E8)
    )
  )

  Box(
    modifier = modifier
      .fillMaxHeight()
      .background(sidebarBackgroundBrush)
  ) {
    // Glowing cyan, blue, and violet-magenta wave curves at the bottom of the sidebar
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      val backGlow = Path().apply {
        moveTo(0f, h * 0.84f)
        cubicTo(w * 0.35f, h * 0.78f, w * 0.65f, h * 0.92f, w, h * 0.82f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
      }
      drawPath(
        path = backGlow,
        brush = Brush.horizontalGradient(
          colors = listOf(
            Color(0xFF06B6D4).copy(alpha = 0.32f),
            Color(0xFF7C3AED).copy(alpha = 0.40f),
            Color(0xFFDB2777).copy(alpha = 0.35f)
          )
        )
      )

      val midWave = Path().apply {
        moveTo(0f, h * 0.89f)
        cubicTo(w * 0.32f, h * 0.82f, w * 0.68f, h * 0.96f, w, h * 0.87f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
      }
      drawPath(
        path = midWave,
        brush = Brush.horizontalGradient(
          colors = listOf(
            Color(0xFF22D3EE).copy(alpha = 0.35f),
            Color(0xFF6366F1).copy(alpha = 0.42f),
            Color(0xFFA855F7).copy(alpha = 0.45f)
          )
        )
      )

      val neonLine = Path().apply {
        moveTo(0f, h * 0.89f)
        cubicTo(w * 0.32f, h * 0.82f, w * 0.68f, h * 0.96f, w, h * 0.87f)
      }
      drawPath(
        path = neonLine,
        brush = Brush.horizontalGradient(
          colors = listOf(
            Color(0xFF38BDF8).copy(alpha = 0.75f),
            Color(0xFFA855F7).copy(alpha = 0.80f),
            Color(0xFFEC4899).copy(alpha = 0.65f)
          )
        ),
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
      )

      // Soft bottom glow
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFF22D3EE).copy(alpha = 0.25f), Color.Transparent),
          center = Offset(w * 0.25f, h * 0.95f),
          radius = w * 0.55f
        ),
        center = Offset(w * 0.25f, h * 0.95f),
        radius = w * 0.55f
      )
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 12.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      navItems.forEachIndexed { index, item ->
        val isSelected = selectedSection == item.section
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(11.dp))
            .then(
              if (isSelected) {
                Modifier
                  .background(activeItemBrush)
                  .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(11.dp))
              } else {
                Modifier.background(Color.Transparent)
              }
            )
            .clickable { onSelectSection(item.section) }
            .padding(horizontal = 12.dp, vertical = 11.dp)
            .testTag("nav_${item.section.name.lowercase()}"),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (item.icon != null) {
            Icon(
              imageVector = item.icon,
              contentDescription = item.section.title,
              tint = if (isSelected) Color.White else item.iconTint,
              modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
          }

          Text(
            text = item.section.title,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.92f),
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          if (item.badgeCount != null && item.badgeCount > 0) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              color = if (isSelected) Color.White.copy(alpha = 0.24f) else Color(0xFF7C3AED).copy(alpha = 0.55f),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = item.badgeCount.toString(),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
              )
            }
          }
        }

        if (index < navItems.lastIndex && !isSelected) {
          HorizontalDivider(
            color = Color.White.copy(alpha = 0.11f),
            thickness = 0.8.dp,
            modifier = Modifier.padding(horizontal = 6.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Subtle translucent car watermark near bottom above the neon waves
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.DirectionsCar,
          contentDescription = null,
          tint = Color.White.copy(alpha = 0.18f),
          modifier = Modifier.size(56.dp)
        )
      }
    }
  }
}

@Composable
fun CommandCenterHeader(
  isCompactScreen: Boolean,
  isBusy: Boolean,
  selectedSection: CrmSection = CrmSection.DASHBOARD,
  onOpenDrawer: () -> Unit,
  onLoadDemoData: () -> Unit = {},
  onNewBooking: () -> Unit = {},
  onViewWebsite: () -> Unit = {},
  onDownloadApk: () -> Unit = {},
) {
  val isDashboard = selectedSection == CrmSection.DASHBOARD
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = if (isDashboard) 14.dp else 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        if (isCompactScreen) {
          IconButton(
            onClick = onOpenDrawer,
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(
                Brush.linearGradient(
                  colors = listOf(
                    Color(0xFF0058DF),
                    SidebarActiveBlue,
                    Color(0xFF0EA5E9)
                  )
                )
              )
              .border(
                width = 1.dp,
                color = if (isDashboard) Color(0xFF93C5FD).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.38f),
                shape = RoundedCornerShape(12.dp)
              )
              .testTag("open_drawer_button")
          ) {
            Icon(
              imageVector = Icons.Default.Menu,
              contentDescription = "Open CRM Navigation Menu",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
        }

        if (isDashboard) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Dashboard View",
              style = MaterialTheme.typography.headlineMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.ExtraBold
            )
          }
        }
      }

      if (!isDashboard) {
        Icon(
          imageVector = Icons.Default.DirectionsCar,
          contentDescription = null,
          tint = Color.White.copy(alpha = 0.85f),
          modifier = Modifier.size(30.dp)
        )
      }
    }
  }
}
