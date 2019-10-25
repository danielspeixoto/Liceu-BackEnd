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

    @Test
    fun deleteCommentPost_UserExist_returnVoid() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "commentId" to testSetup.POST_COMMENT_ID_1
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.POST_ID_8}/comments", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val postAfter = data.getPostById(testSetup.POST_ID_8)
        Truth.assertThat(postAfter.comments?.size).isEqualTo(1)
    }

    @Test
    fun deleteCommentPost_UserDontHaveComment_returnVoid() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_3_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "commentId" to testSetup.INVALID_ID
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.POST_ID_8}/comments", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val postAfter = data.getPostById(testSetup.POST_ID_8)
        Truth.assertThat(postAfter.comments?.size).isEqualTo(2)
    }

    @Test
    fun deleteCommentPost_wrongPostId_returnVoid() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "commentId" to testSetup.POST_COMMENT_ID_1
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.INVALID_ID}/comments", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun deleteCommentPost_emptyCommentId_returnVoid() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "commentId" to ""
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.POST_ID_8}/comments", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun deleteCommentPost_commentIdToNull_returnVoid() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "commentId" to null
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.POST_ID_8}/comments", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }




}