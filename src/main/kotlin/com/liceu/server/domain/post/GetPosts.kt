package com.liceu.server.domain.post

import com.liceu.server.data.MongoPostRepository
import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.util.Logging
import java.util.*

class GetPosts(
        private val postRepository: PostBoundary.IRepository,
        private val userRepository: UserBoundary.IRepository,
        private val maxResults: Int
): PostBoundary.IGetPosts {

    companion object{
        const val EVENT_NAME = "get_posts"
        val TAGS = listOf(RETRIEVAL,POST)
    }
    override fun run(userId: String, date: Date, amount: Int): List<Post>? {
        if(amount == 0) {
            Logging.warn(UNCOMMON_PARAMS, TAGS, hashMapOf(
                    "action" to EVENT_NAME,
                    "value" to amount
            ))
        }
        var finalAmount = amount
        if(amount > maxResults) {
            finalAmount = maxResults
            Logging.warn(
                    MAX_RESULTS_OVERFLOW,
                    TAGS + listOf(OVERFLOW),
                    hashMapOf(
                            "requested" to amount,
                            "max_allowed" to maxResults
                    )
            )
        }
        try{
            val user = userRepository.getUserById(userId)
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to userId,
                    "dateRequired" to date,
                    "amount" to amount
            ))
            return postRepository.getPostsForFeed(user,date,finalAmount)
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}