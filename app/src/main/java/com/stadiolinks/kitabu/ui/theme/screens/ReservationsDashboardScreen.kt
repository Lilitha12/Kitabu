package com.stadiolinks.kitabu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stadiolinks.kitabu.domain.model.BookingStatus
import com.stadiolinks.kitabu.ui.theme.AvailableGreen
import com.stadiolinks.kitabu.ui.theme.BorrowedRed
import com.stadiolinks.kitabu.ui.theme.PendingOrange
import com.stadiolinks.kitabu.ui.theme.ReturnedBlue
import com.stadiolinks.kitabu.viewmodel.BookingWithBook
import com.stadiolinks.kitabu.viewmodel.ReservationsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationsDashboardScreen(viewModel: ReservationsViewModel, onNavigateBack: () -> Unit) {
    val activeBookings by viewModel.activeBookingsWithBooks.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("My Reservations", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface))
        if (activeBookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.Book, null, modifier = Modifier.size(64.dp), tint = Color.Gray.copy(0.5f)); Spacer(modifier = Modifier.height(8.dp)); Text("No active reservations", color = Color.Gray) }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                items(activeBookings, key = { it.booking.bookingId }) { item ->
                    BookingCard(item, onRenew = { viewModel.renewBooking(item.booking.bookingId) }, onReturn = { viewModel.returnBook(item.booking.bookingId) }, onCancel = { viewModel.cancelBooking(item.booking.bookingId) })
                }
            }
        }
    }
}

@Composable
fun BookingCard(item: BookingWithBook, onRenew: () -> Unit, onReturn: () -> Unit, onCancel: () -> Unit) {
    val booking = item.booking; val book = item.book; val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()); val context = LocalContext.current
    val daysRemaining = ((booking.returnDeadline - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt(); val isOverdue = daysRemaining < 0
    val statusColor = when (booking.status) { BookingStatus.PENDING -> PendingOrange; BookingStatus.ACTIVE -> if (isOverdue) BorrowedRed else AvailableGreen; BookingStatus.RETURNED -> ReturnedBlue }
    val statusLabel = when (booking.status) { BookingStatus.PENDING -> "Pending Pickup"; BookingStatus.ACTIVE -> if (isOverdue) "Overdue!" else "Active"; BookingStatus.RETURNED -> "Returned" }
    val imageResId = remember(book.imageRes) { if (book.imageRes.isNotBlank()) context.resources.getIdentifier(book.imageRes, "drawable", context.packageName) else 0 }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                if (imageResId != 0) Image(painter = painterResource(id = imageResId), contentDescription = book.title, modifier = Modifier.size(width = 56.dp, height = 80.dp).clip(RoundedCornerShape(6.dp)), contentScale = ContentScale.Crop) else Icon(Icons.Default.Book, "Book", modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) { Text(book.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("by ${book.author}", fontSize = 13.sp, color = Color.Gray) }
                Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = statusColor.copy(0.15f))) { Text(statusLabel, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { DetailItem("Borrower", booking.userName); DetailItem("Reserved", sdf.format(Date(booking.bookingDate))) }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { DetailItem("Return By", sdf.format(Date(booking.returnDeadline))); DetailItem("Days Left", if (isOverdue) "${-daysRemaining} overdue" else "$daysRemaining days", if (isOverdue) BorrowedRed else AvailableGreen) }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (booking.status == BookingStatus.ACTIVE) { ActionBtn("Renew", Icons.Default.Refresh, AvailableGreen, onRenew, Modifier.weight(1f)); ActionBtn("Return", Icons.Default.CheckCircle, ReturnedBlue, onReturn, Modifier.weight(1f)) }
                if (booking.status == BookingStatus.PENDING) { ActionBtn("Cancel", Icons.Default.Cancel, BorrowedRed, onCancel, Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) { Column { Text(label, fontSize = 11.sp, color = Color.Gray); Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = valueColor) } }

@Composable
fun ActionBtn(text: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = color)) { Icon(icon, text, modifier = Modifier.size(16.dp)); Spacer(modifier = Modifier.width(4.dp)); Text(text, fontSize = 12.sp) }
}