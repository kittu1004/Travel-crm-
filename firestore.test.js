const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function validBookingPayload(uid) {
  const now = new Date();
  return {
    userId: uid,
    customerName: "Rajesh Sharma",
    customerPhone: "+91 9876543210",
    pickupLocation: "Bengaluru Airport T2",
    dropLocation: "Mysore Palace Hotel",
    vehicleCategory: "SUVs",
    vehicleNumber: "KA-01-MG-4590",
    driverName: "Ramesh Kumar",
    tripStatus: "Active",
    paymentStatus: "Paid",
    paymentChannel: "UPI",
    baseAmount: 6000,
    gstAmount: 300,
    totalAmount: 6300,
    expenseAmount: 1400,
    distanceKm: 165,
    tripDate: "2026-11-11",
    createdAt: now,
    updatedAt: now,
  };
}

function validDriverPayload(uid) {
  const now = new Date();
  return {
    userId: uid,
    name: "Ramesh Kumar",
    phone: "+91 9845011223",
    licenseNumber: "KA0120180045912",
    assignedVehicle: "Toyota Innova Crysta (KA-01-MG-4590)",
    vehicleCategory: "SUVs",
    status: "Available",
    tripsCompleted: 42,
    earningsTotal: 84500,
    createdAt: now,
    updatedAt: now,
  };
}

function validVehiclePayload(uid) {
  const now = new Date();
  return {
    userId: uid,
    registrationNumber: "KA-01-MG-4590",
    modelName: "Toyota Innova Crysta",
    category: "SUVs",
    status: "Available",
    maintenanceCost: 12500,
    nextServiceDate: "2026-12-15",
    serviceNotes: "Engine oil, brake pads, and AC filter replaced",
    createdAt: now,
    updatedAt: now,
  };
}

function validLeadPayload(uid) {
  const now = new Date();
  return {
    userId: uid,
    clientName: "Priya Nair",
    clientPhone: "+91 9900112233",
    routeSummary: "Bengaluru to Coorg 3 Days Round Trip",
    preferredCategory: "Compact SUVs",
    quotedAmount: 14500,
    status: "New",
    source: "Website",
    createdAt: now,
    updatedAt: now,
  };
}

function validCustomerPayload(uid) {
  const now = new Date();
  return {
    userId: uid,
    name: "Vikram Malhotra",
    phone: "+91 9820112244",
    email: "vikram@techcorp.in",
    companyName: "TechCorp Solutions Pvt Ltd",
    gstin: "29AABCT1332L1ZV",
    totalBookings: 8,
    totalBilled: 96000,
    pendingBalance: 12500,
    createdAt: now,
    updatedAt: now,
  };
}

test("Unauthenticated user: cannot read or write bookings, drivers, vehicles, leads, customers", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("bookings").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("drivers").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("vehicles").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("leads").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("customers").get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("bookings").doc("b1").set(validBookingPayload(ALICE_UID))
  );
});

test("Authenticated user: can create, read, list, update, and delete own bookings, drivers, vehicles, leads, customers", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  const bookingRef = aliceDb.collection("users").doc(ALICE_UID).collection("bookings").doc("b1");
  await assertSucceeds(bookingRef.set(validBookingPayload(ALICE_UID)));
  await assertSucceeds(bookingRef.get());
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("bookings").get());
  await assertSucceeds(bookingRef.update({ tripStatus: "Completed", updatedAt: new Date() }));

  const driverRef = aliceDb.collection("users").doc(ALICE_UID).collection("drivers").doc("d1");
  await assertSucceeds(driverRef.set(validDriverPayload(ALICE_UID)));
  await assertSucceeds(driverRef.update({ status: "On Trip", updatedAt: new Date() }));

  const vehicleRef = aliceDb.collection("users").doc(ALICE_UID).collection("vehicles").doc("v1");
  await assertSucceeds(vehicleRef.set(validVehiclePayload(ALICE_UID)));
  await assertSucceeds(vehicleRef.update({ status: "Maintenance", maintenanceCost: 15000, updatedAt: new Date() }));

  const leadRef = aliceDb.collection("users").doc(ALICE_UID).collection("leads").doc("l1");
  await assertSucceeds(leadRef.set(validLeadPayload(ALICE_UID)));
  await assertSucceeds(leadRef.update({ status: "Converted", updatedAt: new Date() }));

  const customerRef = aliceDb.collection("users").doc(ALICE_UID).collection("customers").doc("c1");
  await assertSucceeds(customerRef.set(validCustomerPayload(ALICE_UID)));
  await assertSucceeds(customerRef.update({ pendingBalance: 0, updatedAt: new Date() }));
});

test("Cross-user isolation: Bob cannot read, list, or modify Alice's CRM records", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("bookings").doc("b1").set(validBookingPayload(ALICE_UID))
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("bookings").doc("b1").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("bookings").get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("bookings").doc("b1").update({ tripStatus: "Cancelled" })
  );
});

test("Shadow update & schema validation: rejects ghost fields and invalid enum values", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bookingRef = aliceDb.collection("users").doc(ALICE_UID).collection("bookings").doc("b_bad");

  await assertFails(
    bookingRef.set({
      ...validBookingPayload(ALICE_UID),
      isAdminOverride: true,
    })
  );

  await assertFails(
    bookingRef.set({
      ...validBookingPayload(ALICE_UID),
      vehicleCategory: "Helicopter",
    })
  );
});
