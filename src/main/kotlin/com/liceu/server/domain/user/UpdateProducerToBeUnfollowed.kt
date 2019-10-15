package com.liceu.server.domain.user

import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class UpdateProducerToBeUnfollowed(
        private val userRepo: UserBoundary.IRepository
): UserBoundary.IUpdateProducerToBeUnfollowed{

    companion object{
        const val EVENT_NAME = "put_producer_unfollowed_by_user"
        val TAGS = listOf(UPDATE, USER , PRODUCER, UNFOLLOWED)
    }
    override fun run(userId: String, producerId: String) {
        try {
            if(userId.isBlank()){
                throw OverflowSizeException ("userId can't be null")
            }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userUnfollowing" to userId,
                    "producerUnfollowed" to producerId
            ))
            userRepo.updateRemoveProducerToFollowingList(userId,producerId)
            userRepo.updateRemoveUserToProducerFollowerList(userId,producerId)
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}