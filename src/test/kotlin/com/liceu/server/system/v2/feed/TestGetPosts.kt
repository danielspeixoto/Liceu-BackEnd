package com.liceu.server.system.v2.feed

import com.google.common.truth.Truth
import com.liceu.server.data.PostRepository
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.Instant
import java.util.*

class TestGetPosts: TestSystem("/v2/feed")  {
    @Autowired
    lateinit var postRepo: PostRepository

    @Test
    fun getPostsForFeed_PostsAreFollowedFromUser_returnPosts(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<HashMap<String,Any>>>("$baseUrl?before=2019-08-27T12:40:20.00Z&amount=10", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body.size).isEqualTo(2)
        Truth.assertThat(body[0]["type"]).isEqualTo("video")
        Truth.assertThat(body[1]["type"]).isEqualTo("text")
    }

    @Test
    fun getPostsForFeed_wrongDateFormat_throwError(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<HashMap<String,Any>>>("$baseUrl?before=08-27T12:40:20.00Z&amount=10", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun getPostsForFeed_amountToOne_throwError(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<HashMap<String,Any>>>("$baseUrl?before=2019-08-27T12:40:20.00Z&amount=1", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body.size).isEqualTo(1)
        Truth.assertThat(body[0]["type"]).isEqualTo("video")
    }
}