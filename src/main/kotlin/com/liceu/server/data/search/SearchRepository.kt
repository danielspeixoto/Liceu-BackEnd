package com.liceu.server.data.search

import com.liceu.server.domain.global.FALLBACK
import com.liceu.server.domain.global.FINDER
import com.liceu.server.domain.global.POST
import com.liceu.server.domain.post.Post
import com.liceu.server.domain.post.PostBoundary
import com.liceu.server.util.Logging
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClientBuilder
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SearchRepository(
): PostBoundary.ISearch {

    @Autowired
    lateinit var postRepository: PostBoundary.IRepository
    @Autowired
    lateinit var restHighLevelClient: RestHighLevelClient

    @HystrixCommand(fallbackMethod = "mongoDbSearchFinder")
    override fun run(descriptionSearched: String, amount: Int): List<Post> {
                val sourceBuilder = SearchSourceBuilder()
                sourceBuilder.query(QueryBuilders.multiMatchQuery(descriptionSearched, "description", "visionText⁶"))
                sourceBuilder.fetchSource("_id",null)
                sourceBuilder.size(amount)

                val searchRequest = SearchRequest()
                searchRequest.indices("posts")
                searchRequest.source(sourceBuilder)
                val search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT)

                val obj = JSONObject(search)
                var idsFromSearch : MutableList<String> = arrayListOf()
                val returnLength = obj.getJSONObject("hits").getJSONArray("hits").length()
                for (i in 0 until returnLength){
                    idsFromSearch.add(obj.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getString("id"))
                }
                return postRepository.getMultiplePostsFromIds(idsFromSearch,amount)
    }

    fun mongoDbSearchFinder (descriptionSearched: String,amount: Int): List<Post> {
        Logging.warn("post_search_fallback", listOf(FALLBACK,POST,FINDER), hashMapOf(
                        "description" to descriptionSearched,
                        "amount" to amount
        ))
        return postRepository.getPostsByDescription(descriptionSearched,amount)
    }

}