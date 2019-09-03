package com.liceu.server.domain.activities

import com.liceu.server.data.MongoActivityRepository
import com.liceu.server.domain.global.ACTIVITY
import com.liceu.server.domain.global.INSERTION
import com.liceu.server.domain.global.OverflowSizeException
import com.liceu.server.util.Logging
import java.time.Instant
import java.time.ZoneOffset
import java.util.*

class InsertActivity(
        private val activityRepository: ActivityBoundary.IRepository
): ActivityBoundary.IInsertActivity{
    companion object {
        const val EVENT_NAME = "activity_submission"
        val TAGS = listOf(INSERTION, ACTIVITY)
    }
    override fun run(activitySubmission: ActivitySubmission): String {
        try {
            if(activitySubmission.type.isEmpty()){
                throw OverflowSizeException ("Type can't be null in activity")
            }
            if(activitySubmission.params.isEmpty()){
                throw OverflowSizeException ("Params can't be null in activity")
            }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to activitySubmission.userId,
                    "type" to activitySubmission.type
            ))
            return activityRepository.insertActivity(ActivityToInsert(
                    activitySubmission.userId,
                    activitySubmission.type,
                    activitySubmission.params,
                    Date.from(Instant.now().atOffset(ZoneOffset.ofHours(-3)).toInstant())
            ))
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}