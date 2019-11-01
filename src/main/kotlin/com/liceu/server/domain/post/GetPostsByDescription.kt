package com.liceu.server.domain.post

import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class GetPostsByDescription(
        private val postRepository: PostBoundary.IRepository,
        private val maxResults: Int
): PostBoundary.IGetPostsByDescription {

    companion object {
        const val EVENT_NAME = "get_posts_description"
        val TAGS = listOf(RETRIEVAL, POST, DESCRIPTION)
    }

    override fun run(descriptionSearched: String, amount: Int): List<Post> {
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
        try {
            if(descriptionSearched.isBlank()){
                throw UnderflowSizeException("Description desired can't be null")
            }
            Logging.info(GetPosts.EVENT_NAME, TAGS, hashMapOf(
                    "description" to descriptionSearched,
                    "amount" to amount
            ))
            return postRepository.getPostsByDescription(descriptionSearched,finalAmount)
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}