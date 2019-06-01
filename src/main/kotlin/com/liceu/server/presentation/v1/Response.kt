package com.liceu.server.presentation.v1

data class Response<T>(
        val data: T? = null,
        val status: String = STATUS_OK,
        val errorCode: Int? = null
) {
    companion object {
        const val STATUS_OK = "ok"
        const val STATUS_ERROR = "error"

        const val UNKNOWN_ERROR_CODE = 0
        const val VALIDATION_ERROR_CODE = 1
        const val SERVER_ERROR = 2
        const val DB_ERROR = 3
        const val ALREADY_EXISTS_ERROR_CODE = 4
        const val NOT_FOUND_ERROR_CODE = 5
    }
}



