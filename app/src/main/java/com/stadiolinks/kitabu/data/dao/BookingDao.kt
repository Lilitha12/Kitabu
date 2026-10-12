package com.stadiolinks.kitabu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.stadiolinks.kitabu.data.entity.BookingEntity
import com.stadiolinks.kitabu.domain.model.BookingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Query("SELECT * FROM bookings ORDER BY bookingDate DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE status = 'ACTIVE' OR status = 'PENDING' ORDER BY bookingDate DESC")
    fun getActiveBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE status = 'RETURNED' ORDER BY bookingDate DESC")
    fun getReturnedBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE bookOwnerId = :bookId")
    fun getBookingsForBook(bookId: Long): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE bookingId = :id")
    suspend fun getBookingById(id: Long): BookingEntity?

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Query("UPDATE bookings SET returnDeadline = :newDeadline WHERE bookingId = :bookingId")
    suspend fun extendDeadline(bookingId: Long, newDeadline: Long)

    @Query("UPDATE bookings SET status = :status WHERE bookingId = :bookingId")
    suspend fun updateStatus(bookingId: Long, status: BookingStatus)

    @Query("DELETE FROM bookings WHERE bookingId = :id")
    suspend fun deleteBooking(id: Long)

    @Query("DELETE FROM bookings WHERE status = 'PENDING' AND bookingId = :id")
    suspend fun cancelPendingBooking(id: Long)
}