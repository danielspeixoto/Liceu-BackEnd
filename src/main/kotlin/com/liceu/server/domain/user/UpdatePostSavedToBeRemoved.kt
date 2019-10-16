package com.liceu.server.domain.user

import com.liceu.server.domain.global.*
import com.liceu.server.domain.post.PostBoundary
import com.liceu.server.util.Logging

class UpdatePostSavedToBeRemoved(
        private val userRepository: UserBoundary.IRepository
): UserBoundary.IUpdateSavedPostToBeRemoved {

    companion object {
        const val EVENT_NAME = "update_user_saved_posts"
        val TAGS = listOf(UPDATE, USER , DELETE ,SAVED, POST)
    }

    override fun run(userId: String, postId: String) {
        try {
            if(postId.isBlank()){
                throw OverflowSizeException ("postId can't be null")
            }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to userId,
                    "postId" to postId
            ))
            userRepository.updateRemovePostSaved(userId,postId)
        }catch (e:Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}