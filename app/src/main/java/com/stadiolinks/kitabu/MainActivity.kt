package com.stadiolinks.kitabu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.stadiolinks.kitabu.data.database.AppDatabase
import com.stadiolinks.kitabu.data.profile.ProfileManager
import com.stadiolinks.kitabu.data.repository.LibraryRepository
import com.stadiolinks.kitabu.ui.navigation.AppNavigation
import com.stadiolinks.kitabu.ui.theme.KitabuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext)
        val repository = LibraryRepository(db.bookDao(), db.bookingDao())
        val profileManager = ProfileManager(applicationContext)

        setContent {
            KitabuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        repository = repository,
                        profileManager = profileManager
                    )
                }
            }
        }
    }
}