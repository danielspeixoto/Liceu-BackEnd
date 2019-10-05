package com.liceu.server.domain.util.dateFunctions

import java.time.Instant
import java.time.ZoneOffset
import java.util.*

object DateFunctions {

    fun lastTwoWeeks(): Date {
        val noOfDays = 14
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -noOfDays)
        return cal.time
    }

    fun retrieveActualTimeStamp(): Date{
        return Date.from(Instant.now().atOffset(ZoneOffset.ofHours(-3)).toInstant())
    }
}