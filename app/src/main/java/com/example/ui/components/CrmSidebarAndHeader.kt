package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
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
import androidx.compose.material.icons.filled.ViewSidebar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.CrmSection
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DemoDataBlueBg
import com.example.ui.theme.DemoDataBlueBorder
import com.example.ui.theme.DemoDataBlueText
import com.example.ui.theme.DriverBadgePurple
import com.example.ui.theme.DriverBadgeText
import com.example.ui.theme.NewBookingGreen
import com.example.ui.theme.SidebarActiveBlue
import com.example.ui.theme.SidebarNavy
import com.example.ui.theme.SidebarTextMuted
import com.example.ui.theme.SidebarTextPrimary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondarySlate

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
    SidebarNavItem(CrmSection.DASHBOARD, Icons.Default.Explore, Color(0xFF60A5FA)),
    SidebarNavItem(CrmSection.LEADS, Icons.Default.PersonAdd, Color(0xFFFBBF24)),
    SidebarNavItem(CrmSection.BOOKING_HISTORY, Icons.Default.CalendarMonth, Color(0xFF38BDF8)),
    SidebarNavItem(CrmSection.INVOICES_BILLING, Icons.Default.ReceiptLong, Color(0xFF34D399)),
    SidebarNavItem(CrmSection.GST_TAX, Icons.Default.Percent, Color(0xFF2DD4BF)),
    SidebarNavItem(CrmSection.VEHICLES_MAINTENANCE, Icons.Default.DirectionsCar, Color(0xFFFACC15)),
    SidebarNavItem(
      CrmSection.DRIVER_ROSTER,
      Icons.Default.Badge,
      Color(0xFFA78BFA),
      badgeCount = driverCount
    ),
    SidebarNavItem(CrmSection.TRIP_EXPENSES, Icons.Default.SwapVert, Color(0xFFFB923C)),
    SidebarNavItem(CrmSection.EARNINGS_HISTORY, null, Color.Transparent),
    SidebarNavItem(CrmSection.CUSTOMERS_ACCOUNTS, Icons.Default.Groups, Color(0xFF60A5FA)),
    SidebarNavItem(CrmSection.USER_ROLES, Icons.Default.ManageAccounts, Color(0xFF818CF8)),
    SidebarNavItem(CrmSection.REPORTS_EXPORT, Icons.Default.BarChart, Color(0xFF4ADE80)),
    SidebarNavItem(CrmSection.SYSTEM_SETTINGS, Icons.Default.Settings, Color(0xFF94A3B8)),
  )

  Column(
    modifier = modifier
      .fillMaxHeight()
      .background(SidebarNavy)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 12.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    navItems.forEach { item ->
      val isSelected = selectedSection == item.section
      if (item.section == CrmSection.EARNINGS_HISTORY && item.icon == null) {
        // Matches the "Earnings History" text-only navigation row in the reference sidebar
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) SidebarActiveBlue else Color.Transparent)
            .clickable { onSelectSection(item.section) }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("nav_${item.section.name.lowercase()}")
        ) {
          Text(
            text = item.section.title,
            color = if (isSelected) Color.White else SidebarTextMuted,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
          )
        }
      } else {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) SidebarActiveBlue else Color.Transparent)
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
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
          }

          Text(
            text = item.section.title,
            color = if (isSelected) Color.White else SidebarTextPrimary.copy(alpha = 0.88f),
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          if (item.badgeCount != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              color = if (isSelected) Color.White.copy(alpha = 0.22f) else DriverBadgePurple.copy(alpha = 0.55f),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = item.badgeCount.toString(),
                color = if (isSelected) Color.White else DriverBadgeText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun CommandCenterHeader(
  isCompactScreen: Boolean,
  isBusy: Boolean,
  onOpenDrawer: () -> Unit,
  onLoadDemoData: () -> Unit,
  onNewBooking: () -> Unit,
  onViewWebsite: () -> Unit,
  onDownloadApk: () -> Unit = {},
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 14.dp)
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
              .size(40.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(SidebarNavy)
              .testTag("open_drawer_button")
          ) {
            Icon(
              imageVector = Icons.Default.Menu,
              contentDescription = "Open CRM Navigation Menu",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Admin Operations Command Center",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimaryDark,
            fontWeight = FontWeight.ExtraBold
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Real-time fleet performance, revenue metrics, and booking control.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondarySlate
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Top-right action buttons row matching reference screenshot:
    // [✨ Load Live Demo Data]  [+ New Booking]  [↗ View Website]
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedButton(
        onClick = onLoadDemoData,
        enabled = !isBusy,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, DemoDataBlueBorder),
        colors = ButtonDefaults.outlinedButtonColors(
          containerColor = DemoDataBlueBg,
          contentColor = DemoDataBlueText
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        modifier = Modifier.testTag("load_demo_data_button")
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isBusy) "Syncing Demo Data..." else "Load Live Demo Data",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Button(
        onClick = onNewBooking,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = NewBookingGreen,
          contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = Modifier.testTag("new_booking_button")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "New Booking",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }

      OutlinedButton(
        onClick = onViewWebsite,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CardBorder),
        colors = ButtonDefaults.outlinedButtonColors(
          containerColor = Color.White,
          contentColor = TextPrimaryDark
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        modifier = Modifier.testTag("view_website_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.OpenInNew,
          contentDescription = null,
          modifier = Modifier.size(15.dp),
          tint = TextSecondarySlate
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "View Website",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      OutlinedButton(
        onClick = onDownloadApk,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SidebarActiveBlue.copy(alpha = 0.4f)),
        colors = ButtonDefaults.outlinedButtonColors(
          containerColor = Color.White,
          contentColor = SidebarActiveBlue
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        modifier = Modifier.testTag("header_download_apk_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.OpenInNew,
          contentDescription = null,
          modifier = Modifier.size(15.dp),
          tint = SidebarActiveBlue
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Download APK",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
