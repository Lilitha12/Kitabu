package com.stadiolinks.kitabu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.stadiolinks.kitabu.data.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllBooks(books: List<BookEntity>)

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isAvailable = 1 ORDER BY title ASC")
    fun getAvailableBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isAvailable = 0 ORDER BY title ASC")
    fun getBorrowedBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchBooks(query: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE bookId = :id")
    suspend fun getBookById(id: Long): BookEntity?

    @Update
    suspend fun updateBook(book: BookEntity)

    @Query("DELETE FROM books WHERE bookId = :id")
    suspend fun deleteBook(id: Long)
}