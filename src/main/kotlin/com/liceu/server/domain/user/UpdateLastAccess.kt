package com.liceu.server.domain.user

import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.global.*
import com.liceu.server.domain.util.TimeStamp.retrieveActualTimeStamp
import com.liceu.server.domain.util.activitiesInsertion.activityInsertion
import com.liceu.server.util.Logging
import java.lang.Exception
import java.util.*

class UpdateLastAccess(
        private val userRepository: UserBoundary.IRepository,
        private val activityRepository: ActivityBoundary.IRepository
): UserBoundary.IUpdateLastAccess {
    companion object {
        const val EVENT_NAME = "update_user_last_access"
        val TAGS = listOf(UPDATE, USER , LAST, ACCESS)
    }

    override fun run(userId: String) {
        try {
            val actualTime = retrieveActualTimeStamp()
            Logging.info(EVENT_NAME,TAGS, hashMapOf(
                    "userId" to userId,
                    "lastAccess" to actualTime
            ))
            activityInsertion(activityRepository,userId,"lastAccessRegister", hashMapOf(
                    "userdId" to userId,
                    "lastAccess" to actualTime
            ))
            userRepository.updateLastAccess(userId,actualTime)
        }catch (e: Exception){
            Logging.error(EVENT_NAME,TAGS,e)
            throw e
        }
    }
}