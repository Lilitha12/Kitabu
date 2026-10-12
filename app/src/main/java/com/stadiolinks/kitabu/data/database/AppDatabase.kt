package com.stadiolinks.kitabu.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.stadiolinks.kitabu.data.dao.BookDao
import com.stadiolinks.kitabu.data.dao.BookingDao
import com.stadiolinks.kitabu.data.entity.BookEntity
import com.stadiolinks.kitabu.data.entity.BookingEntity

@Database(
    entities = [BookEntity::class, BookingEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kitabu_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(PrePopulateCallback)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

private object PrePopulateCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        listOf(
            Triple("Introduction to Psychology", "James W. Kalat", "Psychology") to "book_psychology",
            Triple("Principles of Economics", "N. Gregory Mankiw", "Economics") to "book_economics",
            Triple("Campbell Biology", "Lisa A. Urry", "Biology") to "book_biology",
            Triple("Chemistry: The Central Science", "Theodore E. Brown", "Chemistry") to "book_chemistry",
            Triple("Physics for Scientists and Engineers", "Raymond A. Serway", "Physics") to "book_physics",
            Triple("Calculus: Early Transcendentals", "James Stewart", "Mathematics") to "book_calculus",
            Triple("Introduction to Algorithms", "Thomas H. Cormen", "Computer Science") to "book_algorithms",
            Triple("World History: Patterns of Interaction", "McDougal Littell", "History") to "book_history",
            Triple("Business Law: Text and Cases", "Kenneth W. Clarkson", "Business") to "book_business_law",
            Triple("The Elements of Style", "William Strunk Jr.", "English") to "book_elements_style"
        ).forEach { (bookInfo, imageRes) ->
            val (title, author, category) = bookInfo
            db.execSQL(
                "INSERT OR IGNORE INTO books (title, author, category, isAvailable, imageRes) VALUES (?, ?, ?, ?, ?)",
                arrayOf<Any>(title, author, category, 1, imageRes)
            )
        }
    }
}