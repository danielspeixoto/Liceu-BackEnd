package com.liceu.server.domain.user

import com.liceu.server.domain.global.*
import com.liceu.server.domain.post.PostBoundary
import com.liceu.server.util.Logging

class UpdatePostToBeSaved(
        private val userRepository: UserBoundary.IRepository,
        private val postRepository: PostBoundary.IRepository
): UserBoundary.IUpdatePostToBeSaved {

    companion object {
        const val EVENT_NAME = "update_user_saved_posts"
        val TAGS = listOf(UPDATE ,USER ,SAVED, POST)
    }

    override fun run(userId: String, postId: String) {
        try {
            if(postId.isBlank()){
                throw OverflowSizeException ("postId can't be null")
            }
            if(!postRepository.postExists(postId)) {
                throw ItemNotFoundException("Post does not exists")
            }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to userId,
                    "postId" to postId
            ))
            userRepository.updateAddPostToBeSaved(userId,postId)
        }catch (e:Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}