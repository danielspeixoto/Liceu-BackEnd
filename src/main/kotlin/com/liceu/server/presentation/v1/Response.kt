package com.liceu.server.presentation.v1

data class Response<T>(
        val data: T,
        val status: String="ok"
)