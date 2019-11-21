package com.liceu.server.system.v2.user

import com.google.common.truth.Truth
import com.liceu.server.data.MongoUserRepository
import com.liceu.server.data.PostRepository
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class TestSavePosts: TestSystem("/v2/user") {
    @Autowired
    lateinit var postRepo: PostRepository

    @Autowired
    lateinit var userRepo: MongoUserRepository

    @Test
    fun updateSavedPosts_postsIdExists_returnVoid() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "postId" to testSetup.POST_ID_3
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_1}/savePost", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val userChanged = userRepo.getUserById(testSetup.USER_ID_1)
        Truth.assertThat(userChanged.savedPosts?.size).isEqualTo(1)
        Truth.assertThat(userChanged.savedPosts).contains(testSetup.POST_ID_3)
    }

    @Test
    fun deleteSavedPosts_postsIdExists_returnVoid() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/${testSetup.POST_ID_2}", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val userChanged = userRepo.getUserById(testSetup.USER_ID_2)
        Truth.assertThat(userChanged.savedPosts?.size).isEqualTo(1)
    }

    @Test
    fun getSavedPosts_userExists_returnListOfSavedPosts() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<String>>("$baseUrl/${testSetup.USER_ID_2}/savedPosts", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body.size).isEqualTo(2)
        Truth.assertThat(body[0]).isEqualTo(testSetup.POST_ID_2)
        Truth.assertThat(body[1]).isEqualTo(testSetup.POST_ID_1)
    }

    @Test
    fun getSavedPosts_userExists_returnSinglePost() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<String>>("$baseUrl/${testSetup.USER_ID_2}/savedPosts?amount=1&start=1", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body.size).isEqualTo(1)
        Truth.assertThat(body[0]).isEqualTo(testSetup.POST_ID_1)
    }

    @Test
    fun getSavedPosts_amountToZero_returnNull() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<String>>("$baseUrl/${testSetup.USER_ID_2}/savedPosts?amount=0", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun getSavedPosts_userDontExists_returnListOfSavedPosts() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<String>>("$baseUrl/${testSetup.INVALID_ID}/savedPosts", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }


    @Test
    fun updateSavedPosts_postsIdDoesNotExists_returnVoid() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "postId" to testSetup.INVALID_ID
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_1}/savePost", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }


    @Test
    fun updateSavedPosts_postIdToNull_returnVoid() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "postId" to null
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_1}/savePost", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateSavedPosts_emptyPostId_returnVoid() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "postId" to ""
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_1}/savePost", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun deleteSavedPosts_postIdToNull_returnVoid() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/${null}", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun deleteSavedPosts_emptyPostId_returnVoid() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_2_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.USER_ID_2}/ ", HttpMethod.DELETE, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }


}