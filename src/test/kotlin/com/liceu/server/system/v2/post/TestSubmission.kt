package com.liceu.server.system.v2.post

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
import java.util.HashMap

class TestSubmission: TestSystem("/v2/post") {

    @Autowired
    lateinit var postRepo: PostRepository

    @Test
    fun submitTextPost_Valid_Success() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "text",
                "description" to "esse e um teste aha"
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!
        val id = body["id"] as String
        val insertedPost = postRepo.findById(id).get()

        Truth.assertThat(insertedPost).isNotNull()
        Truth.assertThat(insertedPost.userId.toHexString()).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(insertedPost.type).isEqualTo("text")
        Truth.assertThat(insertedPost.description).isEqualTo("esse e um teste aha")
    }

    @Test
    fun submitVideoPost_Valid_Success() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to "esse e um teste aha",
                "videoUrl" to "www.youtube.com/lalal",
                "high" to "highQuality",
                "default" to "defaulQuality",
                "medium" to "mediumQuality"
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!
        val id = body["id"] as String
        val insertedPost = postRepo.findById(id).get()

        Truth.assertThat(insertedPost).isNotNull()
        Truth.assertThat(insertedPost.userId.toHexString()).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(insertedPost.type).isEqualTo("video")
        Truth.assertThat(insertedPost.video?.videoUrl).isEqualTo("www.youtube.com/lalal")
        Truth.assertThat(insertedPost.video?.thumbnails?.high).isEqualTo("highQuality")
    }
    @Test
    fun submitVideoPost_descriptionNull_throwError() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to null,
                "videoUrl" to "www.youtube.com/lalal",
                "high" to "highQuality",
                "default" to "defaulQuality",
                "medium" to "mediumQuality"
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
    @Test
    fun submitVideoPost_descriptionEmptyString_throwError() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to "",
                "videoUrl" to "www.youtube.com/lalal",
                "high" to "highQuality",
                "default" to "defaulQuality",
                "medium" to "mediumQuality"
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
    @Test
    fun submitVideoPost_videoUrlEmpty_throwError() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to "teste teste",
                "videoUrl" to "",
                "high" to "highQuality",
                "default" to "defaulQuality",
                "medium" to "mediumQuality"
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitVideoPost_defaultThumbnailEmpty_throwError() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to "teste teste",
                "videoUrl" to "www.youtube.com/lalal",
                "high" to "highQuality",
                "default" to "",
                "medium" to "mediumQuality"
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
}