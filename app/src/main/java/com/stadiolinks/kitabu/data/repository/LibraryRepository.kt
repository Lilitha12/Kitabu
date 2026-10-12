package com.stadiolinks.kitabu.data.repository

import com.stadiolinks.kitabu.data.dao.BookDao
import com.stadiolinks.kitabu.data.dao.BookingDao
import com.stadiolinks.kitabu.data.entity.BookEntity
import com.stadiolinks.kitabu.data.entity.BookingEntity
import com.stadiolinks.kitabu.domain.model.BookingStatus
import kotlinx.coroutines.flow.Flow

class LibraryRepository(
    private val bookDao: BookDao,
    private val bookingDao: BookingDao
) {
    fun getAllBooks(): Flow<List<BookEntity>> = bookDao.getAllBooks()
    fun getAvailableBooks(): Flow<List<BookEntity>> = bookDao.getAvailableBooks()
    fun getBorrowedBooks(): Flow<List<BookEntity>> = bookDao.getBorrowedBooks()
    fun searchBooks(query: String): Flow<List<BookEntity>> = bookDao.searchBooks(query)
    suspend fun getBookById(id: Long): BookEntity? = bookDao.getBookById(id)
    suspend fun updateBook(book: BookEntity) = bookDao.updateBook(book)

    fun getAllBookings(): Flow<List<BookingEntity>> = bookingDao.getAllBookings()
    fun getActiveBookings(): Flow<List<BookingEntity>> = bookingDao.getActiveBookings()
    fun getReturnedBookings(): Flow<List<BookingEntity>> = bookingDao.getReturnedBookings()
    suspend fun getBookingById(id: Long): BookingEntity? = bookingDao.getBookingById(id)

    suspend fun reserveBook(bookId: Long, userName: String, rentalDays: Int) {
        val now = System.currentTimeMillis()
        val deadline = now + (rentalDays * 24L * 60 * 60 * 1000)
        bookingDao.insertBooking(
            BookingEntity(
                bookOwnerId = bookId,
                userName = userName,
                bookingDate = now,
                returnDeadline = deadline,
                status = BookingStatus.ACTIVE
            )
        )
        bookDao.getBookById(bookId)?.let { bookDao.updateBook(it.copy(isAvailable = false)) }
    }

    suspend fun renewBooking(bookingId: Long, additionalDays: Int) {
        bookingDao.getBookingById(bookingId)?.let {
            bookingDao.extendDeadline(bookingId, it.returnDeadline + (additionalDays * 24L * 60 * 60 * 1000))
        }
    }

    suspend fun returnBook(bookingId: Long) {
        bookingDao.getBookingById(bookingId)?.let { booking ->
            bookingDao.updateStatus(bookingId, BookingStatus.RETURNED)
            bookDao.getBookById(booking.bookOwnerId)?.let { bookDao.updateBook(it.copy(isAvailable = true)) }
        }
    }

    suspend fun cancelBooking(bookingId: Long) {
        bookingDao.getBookingById(bookingId)?.let { booking ->
            if (booking.status == BookingStatus.PENDING) {
                bookingDao.cancelPendingBooking(bookingId)
                bookDao.getBookById(booking.bookOwnerId)?.let { bookDao.updateBook(it.copy(isAvailable = true)) }
            }
        }
    }
}