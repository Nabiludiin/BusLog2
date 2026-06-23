package com.d3if4802.buslog2.network

import com.d3if4802.buslog2.model.BusLog
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET("rest/v1/bus_logs")
    suspend fun getBusLogs(
        @Query("userEmail") userEmail: String,
        @Query("select") select: String = "*"
    ): List<BusLog>

    @POST("rest/v1/bus_logs")
    suspend fun addBusLog(
        @Body busLog: BusLog
    )

    @PATCH("rest/v1/bus_logs")
    suspend fun updateBusLog(
        @Query("id") id: String,
        @Body busLog: BusLog
    )

    @DELETE("rest/v1/bus_logs")
    suspend fun deleteBusLog(
        @Query("id") id: String
    )

    @POST
    suspend fun uploadImage(
        @Url url: String,
        @Body image: RequestBody
    )
}