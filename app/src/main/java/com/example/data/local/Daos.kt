package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.CompanyReview
import com.example.data.models.DispatchMessage
import com.example.data.models.Ticket
import com.example.data.models.TransportCompany
import com.example.data.models.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TransportCompanyDao {
    @Query("SELECT * FROM companies ORDER BY rating DESC")
    fun getAllCompanies(): Flow<List<TransportCompany>>

    @Query("SELECT * FROM companies WHERE id = :id")
    suspend fun getCompanyById(id: String): TransportCompany?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanies(companies: List<TransportCompany>)
}

@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE departureCity LIKE '%' || :departure || '%' AND arrivalCity LIKE '%' || :arrival || '%'")
    fun searchTrips(departure: String, arrival: String): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: String): Trip?

    @Query("UPDATE trips SET currentLat = :lat, currentLng = :lng, speedKmH = :speed, currentStopName = :currentStop, nextStopName = :nextStop WHERE id = :tripId")
    suspend fun updateTripGps(tripId: String, lat: Double, lng: Double, speed: Int, currentStop: String, nextStop: String)

    @Query("UPDATE trips SET availableSeats = :availableSeats WHERE id = :tripId")
    suspend fun updateSeats(tripId: String, availableSeats: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<Trip>)
}

@Dao
interface TicketDao {
    @Query("SELECT * FROM tickets ORDER BY bookingTimestamp DESC")
    fun getAllTickets(): Flow<List<Ticket>>

    @Query("SELECT * FROM tickets WHERE id = :id")
    suspend fun getTicketById(id: String): Ticket?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: Ticket)

    @Update
    suspend fun updateTicket(ticket: Ticket)

    @Query("UPDATE tickets SET isCheckedIn = :isCheckedIn WHERE id = :ticketId")
    suspend fun updateCheckInStatus(ticketId: String, isCheckedIn: Boolean)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews ORDER BY id DESC")
    fun getAllReviews(): Flow<List<CompanyReview>>

    @Query("SELECT * FROM reviews WHERE companyId = :companyId ORDER BY id DESC")
    fun getReviewsForCompany(companyId: String): Flow<List<CompanyReview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: CompanyReview)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<CompanyReview>)
}

@Dao
interface DispatchMessageDao {
    @Query("SELECT * FROM dispatch_messages ORDER BY id DESC")
    fun getMessages(): Flow<List<DispatchMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: DispatchMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<DispatchMessage>)
}
