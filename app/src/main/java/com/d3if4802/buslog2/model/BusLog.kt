package com.d3if4802.buslog2.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BusLog(
    val id: Long? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "plat_nomor") val platNomor: String,
    @Json(name = "catatan") val catatan: String,
    @Json(name = "image_url") val imageUrl: String?,
    @Json(name = "user_email") val userEmail: String
)

@JsonClass(generateAdapter = true)
data class BusLogRequest(
    @Json(name = "plat_nomor") val platNomor: String,
    @Json(name = "catatan") val catatan: String,
    @Json(name = "image_url") val imageUrl: String?,
    @Json(name = "user_email") val userEmail: String
)