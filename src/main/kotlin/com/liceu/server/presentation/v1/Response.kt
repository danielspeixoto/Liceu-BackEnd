package com.liceu.server.presentation.v1

data class Response<T>(
        val data: T?=null,
        val status: String= STATUS_OK,
        val errorCode: Int=UNKNOWN_ERROR
)

val STATUS_OK = "ok"
val STATUS_ERROR = "error"

val UNKNOWN_ERROR = 0
val VALIDATION_ERROR = 1
val SERVER_ERROR = 2
val DB_ERROR = 3