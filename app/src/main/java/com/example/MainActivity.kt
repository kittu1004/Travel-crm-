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
import com.example.auth.MobileAuthStore
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
import com.example.ui.components.AddVehicleDetailsDialog
import com.example.ui.components.AddVehicleMaintenanceDialog
import com.example.ui.components.CommandCenterHeader
import com.example.ui.components.CrmDarkSidebar
import com.example.ui.components.CrmModuleDispatcher
import com.example.ui.components.DashboardOverviewContent
import com.example.ui.components.GstTaxPdfDownloadDialog
import com.example.ui.components.InvoicePdfGeneratedDialog
import com.example.ui.components.KpiCardSpec
import com.example.ui.components.KpiDetailsDialog
import com.example.ui.components.NewBookingDialog
import com.example.ui.components.TripExpenseEntryDialog
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
  var isWindowReady by remember { mutableStateOf(false) }
  androidx.compose.runtime.LaunchedEffect(Unit) {
    androidx.compose.runtime.withFrameNanos { }
    isWindowReady = true
  }

  if (!isWindowReady) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(SidebarNavy)
    )
    return
  }

  val context = LocalContext.current
  val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
  val user = currentUser
  var isMobileLoggedIn by remember { mutableStateOf(MobileAuthStore.isLoggedIn(context)) }

  if (user == null || !isMobileLoggedIn) {
    AuthScreen(
      onAuthSuccess = {
        isMobileLoggedIn = MobileAuthStore.isLoggedIn(context)
      }
    )
  } else {
    key(user.uid) {
      FleetCrmCommandCenterScreen(
        currentUserId = user.uid,
        userEmail = user.email ?: "admin@fleetcrm.in",
        onSignedOut = { isMobileLoggedIn = false }
      )
    }
  }
}

@Composable
fun FleetCrmCommandCenterScreen(
  currentUserId: String,
  userEmail: String,
  onSignedOut: () -> Unit = {},
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

  val selectedSection by viewModel.selectedSection.collectAsStateWithLifecycle()
  val selectedInflowCategory by viewModel.selectedInflowCategory.collectAsStateWithLifecycle()
  val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
  val latestInvoiceBooking by viewModel.latestInvoiceBooking.collectAsStateWithLifecycle()
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
  var editingBooking by remember { mutableStateOf<Booking?>(null) }
  var isInvoiceFormMode by remember { mutableStateOf(false) }

  var showAddDriverDialog by remember { mutableStateOf(false) }
  var editingDriver by remember { mutableStateOf<Driver?>(null) }

  var showAddVehicleDetailsDialog by remember { mutableStateOf(false) }
  var editingVehicleDetails by remember { mutableStateOf<Vehicle?>(null) }

  var showAddVehicleMaintenanceDialog by remember { mutableStateOf(false) }
  var editingVehicleMaintenance by remember { mutableStateOf<Vehicle?>(null) }

  var showGstDownloadDialog by remember { mutableStateOf(false) }

  var showAddLeadDialog by remember { mutableStateOf(false) }
  var editingLead by remember { mutableStateOf<Lead?>(null) }

  var showAddCustomerDialog by remember { mutableStateOf(false) }
  var editingCustomer by remember { mutableStateOf<Customer?>(null) }

  var editingExpenseBooking by remember { mutableStateOf<Booking?>(null) }
  var inspectedKpiSpec by remember { mutableStateOf<KpiCardSpec?>(null) }

  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .background(CanvasBackground)
      .windowInsetsPadding(WindowInsets.safeDrawing)
  ) {
    val isWideScreen = maxWidth >= 700.dp
    val showPermanentSidebar = isWideScreen
    val kpiColumns = when {
      maxWidth >= 840.dp -> 4
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
            driverCount = drivers.size,
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
            driverCount = drivers.size,
            onSelectSection = { viewModel.selectSection(it) },
            modifier = Modifier
              .width(235.dp)
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
            isBusy = isActionBusy,
            selectedSection = selectedSection,
            onOpenDrawer = { scope.launch { drawerState.open() } },
            onLoadDemoData = { viewModel.loadLiveDemoData() },
            onNewBooking = {
              editingBooking = null
              isInvoiceFormMode = false
              showNewBookingDialog = true
            }
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
              onOpenNewBooking = {
                editingBooking = null
                isInvoiceFormMode = false
                showNewBookingDialog = true
              },
              onOpenNewInvoice = {
                editingBooking = null
                isInvoiceFormMode = true
                showNewBookingDialog = true
              },
              onDownloadGstDetails = {
                showGstDownloadDialog = true
              },
              onOpenAddLead = {
                editingLead = null
                showAddLeadDialog = true
              },
              onOpenAddDriver = {
                editingDriver = null
                showAddDriverDialog = true
              },
              onOpenAddVehicle = {
                editingVehicleDetails = null
                showAddVehicleDetailsDialog = true
              },
              onOpenAddVehicleMaintenance = {
                editingVehicleMaintenance = null
                showAddVehicleMaintenanceDialog = true
              },
              onOpenAddCustomer = {
                editingCustomer = null
                showAddCustomerDialog = true
              },
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
                val credentialManager = CredentialManager.create(context)
                onSignedOut()
                signOut(context = context, credentialManager = credentialManager, scope = scope)
              },
              onEditBooking = { booking ->
                editingBooking = booking
                isInvoiceFormMode = false
                showNewBookingDialog = true
              },
              onEditInvoice = { booking ->
                editingBooking = booking
                isInvoiceFormMode = true
                showNewBookingDialog = true
              },
              onEditLead = { lead ->
                editingLead = lead
                showAddLeadDialog = true
              },
              onEditDriver = { driver ->
                editingDriver = driver
                showAddDriverDialog = true
              },
              onEditVehicle = { vehicle ->
                editingVehicleDetails = vehicle
                showAddVehicleDetailsDialog = true
              },
              onEditVehicleMaintenance = { vehicle ->
                editingVehicleMaintenance = vehicle
                showAddVehicleMaintenanceDialog = true
              },
              onEditCustomer = { customer ->
                editingCustomer = customer
                showAddCustomerDialog = true
              },
              onDeleteCustomer = { customer ->
                if (customer.id.startsWith("booking_cust_")) {
                  val bookingId = customer.id.removePrefix("booking_cust_")
                  viewModel.deleteBooking(bookingId)
                } else {
                  viewModel.deleteCustomer(customer.id)
                }
              },
              onEditTripExpenses = { booking ->
                editingExpenseBooking = booking
              },
              onDownloadInvoicePdf = { booking ->
                viewModel.openInvoicePdfForBooking(booking)
              }
            )
          }

          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }

  if (showNewBookingDialog) {
    NewBookingDialog(
      defaultDate = viewModel.todayDateString(),
      initialBooking = editingBooking,
      isInvoiceForm = isInvoiceFormMode,
      availableDrivers = drivers,
      availableVehicles = vehicles,
      availableCustomers = customers,
      defaultGstPercent = defaultGstPercent,
      onDismiss = {
        showNewBookingDialog = false
        editingBooking = null
        isInvoiceFormMode = false
      },
      onConfirm = { name, compName, phone, pickup, drop, cat, vehModel, vehNo, driver, drvPhone, tStat, pStat, chan, base, toll, parking, gst, exp, dist, date ->
        viewModel.saveBookingDetails(
          existingBooking = editingBooking,
          customerName = name,
          companyName = compName,
          customerPhone = phone,
          pickupLocation = pickup,
          dropLocation = drop,
          vehicleCategory = cat,
          vehicleModel = vehModel,
          vehicleNumber = vehNo,
          driverName = driver,
          driverPhone = drvPhone,
          tripStatus = tStat,
          paymentStatus = pStat,
          paymentChannel = chan,
          baseAmount = base,
          tollCharges = toll,
          parkingCharges = parking,
          gstAmount = gst,
          expenseAmount = exp,
          distanceKm = dist,
          tripDate = date
        )
        editingBooking = null
        isInvoiceFormMode = false
      }
    )
  }

  if (showAddDriverDialog) {
    AddDriverDialog(
      initialDriver = editingDriver,
      availableVehicles = vehicles,
      onDismiss = {
        showAddDriverDialog = false
        editingDriver = null
      },
      onConfirm = { name, phone, address, lic, aadhar, selfieUri, docsUploaded, veh, cat, st, trips, earnings ->
        viewModel.saveDriverDetails(
          existingDriver = editingDriver,
          name = name,
          phone = phone,
          address = address,
          licenseNumber = lic,
          aadharNumber = aadhar,
          selfiePhotoUri = selfieUri,
          documentsUploaded = docsUploaded,
          assignedVehicle = veh,
          vehicleCategory = cat,
          status = st,
          tripsCompleted = trips,
          earningsTotal = earnings
        )
        editingDriver = null
      }
    )
  }

  if (showAddVehicleDetailsDialog) {
    AddVehicleDetailsDialog(
      initialVehicle = editingVehicleDetails,
      onDismiss = {
        showAddVehicleDetailsDialog = false
        editingVehicleDetails = null
      },
      onConfirm = { reg, mod, mfgYear, cat, st, cost, srvDate, nextDate, srvKm, runKm, nextKm, partsChanged, ins, insExp, fit, fitExp, pol, polExp, notes ->
        viewModel.saveVehicleDetails(
          existingVehicle = editingVehicleDetails,
          registrationNumber = reg,
          modelName = mod,
          manufacturingYear = mfgYear,
          category = cat,
          status = st,
          maintenanceCost = cost,
          serviceDate = srvDate,
          nextServiceDate = nextDate,
          serviceKilometers = srvKm,
          presentRunningKilometers = runKm,
          nextServiceKilometers = nextKm,
          partsChanged = partsChanged,
          insuranceDetails = ins,
          insuranceExpiryDate = insExp,
          fitnessDetails = fit,
          fitnessExpiryDate = fitExp,
          pollutionDetails = pol,
          pollutionExpiryDate = polExp,
          serviceNotes = notes
        )
        editingVehicleDetails = null
      }
    )
  }

  if (showAddVehicleMaintenanceDialog) {
    AddVehicleMaintenanceDialog(
      initialVehicle = editingVehicleMaintenance,
      availableVehicles = vehicles,
      onDismiss = {
        showAddVehicleMaintenanceDialog = false
        editingVehicleMaintenance = null
      },
      onConfirm = { targetVeh, reg, mod, mfgYear, cat, st, cost, srvDate, nextDate, srvKm, runKm, nextKm, partsChanged, ins, insExp, fit, fitExp, pol, polExp, notes ->
        viewModel.saveVehicleDetails(
          existingVehicle = targetVeh,
          registrationNumber = reg,
          modelName = mod,
          manufacturingYear = mfgYear,
          category = cat,
          status = st,
          maintenanceCost = cost,
          serviceDate = srvDate,
          nextServiceDate = nextDate,
          serviceKilometers = srvKm,
          presentRunningKilometers = runKm,
          nextServiceKilometers = nextKm,
          partsChanged = partsChanged,
          insuranceDetails = ins,
          insuranceExpiryDate = insExp,
          fitnessDetails = fit,
          fitnessExpiryDate = fitExp,
          pollutionDetails = pol,
          pollutionExpiryDate = polExp,
          serviceNotes = notes
        )
        editingVehicleMaintenance = null
      }
    )
  }

  if (showGstDownloadDialog) {
    GstTaxPdfDownloadDialog(
      bookings = bookings,
      agencyName = agencyName,
      agencyGstin = agencyGstin,
      defaultGstPercent = defaultGstPercent,
      onDismiss = { showGstDownloadDialog = false },
      onStatusMessage = { msg -> viewModel.showStatusMessage(msg) }
    )
  }

  if (showAddLeadDialog) {
    AddLeadDialog(
      initialLead = editingLead,
      onDismiss = {
        showAddLeadDialog = false
        editingLead = null
      },
      onConfirm = { name, phone, route, cat, quote, src, status ->
        viewModel.saveLeadDetails(
          existingLead = editingLead,
          clientName = name,
          clientPhone = phone,
          routeSummary = route,
          preferredCategory = cat,
          quotedAmount = quote,
          source = src,
          status = status
        )
        editingLead = null
      }
    )
  }

  if (showAddCustomerDialog) {
    AddCustomerDialog(
      initialCustomer = editingCustomer,
      onDismiss = {
        showAddCustomerDialog = false
        editingCustomer = null
      },
      onConfirm = { name, address, phone, email, comp, gst, totalBookings, billed, pending ->
        val persistedCustomer = editingCustomer?.takeUnless { it.id.startsWith("booking_cust_") }
        viewModel.saveCustomerDetails(
          existingCustomer = persistedCustomer,
          name = name,
          address = address,
          phone = phone,
          email = email,
          companyName = comp,
          gstin = gst,
          totalBookings = totalBookings,
          totalBilled = billed,
          pendingBalance = pending
        )
        editingCustomer = null
      }
    )
  }

  editingExpenseBooking?.let { targetBooking ->
    TripExpenseEntryDialog(
      booking = targetBooking,
      onDismiss = { editingExpenseBooking = null },
      onConfirm = { newExpense, newDistanceKm ->
        viewModel.updateBookingExpenses(targetBooking, newExpense, newDistanceKm)
        editingExpenseBooking = null
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

  latestInvoiceBooking?.let { invoiceBooking ->
    InvoicePdfGeneratedDialog(
      booking = invoiceBooking,
      agencyName = agencyName,
      agencyGstin = agencyGstin,
      gstPercent = defaultGstPercent,
      onEditInvoice = {
        editingBooking = invoiceBooking
        isInvoiceFormMode = true
        showNewBookingDialog = true
      },
      onDismiss = { viewModel.dismissInvoicePdfDialog() },
      onStatusMessage = { msg -> viewModel.showStatusMessage(msg) }
    )
  }
}
