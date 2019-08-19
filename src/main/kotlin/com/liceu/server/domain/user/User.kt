package com.liceu.server.domain.user

import com.liceu.server.domain.aggregates.Picture
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

data class User(
        val id: String,
        val name: String,
        val email: String,
        val picture: Picture,
        val location: GeoJsonPoint?,
        val school: String?,
        val age: Int?,
        val youtubeChannel: String?,
        val instagramProfile: String?,
        val description: String?,
        val website: String?
)