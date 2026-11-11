package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class CrmRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun addAndObserveBooking_authenticatedOwner_succeeds() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repository = CrmRepository(firestore)

    val sampleBooking = Booking(
      customerName = "Vikram Malhotra",
      customerPhone = "+91 98201 12244",
      pickupLocation = "Bengaluru Airport",
      dropLocation = "Whitefield",
      vehicleCategory = "Sedans",
      vehicleNumber = "KA-05-AB-2104",
      driverName = "Suresh Gowda",
      tripStatus = "Active",
      paymentStatus = "Paid",
      paymentChannel = "UPI",
      baseAmount = 3000L,
      gstAmount = 150L,
      totalAmount = 3150L,
      expenseAmount = 600L,
      distanceKm = 48L,
      tripDate = "2026-11-11"
    )

    val createResult = withTimeout(DEFAULT_TIMEOUT_MS) { repository.addBooking(sampleBooking) }
    assertTrue("Expected addBooking to succeed", createResult.isSuccess)
    val bookingId = createResult.getOrThrow()
    assertTrue(bookingId.isNotBlank())

    val emitted = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeBookings(uid).first { list -> list.any { it.id == bookingId } }
    }
    val createdBooking = emitted.first { it.id == bookingId }
    assertEquals("Vikram Malhotra", createdBooking.customerName)
    assertEquals(3150L, createdBooking.totalAmount)
  }

  @Test
  fun getBookingById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val aliceRepo = CrmRepository(firestore)
    val bookingId = withTimeout(DEFAULT_TIMEOUT_MS) {
      aliceRepo.addBooking(
        Booking(
          customerName = "Alice Client",
          customerPhone = "+91 90000 11111",
          pickupLocation = "Point A",
          dropLocation = "Point B",
          vehicleCategory = "SUVs",
          vehicleNumber = "KA-01-MG-4590",
          driverName = "Ramesh Kumar",
          tripStatus = "Active",
          paymentStatus = "Pending",
          paymentChannel = "UPI",
          baseAmount = 5000L,
          gstAmount = 250L,
          totalAmount = 5250L,
          expenseAmount = 1000L,
          distanceKm = 120L,
          tripDate = "2026-11-11"
        )
      ).getOrThrow()
    }

    signInTestUser(BOB_EMAIL)
    val bobRepo = CrmRepository(firestore)
    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      bobRepo.getBookingById(aliceUid, bookingId)
    }
    assertTrue("Expected cross-user access to fail", result.isFailure)
    val firestoreException = result.exceptionOrNull()?.unwrapFirestoreException()
    assertNotNull("Expected FirebaseFirestoreException", firestoreException)
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
  }

  @Test
  fun observeBookings_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    auth.signOut()
    val repository = CrmRepository(firestore)

    try {
      withTimeout(FLOW_TIMEOUT_MS) {
        repository.observeBookings(aliceUid).first()
      }
      fail("Expected unauthenticated observeBookings to throw exception")
    } catch (e: Exception) {
      val firestoreException = e.unwrapFirestoreException()
      assertNotNull("Expected nested FirebaseFirestoreException, got $e", firestoreException)
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
    }
  }

  private companion object {
    const val ALICE_EMAIL = "alice@fleetcrm.test"
    const val BOB_EMAIL = "bob@fleetcrm.test"
    const val DEFAULT_TIMEOUT_MS = 5000L
    const val FLOW_TIMEOUT_MS = 4000L
  }
}
