package com.stadiolinks.kitabu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stadiolinks.kitabu.data.entity.BookEntity
import com.stadiolinks.kitabu.data.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookCatalogViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow("All")
    val filter: StateFlow<String> = _filter.asStateFlow()

    val filteredBooks: StateFlow<List<BookEntity>> = _filter.flatMapLatest { filter ->
        when (filter) {
            "Available" -> repository.getAvailableBooks()
            "Borrowed" -> repository.getBorrowedBooks()
            else -> repository.getAllBooks()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchResults: StateFlow<List<BookEntity>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) repository.getAllBooks() else repository.searchBooks(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val books: StateFlow<List<BookEntity>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            _filter.flatMapLatest { filter ->
                when (filter) {
                    "Available" -> repository.getAvailableBooks()
                    "Borrowed" -> repository.getBorrowedBooks()
                    else -> repository.getAllBooks()
                }
            }
        } else {
            repository.searchBooks(query)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updateFilter(newFilter: String) { _filter.value = newFilter }

    fun reserveBook(bookId: Long, userName: String, rentalDays: Int) {
        viewModelScope.launch { repository.reserveBook(bookId, userName, rentalDays) }
    }
}