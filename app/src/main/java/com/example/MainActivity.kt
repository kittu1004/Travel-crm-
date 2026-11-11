package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.auth.AuthScreen
import com.example.auth.authStateFlow
import com.example.auth.signOut
import com.example.data.Booking
import com.example.data.CrmRepository
import com.example.data.CrmSection
import com.example.data.Customer
import com.example.data.Driver
import com.example.data.Lead
import com.example.data.UiState
import com.example.data.Vehicle
import com.example.ui.CrmViewModel
import com.example.ui.components.AddCustomerDialog
import com.example.ui.components.AddDriverDialog
import com.example.ui.components.AddLeadDialog
import com.example.ui.components.AddVehicleDialog
import com.example.ui.components.CommandCenterHeader
import com.example.ui.components.CrmDarkSidebar
import com.example.ui.components.CrmModuleDispatcher
import com.example.ui.components.CustomerWebsitePreviewDialog
import com.example.ui.components.DashboardOverviewContent
import com.example.ui.components.KpiCardSpec
import com.example.ui.components.KpiDetailsDialog
import com.example.ui.components.NewBookingDialog
import com.example.ui.theme.CanvasBackground
import com.example.ui.theme.FleetCRMTheme
import com.example.ui.theme.KpiGreenBg
import com.example.ui.theme.KpiGreenIcon
import com.example.ui.theme.SidebarNavy
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      FleetCRMTheme {
        AppNavigation()
      }
    }
  }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
  val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
  val user = currentUser

  if (user == null) {
    AuthScreen()
  } else {
    key(user.uid) {
      FleetCrmCommandCenterScreen(
        currentUserId = user.uid,
        userEmail = user.email ?: "admin@fleetcrm.in"
      )
    }
  }
}

@Composable
fun FleetCrmCommandCenterScreen(
  currentUserId: String,
  userEmail: String,
) {
  val viewModel: CrmViewModel = viewModel(
    key = currentUserId,
    factory = viewModelFactory {
      initializer {
        val app = checkNotNull(this[APPLICATION_KEY]) {
          "APPLICATION_KEY missing from CreationExtras"
        }
        val databaseId = app.getString(R.string.firestore_database_id)
        val db = FirebaseFirestore.getInstance(databaseId)
        CrmViewModel(CrmRepository(db), currentUserId)
      }
    }
  )

  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val credentialManager = remember { CredentialManager.create(context) }

  val selectedSection by viewModel.selectedSection.collectAsStateWithLifecycle()
  val selectedInflowCategory by viewModel.selectedInflowCategory.collectAsStateWithLifecycle()
  val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
  val isActionBusy by viewModel.isActionBusy.collectAsStateWithLifecycle()
  val agencyName by viewModel.agencyName.collectAsStateWithLifecycle()
  val agencyGstin by viewModel.agencyGstin.collectAsStateWithLifecycle()
  val defaultGstPercent by viewModel.defaultGstPercent.collectAsStateWithLifecycle()

  val bookingsState by viewModel.bookingsState.collectAsStateWithLifecycle()
  val driversState by viewModel.driversState.collectAsStateWithLifecycle()
  val vehiclesState by viewModel.vehiclesState.collectAsStateWithLifecycle()
  val leadsState by viewModel.leadsState.collectAsStateWithLifecycle()
  val customersState by viewModel.customersState.collectAsStateWithLifecycle()

  val bookings = (bookingsState as? UiState.Success<List<Booking>>)?.data ?: emptyList()
  val drivers = (driversState as? UiState.Success<List<Driver>>)?.data ?: emptyList()
  val vehicles = (vehiclesState as? UiState.Success<List<Vehicle>>)?.data ?: emptyList()
  val leads = (leadsState as? UiState.Success<List<Lead>>)?.data ?: emptyList()
  val customers = (customersState as? UiState.Success<List<Customer>>)?.data ?: emptyList()

  // Handle system back press on secondary modules to return to Dashboard Overview
  BackHandler(enabled = selectedSection != CrmSection.DASHBOARD) {
    viewModel.selectSection(CrmSection.DASHBOARD)
  }

  var showNewBookingDialog by remember { mutableStateOf(false) }
  var showAddDriverDialog by remember { mutableStateOf(false) }
  var showAddVehicleDialog by remember { mutableStateOf(false) }
  var showAddLeadDialog by remember { mutableStateOf(false) }
  var showAddCustomerDialog by remember { mutableStateOf(false) }
  var showWebsitePreviewDialog by remember { mutableStateOf(false) }
  var showApkDownloadDialog by remember { mutableStateOf(false) }
  var inspectedKpiSpec by remember { mutableStateOf<KpiCardSpec?>(null) }

  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  var isSplitSidebarVisibleOnCompact by remember { mutableStateOf(false) }

  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .background(CanvasBackground)
      .windowInsetsPadding(WindowInsets.safeDrawing)
  ) {
    val isWideScreen = maxWidth >= 700.dp
    val showPermanentSidebar = isWideScreen || isSplitSidebarVisibleOnCompact
    val kpiColumns = when {
      maxWidth >= 840.dp -> 4
      showPermanentSidebar && maxWidth < 700.dp -> 1
      else -> 2
    }

    ModalNavigationDrawer(
      drawerState = drawerState,
      gesturesEnabled = !showPermanentSidebar,
      drawerContent = {
        ModalDrawerSheet(
          drawerContainerColor = SidebarNavy,
          modifier = Modifier.width(250.dp)
        ) {
          CrmDarkSidebar(
            selectedSection = selectedSection,
            driverCount = drivers.size.coerceAtLeast(4),
            onSelectSection = { section ->
              viewModel.selectSection(section)
              scope.launch { drawerState.close() }
            },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    ) {
      Row(modifier = Modifier.fillMaxSize()) {
        if (showPermanentSidebar) {
          CrmDarkSidebar(
            selectedSection = selectedSection,
            driverCount = drivers.size.coerceAtLeast(4),
            onSelectSection = { viewModel.selectSection(it) },
            modifier = Modifier
              .width(if (isWideScreen) 235.dp else 185.dp)
              .fillMaxHeight()
          )
        }

        Column(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
          CommandCenterHeader(
            isCompactScreen = !isWideScreen,
            isSplitSidebarVisible = isSplitSidebarVisibleOnCompact,
            isBusy = isActionBusy,
            onOpenDrawer = { scope.launch { drawerState.open() } },
            onToggleSplitSidebar = { isSplitSidebarVisibleOnCompact = !isSplitSidebarVisibleOnCompact },
            onLoadDemoData = { viewModel.loadLiveDemoData() },
            onNewBooking = { showNewBookingDialog = true },
            onViewWebsite = { showWebsitePreviewDialog = true },
            onDownloadApk = { showApkDownloadDialog = true }
          )

          AnimatedVisibility(visible = statusBanner != null) {
            statusBanner?.let { msg ->
              Surface(
                color = KpiGreenBg,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 12.dp)
                  .clickable { viewModel.clearBanner() }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = msg,
                    color = KpiGreenIcon,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Dismiss",
                    color = KpiGreenIcon,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                  )
                }
              }
            }
          }

          if (selectedSection == CrmSection.DASHBOARD) {
            DashboardOverviewContent(
              bookings = bookings,
              drivers = drivers,
              vehicles = vehicles,
              customers = customers,
              todayDate = viewModel.todayDateString(),
              selectedCategory = selectedInflowCategory,
              columnsPerRow = kpiColumns,
              onSelectCategory = { viewModel.selectInflowCategory(it) },
              onRefreshRevenue = { viewModel.refreshRevenueMetrics() },
              onNavigateToSection = { viewModel.selectSection(it) },
              onInspectKpiDetails = { inspectedKpiSpec = it },
              onToggleBookingPayment = { viewModel.toggleBookingPaymentStatus(it) }
            )
          } else {
            CrmModuleDispatcher(
              section = selectedSection,
              bookings = bookings,
              drivers = drivers,
              vehicles = vehicles,
              leads = leads,
              customers = customers,
              agencyName = agencyName,
              agencyGstin = agencyGstin,
              defaultGstPercent = defaultGstPercent,
              userEmail = userEmail,
              onBackToDashboard = { viewModel.selectSection(CrmSection.DASHBOARD) },
              onOpenNewBooking = { showNewBookingDialog = true },
              onOpenAddLead = { showAddLeadDialog = true },
              onOpenAddDriver = { showAddDriverDialog = true },
              onOpenAddVehicle = { showAddVehicleDialog = true },
              onOpenAddCustomer = { showAddCustomerDialog = true },
              onConvertLead = { viewModel.convertLeadToBooking(it) },
              onDeleteLead = { viewModel.deleteLead(it) },
              onUpdateBookingStatus = { b, st -> viewModel.updateBookingStatus(b, st) },
              onToggleBookingPayment = { viewModel.toggleBookingPaymentStatus(it) },
              onDeleteBooking = { viewModel.deleteBooking(it) },
              onCycleDriverStatus = { viewModel.cycleDriverStatus(it) },
              onDeleteDriver = { viewModel.deleteDriver(it) },
              onCycleVehicleStatus = { viewModel.cycleVehicleStatus(it) },
              onDeleteVehicle = { viewModel.deleteVehicle(it) },
              onSettleCustomer = { viewModel.settleCustomerBalance(it) },
              onSaveSettings = { a, g, s -> viewModel.updateSystemSettings(a, g, s) },
              onLoadDemoData = { viewModel.loadLiveDemoData() },
              onSignOut = {
                signOut(credentialManager = credentialManager, scope = scope)
              },
              onOpenApkDownloadCenter = { showApkDownloadDialog = true }
            )
          }

          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }

  if (showApkDownloadDialog) {
    com.example.ui.components.ApkDownloadCenterDialog(
      onDismiss = { showApkDownloadDialog = false },
      onStatusMessage = {
        viewModel.refreshRevenueMetrics()
      }
    )
  }

  if (showNewBookingDialog) {
    NewBookingDialog(
      defaultDate = viewModel.todayDateString(),
      onDismiss = { showNewBookingDialog = false },
      onConfirm = { name, phone, pickup, drop, cat, vehNo, driver, tStat, pStat, chan, base, exp, dist, date ->
        viewModel.createBooking(name, phone, pickup, drop, cat, vehNo, driver, tStat, pStat, chan, base, exp, dist, date)
      }
    )
  }

  if (showAddDriverDialog) {
    AddDriverDialog(
      onDismiss = { showAddDriverDialog = false },
      onConfirm = { name, phone, lic, veh, cat, st ->
        viewModel.createDriver(name, phone, lic, veh, cat, st)
      }
    )
  }

  if (showAddVehicleDialog) {
    AddVehicleDialog(
      onDismiss = { showAddVehicleDialog = false },
      onConfirm = { reg, mod, cat, st, cost, nextDate, notes ->
        viewModel.createVehicle(reg, mod, cat, st, cost, nextDate, notes)
      }
    )
  }

  if (showAddLeadDialog) {
    AddLeadDialog(
      onDismiss = { showAddLeadDialog = false },
      onConfirm = { name, phone, route, cat, quote, src ->
        viewModel.createLead(name, phone, route, cat, quote, src)
      }
    )
  }

  if (showAddCustomerDialog) {
    AddCustomerDialog(
      onDismiss = { showAddCustomerDialog = false },
      onConfirm = { name, phone, email, comp, gst, billed, pending ->
        viewModel.createCustomer(name, phone, email, comp, gst, billed, pending)
      }
    )
  }

  if (showWebsitePreviewDialog) {
    CustomerWebsitePreviewDialog(
      agencyName = agencyName,
      onDismiss = { showWebsitePreviewDialog = false },
      onSubmitWebLead = { name, phone, route, cat, quote ->
        viewModel.createLead(name, phone, route, cat, quote, "Website")
      }
    )
  }

  inspectedKpiSpec?.let { spec ->
    KpiDetailsDialog(
      spec = spec,
      onDismiss = { inspectedKpiSpec = null },
      onOpenSection = { viewModel.selectSection(it) }
    )
  }
}
