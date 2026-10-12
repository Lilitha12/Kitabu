package com.stadiolinks.kitabu.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stadiolinks.kitabu.data.profile.ProfileManager
import com.stadiolinks.kitabu.data.repository.LibraryRepository
import com.stadiolinks.kitabu.ui.screens.BookCatalogScreen
import com.stadiolinks.kitabu.ui.screens.ProfileScreen
import com.stadiolinks.kitabu.ui.screens.ReservationsDashboardScreen
import com.stadiolinks.kitabu.viewmodel.BookCatalogViewModel
import com.stadiolinks.kitabu.viewmodel.ReservationsViewModel

sealed class Screen(val route: String) {
    data object Catalog : Screen("catalog")
    data object Reservations : Screen("reservations")
    data object Profile : Screen("profile")
}

@Composable
fun AppNavigation(
    repository: LibraryRepository,
    profileManager: ProfileManager,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Catalog.route,
        modifier = modifier
    ) {
        composable(Screen.Catalog.route) {
            val vm: BookCatalogViewModel = viewModel { BookCatalogViewModel(repository) }
            BookCatalogScreen(
                viewModel = vm,
                profileManager = profileManager,
                onNavigateToReservations = {
                    navController.navigate(Screen.Reservations.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.Reservations.route) {
            val vm: ReservationsViewModel = viewModel { ReservationsViewModel(repository) }
            ReservationsDashboardScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                profileManager = profileManager,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}