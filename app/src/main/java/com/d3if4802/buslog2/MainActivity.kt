package com.d3if4802.buslog2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.d3if4802.buslog2.datastore.UserPreferences
import com.d3if4802.buslog2.ui.HomeScreen
import com.d3if4802.buslog2.ui.LoginScreen
import com.d3if4802.buslog2.ui.ProfileScreen
import com.d3if4802.buslog2.ui.theme.BusLog2Theme
import com.d3if4802.buslog2.viewmodel.BusLogViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userPreferences = UserPreferences(this)

        setContent {
            BusLog2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isLoggedIn by userPreferences.isLoggedIn.collectAsState(initial = false)
                    val userEmail by userPreferences.userEmail.collectAsState(initial = "")

                    val viewModel: BusLogViewModel = viewModel()
                    val navController = rememberNavController()
                    val coroutineScope = rememberCoroutineScope()

                    NavHost(
                        navController = navController,
                        startDestination = if (isLoggedIn) "home" else "login"
                    ) {

                        composable("login") {
                            LoginScreen(
                                userPreferences = userPreferences,
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                userEmail = userEmail,
                                onProfileClick = {
                                    navController.navigate("profile")
                                },
                                onAddLogClick = {
                                    // TODO: Navigasi ke Form Tambah (Langkah Berikutnya)
                                },
                                onEditLogClick = { log ->
                                    // TODO: Navigasi ke Form Edit (Langkah Berikutnya)
                                }
                            )
                        }

                        composable("profile") {
                            ProfileScreen(
                                userEmail = userEmail,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onLogoutClick = {
                                    coroutineScope.launch {
                                        userPreferences.logout()
                                        navController.navigate("login") {
                                            popUpTo(0)
                                        }
                                    }
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}