package com.liceu.server.domain.user

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.domain.report.SubmitReport
import com.liceu.server.util.Logging
import java.time.LocalDate
import java.time.Period


class UpdateAge(
    private var userRepo: UserBoundary.IRepository
): UserBoundary.IUpdateAge {

    companion object {
        const val EVENT_NAME = "update_age_user"
        val TAGS = listOf(RETRIEVAL, USER , UPDATE, AGE)
    }

    override fun run(userId: String, day: Int,month: Int, year: Int) {
        try {
            if(day <= 0 || month <= 0 || year <= 0){
                throw TypeMismatchException("Number must be greater than 0")
            }
            val today = LocalDate.now()
            val birthday = LocalDate.of(year, month, day)
            val correctAge = Period.between(birthday, today).years
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to userId,
                    "userAge" to correctAge
            ))
            userRepo.updateAgeFromUser(userId,correctAge)
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }

    }
}