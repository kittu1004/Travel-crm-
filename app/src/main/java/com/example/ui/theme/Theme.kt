package com.example.ui.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

private val FleetCrmLightColorScheme = lightColorScheme(
  primary = SidebarActiveBlue,
  onPrimary = Color.White,
  primaryContainer = DemoDataBlueBg,
  onPrimaryContainer = DemoDataBlueText,
  secondary = NewBookingGreen,
  onSecondary = Color.White,
  secondaryContainer = KpiGreenBg,
  onSecondaryContainer = KpiGreenIcon,
  tertiary = KpiPurpleIcon,
  onTertiary = Color.White,
  background = CanvasBackground,
  onBackground = TextPrimaryDark,
  surface = CardSurface,
  onSurface = TextPrimaryDark,
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = TextSecondarySlate,
  outline = CardBorder,
  error = KpiRedIcon,
  onError = Color.White,
)

@Composable
fun FleetCRMTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = FleetCrmLightColorScheme,
    typography = Typography,
  ) {
    CompositionLocalProvider(
      LocalTextStyle provides LocalTextStyle.current.copy(
        fontFamily = AppBoldFontFamily,
        fontWeight = FontWeight.Bold
      ),
      content = content
    )
  }
}
