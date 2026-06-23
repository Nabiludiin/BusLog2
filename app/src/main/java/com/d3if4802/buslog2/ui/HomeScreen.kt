package com.d3if4802.buslog2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.d3if4802.buslog2.R
import com.d3if4802.buslog2.network.ApiState
import com.d3if4802.buslog2.model.BusLog
import com.d3if4802.buslog2.viewmodel.BusLogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BusLogViewModel,
    userEmail: String,
    onProfileClick: () -> Unit,
    onAddLogClick: () -> Unit,
    onEditLogClick: (BusLog) -> Unit
) {
    val logState by viewModel.logState.collectAsState()

    LaunchedEffect(userEmail) {
        viewModel.getLogs(userEmail)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = stringResource(R.string.cd_profile))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddLogClick) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add_log))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (logState) {
                is ApiState.Idle, is ApiState.Loading -> {
                    CircularProgressIndicator()
                }
                is ApiState.Error -> {
                    val errorMessage = (logState as ApiState.Error).message
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.getLogs(userEmail) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is ApiState.Success -> {
                    val logs = (logState as ApiState.Success).data
                    if (logs.isEmpty()) {
                        Text(stringResource(R.string.empty_log_msg))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(logs) { log ->
                                LogCardItem(
                                    log = log,
                                    onEdit = { onEditLogClick(log) },
                                    onDelete = { viewModel.deleteLog(log.id.toString(), userEmail) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogCardItem(
    log: BusLog,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_dialog_title)) },
            text = { Text(stringResource(R.string.delete_dialog_msg, log.platNomor)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) { Text(stringResource(R.string.btn_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.btn_cancel)) }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!log.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = log.imageUrl,
                    contentDescription = stringResource(R.string.cd_bus_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 8.dp)
                )
            }
            Text(text = log.platNomor, style = MaterialTheme.typography.titleLarge)
            Text(text = log.catatan, style = MaterialTheme.typography.bodyMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.cd_edit))
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.cd_delete), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}