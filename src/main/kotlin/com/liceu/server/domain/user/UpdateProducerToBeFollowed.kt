package com.liceu.server.domain.user

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class UpdateProducerToBeFollowed(
        private val userRepo: MongoUserRepository
): UserBoundary.IUpdateProducerToBeFollowed {
    companion object{
        const val EVENT_NAME = "put_producer_followed_by_user"
        val TAGS = listOf(UPDATE, USER , PRODUCER, FOLLOWED)
    }

    override fun run(userId: String, producerId: String) {
        try {
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userFollowing" to userId,
                    "producerFollowed" to producerId
            ))
            userRepo.updateAddProducerToFollowingList(userId,producerId)
            userRepo.updateProducerToBeFollowed(producerId)
        } catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}