package com.liceu.server.data.search

import com.liceu.server.domain.post.Post
import com.liceu.server.domain.post.PostBoundary
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClientBuilder
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.json.JSONObject
import org.springframework.stereotype.Service

@Service
class SearchRepository(
        private val postRepository: PostBoundary.IRepository,
        private val restClientBuilder: RestClientBuilder
): PostBoundary.ISearch {

    val client = RestHighLevelClient(restClientBuilder)

    //@HystrixCommand(fallbackMethod = "mongoDbSearchFinder")
    override fun run(descriptionSearched: String, amount: Int): List<Post> {
                val sourceBuilder = SearchSourceBuilder()
                sourceBuilder.query(QueryBuilders.multiMatchQuery(descriptionSearched, "description", "visionText⁶"))
                sourceBuilder.fetchSource("_id",null)
                sourceBuilder.size(amount)

                val searchRequest = SearchRequest()
                searchRequest.indices("posts")
                searchRequest.source(sourceBuilder)
                val search = client.search(searchRequest, RequestOptions.DEFAULT)

                val obj = JSONObject(search)
                var idsFromSearch : MutableList<String> = arrayListOf()
                val returnLength = obj.getJSONObject("hits").getJSONArray("hits").length()
                for (i in 0 until returnLength){
                    idsFromSearch.add(obj.getJSONObject("hits").getJSONArray("hits").getJSONObject(i).getString("id"))
                }
                //idsFromSearch.forEach { println(it) }
                //return idsFromSearch
                val postsRetrieved : MutableList<Post> = arrayListOf()
                idsFromSearch.forEach {
                    postsRetrieved.add(postRepository.getPostById(it))
                }
                return postsRetrieved.toList()
    }

    fun mongoDbSearchFinder (descriptionSearched: String,amount: Int): List<Post> {
        return postRepository.getPostsByDescription(descriptionSearched,amount)
    }
}