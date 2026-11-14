package com.example.auth

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CanvasBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.KpiGreenBg
import com.example.ui.theme.KpiGreenIcon
import com.example.ui.theme.KpiRedBg
import com.example.ui.theme.KpiRedIcon
import com.example.ui.theme.SidebarActiveBlue
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondarySlate

fun signOut(
  context: Context,
  onSignOutComplete: () -> Unit = {},
) {
  MobileAuthStore.logout(context)
  onSignOutComplete()
}

@Composable
fun AuthScreen(
  onAuthSuccess: () -> Unit = {}
) {
  val context = LocalContext.current
  val focusManager = LocalFocusManager.current

  val hasAccount = remember { MobileAuthStore.hasRegisteredAccount(context) }
  // 0 = Login, 1 = Register
  var selectedTab by remember { mutableIntStateOf(if (hasAccount) 0 else 1) }

  var mobileNumber by remember { mutableStateOf(MobileAuthStore.getLastMobile(context)) }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var showPassword by remember { mutableStateOf(false) }
  var showConfirmPassword by remember { mutableStateOf(false) }

  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var infoMessage by remember { mutableStateOf<String?>(null) }
  var showForgotPasswordDialog by remember { mutableStateOf(false) }

  fun completeMobileAuth(verifiedMobile: String) {
    MobileAuthStore.markSessionLoggedIn(context, verifiedMobile)
    isLoading = false
    onAuthSuccess()
  }

  fun submitMobileForm() {
    focusManager.clearFocus()
    errorMessage = null
    infoMessage = null
    isLoading = true

    if (selectedTab == 0) {
      val result = MobileAuthStore.verifyCredentials(context, mobileNumber, password)
      result.onSuccess { verifiedMobile ->
        completeMobileAuth(verifiedMobile)
      }.onFailure { err ->
        isLoading = false
        errorMessage = err.message ?: "Invalid credentials."
      }
    } else {
      val result = MobileAuthStore.registerAccount(context, mobileNumber, password, confirmPassword)
      result.onSuccess { registeredMobile ->
        completeMobileAuth(registeredMobile)
      }.onFailure { err ->
        isLoading = false
        errorMessage = err.message ?: "Registration failed."
      }
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(CanvasBackground)
      .windowInsetsPadding(WindowInsets.safeDrawing)
      .imePadding()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top Vibrant Royal-Blue Wave Header matching the Dashboard theme
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(210.dp)
        .background(
          Brush.linearGradient(
            colors = listOf(Color(0xFF0052D4), Color(0xFF0088F5), Color(0xFF0EC3FF)),
            start = Offset(0f, 0f),
            end = Offset(900f, 500f)
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val wave1 = Path().apply {
          moveTo(0f, h * 0.78f)
          cubicTo(w * 0.35f, h * 0.55f, w * 0.68f, h * 0.35f, w, h * 0.25f)
          lineTo(w, h)
          lineTo(0f, h)
          close()
        }
        drawPath(path = wave1, color = Color.White.copy(alpha = 0.11f))

        val wave2 = Path().apply {
          moveTo(w * 0.12f, h)
          cubicTo(w * 0.48f, h * 0.65f, w * 0.80f, h * 0.52f, w, h * 0.45f)
          lineTo(w, h)
          close()
        }
        drawPath(path = wave2, color = Color.White.copy(alpha = 0.14f))
      }

      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 26.dp)
      ) {
        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.DirectionsCar,
            contentDescription = stringResource(R.string.app_name),
            tint = Color(0xFF0058DF),
            modifier = Modifier.size(36.dp)
          )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = stringResource(R.string.app_name),
          fontSize = 26.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Color.White,
          letterSpacing = 0.4.sp
        )
      }
    }

    // Main Elevated White Login Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 460.dp)
        .padding(horizontal = 20.dp)
        .offset(y = (-34).dp),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = CardSurface),
      border = BorderStroke(1.dp, CardBorder),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Segmented Pill Switcher: Login / Register
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9))
            .padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          listOf("Login", "Register").forEachIndexed { idx, label ->
            val isSelected = selectedTab == idx
            Surface(
              color = if (isSelected) SidebarActiveBlue else Color.Transparent,
              shape = RoundedCornerShape(9.dp),
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(9.dp))
                .clickable(enabled = !isLoading) {
                  selectedTab = idx
                  errorMessage = null
                  infoMessage = null
                }
                .testTag("auth_tab_${label.lowercase()}")
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(
                  text = label,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (isSelected) Color.White else TextSecondarySlate
                )
              }
            }
          }
        }

        // Mobile Number Input
        OutlinedTextField(
          value = mobileNumber,
          onValueChange = { raw ->
            val digitsOnly = raw.filter { it.isDigit() }.take(10)
            mobileNumber = digitsOnly
            errorMessage = null
          },
          label = { Text(stringResource(R.string.mobile_number_label)) },
          placeholder = { Text(stringResource(R.string.mobile_number_placeholder)) },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Phone,
              contentDescription = null,
              tint = SidebarActiveBlue
            )
          },
          prefix = {
            Text(
              text = "+91 ",
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
          },
          singleLine = true,
          enabled = !isLoading,
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next
          ),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SidebarActiveBlue,
            unfocusedBorderColor = CardBorder,
            focusedLabelColor = SidebarActiveBlue
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("login_mobile_input")
        )

        // Password Input
        OutlinedTextField(
          value = password,
          onValueChange = {
            password = it
            errorMessage = null
          },
          label = { Text(stringResource(R.string.password_label)) },
          placeholder = { Text(stringResource(R.string.password_placeholder)) },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = SidebarActiveBlue
            )
          },
          trailingIcon = {
            IconButton(
              onClick = { showPassword = !showPassword },
              modifier = Modifier.size(48.dp)
            ) {
              Icon(
                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = if (showPassword) "Hide password" else "Show password",
                tint = TextSecondarySlate
              )
            }
          },
          visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
          singleLine = true,
          enabled = !isLoading,
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = if (selectedTab == 1) ImeAction.Next else ImeAction.Done
          ),
          keyboardActions = KeyboardActions(
            onDone = {
              if (selectedTab == 0 && !isLoading) {
                submitMobileForm()
              }
            }
          ),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SidebarActiveBlue,
            unfocusedBorderColor = CardBorder,
            focusedLabelColor = SidebarActiveBlue
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("login_password_input")
        )

        // Confirm Password Input (Register mode)
        if (selectedTab == 1) {
          OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
              confirmPassword = it
              errorMessage = null
            },
            label = { Text(stringResource(R.string.confirm_password_label)) },
            placeholder = { Text(stringResource(R.string.confirm_password_placeholder)) },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = SidebarActiveBlue
              )
            },
            trailingIcon = {
              IconButton(
                onClick = { showConfirmPassword = !showConfirmPassword },
                modifier = Modifier.size(48.dp)
              ) {
                Icon(
                  imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = if (showConfirmPassword) "Hide confirm password" else "Show confirm password",
                  tint = TextSecondarySlate
                )
              }
            },
            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Password,
              imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
              onDone = {
                if (!isLoading) {
                  submitMobileForm()
                }
              }
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = SidebarActiveBlue,
              unfocusedBorderColor = CardBorder,
              focusedLabelColor = SidebarActiveBlue
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_confirm_password_input")
          )
        } else {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(
              onClick = { showForgotPasswordDialog = true },
              enabled = !isLoading,
              modifier = Modifier.testTag("forgot_password_button")
            ) {
              Text(
                text = "Forgot Password?",
                color = SidebarActiveBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        if (errorMessage != null) {
          Surface(
            color = KpiRedBg,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, KpiRedIcon.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = errorMessage ?: "",
              color = KpiRedIcon,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(12.dp),
              textAlign = TextAlign.Center
            )
          }
        }

        if (infoMessage != null) {
          Surface(
            color = KpiGreenBg,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, KpiGreenIcon.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = infoMessage ?: "",
              color = KpiGreenIcon,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(12.dp),
              textAlign = TextAlign.Center
            )
          }
        }

        // Primary Mobile Login / Register Button (no email/Google prompt)
        Button(
          onClick = { submitMobileForm() },
          enabled = !isLoading,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag(if (selectedTab == 0) "mobile_login_button" else "mobile_register_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = SidebarActiveBlue,
            contentColor = Color.White
          )
        ) {
          if (isLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(22.dp),
              color = Color.White,
              strokeWidth = 2.dp
            )
          } else {
            Text(
              text = if (selectedTab == 0) stringResource(R.string.login_button) else stringResource(R.string.register_login_button),
              fontSize = 15.sp,
              fontWeight = FontWeight.ExtraBold
            )
          }
        }
      }
    }
  }

  if (showForgotPasswordDialog) {
    ResetPasswordDialog(
      initialMobile = mobileNumber,
      onDismiss = { showForgotPasswordDialog = false },
      onResetSuccess = { updatedMobile ->
        mobileNumber = updatedMobile
        password = ""
        confirmPassword = ""
        selectedTab = 0
        showForgotPasswordDialog = false
        infoMessage = "Password updated for +91 $updatedMobile. Please login."
      }
    )
  }
}

@Composable
private fun ResetPasswordDialog(
  initialMobile: String,
  onDismiss: () -> Unit,
  onResetSuccess: (String) -> Unit,
) {
  val context = LocalContext.current

  var resetMobile by remember { mutableStateOf(initialMobile) }
  var newPassword by remember { mutableStateOf("") }
  var confirmNewPassword by remember { mutableStateOf("") }
  var showPwd by remember { mutableStateOf(false) }
  var dialogError by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = CardSurface,
    shape = RoundedCornerShape(18.dp),
    title = {
      Text(
        text = "Reset Password",
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp,
        color = TextPrimaryDark
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = resetMobile,
          onValueChange = { raw ->
            resetMobile = raw.filter { it.isDigit() }.take(10)
            dialogError = null
          },
          label = { Text("Registered Mobile Number") },
          prefix = { Text("+91 ", fontWeight = FontWeight.Bold) },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = newPassword,
          onValueChange = {
            newPassword = it
            dialogError = null
          },
          label = { Text("New Password") },
          visualTransformation = if (showPwd) VisualTransformation.None else PasswordVisualTransformation(),
          trailingIcon = {
            IconButton(onClick = { showPwd = !showPwd }) {
              Icon(
                imageVector = if (showPwd) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = "Toggle password visibility"
              )
            }
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = confirmNewPassword,
          onValueChange = {
            confirmNewPassword = it
            dialogError = null
          },
          label = { Text("Confirm New Password") },
          visualTransformation = if (showPwd) VisualTransformation.None else PasswordVisualTransformation(),
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
          modifier = Modifier.fillMaxWidth()
        )

        if (dialogError != null) {
          Text(
            text = dialogError ?: "",
            color = KpiRedIcon,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          dialogError = null
          MobileAuthStore.resetPassword(context, resetMobile, newPassword, confirmNewPassword)
            .onSuccess { onResetSuccess(it) }
            .onFailure { dialogError = it.message }
        },
        colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue)
      ) {
        Text("Update Password", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
