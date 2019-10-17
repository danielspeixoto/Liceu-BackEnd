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

class TestDeletePost: TestSystem ("/v2/post") {

    @Autowired
    lateinit var postRepo: PostRepository

    @Autowired
    lateinit var data: MongoPostRepository

    @Test
    fun deletePost_postExist_verifyPosts(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<HashMap<String, Any>>>("$baseUrl/${testSetup.POST_ID_1}", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val postsAfter = data.getPostFromUser(testSetup.USER_ID_1,10,0)
        Truth.assertThat(postsAfter.size).isEqualTo(0)
    }

    @Test
    fun deletePost_UserPassedIsNotTheOwner_postNotDeleted(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<HashMap<String, Any>>>("$baseUrl/${testSetup.POST_ID_2}", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val postsAfter = data.getPostFromUser(testSetup.USER_ID_1,10,0)
        Truth.assertThat(postsAfter.size).isEqualTo(1)
    }

}