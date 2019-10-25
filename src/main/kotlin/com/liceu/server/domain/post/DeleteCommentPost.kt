package com.liceu.server.domain.post

import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class DeleteCommentPost(
        private val postRepository: PostBoundary.IRepository
): PostBoundary.IDeleteCommentPost {
    companion object {
        const val EVENT_NAME = "delete_comment_post"
        val TAGS = listOf(DELETE, COMMENT,POST)
    }
    override fun run(postId: String, commentId: String, userId: String) {
        try {
            if(commentId.isBlank()){
                throw UnderflowSizeException("commentId can't be null")
            }
            if(userId.isBlank()){
                throw UnderflowSizeException("userId can't be null")
            }
            if(!postRepository.postExists(postId)){
                throw ItemNotFoundException ("Post requested don't exist")
            }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "postId" to postId,
                    "commentId" to commentId,
                    "userId" to userId
            ))
            postRepository.deleteCommentInPost(postId,commentId,userId)
        }catch (e: Exception){
            Logging.error(EVENT_NAME,TAGS,e)
            throw e
        }
    }
}