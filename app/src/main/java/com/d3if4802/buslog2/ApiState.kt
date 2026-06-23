package com.d3if4802.buslog2

sealed class ApiState {
    object Idle : ApiState()
    object Loading : ApiState()
    data class Success(val data: List<BusLog>) : ApiState()
    data class Error(val message: String) : ApiState()
}