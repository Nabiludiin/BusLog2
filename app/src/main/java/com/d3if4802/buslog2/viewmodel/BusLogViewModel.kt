package com.d3if4802.buslog2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d3if4802.buslog2.model.BusLog
import com.d3if4802.buslog2.network.ApiConfig
import com.d3if4802.buslog2.network.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BusLogViewModel : ViewModel() {
    private val _logState = MutableStateFlow<ApiState>(ApiState.Idle)
    val logState: StateFlow<ApiState> = _logState

    fun getLogs(userEmail: String) {
        viewModelScope.launch {
            _logState.value = ApiState.Loading
            try {
                val response = ApiConfig.getApiService().getBusLogs(
                    emailFilter = "eq.$userEmail"
                )
                _logState.value = ApiState.Success(response)
            } catch (e: Exception) {
                _logState.value = ApiState.Error("Gagal memuat data: Periksa koneksi internetmu.")
            }
        }
    }
    fun addLog(log: BusLog, userEmail: String) {
        viewModelScope.launch {
            _logState.value = ApiState.Loading
            try {
                ApiConfig.getApiService().addBusLog(log)
                getLogs(userEmail)
            } catch (e: Exception) {
                _logState.value = ApiState.Error("Gagal menyimpan data: Periksa koneksi internetmu.")
            }
        }
    }

    fun deleteLog(id: Long, userEmail: String) {
        viewModelScope.launch {
            _logState.value = ApiState.Loading
            try {
                ApiConfig.getApiService().deleteBusLog("eq.$id")
                getLogs(userEmail)
            } catch (e: Exception) {
                _logState.value = ApiState.Error("Gagal menghapus data.")
            }
        }
    }

    fun updateLog(id: Long, log: BusLog, userEmail: String) {
        viewModelScope.launch {
            _logState.value = ApiState.Loading
            try {
                ApiConfig.getApiService().updateBusLog("eq.$id", log)
                getLogs(userEmail)
            } catch (e: Exception) {
                _logState.value = ApiState.Error("Gagal mengubah data.")
            }
        }
    }
}