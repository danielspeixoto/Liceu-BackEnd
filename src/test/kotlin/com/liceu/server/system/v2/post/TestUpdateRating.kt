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

class TestUpdateRating: TestSystem("/v2/post") {
    @Autowired
    lateinit var postRepo: PostRepository

    @Autowired
    lateinit var data: MongoPostRepository

    @Test
    fun updateRating_postExsits_verifyPost(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.POST_ID_4}/rating", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val postChanged = data.getPostById(testSetup.POST_ID_4)
        Truth.assertThat(postChanged.likes).isEqualTo(11)
    }

}