/* ==========================================================================
   FleetCRM Complete Web Application Logic (app.js)
   - Only Mobile Number + Password Login & Registration (No Email Login)
   - Zero Default/Sample Data (Only User-Saved Records)
   - Vertically Stacked Step Forms + Next Service Note at End of Vehicle Form
   - Instant Printable Tax Invoice PDF & Financial Reports
   ========================================================================== */

const STORAGE_KEYS = {
  ACCOUNTS: 'fleetcrm_mobile_accounts',
  SESSION: 'fleetcrm_active_mobile_session',
  DATA_PREFIX: 'fleetcrm_user_data_'
};

const SECTIONS = [
  { id: 'DASHBOARD', title: 'Dashboard Overview', icon: '&#129517;', tint: '#38BDF8' },
  { id: 'LEADS', title: 'Leads', icon: '&#128100;', tint: '#FBBF24' },
  { id: 'BOOKING_HISTORY', title: 'Booking History', icon: '&#128197;', tint: '#38BDF8' },
  { id: 'INVOICES_BILLING', title: 'Invoices History', icon: '&#129534;', tint: '#34D399' },
  { id: 'GST_TAX', title: 'GST Tax Details', icon: '&#37;', tint: '#2DD4BF' },
  { id: 'VEHICLES_MAINTENANCE', title: 'Vehicles Maintenance', icon: '&#128663;', tint: '#FACC15' },
  { id: 'DRIVER_ROSTER', title: 'Driver Roster', icon: '&#129706;', tint: '#F472B6' },
  { id: 'TRIP_EXPENSES', title: 'Trip Details and Trip Expenses', icon: '&#8645;', tint: '#FB7185' },
  { id: 'CUSTOMERS_ACCOUNTS', title: 'Customer Data', icon: '&#128101;', tint: '#38BDF8' },
  { id: 'USER_ROLES', title: 'User Accounts & Roles', icon: '&#128272;', tint: '#C4B5FD' },
  { id: 'REPORTS_EXPORT', title: 'Reports & Export', icon: '&#128202;', tint: '#4ADE80' },
  { id: 'SYSTEM_SETTINGS', title: 'System Settings', icon: '&#9881;', tint: '#E2E8F0' }
];

const INFLOW_CATEGORIES = [
  'All Inflows',
  'GST Tax Tab',
  'Realized (Paid)',
  'Pending Receivables',
  'Sedans',
  'Compact SUVs',
  'SUVs',
  'Tourist Travellers'
];

let state = {
  authMode: 'login',
  activeMobile: '',
  selectedSection: 'DASHBOARD',
  selectedInflowCategory: 'All Inflows',
  selectedVehicleTab: 0, // 0 = Vehicles & Documents, 1 = Vehicle Maintenance
  selectedReportTab: 0,  // 0 = All, 1 = Revenue, 2 = Expenses, 3 = Profits
  bookings: [],
  drivers: [],
  vehicles: [],
  leads: [],
  customers: [],
  settings: {
    agencyName: '',
    agencyGstin: '',
    defaultGstPercent: 0
  }
};

function todayDdMmYyyy() {
  const d = new Date();
  const dd = String(d.getDate()).padStart(2, '0');
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const yyyy = d.getFullYear();
  return `${dd}-${mm}-${yyyy}`;
}

function formatInr(amount) {
  const num = Number(amount) || 0;
  return '₹' + num.toLocaleString('en-IN');
}

function normalizeMobile(raw) {
  return String(raw || '').replace(/\D/g, '').slice(-10);
}

/* ==========================================================================
   MOBILE NUMBER + PASSWORD AUTHENTICATION (NO EMAIL LOGIN)
   ========================================================================== */
function getAccounts() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEYS.ACCOUNTS) || '{}');
  } catch (_) {
    return {};
  }
}

function saveAccounts(accounts) {
  localStorage.setItem(STORAGE_KEYS.ACCOUNTS, JSON.stringify(accounts));
}

function switchAuthTab(mode) {
  state.authMode = mode;
  document.getElementById('tab-login').classList.toggle('active', mode === 'login');
  document.getElementById('tab-register').classList.toggle('active', mode === 'register');
  document.getElementById('confirm-password-group').style.display = mode === 'register' ? 'block' : 'none';
  document.getElementById('forgot-password-row').style.display = mode === 'login' ? 'block' : 'none';
  document.getElementById('auth-submit-btn').textContent = mode === 'login' ? 'Login' : 'Register & Login';
  hideAuthAlert();
}

function showAuthAlert(msg, type = 'error') {
  const el = document.getElementById('auth-alert');
  el.textContent = msg;
  el.className = `alert-banner ${type}`;
  el.style.display = 'block';
}

function hideAuthAlert() {
  const el = document.getElementById('auth-alert');
  if (el) el.style.display = 'none';
}

function handleAuthSubmit(e) {
  e.preventDefault();
  const mobile = normalizeMobile(document.getElementById('auth-mobile').value);
  const password = document.getElementById('auth-password').value;
  const confirmPassword = document.getElementById('auth-confirm-password').value;

  if (mobile.length !== 10) {
    return showAuthAlert('Enter a valid 10-digit mobile number.');
  }
  if (password.length < 4) {
    return showAuthAlert('Password must be at least 4 characters.');
  }

  const accounts = getAccounts();
  if (state.authMode === 'register') {
    if (password !== confirmPassword) {
      return showAuthAlert('Passwords do not match.');
    }
    accounts[mobile] = { password };
    saveAccounts(accounts);
    completeMobileLogin(mobile);
  } else {
    if (!accounts[mobile]) {
      return showAuthAlert('Mobile number not registered. Tap Register to create login.');
    }
    if (accounts[mobile].password !== password) {
      return showAuthAlert('Incorrect mobile number or password.');
    }
    completeMobileLogin(mobile);
  }
}

function completeMobileLogin(mobile) {
  state.activeMobile = mobile;
  localStorage.setItem(STORAGE_KEYS.SESSION, mobile);
  loadUserData(mobile);
  document.getElementById('auth-view').style.display = 'none';
  document.getElementById('app-view').style.display = 'flex';
  renderApp();
}

function signOutMobile() {
  localStorage.removeItem(STORAGE_KEYS.SESSION);
  state.activeMobile = '';
  document.getElementById('app-view').style.display = 'none';
  document.getElementById('auth-view').style.display = 'flex';
  document.getElementById('auth-password').value = '';
}

function openResetPasswordModal() {
  const currentMobile = document.getElementById('auth-mobile').value || '';
  openModal(`
    <div class="modal-wave-header">
      <div>
        <div class="modal-badge-pill">MOBILE PASSWORD RESET</div>
        <div class="modal-title">Reset Password</div>
      </div>
      <button class="back-white-btn" onclick="closeModal()">&#10005;</button>
    </div>
    <form onsubmit="submitPasswordReset(event)">
      <div class="modal-body-scroll">
        <div class="step-card">
          <div class="step-card-body">
            <div class="field-group">
              <label class="field-label">Registered 10-Digit Mobile Number</label>
              <div class="input-box">
                <span class="input-prefix">+91</span>
                <input type="tel" id="reset-mobile" maxlength="10" value="${currentMobile}" required />
              </div>
            </div>
            <div class="field-group">
              <label class="field-label">New Password</label>
              <div class="input-box">
                <input type="password" id="reset-new-pwd" placeholder="Enter new password" required />
              </div>
            </div>
            <div class="field-group">
              <label class="field-label">Confirm New Password</label>
              <div class="input-box">
                <input type="password" id="reset-confirm-pwd" placeholder="Confirm new password" required />
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer-bar">
        <button type="submit" class="btn-save-green">Update Password</button>
        <button type="button" class="btn-cancel-outline" onclick="closeModal()">Cancel</button>
      </div>
    </form>
  `);
}

function submitPasswordReset(e) {
  e.preventDefault();
  const mobile = normalizeMobile(document.getElementById('reset-mobile').value);
  const newPwd = document.getElementById('reset-new-pwd').value;
  const confirmPwd = document.getElementById('reset-confirm-pwd').value;
  const accounts = getAccounts();
  if (mobile.length !== 10 || !accounts[mobile]) {
    alert('Mobile number is not registered.');
    return;
  }
  if (newPwd.length < 4 || newPwd !== confirmPwd) {
    alert('Passwords must match and be at least 4 characters.');
    return;
  }
  accounts[mobile].password = newPwd;
  saveAccounts(accounts);
  closeModal();
  switchAuthTab('login');
  document.getElementById('auth-mobile').value = mobile;
  showAuthAlert(`Password updated for +91 ${mobile}. Please login.`, 'success');
}

/* ==========================================================================
   LOCAL USER DATA PERSISTENCE (SCOPED PER LOGGED-IN MOBILE NUMBER)
   ========================================================================== */
function loadUserData(mobile) {
  try {
    const raw = localStorage.getItem(STORAGE_KEYS.DATA_PREFIX + mobile);
    if (!raw) {
      state.bookings = [];
      state.drivers = [];
      state.vehicles = [];
      state.leads = [];
      state.customers = [];
      return;
    }
    const parsed = JSON.parse(raw);
    state.bookings = parsed.bookings || [];
    state.drivers = parsed.drivers || [];
    state.vehicles = parsed.vehicles || [];
    state.leads = parsed.leads || [];
    state.customers = parsed.customers || [];
    state.settings = parsed.settings || { agencyName: '', agencyGstin: '', defaultGstPercent: 0 };
  } catch (_) {}
}

function saveUserData() {
  if (!state.activeMobile) return;
  const payload = {
    bookings: state.bookings,
    drivers: state.drivers,
    vehicles: state.vehicles,
    leads: state.leads,
    customers: state.customers,
    settings: state.settings
  };
  localStorage.setItem(STORAGE_KEYS.DATA_PREFIX + state.activeMobile, JSON.stringify(payload));
}

function showStatusBanner(msg) {
  const banner = document.getElementById('status-banner');
  banner.textContent = msg + ' (Click to dismiss)';
  banner.style.display = 'block';
}

function hideStatusBanner() {
  document.getElementById('status-banner').style.display = 'none';
}

function toggleMobileSidebar() {
  document.getElementById('crm-sidebar').classList.toggle('open');
}

function selectSection(sectionId) {
  state.selectedSection = sectionId;
  document.getElementById('crm-sidebar').classList.remove('open');
  renderApp();
}

/* ==========================================================================
   MAIN RENDERING ENGINE
   ========================================================================== */
function renderApp() {
  renderSidebar();
  const isDashboard = state.selectedSection === 'DASHBOARD';
  document.getElementById('module-wave-backdrop').style.display = isDashboard ? 'none' : 'block';
  document.getElementById('dashboard-top-heading').style.display = isDashboard ? 'block' : 'none';
  document.getElementById('module-top-car-icon').style.display = isDashboard ? 'none' : 'block';

  const container = document.getElementById('section-content');
  switch (state.selectedSection) {
    case 'DASHBOARD':
      container.innerHTML = renderDashboardSection();
      break;
    case 'LEADS':
      container.innerHTML = renderLeadsSection();
      break;
    case 'BOOKING_HISTORY':
      container.innerHTML = renderBookingHistorySection();
      break;
    case 'INVOICES_BILLING':
      container.innerHTML = renderInvoicesSection();
      break;
    case 'GST_TAX':
      container.innerHTML = renderGstTaxSection();
      break;
    case 'VEHICLES_MAINTENANCE':
      container.innerHTML = renderVehiclesSection();
      break;
    case 'DRIVER_ROSTER':
      container.innerHTML = renderDriversSection();
      break;
    case 'TRIP_EXPENSES':
      container.innerHTML = renderTripExpensesSection();
      break;
    case 'CUSTOMERS_ACCOUNTS':
      container.innerHTML = renderCustomersSection();
      break;
    case 'USER_ROLES':
      container.innerHTML = renderUserRolesSection();
      break;
    case 'REPORTS_EXPORT':
      container.innerHTML = renderReportsSection();
      break;
    case 'SYSTEM_SETTINGS':
      container.innerHTML = renderSettingsSection();
      break;
  }
}

function renderSidebar() {
  const nav = document.getElementById('sidebar-nav-container');
  nav.innerHTML = SECTIONS.map((sec, idx) => {
    const isSelected = state.selectedSection === sec.id;
    const badge = sec.id === 'DRIVER_ROSTER' && state.drivers.length > 0
      ? `<span class="sidebar-badge">${state.drivers.length}</span>`
      : '';
    return `
      <button class="sidebar-nav-item ${isSelected ? 'active' : ''}" onclick="selectSection('${sec.id}')">
        <span class="sidebar-nav-icon" style="color:${isSelected ? '#FFFFFF' : sec.tint};">${sec.icon}</span>
        <span>${sec.title}</span>
        ${badge}
      </button>
      ${idx < SECTIONS.length - 1 && !isSelected ? '<div class="sidebar-divider"></div>' : ''}
    `;
  }).join('');
}

function renderModuleHeader(title, actionLabel, onActionClickJs) {
  const actionBtn = actionLabel
    ? `<button class="module-primary-action-btn" onclick="${onActionClickJs}"><span>&#43;</span> <span>${actionLabel}</span></button>`
    : '';
  return `
    <div class="module-header-row">
      <div class="module-header-left">
        <button class="back-white-btn" onclick="selectSection('DASHBOARD')" title="Back to Dashboard Overview">&#8592;</button>
        <h2 class="module-header-title">${title}</h2>
      </div>
      ${actionBtn}
    </div>
  `;
}

/* ==========================================================================
   1. DASHBOARD OVERVIEW (12 VIBRANT GRADIENT WAVE KPI CARDS)
   ========================================================================== */
function renderDashboardSection() {
  const valid = state.bookings.filter(b => b.tripStatus !== 'Cancelled');
  const today = todayDdMmYyyy();
  const todaysTrips = valid.filter(b => b.tripDate === today).length;
  const totalRevenue = valid.reduce((s, b) => s + (Number(b.totalAmount) || 0), 0);
  const realizedRevenue = valid.filter(b => b.paymentStatus === 'Paid').reduce((s, b) => s + (Number(b.totalAmount) || 0), 0);
  const pendingRevenue = valid.filter(b => b.paymentStatus !== 'Paid').reduce((s, b) => s + (Number(b.totalAmount) || 0), 0);
  const totalGst = valid.reduce((s, b) => s + (Number(b.gstAmount) || 0), 0);
  const tripExpenses = valid.reduce((s, b) => s + (Number(b.expenseAmount) || 0), 0);
  const maintExpenses = state.vehicles.reduce((s, v) => s + (Number(v.maintenanceCost) || 0), 0);
  const totalExpenses = tripExpenses + maintExpenses;
  const netProfit = totalRevenue - totalExpenses - totalGst;

  const kpis = [
    { title: 'TOTAL BOOKINGS', value: valid.length, sub: 'Saved bookings', badge: 'View History →', section: 'BOOKING_HISTORY', grad: 'linear-gradient(135deg,#0058DF,#008BF8,#0EA5E9)', icon: '&#128197;' },
    { title: "TODAY'S TRIPS", value: todaysTrips, sub: today, badge: 'View Trips →', section: 'BOOKING_HISTORY', grad: 'linear-gradient(135deg,#0284C7,#06B6D4,#22D3EE)', icon: '&#128663;' },
    { title: 'GROSS REVENUE', value: formatInr(totalRevenue), sub: 'Total billed', badge: 'Invoices →', section: 'INVOICES_BILLING', grad: 'linear-gradient(135deg,#0D9B5C,#10B981,#34D399)', icon: '&#128176;' },
    { title: 'REALIZED (PAID)', value: formatInr(realizedRevenue), sub: 'Collected inflows', badge: 'View Paid →', section: 'INVOICES_BILLING', grad: 'linear-gradient(135deg,#0891B2,#06B6D4,#2DD4BF)', icon: '&#9989;' },
    { title: 'PENDING DUES', value: formatInr(pendingRevenue), sub: 'Receivables', badge: 'Collect →', section: 'CUSTOMERS_ACCOUNTS', grad: 'linear-gradient(135deg,#E11D48,#F43F5E,#FB7185)', icon: '&#9203;' },
    { title: 'GST COLLECTED', value: formatInr(totalGst), sub: 'Tax ledger', badge: 'GST Tab →', section: 'GST_TAX', grad: 'linear-gradient(135deg,#6D28D9,#7C3AED,#A855F7)', icon: '&#37;' },
    { title: 'TRIP & FLEET EXPENSES', value: formatInr(totalExpenses), sub: 'Fuel, toll & service', badge: 'Expenses →', section: 'TRIP_EXPENSES', grad: 'linear-gradient(135deg,#EA580C,#F97316,#FBBF24)', icon: '&#9981;' },
    { title: 'NET OPERATING PROFIT', value: formatInr(netProfit), sub: 'After expenses & GST', badge: 'Reports →', section: 'REPORTS_EXPORT', grad: 'linear-gradient(135deg,#2563EB,#3B82F6,#60A5FA)', icon: '&#128200;' },
    { title: 'FLEET VEHICLES', value: state.vehicles.length, sub: 'Registered vehicles', badge: 'Fleet →', section: 'VEHICLES_MAINTENANCE', grad: 'linear-gradient(135deg,#4F46E5,#6366F1,#818CF8)', icon: '&#128661;' },
    { title: 'DRIVER ROSTER', value: state.drivers.length, sub: 'Active drivers', badge: 'Drivers →', section: 'DRIVER_ROSTER', grad: 'linear-gradient(135deg,#9333EA,#A855F7,#C084FC)', icon: '&#129706;' },
    { title: 'CUSTOMER ACCOUNTS', value: state.customers.length, sub: 'Saved clients', badge: 'Customers →', section: 'CUSTOMERS_ACCOUNTS', grad: 'linear-gradient(135deg,#0369A1,#0284C7,#38BDF8)', icon: '&#128101;' },
    { title: 'ACTIVE LEADS', value: state.leads.length, sub: 'Inquiries pipeline', badge: 'Leads →', section: 'LEADS', grad: 'linear-gradient(135deg,#D97706,#F59E0B,#FBBF24)', icon: '&#128222;' }
  ];

  const kpiCardsHtml = kpis.map(k => `
    <div class="kpi-wave-card" style="background:${k.grad};" onclick="selectSection('${k.section}')">
      <div class="kpi-card-top">
        <span class="kpi-card-title">${k.title}</span>
        <span class="kpi-icon-badge" style="color:#0A66EE;">${k.icon}</span>
      </div>
      <div class="kpi-card-value">${k.value}</div>
      <div class="kpi-card-bottom">
        <span class="kpi-subtext">${k.sub}</span>
        <span class="kpi-pill">${k.badge}</span>
      </div>
    </div>
  `).join('');

  const pillsHtml = INFLOW_CATEGORIES.map(cat => `
    <button class="category-pill ${state.selectedInflowCategory === cat ? 'active' : ''}" onclick="setInflowCategory('${cat}')">${cat}</button>
  `).join('');

  const filteredBookings = valid.filter(b => {
    const c = state.selectedInflowCategory;
    if (c === 'All Inflows') return true;
    if (c === 'GST Tax Tab') return (Number(b.gstAmount) || 0) > 0;
    if (c === 'Realized (Paid)') return b.paymentStatus === 'Paid';
    if (c === 'Pending Receivables') return b.paymentStatus !== 'Paid';
    if (c === 'Sedans') return b.vehicleCategory === 'Sedan' || b.vehicleCategory === 'Sedans';
    return b.vehicleCategory === c;
  });

  const recentRows = filteredBookings.length === 0
    ? `<div style="padding:18px; text-align:center; color:var(--text-secondary);">No saved transactions in "${state.selectedInflowCategory}" yet.</div>`
    : filteredBookings.slice(0, 6).map(b => `
        <div style="display:flex; justify-content:space-between; align-items:center; padding:10px 0; border-bottom:1px solid var(--card-border);">
          <div>
            <div style="font-size:14px; color:var(--text-primary);">${b.customerName} • ${b.vehicleCategory}</div>
            <div style="font-size:12px; color:var(--text-secondary);">${b.pickupLocation} → ${b.dropLocation} (${b.paymentChannel})</div>
          </div>
          <div style="display:flex; align-items:center; gap:10px;">
            <div style="text-align:right;">
              <div style="font-size:14px;">${formatInr(b.totalAmount)}</div>
              <div style="font-size:11px; color:var(--text-secondary);">GST: ${formatInr(b.gstAmount)}</div>
            </div>
            <span class="status-badge ${b.paymentStatus === 'Paid' ? 'green' : 'amber'}" style="cursor:pointer;" onclick="toggleBookingPayment('${b.id}')">${b.paymentStatus}</span>
          </div>
        </div>
      `).join('');

  return `
    <div class="kpi-grid">${kpiCardsHtml}</div>
    <div class="crm-white-card">
      <div class="card-header-line">
        <div class="card-main-heading">Revenue Details &amp; Cashflow Performance</div>
        <button class="btn-blue-filled" onclick="openBookingModal()">&#43; New Booking</button>
      </div>
      <div class="filter-pills-row">${pillsHtml}</div>
      <div>${recentRows}</div>
    </div>
  `;
}

function setInflowCategory(cat) {
  state.selectedInflowCategory = cat;
  renderApp();
}

/* ==========================================================================
   2. BOOKING HISTORY & INVOICES & GST
   ========================================================================== */
function renderBookingHistorySection() {
  const header = renderModuleHeader('Booking History', 'New Booking', 'openBookingModal()');
  if (state.bookings.length === 0) {
    return header + `<div class="crm-white-card" style="text-align:center; padding:28px;">No saved bookings yet. Tap "+ New Booking" to create a booking.</div>`;
  }
  const cards = state.bookings.map(b => `
    <div class="crm-white-card">
      <div class="card-header-line">
        <div class="card-title-group">
          <div class="blue-icon-tile">&#128663;</div>
          <div>
            <div class="card-main-heading">${b.customerName} (${b.vehicleCategory}${b.vehicleModel ? ' • ' + b.vehicleModel : ''})</div>
            <div class="card-route-sub">${b.pickupLocation} → ${b.dropLocation}</div>
          </div>
        </div>
        <span class="status-badge ${b.tripStatus === 'Completed' ? 'green' : b.tripStatus === 'Cancelled' ? 'red' : 'green'}">${b.tripStatus}</span>
      </div>
      <div class="meta-info-list">
        <div class="meta-info-item"><span class="icon">&#128100;</span>Driver: ${b.driverName || 'Unassigned'} ${b.driverPhone ? '(' + b.driverPhone + ')' : ''}</div>
        <div class="meta-info-item"><span class="icon">&#128663;</span>Vehicle: ${b.vehicleModel ? b.vehicleModel + ' - ' : ''}${b.vehicleNumber || 'N/A'}</div>
        <div class="meta-info-item"><span class="icon">&#128197;</span>Date: ${b.tripDate}</div>
      </div>
      <div class="icy-summary-box">
        <div>
          <div class="icy-total-text">Total: ${formatInr(b.totalAmount)}</div>
          <div class="icy-breakdown-text">Base: ${formatInr(b.baseAmount)} • Toll: ${formatInr(b.tollCharges)} • GST: ${formatInr(b.gstAmount)}</div>
        </div>
        <span class="status-badge ${b.paymentStatus === 'Paid' ? 'green' : 'amber'}" style="cursor:pointer;" onclick="toggleBookingPayment('${b.id}')">${b.paymentStatus} (${b.paymentChannel})</span>
      </div>
      <div class="card-actions-row">
        <button class="btn-blue-filled" onclick="openInvoicePreviewModal('${b.id}')">&#128196; Invoice PDF</button>
        <button class="btn-outlined" onclick="openBookingModal('${b.id}')">&#9998; Edit Details</button>
        ${b.tripStatus !== 'Completed' ? `<button class="btn-outlined" onclick="setBookingStatus('${b.id}','Completed')">&#10003; Complete</button>` : ''}
        <button class="btn-outlined danger" onclick="deleteBooking('${b.id}')">&#128465; Delete</button>
      </div>
    </div>
  `).join('');
  return header + cards;
}

function renderInvoicesSection() {
  const header = renderModuleHeader('Invoices History', 'Generate New Invoice', 'openBookingModal(null, true)');
  if (state.bookings.length === 0) {
    return header + `<div class="crm-white-card" style="text-align:center; padding:28px;">No saved invoices yet. Tap "+ Generate New Invoice" to create one.</div>`;
  }
  return header + state.bookings.map(b => `
    <div class="crm-white-card">
      <div class="card-header-line">
        <div class="card-title-group">
          <div class="blue-icon-tile">&#129534;</div>
          <div>
            <div class="card-main-heading">${b.customerName} ${b.companyName ? '• ' + b.companyName : ''}</div>
            <div class="card-route-sub">${b.pickupLocation} → ${b.dropLocation} (${b.tripDate})</div>
          </div>
        </div>
        <span class="status-badge ${b.paymentStatus === 'Paid' ? 'green' : 'amber'}" style="cursor:pointer;" onclick="toggleBookingPayment('${b.id}')">${b.paymentStatus}</span>
      </div>
      <div class="icy-summary-box">
        <div class="icy-total-text">Invoice Total: ${formatInr(b.totalAmount)}</div>
        <div class="icy-breakdown-text">Base: ${formatInr(b.baseAmount)} • Toll: ${formatInr(b.tollCharges)} • Parking: ${formatInr(b.parkingCharges)} • GST: ${formatInr(b.gstAmount)}</div>
      </div>
      <div class="card-actions-row">
        <button class="btn-blue-filled" onclick="openInvoicePreviewModal('${b.id}')">&#128196; View / Print Invoice PDF</button>
        <button class="btn-outlined" onclick="openBookingModal('${b.id}', true)">&#9998; Edit Invoice</button>
      </div>
    </div>
  `).join('');
}

function renderGstTaxSection() {
  const header = renderModuleHeader('GST Tax Details', 'Download GST Details', 'window.print()');
  const taxable = state.bookings.filter(b => b.tripStatus !== 'Cancelled');
  const totalBase = taxable.reduce((s, b) => s + (Number(b.baseAmount) || 0), 0);
  const totalGst = taxable.reduce((s, b) => s + (Number(b.gstAmount) || 0), 0);
  const cgst = Math.round(totalGst / 2);
  const sgst = totalGst - cgst;

  return header + `
    <div class="crm-white-card">
      <div class="card-main-heading" style="margin-bottom:10px;">GST Return &amp; Tax Summary (${state.settings.agencyName || 'Travel Agency'} • GSTIN: ${state.settings.agencyGstin || 'Unregistered'})</div>
      <div class="icy-summary-box">
        <div>
          <div class="icy-total-text">Total GST Collected: ${formatInr(totalGst)}</div>
          <div class="icy-breakdown-text">Taxable Base: ${formatInr(totalBase)} • CGST: ${formatInr(cgst)} • SGST: ${formatInr(sgst)}</div>
        </div>
        <button class="btn-blue-filled" onclick="window.print()">&#128196; Print / Save GST PDF</button>
      </div>
    </div>
  `;
}

/* ==========================================================================
   3. VEHICLES MAINTENANCE (WITH NEXT SERVICE NOTE AT END OF FORM)
   ========================================================================== */
function renderVehiclesSection() {
  const actionLabel = state.selectedVehicleTab === 0 ? 'Add Vehicle & Documents' : 'Add Vehicle Maintenance';
  const actionFn = state.selectedVehicleTab === 0 ? 'openVehicleModal(null, false)' : 'openVehicleModal(null, true)';
  const header = renderModuleHeader('Vehicles Maintenance', actionLabel, actionFn);

  const switcher = `
    <div class="segmented-tabs" style="max-width:480px; margin-bottom:14px;">
      <button class="segmented-tab-btn ${state.selectedVehicleTab === 0 ? 'active' : ''}" onclick="state.selectedVehicleTab=0; renderApp();">Vehicles &amp; Documents</button>
      <button class="segmented-tab-btn ${state.selectedVehicleTab === 1 ? 'active' : ''}" onclick="state.selectedVehicleTab=1; renderApp();">Vehicle Maintenance</button>
    </div>
  `;

  if (state.vehicles.length === 0) {
    return header + switcher + `<div class="crm-white-card" style="text-align:center; padding:28px;">No saved vehicles in registry yet. Tap "+ ${actionLabel}" above.</div>`;
  }

  const cards = state.vehicles.map(v => {
    if (state.selectedVehicleTab === 0) {
      return `
        <div class="crm-white-card">
          <div class="card-header-line">
            <div class="card-title-group">
              <div class="blue-icon-tile">&#128663;</div>
              <div>
                <div class="card-main-heading">${v.modelName} (${v.registrationNumber})</div>
                <div class="card-route-sub">${v.category} • Mfg Year: ${v.manufacturingYear || 'N/A'}</div>
              </div>
            </div>
            <span class="status-badge green">${v.status || 'Available'}</span>
          </div>
          <div class="icy-summary-box" style="flex-direction:column; align-items:flex-start;">
            <div>Insurance: ${v.insuranceDetails || 'N/A'} (Exp: ${v.insuranceExpiryDate || 'N/A'})</div>
            <div>Fitness: ${v.fitnessDetails || 'N/A'} (Exp: ${v.fitnessExpiryDate || 'N/A'}) • Pollution Exp: ${v.pollutionExpiryDate || 'N/A'}</div>
            <div>Next Service Note: ${v.partsChanged || 'Not specified'}</div>
          </div>
          <div class="card-actions-row">
            <button class="btn-outlined" onclick="openVehicleModal('${v.id}', false)">&#9998; Edit Vehicle &amp; Documents</button>
            <button class="btn-outlined danger" onclick="deleteVehicle('${v.id}')">&#128465; Delete</button>
          </div>
        </div>
      `;
    } else {
      return `
        <div class="crm-white-card">
          <div class="card-header-line">
            <div class="card-title-group">
              <div class="blue-icon-tile">&#128663;</div>
              <div class="card-main-heading">${v.modelName} (${v.registrationNumber})</div>
            </div>
            <span class="status-badge amber">Cost: ${formatInr(v.maintenanceCost)}</span>
          </div>
          <div class="icy-summary-box" style="flex-direction:column; align-items:flex-start;">
            <div>Present Odometer KM: ${v.presentRunningKilometers || 0} km</div>
            <div style="color:var(--primary-blue);">Present Service Date: ${v.serviceDate || 'Not set'} • Present Service KM: ${v.serviceKilometers || 0} km</div>
            <div style="color:var(--kpi-green-icon);">Next Service Date: ${v.nextServiceDate || 'Not set'} • Next Service KM: ${v.nextServiceKilometers || 0} km</div>
            <div>Next Service Note: ${v.partsChanged || 'Not specified'}</div>
          </div>
          <div class="card-actions-row">
            <button class="btn-outlined" onclick="openVehicleModal('${v.id}', true)">&#9998; Edit Vehicle Maintenance</button>
            <button class="btn-outlined danger" onclick="deleteVehicle('${v.id}')">&#128465; Delete</button>
          </div>
        </div>
      `;
    }
  }).join('');

  return header + switcher + cards;
}

/* ==========================================================================
   4. LEADS, DRIVER ROSTER, TRIP EXPENSES, CUSTOMERS, USER ROLES, REPORTS, SETTINGS
   ========================================================================== */
function renderLeadsSection() {
  const header = renderModuleHeader('Leads & Inquiries Pipeline', 'Add Lead', 'openLeadModal()');
  if (state.leads.length === 0) {
    return header + `<div class="crm-white-card" style="text-align:center; padding:28px;">No saved travel leads yet. Tap "+ Add Lead" to create an inquiry.</div>`;
  }
  return header + state.leads.map(l => `
    <div class="crm-white-card">
      <div class="card-header-line">
        <div class="card-title-group">
          <div class="blue-icon-tile">&#128100;</div>
          <div>
            <div class="card-main-heading">${l.clientName}</div>
            <div class="card-route-sub">${l.clientPhone} • Source: ${l.source}</div>
          </div>
        </div>
        <span class="status-badge blue">${l.status}</span>
      </div>
      <div class="icy-summary-box">
        <div>Route: ${l.routeSummary} (${l.preferredCategory})</div>
        <div class="icy-total-text">Quote: ${formatInr(l.quotedAmount)}</div>
      </div>
      <div class="card-actions-row">
        <button class="btn-green-filled" onclick="convertLeadToBooking('${l.id}')">&#10003; Convert to Booking</button>
        <button class="btn-outlined danger" onclick="deleteLead('${l.id}')">&#128465; Delete</button>
      </div>
    </div>
  `).join('');
}

function renderDriversSection() {
  const header = renderModuleHeader('Driver Roster', 'Add Driver', 'openDriverModal()');
  if (state.drivers.length === 0) {
    return header + `<div class="crm-white-card" style="text-align:center; padding:28px;">No saved drivers yet. Tap "+ Add Driver" to register a driver.</div>`;
  }
  return header + state.drivers.map(d => `
    <div class="crm-white-card">
      <div class="card-header-line">
        <div class="card-title-group">
          <div class="blue-icon-tile">&#129706;</div>
          <div>
            <div class="card-main-heading">${d.name}</div>
            <div class="card-route-sub">${d.phone} • ${d.address || 'Address N/A'}</div>
          </div>
        </div>
        <span class="status-badge green">${d.status}</span>
      </div>
      <div class="icy-summary-box" style="flex-direction:column; align-items:flex-start;">
        <div>DL No: ${d.licenseNumber || 'N/A'} • Aadhar: ${d.aadharNumber || 'N/A'}</div>
        <div>Assigned Vehicle: ${d.assignedVehicle || 'Unassigned'} (${d.vehicleCategory || 'Sedan'})</div>
      </div>
      <div class="card-actions-row">
        <button class="btn-outlined" onclick="openDriverModal('${d.id}')">&#9998; Edit Driver</button>
        <button class="btn-outlined danger" onclick="deleteDriver('${d.id}')">&#128465; Delete</button>
      </div>
    </div>
  `).join('');
}

function renderTripExpensesSection() {
  const header = renderModuleHeader('Trip Details and Trip Expenses', 'Log New Trip', 'openBookingModal()');
  if (state.bookings.length === 0) {
    return header + `<div class="crm-white-card" style="text-align:center; padding:28px;">No saved trips logged yet.</div>`;
  }
  return header + state.bookings.map(b => {
    const netMargin = (Number(b.totalAmount) || 0) - (Number(b.expenseAmount) || 0) - (Number(b.gstAmount) || 0);
    return `
      <div class="crm-white-card">
        <div class="card-header-line">
          <div class="card-title-group">
            <div class="blue-icon-tile">&#128663;</div>
            <div>
              <div class="card-main-heading">${b.pickupLocation} → ${b.dropLocation}</div>
              <div class="card-route-sub">Driver: ${b.driverName || 'N/A'} • ${b.vehicleNumber || 'N/A'} • ${b.distanceKm || 0} km</div>
            </div>
          </div>
          <button class="btn-outlined" onclick="openBookingModal('${b.id}')">&#9998; Edit Expenses</button>
        </div>
        <div class="icy-summary-box">
          <div>Gross: ${formatInr(b.totalAmount)} • Trip Expenses: ${formatInr(b.expenseAmount)}</div>
          <div class="icy-total-text" style="color:var(--kpi-green-icon);">Net Profit: ${formatInr(netMargin)}</div>
        </div>
      </div>
    `;
  }).join('');
}

function renderCustomersSection() {
  const header = renderModuleHeader('Customer Data', 'Add Customer', 'openCustomerModal()');
  if (state.customers.length === 0) {
    return header + `<div class="crm-white-card" style="text-align:center; padding:28px;">No saved customer records yet. Save a booking or tap "+ Add Customer".</div>`;
  }
  return header + state.customers.map(c => `
    <div class="crm-white-card">
      <div class="card-header-line">
        <div class="card-title-group">
          <div class="blue-icon-tile">&#128101;</div>
          <div>
            <div class="card-main-heading">${c.name}</div>
            <div class="card-route-sub">${c.companyName || 'Individual Client'}</div>
          </div>
        </div>
        <span class="status-badge blue">${c.totalBookings || 1} Trips</span>
      </div>
      <div class="icy-summary-box" style="flex-direction:column; align-items:flex-start;">
        <div>Mobile Number: ${c.phone}</div>
        <div>Address: ${c.address || 'N/A'} • Total Billed: ${formatInr(c.totalBilled)}</div>
      </div>
      <div class="card-actions-row">
        <button class="btn-outlined" onclick="openCustomerModal('${c.id}')">&#9998; Edit Customer</button>
        <button class="btn-outlined danger" onclick="deleteCustomer('${c.id}')">&#128465; Delete</button>
      </div>
    </div>
  `).join('');
}

function renderUserRolesSection() {
  const header = renderModuleHeader('User Accounts & Access Roles', null, null);
  return header + `
    <div class="crm-white-card">
      <div class="card-header-line">
        <div class="card-title-group">
          <div class="blue-icon-tile">&#128100;</div>
          <div>
            <div class="card-main-heading">Administrator</div>
            <div class="card-route-sub" style="color:var(--primary-blue);">+91 ${state.activeMobile}</div>
            <div style="font-size:12px; color:var(--text-secondary);">Full Access: Dashboard View, Bookings, Invoices, GST &amp; Fleet</div>
          </div>
        </div>
        <span class="status-badge green">Active</span>
      </div>
    </div>
  `;
}

function renderReportsSection() {
  const header = renderModuleHeader('Reports & Export', 'Save Report PDF', 'window.print()');
  const valid = state.bookings.filter(b => b.tripStatus !== 'Cancelled');
  const totalRevenue = valid.reduce((s, b) => s + (Number(b.totalAmount) || 0), 0);
  const tripExpenses = valid.reduce((s, b) => s + (Number(b.expenseAmount) || 0), 0);
  const vehExpenses = state.vehicles.reduce((s, v) => s + (Number(v.maintenanceCost) || 0), 0);
  const totalGst = valid.reduce((s, b) => s + (Number(b.gstAmount) || 0), 0);
  const netProfit = totalRevenue - (tripExpenses + vehExpenses) - totalGst;

  return header + `
    <div class="crm-white-card">
      <div class="card-main-heading" style="margin-bottom:12px;">Complete Revenue, Expenses &amp; Net Operating Profit Summary</div>
      <div class="icy-summary-box">
        <div>Gross Billed Revenue: ${formatInr(totalRevenue)}</div>
        <div>Combined Expenses: ${formatInr(tripExpenses + vehExpenses)}</div>
        <div>GST Collected: ${formatInr(totalGst)}</div>
        <div class="icy-total-text" style="color:var(--kpi-green-icon);">Net Profit: ${formatInr(netProfit)}</div>
      </div>
      <button class="btn-blue-filled" onclick="window.print()">&#128196; Print / Save Full Financial Report PDF</button>
    </div>
  `;
}

function renderSettingsSection() {
  const header = renderModuleHeader('System Settings & Tariff Control', null, null);
  return header + `
    <div class="crm-white-card">
      <form onsubmit="saveSystemSettings(event)">
        <div class="field-group">
          <label class="field-label">Travel Agency / Company Name</label>
          <div class="input-box">
            <input type="text" id="set-agency-name" value="${state.settings.agencyName || ''}" placeholder="Enter Agency Name" />
          </div>
        </div>
        <div class="field-group">
          <label class="field-label">Company GSTIN</label>
          <div class="input-box">
            <input type="text" id="set-agency-gstin" value="${state.settings.agencyGstin || ''}" placeholder="Enter GSTIN" />
          </div>
        </div>
        <div class="field-group">
          <label class="field-label">Optional Passenger Transport GST % (0, 5, 12, or 18)</label>
          <div class="input-box">
            <input type="number" id="set-gst-slab" value="${state.settings.defaultGstPercent || 0}" min="0" max="18" />
          </div>
        </div>
        <div style="display:flex; flex-direction:column; gap:10px; margin-top:14px;">
          <button type="submit" class="primary-submit-btn">Save Configuration</button>
          <button type="button" class="btn-outlined danger" style="justify-content:center; height:44px;" onclick="signOutMobile()">Sign Out (+91 ${state.activeMobile})</button>
        </div>
      </form>
    </div>
  `;
}

function saveSystemSettings(e) {
  e.preventDefault();
  state.settings.agencyName = document.getElementById('set-agency-name').value.trim();
  state.settings.agencyGstin = document.getElementById('set-agency-gstin').value.trim();
  state.settings.defaultGstPercent = Number(document.getElementById('set-gst-slab').value) || 0;
  saveUserData();
  showStatusBanner('System settings & GST rate saved.');
}

/* ==========================================================================
   MODAL HELPERS & VERTICALLY STACKED STEP FORMS
   ========================================================================== */
function openModal(html) {
  document.getElementById('modal-container').innerHTML = html;
  document.getElementById('modal-backdrop').classList.add('open');
}

function closeModal() {
  document.getElementById('modal-backdrop').classList.remove('open');
}

function openBookingModal(bookingId = null, isInvoiceForm = false) {
  const existing = state.bookings.find(b => b.id === bookingId) || {};
  const title = existing.id ? 'Edit Booking Form' : (isInvoiceForm ? 'Generate Invoice Form' : 'New Booking Form');
  const badge = isInvoiceForm ? 'TAX INVOICE ENTRY FORM' : 'BOOKING ENTRY FORM';

  openModal(`
    <div class="modal-wave-header">
      <div>
        <div class="modal-badge-pill">${badge}</div>
        <div class="modal-title">${title}</div>
      </div>
      <button class="back-white-btn" onclick="closeModal()">&#10005;</button>
    </div>
    <form onsubmit="submitBookingForm(event, '${existing.id || ''}')">
      <div class="modal-body-scroll">
        <!-- Step 1 -->
        <div class="step-card">
          <div class="step-banner">
            <span class="step-number-circle">1</span>
            <span>Customer Name &amp; Company Name</span>
          </div>
          <div class="step-card-body">
            <div class="input-box"><input type="text" id="bk-customer" placeholder="Customer Name" value="${existing.customerName || ''}" required /></div>
            <div class="input-box"><input type="text" id="bk-company" placeholder="Company Name" value="${existing.companyName || ''}" /></div>
            <div class="input-box"><input type="tel" id="bk-phone" placeholder="Contact Mobile" value="${existing.customerPhone || ''}" required /></div>
          </div>
        </div>

        <!-- Step 2 -->
        <div class="step-card">
          <div class="step-banner purple">
            <span class="step-number-circle">2</span>
            <span>Pickup &amp; Drop Details</span>
          </div>
          <div class="step-card-body">
            <div class="input-box"><input type="text" id="bk-date" placeholder="Date (DD-MM-YYYY)" value="${existing.tripDate || todayDdMmYyyy()}" required /></div>
            <div class="input-box"><input type="text" id="bk-pickup" placeholder="Pickup Location / Terminal / Hotel" value="${existing.pickupLocation || ''}" required /></div>
            <div class="input-box"><input type="text" id="bk-drop" placeholder="Drop Location / Destination" value="${existing.dropLocation || ''}" required /></div>
            <div class="input-box"><input type="number" id="bk-distance" placeholder="Trip Distance (KM)" value="${existing.distanceKm || ''}" /></div>
          </div>
        </div>

        <!-- Step 3 -->
        <div class="step-card">
          <div class="step-banner">
            <span class="step-number-circle">3</span>
            <span>Vehicle &amp; Driver Assignment</span>
          </div>
          <div class="step-card-body">
            <div class="input-box">
              <select id="bk-category">
                ${['Sedan', 'Compact SUVs', 'SUVs', 'Tourist Travellers'].map(c => `<option value="${c}" ${existing.vehicleCategory === c ? 'selected' : ''}>${c}</option>`).join('')}
              </select>
            </div>
            <div class="input-box"><input type="text" id="bk-veh-model" placeholder="Vehicle Model (e.g. Swift Dzire)" value="${existing.vehicleModel || ''}" /></div>
            <div class="input-box"><input type="text" id="bk-veh-no" placeholder="Vehicle Number (e.g. AP39Y8820)" value="${existing.vehicleNumber || ''}" /></div>
            <div class="input-box"><input type="text" id="bk-driver" placeholder="Driver Name" value="${existing.driverName || ''}" /></div>
            <div class="input-box"><input type="tel" id="bk-driver-phone" placeholder="Driver Mobile" value="${existing.driverPhone || ''}" /></div>
          </div>
        </div>

        <!-- Step 4 -->
        <div class="step-card">
          <div class="step-banner purple">
            <span class="step-number-circle">4</span>
            <span>Tariff, Toll, GST &amp; Trip Expenses</span>
          </div>
          <div class="step-card-body">
            <div class="input-box"><input type="number" id="bk-base" placeholder="Base Fare Amount (₹)" value="${existing.baseAmount || ''}" required /></div>
            <div class="input-box"><input type="number" id="bk-toll" placeholder="Tollgate &amp; Fastag Charges (₹)" value="${existing.tollCharges || ''}" /></div>
            <div class="input-box"><input type="number" id="bk-parking" placeholder="Parking Charges (₹)" value="${existing.parkingCharges || ''}" /></div>
            <div class="input-box"><input type="number" id="bk-gst" placeholder="GST Amount (₹)" value="${existing.gstAmount || ''}" /></div>
            <div class="input-box"><input type="number" id="bk-expense" placeholder="Driver Bata / Fuel / Trip Expense (₹)" value="${existing.expenseAmount || ''}" /></div>
            <div class="input-box">
              <select id="bk-pay-status">
                <option value="Paid" ${existing.paymentStatus === 'Paid' ? 'selected' : ''}>Paid</option>
                <option value="Pending" ${existing.paymentStatus === 'Pending' ? 'selected' : ''}>Pending</option>
              </select>
            </div>
            <div class="input-box">
              <select id="bk-pay-channel">
                ${['UPI', 'Cash', 'Bank Transfer', 'Corporate Credit'].map(ch => `<option value="${ch}" ${existing.paymentChannel === ch ? 'selected' : ''}>${ch}</option>`).join('')}
              </select>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer-bar">
        <button type="submit" class="btn-save-green">Save Booking &amp; Generate PDF</button>
        <button type="button" class="btn-cancel-outline" onclick="closeModal()">&#10005; Cancel</button>
      </div>
    </form>
  `);
}

function submitBookingForm(e, existingId) {
  e.preventDefault();
  const base = Math.max(0, Number(document.getElementById('bk-base').value) || 0);
  const toll = Math.max(0, Number(document.getElementById('bk-toll').value) || 0);
  const parking = Math.max(0, Number(document.getElementById('bk-parking').value) || 0);
  const gst = Math.max(0, Number(document.getElementById('bk-gst').value) || 0);
  const total = base + toll + parking + gst;

  const payload = {
    id: existingId || ('bk_' + Date.now()),
    customerName: document.getElementById('bk-customer').value.trim(),
    companyName: document.getElementById('bk-company').value.trim(),
    customerPhone: document.getElementById('bk-phone').value.trim(),
    tripDate: document.getElementById('bk-date').value.trim(),
    pickupLocation: document.getElementById('bk-pickup').value.trim(),
    dropLocation: document.getElementById('bk-drop').value.trim(),
    distanceKm: Number(document.getElementById('bk-distance').value) || 0,
    vehicleCategory: document.getElementById('bk-category').value,
    vehicleModel: document.getElementById('bk-veh-model').value.trim(),
    vehicleNumber: document.getElementById('bk-veh-no').value.trim(),
    driverName: document.getElementById('bk-driver').value.trim(),
    driverPhone: document.getElementById('bk-driver-phone').value.trim(),
    baseAmount: base,
    tollCharges: toll,
    parkingCharges: parking,
    gstAmount: gst,
    totalAmount: total,
    expenseAmount: Number(document.getElementById('bk-expense').value) || 0,
    paymentStatus: document.getElementById('bk-pay-status').value,
    paymentChannel: document.getElementById('bk-pay-channel').value,
    tripStatus: 'Active'
  };

  if (existingId) {
    state.bookings = state.bookings.map(b => b.id === existingId ? payload : b);
  } else {
    state.bookings.unshift(payload);
  }

  // Auto-sync Customer Data
  if (payload.customerName) {
    const existingCust = state.customers.find(c => c.name.toLowerCase() === payload.customerName.toLowerCase() || (payload.customerPhone && c.phone === payload.customerPhone));
    if (existingCust) {
      existingCust.totalBookings = (Number(existingCust.totalBookings) || 1) + (existingId ? 0 : 1);
      existingCust.totalBilled = (Number(existingCust.totalBilled) || 0) + total;
    } else {
      state.customers.unshift({
        id: 'cust_' + Date.now(),
        name: payload.customerName,
        companyName: payload.companyName,
        phone: payload.customerPhone,
        address: `${payload.pickupLocation} → ${payload.dropLocation}`,
        totalBookings: 1,
        totalBilled: total
      });
    }
  }

  saveUserData();
  closeModal();
  renderApp();
  showStatusBanner(`Saved booking for ${payload.customerName} (${formatInr(total)})`);
}

function toggleBookingPayment(id) {
  state.bookings = state.bookings.map(b => b.id === id ? { ...b, paymentStatus: b.paymentStatus === 'Paid' ? 'Pending' : 'Paid' } : b);
  saveUserData();
  renderApp();
}

function setBookingStatus(id, status) {
  state.bookings = state.bookings.map(b => b.id === id ? { ...b, tripStatus: status } : b);
  saveUserData();
  renderApp();
}

function deleteBooking(id) {
  state.bookings = state.bookings.filter(b => b.id !== id);
  saveUserData();
  renderApp();
}

/* Vehicle Form with Next Service Note at the end of the form */
function openVehicleModal(vehId = null, isMaint = false) {
  const existing = state.vehicles.find(v => v.id === vehId) || {};
  openModal(`
    <div class="modal-wave-header">
      <div>
        <div class="modal-badge-pill">${isMaint ? 'VEHICLE MAINTENANCE FORM' : 'VEHICLE REGISTRY FORM'}</div>
        <div class="modal-title">${existing.id ? 'Edit Vehicle' : (isMaint ? 'Add Vehicle Maintenance' : 'Add Vehicle & Documents')}</div>
      </div>
      <button class="back-white-btn" onclick="closeModal()">&#10005;</button>
    </div>
    <form onsubmit="submitVehicleForm(event, '${existing.id || ''}')">
      <div class="modal-body-scroll">
        <div class="step-card">
          <div class="step-banner">
            <span class="step-number-circle">1</span>
            <span>Vehicle Identification</span>
          </div>
          <div class="step-card-body">
            <div class="input-box"><input type="text" id="vh-reg" placeholder="Registration Number (e.g. AP39Y8820)" value="${existing.registrationNumber || ''}" required /></div>
            <div class="input-box"><input type="text" id="vh-model" placeholder="Vehicle Model Name (e.g. Swift Dzire)" value="${existing.modelName || ''}" required /></div>
            <div class="input-box"><input type="text" id="vh-year" placeholder="Manufacturing Year" value="${existing.manufacturingYear || ''}" /></div>
            <div class="input-box">
              <select id="vh-category">
                ${['Sedan', 'Compact SUVs', 'SUVs', 'Tourist Travellers'].map(c => `<option value="${c}" ${existing.category === c ? 'selected' : ''}>${c}</option>`).join('')}
              </select>
            </div>
          </div>
        </div>

        <div class="step-card">
          <div class="step-banner purple">
            <span class="step-number-circle">2</span>
            <span>Service Odometer &amp; Compliance Dates</span>
          </div>
          <div class="step-card-body">
            <div class="input-box"><input type="number" id="vh-run-km" placeholder="Present Running Odometer (KM)" value="${existing.presentRunningKilometers || ''}" /></div>
            <div class="input-box"><input type="text" id="vh-srv-date" placeholder="Present Service Date (DD-MM-YYYY)" value="${existing.serviceDate || todayDdMmYyyy()}" /></div>
            <div class="input-box"><input type="number" id="vh-srv-km" placeholder="Present Service Kilometers (KM)" value="${existing.serviceKilometers || ''}" /></div>
            <div class="input-box"><input type="text" id="vh-next-date" placeholder="Next Service Date (DD-MM-YYYY)" value="${existing.nextServiceDate || ''}" /></div>
            <div class="input-box"><input type="number" id="vh-next-km" placeholder="Next Service Kilometers (KM)" value="${existing.nextServiceKilometers || ''}" /></div>
            <div class="input-box"><input type="number" id="vh-cost" placeholder="Maintenance Service Cost (₹)" value="${existing.maintenanceCost || ''}" /></div>
            <div class="input-box"><input type="text" id="vh-ins" placeholder="Insurance Company / Policy Details" value="${existing.insuranceDetails || ''}" /></div>
            <div class="input-box"><input type="text" id="vh-ins-exp" placeholder="Insurance Expiry Date (DD-MM-YYYY)" value="${existing.insuranceExpiryDate || ''}" /></div>
            <div class="input-box"><input type="text" id="vh-fit-exp" placeholder="Fitness Expiry Date (DD-MM-YYYY)" value="${existing.fitnessExpiryDate || ''}" /></div>
            <div class="input-box"><input type="text" id="vh-pol-exp" placeholder="Pollution Expiry Date (DD-MM-YYYY)" value="${existing.pollutionExpiryDate || ''}" /></div>
          </div>
        </div>

        <!-- Next Service Note placed at the very end of the form per user requirement -->
        <div class="step-card">
          <div class="step-banner">
            <span class="step-number-circle">3</span>
            <span>Next Service Note</span>
          </div>
          <div class="step-card-body">
            <div class="input-box" style="height:auto; padding:10px 12px;">
              <textarea id="vh-next-note" rows="3" placeholder="Next Service Note (Parts changed / upcoming maintenance note)">${existing.partsChanged || ''}</textarea>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer-bar">
        <button type="submit" class="btn-save-green">Save Vehicle Record</button>
        <button type="button" class="btn-cancel-outline" onclick="closeModal()">&#10005; Cancel</button>
      </div>
    </form>
  `);
}

function submitVehicleForm(e, existingId) {
  e.preventDefault();
  const payload = {
    id: existingId || ('vh_' + Date.now()),
    registrationNumber: document.getElementById('vh-reg').value.trim(),
    modelName: document.getElementById('vh-model').value.trim(),
    manufacturingYear: document.getElementById('vh-year').value.trim(),
    category: document.getElementById('vh-category').value,
    presentRunningKilometers: Number(document.getElementById('vh-run-km').value) || 0,
    serviceDate: document.getElementById('vh-srv-date').value.trim(),
    serviceKilometers: Number(document.getElementById('vh-srv-km').value) || 0,
    nextServiceDate: document.getElementById('vh-next-date').value.trim(),
    nextServiceKilometers: Number(document.getElementById('vh-next-km').value) || 0,
    maintenanceCost: Number(document.getElementById('vh-cost').value) || 0,
    insuranceDetails: document.getElementById('vh-ins').value.trim(),
    insuranceExpiryDate: document.getElementById('vh-ins-exp').value.trim(),
    fitnessExpiryDate: document.getElementById('vh-fit-exp').value.trim(),
    pollutionExpiryDate: document.getElementById('vh-pol-exp').value.trim(),
    partsChanged: document.getElementById('vh-next-note').value.trim(),
    status: 'Available'
  };

  if (existingId) {
    state.vehicles = state.vehicles.map(v => v.id === existingId ? payload : v);
  } else {
    state.vehicles.unshift(payload);
  }
  saveUserData();
  closeModal();
  renderApp();
  showStatusBanner(`Saved vehicle ${payload.registrationNumber}`);
}

function deleteVehicle(id) {
  state.vehicles = state.vehicles.filter(v => v.id !== id);
  saveUserData();
  renderApp();
}

function openDriverModal(drvId = null) {
  const existing = state.drivers.find(d => d.id === drvId) || {};
  openModal(`
    <div class="modal-wave-header">
      <div>
        <div class="modal-badge-pill">DRIVER ROSTER FORM</div>
        <div class="modal-title">${existing.id ? 'Edit Driver' : 'Add Driver'}</div>
      </div>
      <button class="back-white-btn" onclick="closeModal()">&#10005;</button>
    </div>
    <form onsubmit="submitDriverForm(event, '${existing.id || ''}')">
      <div class="modal-body-scroll">
        <div class="step-card">
          <div class="step-banner"><span class="step-number-circle">1</span><span>Driver Details &amp; License</span></div>
          <div class="step-card-body">
            <div class="input-box"><input type="text" id="dr-name" placeholder="Driver Full Name" value="${existing.name || ''}" required /></div>
            <div class="input-box"><input type="tel" id="dr-phone" placeholder="Mobile Number" value="${existing.phone || ''}" required /></div>
            <div class="input-box"><input type="text" id="dr-address" placeholder="Residential Address" value="${existing.address || ''}" /></div>
            <div class="input-box"><input type="text" id="dr-lic" placeholder="Driving License Number" value="${existing.licenseNumber || ''}" /></div>
            <div class="input-box"><input type="text" id="dr-aadhar" placeholder="Aadhar Card Number" value="${existing.aadharNumber || ''}" /></div>
            <div class="input-box"><input type="text" id="dr-veh" placeholder="Assigned Vehicle (Model &amp; Reg No)" value="${existing.assignedVehicle || ''}" /></div>
          </div>
        </div>
      </div>
      <div class="modal-footer-bar">
        <button type="submit" class="btn-save-green">Save Driver</button>
        <button type="button" class="btn-cancel-outline" onclick="closeModal()">Cancel</button>
      </div>
    </form>
  `);
}

function submitDriverForm(e, existingId) {
  e.preventDefault();
  const payload = {
    id: existingId || ('drv_' + Date.now()),
    name: document.getElementById('dr-name').value.trim(),
    phone: document.getElementById('dr-phone').value.trim(),
    address: document.getElementById('dr-address').value.trim(),
    licenseNumber: document.getElementById('dr-lic').value.trim(),
    aadharNumber: document.getElementById('dr-aadhar').value.trim(),
    assignedVehicle: document.getElementById('dr-veh').value.trim(),
    vehicleCategory: 'Sedan',
    status: 'Available'
  };
  if (existingId) {
    state.drivers = state.drivers.map(d => d.id === existingId ? payload : d);
  } else {
    state.drivers.unshift(payload);
  }
  saveUserData();
  closeModal();
  renderApp();
}

function deleteDriver(id) {
  state.drivers = state.drivers.filter(d => d.id !== id);
  saveUserData();
  renderApp();
}

function openLeadModal() {
  openModal(`
    <div class="modal-wave-header">
      <div>
        <div class="modal-badge-pill">NEW INQUIRY LEAD</div>
        <div class="modal-title">Add Travel Lead</div>
      </div>
      <button class="back-white-btn" onclick="closeModal()">&#10005;</button>
    </div>
    <form onsubmit="submitLeadForm(event)">
      <div class="modal-body-scroll">
        <div class="step-card">
          <div class="step-banner"><span class="step-number-circle">1</span><span>Client &amp; Route Quote</span></div>
          <div class="step-card-body">
            <div class="input-box"><input type="text" id="ld-name" placeholder="Client Name" required /></div>
            <div class="input-box"><input type="tel" id="ld-phone" placeholder="Client Mobile" required /></div>
            <div class="input-box"><input type="text" id="ld-route" placeholder="Route (e.g. Vijayawada to Guntur)" required /></div>
            <div class="input-box"><input type="number" id="ld-quote" placeholder="Quoted Fare (₹)" required /></div>
            <div class="input-box"><input type="text" id="ld-source" placeholder="Lead Source (Phone / Walk-in / Website)" value="Direct Call" /></div>
          </div>
        </div>
      </div>
      <div class="modal-footer-bar">
        <button type="submit" class="btn-save-green">Save Lead</button>
        <button type="button" class="btn-cancel-outline" onclick="closeModal()">Cancel</button>
      </div>
    </form>
  `);
}

function submitLeadForm(e) {
  e.preventDefault();
  state.leads.unshift({
    id: 'ld_' + Date.now(),
    clientName: document.getElementById('ld-name').value.trim(),
    clientPhone: document.getElementById('ld-phone').value.trim(),
    routeSummary: document.getElementById('ld-route').value.trim(),
    quotedAmount: Number(document.getElementById('ld-quote').value) || 0,
    source: document.getElementById('ld-source').value.trim(),
    preferredCategory: 'Sedan',
    status: 'New'
  });
  saveUserData();
  closeModal();
  renderApp();
}

function convertLeadToBooking(leadId) {
  const lead = state.leads.find(l => l.id === leadId);
  if (!lead) return;
  lead.status = 'Converted';
  state.bookings.unshift({
    id: 'bk_' + Date.now(),
    customerName: lead.clientName,
    companyName: '',
    customerPhone: lead.clientPhone,
    pickupLocation: lead.routeSummary.split(' to ')[0] || lead.routeSummary,
    dropLocation: lead.routeSummary.split(' to ')[1] || lead.routeSummary,
    vehicleCategory: lead.preferredCategory || 'Sedan',
    vehicleModel: '',
    vehicleNumber: '',
    driverName: '',
    driverPhone: '',
    baseAmount: lead.quotedAmount,
    tollCharges: 0,
    parkingCharges: 0,
    gstAmount: 0,
    totalAmount: lead.quotedAmount,
    expenseAmount: 0,
    distanceKm: 0,
    paymentStatus: 'Pending',
    paymentChannel: 'UPI',
    tripStatus: 'Active',
    tripDate: todayDdMmYyyy()
  });
  saveUserData();
  renderApp();
  showStatusBanner(`Converted lead "${lead.clientName}" to Booking!`);
}

function deleteLead(id) {
  state.leads = state.leads.filter(l => l.id !== id);
  saveUserData();
  renderApp();
}

function openCustomerModal(custId = null) {
  const existing = state.customers.find(c => c.id === custId) || {};
  openModal(`
    <div class="modal-wave-header">
      <div>
        <div class="modal-badge-pill">CUSTOMER RECORD</div>
        <div class="modal-title">${existing.id ? 'Edit Customer' : 'Add Customer'}</div>
      </div>
      <button class="back-white-btn" onclick="closeModal()">&#10005;</button>
    </div>
    <form onsubmit="submitCustomerForm(event, '${existing.id || ''}')">
      <div class="modal-body-scroll">
        <div class="step-card">
          <div class="step-banner"><span class="step-number-circle">1</span><span>Customer Details</span></div>
          <div class="step-card-body">
            <div class="input-box"><input type="text" id="cu-name" placeholder="Customer Name" value="${existing.name || ''}" required /></div>
            <div class="input-box"><input type="text" id="cu-company" placeholder="Company Name" value="${existing.companyName || ''}" /></div>
            <div class="input-box"><input type="tel" id="cu-phone" placeholder="Mobile Number" value="${existing.phone || ''}" required /></div>
            <div class="input-box"><input type="text" id="cu-address" placeholder="Address" value="${existing.address || ''}" /></div>
          </div>
        </div>
      </div>
      <div class="modal-footer-bar">
        <button type="submit" class="btn-save-green">Save Customer</button>
        <button type="button" class="btn-cancel-outline" onclick="closeModal()">Cancel</button>
      </div>
    </form>
  `);
}

function submitCustomerForm(e, existingId) {
  e.preventDefault();
  const payload = {
    id: existingId || ('cust_' + Date.now()),
    name: document.getElementById('cu-name').value.trim(),
    companyName: document.getElementById('cu-company').value.trim(),
    phone: document.getElementById('cu-phone').value.trim(),
    address: document.getElementById('cu-address').value.trim(),
    totalBookings: 1,
    totalBilled: 0
  };
  if (existingId) {
    state.customers = state.customers.map(c => c.id === existingId ? { ...c, ...payload } : c);
  } else {
    state.customers.unshift(payload);
  }
  saveUserData();
  closeModal();
  renderApp();
}

function deleteCustomer(id) {
  state.customers = state.customers.filter(c => c.id !== id);
  saveUserData();
  renderApp();
}

function openInvoicePreviewModal(bookingId) {
  const b = state.bookings.find(x => x.id === bookingId);
  if (!b) return;
  openModal(`
    <div class="modal-wave-header">
      <div>
        <div class="modal-badge-pill">PRINTABLE TAX INVOICE PDF</div>
        <div class="modal-title">Invoice — ${b.customerName}</div>
      </div>
      <button class="back-white-btn" onclick="closeModal()">&#10005;</button>
    </div>
    <div class="modal-body-scroll">
      <div class="crm-white-card">
        <div style="border-bottom:2px solid var(--primary-blue); padding-bottom:10px; margin-bottom:10px;">
          <div style="font-size:18px; color:var(--primary-blue);">${state.settings.agencyName || 'FleetCRM Travel Agency'}</div>
          <div style="font-size:12px; color:var(--text-secondary);">GSTIN: ${state.settings.agencyGstin || 'Unregistered'} • Date: ${b.tripDate}</div>
        </div>
        <div class="meta-info-list">
          <div>Customer: ${b.customerName} (${b.customerPhone})</div>
          <div>Route: ${b.pickupLocation} → ${b.dropLocation}</div>
          <div>Vehicle: ${b.vehicleCategory} • ${b.vehicleModel} (${b.vehicleNumber})</div>
          <div>Driver: ${b.driverName} (${b.driverPhone})</div>
        </div>
        <div class="icy-summary-box" style="flex-direction:column; align-items:stretch;">
          <div style="display:flex; justify-content:space-between;"><span>Base Taxi Fare:</span><span>${formatInr(b.baseAmount)}</span></div>
          <div style="display:flex; justify-content:space-between;"><span>Tollgate &amp; Fastag:</span><span>${formatInr(b.tollCharges)}</span></div>
          <div style="display:flex; justify-content:space-between;"><span>Parking Charges:</span><span>${formatInr(b.parkingCharges)}</span></div>
          <div style="display:flex; justify-content:space-between;"><span>GST Tax:</span><span>${formatInr(b.gstAmount)}</span></div>
          <hr style="border:none; border-top:1px solid var(--card-border); margin:6px 0;" />
          <div style="display:flex; justify-content:space-between; font-size:16px;"><span>Grand Total (${b.paymentStatus}):</span><span>${formatInr(b.totalAmount)}</span></div>
        </div>
      </div>
    </div>
    <div class="modal-footer-bar">
      <button type="button" class="btn-save-green" onclick="window.print()">&#128196; Print / Save Invoice PDF</button>
      <button type="button" class="btn-cancel-outline" onclick="closeModal()">Close</button>
    </div>
  `);
}

/* Initialize Session on Page Load */
window.addEventListener('DOMContentLoaded', () => {
  const savedSession = localStorage.getItem(STORAGE_KEYS.SESSION);
  const accounts = getAccounts();
  if (savedSession && accounts[savedSession]) {
    completeMobileLogin(savedSession);
  } else {
    const hasAnyAccount = Object.keys(accounts).length > 0;
    switchAuthTab(hasAnyAccount ? 'login' : 'register');
  }
});
