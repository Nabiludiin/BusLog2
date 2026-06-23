package com.d3if4802.buslog2.model

import com.squareup.moshi.Json

data class BusLog(
    val id: Long = 0,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "plat_nomor") val platNomor: String,
    val catatan: String,
    @Json(name = "image_url") val imageUrl: String?,
    @Json(name = "user_email") val userEmail: String
)