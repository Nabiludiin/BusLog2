package com.d3if4802.buslog2.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.d3if4802.buslog2.R
import com.d3if4802.buslog2.model.BusLog
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormLogScreen(
    logToEdit: BusLog? = null,
    onBackClick: () -> Unit,
    onSaveClick: (String, String, ByteArray?) -> Unit
) {
    var platNomor by remember { mutableStateOf(logToEdit?.platNomor ?: "") }
    var catatan by remember { mutableStateOf(logToEdit?.catatan ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (logToEdit == null) R.string.form_title_add else R.string.form_title_edit))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.LightGray)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = stringResource(R.string.cd_pick_image),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (!logToEdit?.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = logToEdit?.imageUrl,
                        contentDescription = stringResource(R.string.cd_pick_image),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(stringResource(R.string.cd_pick_image))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = platNomor,
                onValueChange = { platNomor = it },
                label = { Text(stringResource(R.string.plat_nomor_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = catatan,
                onValueChange = { catatan = it },
                label = { Text(stringResource(R.string.catatan_label)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    var imageBytes: ByteArray? = null
                    imageUri?.let { uri ->
                        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                        imageBytes = inputStream?.readBytes()
                    }
                    onSaveClick(platNomor, catatan, imageBytes)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = platNomor.isNotBlank() && catatan.isNotBlank()
            ) {
                Text(stringResource(R.string.btn_save))
            }
        }
    }
}