package com.liceu.server.domain.user

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class UpdateProducerToBeUnfollowed(
        private val userRepo: MongoUserRepository
): UserBoundary.IupdateProducerToBeUnfollowed{

    companion object{
        const val EVENT_NAME = "put_producer_unfollowed_by_user"
        val TAGS = listOf(UPDATE, USER , PRODUCER, UNFOLLOWED)
    }
    override fun run(userId: String, producerId: String) {
        try {
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userUnfollowing" to userId,
                    "producerUnfollowed" to producerId
            ))
            userRepo.updateRemoveProducerToFollowingList(userId,producerId)
            userRepo.updateProducerToBeUnfollowed(producerId)
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}