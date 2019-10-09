package com.liceu.server.domain.post

import com.liceu.server.domain.global.ID
import com.liceu.server.domain.global.POST
import com.liceu.server.domain.global.RETRIEVAL
import com.liceu.server.domain.global.UnderflowSizeException
import com.liceu.server.util.Logging

class GetPostById(
        private val postRepository: PostBoundary.IRepository
): PostBoundary.IGetPostById {
    companion object {
        const val EVENT_NAME = "get_post_by_id"
        val TAGS = listOf(RETRIEVAL, POST, ID)
    }

    override fun run(postId: String): Post {
        try {
            if(postId.isBlank()){
                throw UnderflowSizeException("postId can't be empty")
            }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "postId" to postId
            ))
            val post = postRepository.getPostById(postId)
            return post
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}
