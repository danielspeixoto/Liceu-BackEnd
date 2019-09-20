package com.liceu.server.presentation.util

import com.liceu.server.domain.global.AuthenticationException
import com.liceu.server.domain.global.ItemNotFoundException
import com.liceu.server.util.Logging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.lang.ClassCastException
import javax.validation.ValidationException

fun <T>handleException(e: Exception,
                    eventName: String,
                    eventTags: List<String>,
                    data: Map<String, *>
): ResponseEntity<T> {
    return when (e) {
        is AuthenticationException -> {
            Logging.error(
                    eventName,
                    eventTags,
                    e, data = data
            )
            ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        is ValidationException, is ClassCastException -> {
            Logging.error(
                    eventName,
                    eventTags,
                    e, data = data
            )
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        is ItemNotFoundException -> {
            Logging.error(
                    eventName,
                    eventTags,
                    e, data = data
            )
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
        else -> {
            Logging.error(
                    eventName,
                    eventTags,
                    e, data = data
            )
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}