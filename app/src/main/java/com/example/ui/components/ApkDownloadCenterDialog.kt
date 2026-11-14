package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.ui.theme.CardBorder
import com.example.ui.theme.KpiAmberBg
import com.example.ui.theme.KpiAmberIcon
import com.example.ui.theme.KpiBlueBg
import com.example.ui.theme.KpiBlueIcon
import com.example.ui.theme.KpiGreenBg
import com.example.ui.theme.KpiGreenIcon
import com.example.ui.theme.NewBookingGreen
import com.example.ui.theme.SidebarActiveBlue
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondarySlate
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val GITHUB_REPO_SLUG = "kittu1004/Travel-crm-"
const val GITHUB_DIRECT_APK_URL = "https://github.com/kittu1004/Travel-crm-/releases/latest/download/FleetCRM-debug.apk"
const val GITHUB_RELEASES_PAGE_URL = "https://github.com/kittu1004/Travel-crm-/releases"
const val GITHUB_ACTIONS_PAGE_URL = "https://github.com/kittu1004/Travel-crm-/actions"

suspend fun copyInstalledApkToUri(context: Context, destUri: Uri): Result<Long> = withContext(Dispatchers.IO) {
  runCatching {
    val sourceApk = File(context.applicationInfo.sourceDir)
    var bytesCopied = 0L
    context.contentResolver.openOutputStream(destUri)?.use { outStream ->
      sourceApk.inputStream().use { inStream ->
        bytesCopied = inStream.copyTo(outStream)
      }
    } ?: error("Unable to open destination file stream")
    bytesCopied
  }
}

suspend fun shareInstalledApkFile(context: Context): Result<Unit> = withContext(Dispatchers.IO) {
  runCatching {
    val sourceApk = File(context.applicationInfo.sourceDir)
    val cacheApkDir = File(context.cacheDir, "apks").apply { mkdirs() }
    val exportedApk = File(cacheApkDir, "FleetCRM-debug.apk")
    sourceApk.copyTo(exportedApk, overwrite = true)

    val uri = FileProvider.getUriForFile(
      context,
      "${context.packageName}.fileprovider",
      exportedApk
    )
    withContext(Dispatchers.Main) {
      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/vnd.android.package-archive"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "FleetCRM-debug.apk")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      context.startActivity(Intent.createChooser(shareIntent, "Save or Share FleetCRM-debug.apk"))
    }
  }
}

private suspend fun checkGitHubReleaseAvailable(): Boolean = withContext(Dispatchers.IO) {
  try {
    val url = URL("https://api.github.com/repos/$GITHUB_REPO_SLUG/releases/latest")
    val conn = (url.openConnection() as HttpURLConnection).apply {
      requestMethod = "GET"
      connectTimeout = 4000
      readTimeout = 4000
      setRequestProperty("Accept", "application/vnd.github+json")
    }
    val code = conn.responseCode
    val body = if (code == 200) conn.inputStream.bufferedReader().use { it.readText() } else ""
    conn.disconnect()
    code == 200 && body.contains("FleetCRM-debug.apk")
  } catch (e: Exception) {
    false
  }
}

@Composable
fun ApkDownloadCenterDialog(
  onDismiss: () -> Unit,
  onStatusMessage: (String) -> Unit,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var isCheckingGitHub by remember { mutableStateOf(true) }
  var isGitHubReleaseLive by remember { mutableStateOf(false) }
  var isExportingLocal by remember { mutableStateOf(false) }

  val saveApkLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("application/vnd.android.package-archive")
  ) { uri: Uri? ->
    if (uri != null) {
      scope.launch {
        isExportingLocal = true
        val res = copyInstalledApkToUri(context, uri)
        isExportingLocal = false
        res.fold(
          onSuccess = { bytes ->
            val sizeMb = String.format("%.1f", bytes / (1024.0 * 1024.0))
            onStatusMessage("Saved FleetCRM-debug.apk ($sizeMb MB) directly to your device!")
            onDismiss()
          },
          onFailure = { e ->
            onStatusMessage("Failed to save APK: ${e.localizedMessage}")
          }
        )
      }
    }
  }

  LaunchedEffect(Unit) {
    isCheckingGitHub = true
    isGitHubReleaseLive = checkGitHubReleaseAvailable()
    isCheckingGitHub = false
  }

  val apkFileSizeMb = remember {
    try {
      val size = File(context.applicationInfo.sourceDir).length()
      String.format("%.1f MB", size / (1024.0 * 1024.0))
    } catch (e: Exception) {
      "Debug APK"
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column {
        Surface(color = KpiGreenBg, shape = RoundedCornerShape(6.dp)) {
          Text(
            text = "DIRECT APK DOWNLOAD & EXPORT CENTER",
            color = KpiGreenIcon,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text("Download FleetCRM-debug.apk", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // 1. Instant Local APK Save (Never 404s!)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(KpiGreenBg)
            .border(1.dp, KpiGreenIcon.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = KpiGreenIcon, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Option 1: Instant Direct APK Save ($apkFileSizeMb)",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 14.sp,
              color = TextPrimaryDark
            )
          }
          Text(
            text = "Save or share the signed FleetCRM-debug.apk directly from this running build without needing GitHub Releases (0% chance of 404 error).",
            fontSize = 12.sp,
            color = TextSecondarySlate
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = { saveApkLauncher.launch("FleetCRM-debug.apk") },
              enabled = !isExportingLocal,
              colors = ButtonDefaults.buttonColors(containerColor = NewBookingGreen),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("save_local_apk_button")
            ) {
              Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(if (isExportingLocal) "Saving..." else "Save APK to Device", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = {
                scope.launch {
                  shareInstalledApkFile(context)
                }
              },
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, KpiGreenIcon)
            ) {
              Icon(Icons.Default.Share, contentDescription = null, tint = KpiGreenIcon, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Share", fontSize = 12.sp, color = KpiGreenIcon, fontWeight = FontWeight.Bold)
            }
          }
        }

        HorizontalDivider(color = CardBorder)

        // 2. GitHub Releases Status & Explanation for 404
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isGitHubReleaseLive) KpiBlueBg else KpiAmberBg)
            .border(
              1.dp,
              if (isGitHubReleaseLive) KpiBlueIcon.copy(alpha = 0.35f) else KpiAmberIcon.copy(alpha = 0.35f),
              RoundedCornerShape(14.dp)
            )
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (isCheckingGitHub) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            } else {
              Icon(
                imageVector = if (isGitHubReleaseLive) Icons.Default.CheckCircle else Icons.Default.Info,
                contentDescription = null,
                tint = if (isGitHubReleaseLive) KpiBlueIcon else KpiAmberIcon,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isCheckingGitHub) {
                "Checking GitHub ($GITHUB_REPO_SLUG)..."
              } else if (isGitHubReleaseLive) {
                "Option 2: GitHub Release Live ($GITHUB_REPO_SLUG)"
              } else {
                "Why GitHub URL Showed 404 ($GITHUB_REPO_SLUG)"
              },
              fontWeight = FontWeight.ExtraBold,
              fontSize = 13.sp,
              color = TextPrimaryDark
            )
          }

          if (isGitHubReleaseLive) {
            Text(
              text = "A published release was found on $GITHUB_REPO_SLUG! Tap below to download directly from GitHub.",
              fontSize = 12.sp,
              color = TextSecondarySlate
            )
            Button(
              onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_DIRECT_APK_URL))
                context.startActivity(intent)
              },
              colors = ButtonDefaults.buttonColors(containerColor = SidebarActiveBlue),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Download from GitHub Release", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          } else if (!isCheckingGitHub) {
            Text(
              text = "GitHub returned 404 because this project has not been pushed to github.com/$GITHUB_REPO_SLUG yet (or the repository is still Private / building its first release).\n\n" +
                "• In Google AI Studio (Browser): Click the Settings (⚙️) menu in the top-right toolbar and choose 'Download APK' or 'Push to GitHub' -> kittu1004/Travel-crm.\n" +
                "• Inside this App right now: Tap the green 'Save APK to Device' button above to immediately save FleetCRM-debug.apk!",
              fontSize = 12.sp,
              color = TextPrimaryDark,
              lineHeight = 17.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedButton(
                onClick = {
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_RELEASES_PAGE_URL))
                  context.startActivity(intent)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("Open Releases", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
              OutlinedButton(
                onClick = {
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_ACTIONS_PAGE_URL))
                  context.startActivity(intent)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("Open Actions", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Done", fontWeight = FontWeight.Bold)
      }
    }
  )
}
