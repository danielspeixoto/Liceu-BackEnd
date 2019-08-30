package com.liceu.server.domain.activities

import com.liceu.server.data.MongoActivityRepository
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class GetActivitiesFromUser(
        private val activityRepository: MongoActivityRepository,
        private val maxResults: Int
): ActivityBoundary.IGetActivitiesFromUser {

    companion object {
        const val EVENT_NAME = "get_activities_from_user"
        val TAGS = listOf(RETRIEVAL, ACTIVITY, USER)
    }

    override fun run(userId: String, amount: Int): List<Activity> {
        if(amount == 0) {
            Logging.warn(UNCOMMON_PARAMS, TAGS, hashMapOf(
                    "action" to EVENT_NAME,
                    "value" to amount
            ))
        }
        var finalAmount = amount
        if(amount > maxResults) {
            finalAmount = maxResults
            Logging.warn(
                    MAX_RESULTS_OVERFLOW,
                    TAGS + listOf(OVERFLOW),
                    hashMapOf(
                            "requested" to amount,
                            "max_allowed" to maxResults
                    )
            )
        }
        try {
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to userId
            ))
            return activityRepository.getActivitiesFromUser(userId,finalAmount)
        }catch (e: Exception){
            Logging.error(EVENT_NAME,TAGS,e)
            throw e
        }
    }
}