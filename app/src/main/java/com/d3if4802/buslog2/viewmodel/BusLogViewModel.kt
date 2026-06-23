package com.d3if4802.buslog2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d3if4802.buslog2.model.BusLog
import com.d3if4802.buslog2.network.ApiConfig
import com.d3if4802.buslog2.network.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class BusLogViewModel : ViewModel() {
    private val _logState = MutableStateFlow<ApiState>(ApiState.Idle)
    val logState: StateFlow<ApiState> = _logState

    private val _logToEdit = MutableStateFlow<BusLog?>(null)
    val logToEdit: StateFlow<BusLog?> = _logToEdit

    fun setLogToEdit(log: BusLog?) {
        _logToEdit.value = log
    }

    fun getLogs(userEmail: String) {
        viewModelScope.launch {
            _logState.value = ApiState.Loading
            try {
                val logs = ApiConfig.getApiService().getBusLogs("eq.$userEmail")
                _logState.value = ApiState.Success(logs)
            } catch (e: Exception) {
                _logState.value = ApiState.Error(e.message ?: "Error")
            }
        }
    }

    fun deleteLog(id: String, userEmail: String) {
        viewModelScope.launch {
            try {
                ApiConfig.getApiService().deleteBusLog("eq.$id")
                getLogs(userEmail)
            } catch (e: Exception) {
                _logState.value = ApiState.Error(e.message ?: "Error")
            }
        }
    }

    fun addLogWithImage(
        platNomor: String,
        catatan: String,
        userEmail: String,
        imageBytes: ByteArray?
    ) {
        viewModelScope.launch {
            _logState.value = ApiState.Loading
            try {
                val projectUrl = "https://mzwjpoquwnbjjhiyvhad.supabase.co"
                var publicImageUrl = ""

                if (imageBytes != null) {
                    val fileName = "img_${System.currentTimeMillis()}.jpg"
                    val uploadUrl = "$projectUrl/storage/v1/object/bus_images/$fileName"
                    val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    ApiConfig.getApiService().uploadImage(uploadUrl, requestBody)
                    publicImageUrl = "$projectUrl/storage/v1/object/public/bus_images/$fileName"
                }

                val newLog = BusLog(
                    platNomor = platNomor,
                    catatan = catatan,
                    imageUrl = publicImageUrl,
                    userEmail = userEmail
                )
                ApiConfig.getApiService().addBusLog(newLog)
                getLogs(userEmail)
            } catch (e: Exception) {
                _logState.value = ApiState.Error(e.message ?: "Error")
            }
        }
    }

    fun updateLog(
        id: String,
        platNomor: String,
        catatan: String,
        userEmail: String,
        imageBytes: ByteArray?,
        existingImageUrl: String?
    ) {
        viewModelScope.launch {
            _logState.value = ApiState.Loading
            try {
                val projectUrl = "https://mzwjpoquwnbjjhiyvhad.supabase.co"
                var finalImageUrl = existingImageUrl ?: ""

                if (imageBytes != null) {
                    val fileName = "img_${System.currentTimeMillis()}.jpg"
                    val uploadUrl = "$projectUrl/storage/v1/object/bus_images/$fileName"
                    val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    ApiConfig.getApiService().uploadImage(uploadUrl, requestBody)
                    finalImageUrl = "$projectUrl/storage/v1/object/public/bus_images/$fileName"
                }

                val updatedLog = BusLog(
                    platNomor = platNomor,
                    catatan = catatan,
                    imageUrl = finalImageUrl,
                    userEmail = userEmail
                )
                ApiConfig.getApiService().updateBusLog("eq.$id", updatedLog)
                getLogs(userEmail)
            } catch (e: Exception) {
                _logState.value = ApiState.Error(e.message ?: "Error")
            }
        }
    }
}