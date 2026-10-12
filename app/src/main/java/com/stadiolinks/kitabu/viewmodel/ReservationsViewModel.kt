package com.stadiolinks.kitabu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stadiolinks.kitabu.data.entity.BookingEntity
import com.stadiolinks.kitabu.data.entity.BookEntity
import com.stadiolinks.kitabu.data.repository.LibraryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BookingWithBook(val booking: BookingEntity, val book: BookEntity)

class ReservationsViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    val activeBookingsWithBooks: StateFlow<List<BookingWithBook>> =
        repository.getActiveBookings()
            .combine(repository.getAllBooks()) { bookings, books ->
                val bookMap = books.associateBy { it.bookId }
                bookings.mapNotNull { booking ->
                    bookMap[booking.bookOwnerId]?.let { BookingWithBook(booking, it) }
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun renewBooking(bookingId: Long, additionalDays: Int = 7) {
        viewModelScope.launch { repository.renewBooking(bookingId, additionalDays) }
    }

    fun returnBook(bookingId: Long) {
        viewModelScope.launch { repository.returnBook(bookingId) }
    }

    fun cancelBooking(bookingId: Long) {
        viewModelScope.launch { repository.cancelBooking(bookingId) }
    }
}