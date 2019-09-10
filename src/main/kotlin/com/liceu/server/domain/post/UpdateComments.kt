package com.liceu.server.domain.post


import com.liceu.server.domain.global.COMMENT
import com.liceu.server.domain.global.OverflowSizeException
import com.liceu.server.domain.global.POST
import com.liceu.server.domain.global.UPDATE
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.util.Logging

class UpdateComments(
        private val postRepository: PostBoundary.IRepository,
        private val userRepository: UserBoundary.IRepository

): PostBoundary.IUpdateListOfComments {
    companion object {
        const val EVENT_NAME = "post_comment_update"
        val TAGS = listOf(UPDATE,POST,COMMENT)
    }

    override fun run(postId: String, userId: String, comment: String) {
        try {
            if(comment.isBlank()){
                throw OverflowSizeException("Comment can't be null")
            }
            if(comment.length > 300){
                throw OverflowSizeException("Comment is too long")
            }
            if(userId.isBlank()){
                throw OverflowSizeException("userId can't be null")
            }
            val authorComment = userRepository.getUserById(userId).name
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "postId" to postId,
                    "author" to authorComment,
                    "comment" to comment
            ))
            postRepository.updateListOfComments(postId,userId,authorComment,comment)
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}