package com.liceu.server.domain.post

import com.liceu.server.domain.global.DELETE
import com.liceu.server.domain.global.OverflowSizeException
import com.liceu.server.domain.global.POST
import com.liceu.server.util.Logging

class DeletePosts(
        private val postRepository: PostBoundary.IRepository
): PostBoundary.IDeletePost {

    companion object {
        const val EVENT_NAME = "delete_post"
        val TAGS = listOf(DELETE, POST)
    }

    override fun run(postId: String, userId: String) {
        if(userId.isBlank()){
            throw OverflowSizeException ("userId can't be an empty string")
        }
        try {
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "postId" to postId,
                    "userId" to userId
            ))
            postRepository.deletePost(postId,userId)
        }catch(e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}