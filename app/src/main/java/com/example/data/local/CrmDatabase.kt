package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.Booking
import com.example.data.Customer
import com.example.data.Driver
import com.example.data.Lead
import com.example.data.Vehicle
import com.google.firebase.Timestamp
import java.util.Date
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "bookings")
data class BookingEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val customerName: String,
  val companyName: String,
  val customerPhone: String,
  val pickupLocation: String,
  val dropLocation: String,
  val vehicleCategory: String,
  val vehicleModel: String,
  val vehicleNumber: String,
  val driverName: String,
  val driverPhone: String,
  val tripStatus: String,
  val paymentStatus: String,
  val paymentChannel: String,
  val baseAmount: Long,
  val tollCharges: Long,
  val parkingCharges: Long,
  val gstAmount: Long,
  val totalAmount: Long,
  val expenseAmount: Long,
  val distanceKm: Long,
  val tripDate: String,
  val createdAtMillis: Long,
  val updatedAtMillis: Long,
)

@Entity(tableName = "drivers")
data class DriverEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val name: String,
  val phone: String,
  val address: String,
  val licenseNumber: String,
  val aadharNumber: String,
  val selfiePhotoUri: String,
  val documentsUploaded: String,
  val assignedVehicle: String,
  val vehicleCategory: String,
  val status: String,
  val tripsCompleted: Long,
  val earningsTotal: Long,
  val createdAtMillis: Long,
  val updatedAtMillis: Long,
)

@Entity(tableName = "vehicles")
data class VehicleEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val registrationNumber: String,
  val modelName: String,
  val manufacturingYear: String,
  val category: String,
  val status: String,
  val maintenanceCost: Long,
  val serviceDate: String,
  val nextServiceDate: String,
  val serviceKilometers: Long,
  val presentRunningKilometers: Long,
  val nextServiceKilometers: Long,
  val partsChanged: String,
  val insuranceDetails: String,
  val insuranceExpiryDate: String,
  val fitnessDetails: String,
  val fitnessExpiryDate: String,
  val pollutionDetails: String,
  val pollutionExpiryDate: String,
  val serviceNotes: String,
  val createdAtMillis: Long,
  val updatedAtMillis: Long,
)

@Entity(tableName = "leads")
data class LeadEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val clientName: String,
  val clientPhone: String,
  val routeSummary: String,
  val preferredCategory: String,
  val quotedAmount: Long,
  val status: String,
  val source: String,
  val createdAtMillis: Long,
  val updatedAtMillis: Long,
)

@Entity(tableName = "customers")
data class CustomerEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val name: String,
  val address: String,
  val phone: String,
  val email: String,
  val companyName: String,
  val gstin: String,
  val totalBookings: Long,
  val totalBilled: Long,
  val pendingBalance: Long,
  val createdAtMillis: Long,
  val updatedAtMillis: Long,
)

fun BookingEntity.toDomain(): Booking = Booking(
  id = id,
  userId = userId,
  customerName = customerName,
  companyName = companyName,
  customerPhone = customerPhone,
  pickupLocation = pickupLocation,
  dropLocation = dropLocation,
  vehicleCategory = vehicleCategory,
  vehicleModel = vehicleModel,
  vehicleNumber = vehicleNumber,
  driverName = driverName,
  driverPhone = driverPhone,
  tripStatus = tripStatus,
  paymentStatus = paymentStatus,
  paymentChannel = paymentChannel,
  baseAmount = baseAmount,
  tollCharges = tollCharges,
  parkingCharges = parkingCharges,
  gstAmount = gstAmount,
  totalAmount = totalAmount,
  expenseAmount = expenseAmount,
  distanceKm = distanceKm,
  tripDate = tripDate,
  createdAt = Timestamp(Date(createdAtMillis)),
  updatedAt = Timestamp(Date(updatedAtMillis)),
)

fun Booking.toEntity(idOverride: String, userId: String, createdAtMillis: Long, updatedAtMillis: Long): BookingEntity =
  BookingEntity(
    id = idOverride,
    userId = userId,
    customerName = customerName.trim(),
    companyName = companyName.trim(),
    customerPhone = customerPhone.trim(),
    pickupLocation = pickupLocation.trim(),
    dropLocation = dropLocation.trim(),
    vehicleCategory = vehicleCategory,
    vehicleModel = vehicleModel.trim(),
    vehicleNumber = vehicleNumber.trim(),
    driverName = driverName.trim(),
    driverPhone = driverPhone.trim(),
    tripStatus = tripStatus,
    paymentStatus = paymentStatus,
    paymentChannel = paymentChannel,
    baseAmount = baseAmount,
    tollCharges = tollCharges,
    parkingCharges = parkingCharges,
    gstAmount = gstAmount,
    totalAmount = totalAmount,
    expenseAmount = expenseAmount,
    distanceKm = distanceKm,
    tripDate = tripDate,
    createdAtMillis = createdAtMillis,
    updatedAtMillis = updatedAtMillis,
  )

fun DriverEntity.toDomain(): Driver = Driver(
  id = id,
  userId = userId,
  name = name,
  phone = phone,
  address = address,
  licenseNumber = licenseNumber,
  aadharNumber = aadharNumber,
  selfiePhotoUri = selfiePhotoUri,
  documentsUploaded = documentsUploaded,
  assignedVehicle = assignedVehicle,
  vehicleCategory = vehicleCategory,
  status = status,
  tripsCompleted = tripsCompleted,
  earningsTotal = earningsTotal,
  createdAt = Timestamp(Date(createdAtMillis)),
  updatedAt = Timestamp(Date(updatedAtMillis)),
)

fun Driver.toEntity(idOverride: String, userId: String, createdAtMillis: Long, updatedAtMillis: Long): DriverEntity =
  DriverEntity(
    id = idOverride,
    userId = userId,
    name = name.trim(),
    phone = phone.trim(),
    address = address.trim(),
    licenseNumber = licenseNumber.trim(),
    aadharNumber = aadharNumber.trim(),
    selfiePhotoUri = selfiePhotoUri.trim(),
    documentsUploaded = documentsUploaded.trim(),
    assignedVehicle = assignedVehicle.trim(),
    vehicleCategory = vehicleCategory,
    status = status,
    tripsCompleted = tripsCompleted,
    earningsTotal = earningsTotal,
    createdAtMillis = createdAtMillis,
    updatedAtMillis = updatedAtMillis,
  )

fun VehicleEntity.toDomain(): Vehicle = Vehicle(
  id = id,
  userId = userId,
  registrationNumber = registrationNumber,
  modelName = modelName,
  manufacturingYear = manufacturingYear,
  category = category,
  status = status,
  maintenanceCost = maintenanceCost,
  serviceDate = serviceDate,
  nextServiceDate = nextServiceDate,
  serviceKilometers = serviceKilometers,
  presentRunningKilometers = presentRunningKilometers,
  nextServiceKilometers = nextServiceKilometers,
  partsChanged = partsChanged,
  insuranceDetails = insuranceDetails,
  insuranceExpiryDate = insuranceExpiryDate,
  fitnessDetails = fitnessDetails,
  fitnessExpiryDate = fitnessExpiryDate,
  pollutionDetails = pollutionDetails,
  pollutionExpiryDate = pollutionExpiryDate,
  serviceNotes = serviceNotes,
  createdAt = Timestamp(Date(createdAtMillis)),
  updatedAt = Timestamp(Date(updatedAtMillis)),
)

fun Vehicle.toEntity(idOverride: String, userId: String, createdAtMillis: Long, updatedAtMillis: Long): VehicleEntity =
  VehicleEntity(
    id = idOverride,
    userId = userId,
    registrationNumber = registrationNumber.trim(),
    modelName = modelName.trim(),
    manufacturingYear = manufacturingYear.trim(),
    category = category,
    status = status,
    maintenanceCost = maintenanceCost,
    serviceDate = serviceDate.trim(),
    nextServiceDate = nextServiceDate.trim(),
    serviceKilometers = serviceKilometers,
    presentRunningKilometers = presentRunningKilometers,
    nextServiceKilometers = nextServiceKilometers,
    partsChanged = partsChanged.trim(),
    insuranceDetails = insuranceDetails.trim(),
    insuranceExpiryDate = insuranceExpiryDate.trim(),
    fitnessDetails = fitnessDetails.trim(),
    fitnessExpiryDate = fitnessExpiryDate.trim(),
    pollutionDetails = pollutionDetails.trim(),
    pollutionExpiryDate = pollutionExpiryDate.trim(),
    serviceNotes = serviceNotes.trim(),
    createdAtMillis = createdAtMillis,
    updatedAtMillis = updatedAtMillis,
  )

fun LeadEntity.toDomain(): Lead = Lead(
  id = id,
  userId = userId,
  clientName = clientName,
  clientPhone = clientPhone,
  routeSummary = routeSummary,
  preferredCategory = preferredCategory,
  quotedAmount = quotedAmount,
  status = status,
  source = source,
  createdAt = Timestamp(Date(createdAtMillis)),
  updatedAt = Timestamp(Date(updatedAtMillis)),
)

fun Lead.toEntity(idOverride: String, userId: String, createdAtMillis: Long, updatedAtMillis: Long): LeadEntity =
  LeadEntity(
    id = idOverride,
    userId = userId,
    clientName = clientName.trim(),
    clientPhone = clientPhone.trim(),
    routeSummary = routeSummary.trim(),
    preferredCategory = preferredCategory,
    quotedAmount = quotedAmount,
    status = status,
    source = source.trim(),
    createdAtMillis = createdAtMillis,
    updatedAtMillis = updatedAtMillis,
  )

fun CustomerEntity.toDomain(): Customer = Customer(
  id = id,
  userId = userId,
  name = name,
  address = address,
  phone = phone,
  email = email,
  companyName = companyName,
  gstin = gstin,
  totalBookings = totalBookings,
  totalBilled = totalBilled,
  pendingBalance = pendingBalance,
  createdAt = Timestamp(Date(createdAtMillis)),
  updatedAt = Timestamp(Date(updatedAtMillis)),
)

fun Customer.toEntity(idOverride: String, userId: String, createdAtMillis: Long, updatedAtMillis: Long): CustomerEntity =
  CustomerEntity(
    id = idOverride,
    userId = userId,
    name = name.trim(),
    address = address.trim(),
    phone = phone.trim(),
    email = email.trim(),
    companyName = companyName.trim(),
    gstin = gstin.trim(),
    totalBookings = totalBookings,
    totalBilled = totalBilled,
    pendingBalance = pendingBalance,
    createdAtMillis = createdAtMillis,
    updatedAtMillis = updatedAtMillis,
  )

@Dao
interface CrmDao {
  @Query("SELECT * FROM bookings WHERE userId = :userId ORDER BY createdAtMillis DESC")
  fun observeBookings(userId: String): Flow<List<BookingEntity>>

  @Query("SELECT * FROM bookings WHERE userId = :userId AND id = :bookingId LIMIT 1")
  suspend fun getBookingById(userId: String, bookingId: String): BookingEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertBooking(entity: BookingEntity)

  @Query("DELETE FROM bookings WHERE userId = :userId AND id = :bookingId")
  suspend fun deleteBooking(userId: String, bookingId: String)

  @Query("SELECT * FROM drivers WHERE userId = :userId ORDER BY createdAtMillis DESC")
  fun observeDrivers(userId: String): Flow<List<DriverEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertDriver(entity: DriverEntity)

  @Query("DELETE FROM drivers WHERE userId = :userId AND id = :driverId")
  suspend fun deleteDriver(userId: String, driverId: String)

  @Query("SELECT * FROM vehicles WHERE userId = :userId ORDER BY createdAtMillis DESC")
  fun observeVehicles(userId: String): Flow<List<VehicleEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertVehicle(entity: VehicleEntity)

  @Query("DELETE FROM vehicles WHERE userId = :userId AND id = :vehicleId")
  suspend fun deleteVehicle(userId: String, vehicleId: String)

  @Query("SELECT * FROM leads WHERE userId = :userId ORDER BY createdAtMillis DESC")
  fun observeLeads(userId: String): Flow<List<LeadEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertLead(entity: LeadEntity)

  @Query("DELETE FROM leads WHERE userId = :userId AND id = :leadId")
  suspend fun deleteLead(userId: String, leadId: String)

  @Query("SELECT * FROM customers WHERE userId = :userId ORDER BY createdAtMillis DESC")
  fun observeCustomers(userId: String): Flow<List<CustomerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertCustomer(entity: CustomerEntity)

  @Query("DELETE FROM customers WHERE userId = :userId AND id = :customerId")
  suspend fun deleteCustomer(userId: String, customerId: String)
}

@Database(
  entities = [
    BookingEntity::class,
    DriverEntity::class,
    VehicleEntity::class,
    LeadEntity::class,
    CustomerEntity::class,
  ],
  version = 1,
  exportSchema = false
)
abstract class CrmDatabase : RoomDatabase() {
  abstract fun crmDao(): CrmDao

  companion object {
    @Volatile
    private var INSTANCE: CrmDatabase? = null

    fun getInstance(context: Context): CrmDatabase {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: Room.databaseBuilder(
          context.applicationContext,
          CrmDatabase::class.java,
          "fleet_crm_local.db"
        ).fallbackToDestructiveMigration(dropAllTables = true)
          .build()
          .also { INSTANCE = it }
      }
    }
  }
}
