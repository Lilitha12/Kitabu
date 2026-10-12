package com.stadiolinks.kitabu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stadiolinks.kitabu.data.entity.BookEntity
import com.stadiolinks.kitabu.data.profile.ProfileManager
import com.stadiolinks.kitabu.ui.theme.AvailableGreen
import com.stadiolinks.kitabu.ui.theme.BorrowedRed
import com.stadiolinks.kitabu.viewmodel.BookCatalogViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCatalogScreen(
    viewModel: BookCatalogViewModel,
    profileManager: ProfileManager,
    onNavigateToReservations: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val books by viewModel.books.collectAsState()
    val studentName by profileManager.studentName.collectAsState(initial = "")

    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }
    var rentalDays by remember { mutableStateOf(7) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Kitabu",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(onClick = onNavigateToProfile) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Button(onClick = onNavigateToReservations) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "My Reservations",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("My Books", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by title or author...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Available", "Borrowed").forEach { f ->
                FilterChip(
                    selected = filter == f,
                    onClick = { viewModel.updateFilter(f) },
                    label = { Text(f, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (books.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No books found", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(books, key = { it.bookId }) { book ->
                    BookCard(
                        book = book,
                        onReserveClick = {
                            selectedBook = book
                            showBottomSheet = true
                        }
                    )
                }
            }
        }
    }

    if (showBottomSheet && selectedBook != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            ReservationSheet(
                book = selectedBook!!,
                rentalDays = rentalDays,
                onRentalDaysChange = { rentalDays = it },
                studentName = studentName,
                hasProfile = studentName.isNotBlank(),
                onGoToProfile = {
                    showBottomSheet = false
                    onNavigateToProfile()
                },
                onReserve = {
                    viewModel.reserveBook(
                        bookId = selectedBook!!.bookId,
                        userName = studentName.trim(),
                        rentalDays = rentalDays
                    )
                    showBottomSheet = false
                    showSuccessDialog = true
                    rentalDays = 7
                }
            )
        }
    }

    if (showSuccessDialog && selectedBook != null) {
        ReservationSuccessDialog(
            book = selectedBook!!,
            studentName = studentName,
            onDismiss = { showSuccessDialog = false }
        )
    }
}

@Composable
fun ReservationSuccessDialog(
    book: BookEntity,
    studentName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(book.imageRes) {
        if (book.imageRes.isNotBlank()) {
            context.resources.getIdentifier(book.imageRes, "drawable", context.packageName)
        } else {
            0
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Book,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = AvailableGreen
            )
        },
        title = {
            Text(
                text = "Reservation Confirmed!",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (imageResId != 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = book.title,
                        modifier = Modifier
                            .size(width = 100.dp, height = 140.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = book.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "by ${book.author}",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Borrower: $studentName", fontSize = 13.sp)
                Text(
                    text = "Status: Active",
                    color = AvailableGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AvailableGreen)
            ) {
                Text("OK")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun BookCard(
    book: BookEntity,
    onReserveClick: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(book.imageRes) {
        if (book.imageRes.isNotBlank()) {
            context.resources.getIdentifier(book.imageRes, "drawable", context.packageName)
        } else {
            0
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageResId != 0) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = book.title,
                    modifier = Modifier
                        .size(width = 72.dp, height = 100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                ) {
                    Box(
                        modifier = Modifier.size(width = 72.dp, height = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Book,
                            contentDescription = "Book",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = book.author,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                val badgeColor = if (book.isAvailable) AvailableGreen else BorrowedRed
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = badgeColor.copy(alpha = 0.15f)
                    )
                ) {
                    Text(
                        text = if (book.isAvailable) "Available" else "Borrowed",
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                if (book.isAvailable) {
                    Button(
                        onClick = onReserveClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvailableGreen),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Reserve", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ReservationSheet(
    book: BookEntity,
    rentalDays: Int,
    onRentalDaysChange: (Int) -> Unit,
    studentName: String,
    hasProfile: Boolean,
    onGoToProfile: () -> Unit,
    onReserve: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(book.imageRes) {
        if (book.imageRes.isNotBlank()) {
            context.resources.getIdentifier(book.imageRes, "drawable", context.packageName)
        } else {
            0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Reserve Book",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (imageResId != 0) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = book.title,
                modifier = Modifier
                    .size(width = 120.dp, height = 170.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(text = book.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = "by ${book.author}", color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(16.dp))

        if (hasProfile) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Borrower: $studentName",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.05f)
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Text(
                        text = "Please set up your profile first",
                        color = Color.Red,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your student details are required to reserve books.",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onGoToProfile,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Go to Profile", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Rental Duration: $rentalDays days",
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(7, 14, 21, 30).forEach { days ->
                FilterChip(
                    selected = rentalDays == days,
                    onClick = { onRentalDaysChange(days) },
                    label = { Text("${days}d", fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val returnDate = Date(System.currentTimeMillis() + rentalDays * 24L * 60 * 60 * 1000)
        Text(
            text = "Return by: ${sdf.format(returnDate)}",
            color = Color.Gray,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onReserve,
            modifier = Modifier.fillMaxWidth(),
            enabled = hasProfile,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AvailableGreen)
        ) {
            Text("Confirm Reservation")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}