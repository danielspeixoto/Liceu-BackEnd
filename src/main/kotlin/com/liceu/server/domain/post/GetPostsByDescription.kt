package com.liceu.server.domain.post

import com.liceu.server.data.elasticsearch.ElasticSearchFinder
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging
import org.springframework.beans.factory.annotation.Autowired

class GetPostsByDescription(
        private val postRepository: PostBoundary.IRepository,
        private val maxResults: Int,
        @Autowired val elasticSearchFinder: PostBoundary.IElasticSearchFinder
): PostBoundary.IGetPostsByDescription {

    companion object {
        const val EVENT_NAME = "get_posts_description"
        val TAGS = listOf(RETRIEVAL, POST, DESCRIPTION)
    }

    override fun run(descriptionSearched: String, searchMethod: String, amount: Int): List<Post> {
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
                    "method" to searchMethod,
                    "amount" to finalAmount
            ))
            return if(searchMethod=="elasticSearch"){
                val idsFromPosts = elasticSearchFinder.run(descriptionSearched, amount)
                val postsRetrieved : MutableList<Post> = arrayListOf()
                idsFromPosts.forEach {
                    postsRetrieved.add(postRepository.getPostById(it))
                }
                postsRetrieved.toList()
            }else{
                postRepository.getPostsByDescription(descriptionSearched,finalAmount)
            }
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}