package com.d3if4802.buslog2.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d3if4802.buslog2.model.BusLog
import com.d3if4802.buslog2.model.BusLogRequest
import com.d3if4802.buslog2.network.ApiConfig
import com.d3if4802.buslog2.network.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

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
                val cleanEmail = userEmail.trim().lowercase()
                val logs = ApiConfig.getApiService().getBusLogs("eq.$cleanEmail")
                _logState.value = ApiState.Success(logs)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("SUPABASE_ERROR", "GET Error: $errorBody")
                _logState.value = ApiState.Error("DB Error: $errorBody")
            } catch (e: Exception) {
                Log.e("SUPABASE_ERROR", "GET Exception: ${e.message}", e)
                _logState.value = ApiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun addLog(platNomor: String, catatan: String, userEmail: String, imageBytes: ByteArray?) {
        viewModelScope.launch {
            _logState.value = ApiState.Loading
            try {
                val cleanEmail = userEmail.trim().lowercase()
                val projectUrl = "https://mzwjpoquwnbjjhiyvhad.supabase.co"
                var publicImageUrl: String? = null

                if (imageBytes != null) {
                    val fileName = "img_${System.currentTimeMillis()}.jpg"
                    val uploadUrl = "$projectUrl/storage/v1/object/bus_images/$fileName"
                    val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    ApiConfig.getApiService().uploadImage(uploadUrl, requestBody)
                    publicImageUrl = "$projectUrl/storage/v1/object/public/bus_images/$fileName"
                }

                val newLog = BusLogRequest(
                    platNomor = platNomor,
                    catatan = catatan,
                    imageUrl = publicImageUrl,
                    userEmail = cleanEmail
                )

                ApiConfig.getApiService().addBusLog(newLog)
                getLogs(cleanEmail)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: "Unknown HTTP Error"
                Log.e("SUPABASE_ERROR", "ADD Error: $errorBody")
                _logState.value = ApiState.Error("HTTP Error - Detail: $errorBody")
            } catch (e: Exception) {
                Log.e("SUPABASE_ERROR", "ADD Exception: ${e.message}", e)
                _logState.value = ApiState.Error(e.message ?: "Error saat menyimpan")
            }
        }
    }

    fun deleteLog(id: String, userEmail: String) {
        viewModelScope.launch {
            _logState.value = ApiState.Loading
            try {
                val cleanEmail = userEmail.trim().lowercase()
                ApiConfig.getApiService().deleteBusLog("eq.$id")
                getLogs(userEmail)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _logState.value = ApiState.Error("Hapus Gagal: $errorBody")
            } catch (e: Exception) {
                _logState.value = ApiState.Error(e.message ?: "Error saat menghapus")
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
                val cleanEmail = userEmail.trim().lowercase()
                val projectUrl = "https://mzwjpoquwnbjjhiyvhad.supabase.co"
                var finalImageUrl: String? = existingImageUrl

                if (imageBytes != null) {
                    val fileName = "img_${System.currentTimeMillis()}.jpg"
                    val uploadUrl = "$projectUrl/storage/v1/object/bus_images/$fileName"
                    val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    ApiConfig.getApiService().uploadImage(uploadUrl, requestBody)
                    finalImageUrl = "$projectUrl/storage/v1/object/public/bus_images/$fileName"
                }

                val updatedLog = BusLogRequest(
                    platNomor = platNomor,
                    catatan = catatan,
                    imageUrl = finalImageUrl,
                    userEmail = cleanEmail
                )

                ApiConfig.getApiService().updateBusLog("eq.$id", updatedLog)
                getLogs(cleanEmail)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _logState.value = ApiState.Error("Update Gagal: $errorBody")
            } catch (e: Exception) {
                _logState.value = ApiState.Error(e.message ?: "Error saat update")
            }
        }
    }
}