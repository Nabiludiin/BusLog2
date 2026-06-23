package com.d3if4802.buslog2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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

    LaunchedEffect(Unit) {
        viewModel.getLogs(userEmail)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BusLog") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddLogClick) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Log")
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
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }
                is ApiState.Success -> {
                    val logs = (logState as ApiState.Success).data
                    if (logs.isEmpty()) {
                        Text("Belum ada log bus. Tekan tombol + untuk menambah.")
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(logs) { log ->
                                LogCardItem(
                                    log = log,
                                    onEdit = { onEditLogClick(log) },
                                    onDelete = { viewModel.deleteLog(log.id, userEmail) }
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
            title = { Text("Hapus Log?") },
            text = { Text("Apakah kamu yakin ingin menghapus data bus ${log.platNomor} ini?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
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
                    contentDescription = "Foto Bus",
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
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}