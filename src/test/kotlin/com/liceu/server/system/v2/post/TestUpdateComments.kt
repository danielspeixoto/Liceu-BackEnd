package com.liceu.server.system.v2.post

import com.google.common.truth.Truth
import com.liceu.server.data.MongoPostRepository
import com.liceu.server.data.PostRepository
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.HashMap

class TestUpdateComments: TestSystem("/v2/post") {
    @Autowired
    lateinit var postRepo: PostRepository

    @Autowired
    lateinit var data: MongoPostRepository

    @Test
    fun updateListOfComments_validComment_verifyPost(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "userId" to testSetup.USER_ID_1,
                "comment" to "comentando no post"
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.POST_ID_1}/comment", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val postChanged = data.getPostById("09c54d325b75357a581d4ca2")
        Truth.assertThat(postChanged.comments?.size).isEqualTo(1)
        Truth.assertThat(postChanged.comments?.get(0)?.comment).isEqualTo("comentando no post")
    }

    @Test
    fun updateListOfComments_nullComment_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "userId" to testSetup.USER_ID_1,
                "comment" to null
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.POST_ID_1}/comment", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateListOfComments_emptyComment_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "userId" to testSetup.USER_ID_1,
                "comment" to ""
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.POST_ID_1}/comment", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
}