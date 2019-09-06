package com.liceu.server.domain.user

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.activities.ActivityToInsert
import com.liceu.server.domain.global.*
import com.liceu.server.domain.util.TimeStamp
import com.liceu.server.util.Logging

class UpdateProducerToBeFollowed(
        private val userRepo: UserBoundary.IRepository,
        private val activityRepository: ActivityBoundary.IRepository
): UserBoundary.IUpdateProducerToBeFollowed {
    companion object{
        const val EVENT_NAME = "put_producer_followed_by_user"
        val TAGS = listOf(UPDATE, USER , PRODUCER, FOLLOWED)
    }

    override fun run(userId: String, producerId: String) {
        try {
            if(userId.isBlank()){
                throw OverflowSizeException ("userId can't be null")
            }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userFollowing" to userId,
                    "producerFollowed" to producerId
            ))
            userRepo.updateAddProducerToFollowingList(userId,producerId)
            userRepo.updateAddUserToProducerFollowerList(userId,producerId)
            activityRepository.insertActivity(ActivityToInsert(
                    producerId,
                    "followedUser",
                    hashMapOf(
                            "userId" to userId
                    ),
                    TimeStamp.retrieveActualTimeStamp()
            ))
        } catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}