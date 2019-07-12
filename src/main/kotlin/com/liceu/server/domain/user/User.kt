package com.liceu.server.domain.user

import com.liceu.server.domain.aggregates.Picture

data class UserForm(
        val name: String,
        val email: String,
        val picture: Picture,
        val facebookId: String
)