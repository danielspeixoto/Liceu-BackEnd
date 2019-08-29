package com.liceu.server.domain.post

import com.liceu.server.data.MongoPostRepository
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class GetRandomPosts(
        private val postRepository: MongoPostRepository,
        private val maxResults: Int
): PostBoundary.IGetRandomPosts {

    companion object{
        const val EVENT_NAME = "get_random_posts"
        val TAGS = listOf(RETRIEVAL,POST, RANDOM)
    }

    override fun run(amount: Int): List<Post> {
        if(amount == 0) {
            Logging.warn(UNCOMMON_PARAMS,TAGS, hashMapOf(
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
        try {
        val retrievedPosts = postRepository.getRandomPosts(finalAmount)
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "amount" to amount,
                    "returnedSize" to retrievedPosts.size
            ))
        return retrievedPosts
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}