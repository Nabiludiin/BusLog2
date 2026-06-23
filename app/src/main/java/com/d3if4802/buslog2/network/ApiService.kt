package com.d3if4802.buslog2.network

import com.d3if4802.buslog2.model.BusLog
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("bus_logs")
    suspend fun getBusLogs(
        @Query("select") select: String = "*",
        @Query("user_email") emailFilter: String
    ): List<BusLog>

    @POST("bus_logs")
    suspend fun addBusLog(@Body log: BusLog)

    @DELETE("bus_logs")
    suspend fun deleteBusLog(@Query("id") idFilter: String)

    @PATCH("bus_logs")
    suspend fun updateBusLog(
        @Query("id") idFilter: String,
        @Body log: BusLog
    )
}