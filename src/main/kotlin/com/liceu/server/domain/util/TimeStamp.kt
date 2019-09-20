package com.liceu.server.domain.util

import java.time.Instant
import java.time.ZoneOffset
import java.util.*

object TimeStamp {
    fun retrieveActualTimeStamp(): Date{
        return Date.from(Instant.now().atOffset(ZoneOffset.ofHours(-3)).toInstant())
    }
}