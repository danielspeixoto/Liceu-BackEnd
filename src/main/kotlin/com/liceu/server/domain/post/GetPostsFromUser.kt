package com.liceu.server.domain.post

import com.liceu.server.data.MongoPostRepository
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class GetPostsFromUser(
        private val postRepository: PostBoundary.IRepository
): PostBoundary.IGetPostsFromUser {
    companion object{
        const val EVENT_NAME = "get_posts_from_user"
        val TAGS = listOf(RETRIEVAL,POST, USER)
    }
    override fun run(userId: String,authenticatedUserId: String, amount: Int, start: Int): List<Post> {
        try {
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to userId,
                    "authenticatedUserId" to authenticatedUserId,
                    "amount" to amount,
                    "start" to start
            ))
            if(userId == authenticatedUserId){
              return postRepository.getPostsFromOwner(userId,amount,start)
            }
            return postRepository.getPostFromUser(userId,amount,start)
        }catch (e: Exception){
            Logging.error(EVENT_NAME,TAGS,e)
            throw e
        }
    }
}