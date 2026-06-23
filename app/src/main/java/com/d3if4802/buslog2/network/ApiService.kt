package com.d3if4802.buslog2.network

import com.d3if4802.buslog2.model.BusLog
import com.d3if4802.buslog2.model.BusLogRequest
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("rest/v1/bus_logs")
    suspend fun getBusLogs(
        @Query("user_email") userEmail: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): List<BusLog>

    @Headers("Prefer: return=minimal")
    @POST("rest/v1/bus_logs")
    suspend fun addBusLog(
        @Body busLogRequest: BusLogRequest
    )

    @Headers("Prefer: return=minimal")
    @PATCH("rest/v1/bus_logs")
    suspend fun updateBusLog(
        @Query("id") id: String,
        @Body busLogRequest: BusLogRequest
    )

    @DELETE("rest/v1/bus_logs")
    suspend fun deleteBusLog(
        @Query("id") id: String
    ): Response<Unit>

    @POST
    suspend fun uploadImage(
        @Url url: String,
        @Body image: RequestBody
    )
}