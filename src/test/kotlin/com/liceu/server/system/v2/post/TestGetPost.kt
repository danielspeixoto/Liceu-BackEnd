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

class TestGetPost: TestSystem("/v2/post") {

    @Autowired
    lateinit var data: MongoPostRepository

    @Test
    fun getPost_postExist_returnOnePostApproved(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<HashMap<String, Any>>("$baseUrl/${testSetup.POST_ID_1}", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body["id"]).isEqualTo(testSetup.POST_ID_1)
        Truth.assertThat(body["type"]).isEqualTo("text")
        Truth.assertThat(body["description"]).isEqualTo("teste de texto")
        Truth.assertThat(body["statusCode"]).isEqualTo("approved")
    }

    @Test
    fun getPost_postExist_returnOnePostInReview(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<HashMap<String, Any>>("$baseUrl/${testSetup.POST_ID_3}", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body["id"]).isEqualTo(testSetup.POST_ID_3)
        Truth.assertThat(body["type"]).isEqualTo("video")
        Truth.assertThat(body["description"]).isEqualTo("teste de video")
        Truth.assertThat(body["statusCode"]).isEqualTo("inReview")
    }

    @Test
    fun getPost_postExist_returnOnePostDenied(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<HashMap<String, Any>>("$baseUrl/${testSetup.POST_ID_6}", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body["id"]).isEqualTo(testSetup.POST_ID_6)
        Truth.assertThat(body["type"]).isEqualTo("text")
        Truth.assertThat(body["description"]).isEqualTo("teste de texto 2222")
        Truth.assertThat(body["statusCode"]).isEqualTo("denied")
    }

    @Test
    fun getPost_multipleImagesPost_returnOnePostDenied(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<HashMap<String, Any>>("$baseUrl/${testSetup.POST_ID_7}", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body["id"]).isEqualTo(testSetup.POST_ID_7)
        Truth.assertThat(body["type"]).isEqualTo("image")
        Truth.assertThat(body["description"]).isEqualTo("teste de multipleImages")
        val defaultImage = body["image"] as HashMap<String,Any>
        Truth.assertThat(defaultImage["title"]).isEqualTo("teste1")
        Truth.assertThat(defaultImage["type"]).isEqualTo("png")
        Truth.assertThat(defaultImage["imageData"]).isEqualTo("www.minhaimagem1.com.br")
        val allImages = body["multipleImages"] as List<HashMap<String,Any>>
        Truth.assertThat(allImages[0]["title"]).isEqualTo("teste1")
        Truth.assertThat(allImages[0]["type"]).isEqualTo("png")
        Truth.assertThat(allImages[0]["imageData"]).isEqualTo("www.minhaimagem1.com.br")
        Truth.assertThat(allImages[1]["title"]).isEqualTo("teste2")
        Truth.assertThat(allImages[1]["type"]).isEqualTo("jpeg")
        Truth.assertThat(allImages[1]["imageData"]).isEqualTo("www.minhaimagem2.com.br")
        Truth.assertThat(body["statusCode"]).isEqualTo("denied")
    }

}