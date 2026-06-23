package com.d3if4802.buslog2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d3if4802.buslog2.datastore.UserPreferences
import com.d3if4802.buslog2.ui.HomeScreen
import com.d3if4802.buslog2.ui.LoginScreen
import com.d3if4802.buslog2.ui.theme.BusLog2Theme
import com.d3if4802.buslog2.viewmodel.BusLogViewModel

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

                    if (isLoggedIn) {
                        HomeScreen(
                            viewModel = viewModel,
                            userEmail = userEmail,
                            onProfileClick = {
                                // TODO: Nanti kita isi untuk buka Profil & Logout
                            },
                            onAddLogClick = {
                                // TODO: Nanti kita isi untuk buka form Tambah Data
                            },
                            onEditLogClick = { log ->
                                // TODO: Nanti kita isi untuk buka form Edit Data
                            }
                        )
                    } else {
                        LoginScreen(
                            userPreferences = userPreferences,
                            onLoginSuccess = {
                            }
                        )
                    }
                }
            }
        }
    }
}