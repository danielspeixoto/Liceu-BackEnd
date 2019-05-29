package com.liceu.server.domain.tag

data class Tag(
        val id: String,
        val name: String,
        val amount: Int
)

data class TagCreation(
        val name: String
)