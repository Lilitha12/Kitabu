package com.stadiolinks.kitabu.data.database

import androidx.room.TypeConverter
import com.stadiolinks.kitabu.domain.model.BookingStatus

class Converters {
    @TypeConverter
    fun fromBookingStatus(status: BookingStatus): String = status.name

    @TypeConverter
    fun toBookingStatus(value: String): BookingStatus = try {
        BookingStatus.valueOf(value)
    } catch (e: IllegalArgumentException) {
        BookingStatus.PENDING
    }
}