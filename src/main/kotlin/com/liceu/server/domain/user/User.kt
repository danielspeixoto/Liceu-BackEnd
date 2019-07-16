package com.liceu.server.domain.user

import com.liceu.server.domain.aggregates.Picture

data class User(
        val id: String,
        val name: String,
        val email: String,
        val picture: Picture
)