package com.liceu.server.domain.user

import com.liceu.server.domain.aggregates.Picture
import org.springframework.data.mongodb.core.geo.GeoJsonPoint

data class UserForm(
        val name: String,
        val email: String,
        val picture: Picture,
        val socialId: String,
        val location: GeoJsonPoint?
)