package com.liceu.server.system.v2.trivia

import com.google.common.truth.Truth
import com.liceu.server.data.MongoTriviaRepository
import com.liceu.server.data.TriviaRepository
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class TestUpdateRating: TestSystem("v2/trivia") {
    @Autowired
    lateinit var triviaRepo: TriviaRepository

    @Autowired
    lateinit var data: MongoTriviaRepository

    @Test
    fun updateRatingLike_validRating_verifyTrivia(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "rating" to 1
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.ACITIVITY_ID_1}/rating", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val postChanged = data.getTriviaById("0a1449a4bdb40abd5ae1e461")
        Truth.assertThat(postChanged.likes).isEqualTo(4)
        Truth.assertThat(postChanged.dislikes).isEqualTo(4)
    }

    @Test
    fun updateRatingDislike_validRating_verifyTrivia(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "rating" to -1
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.ACITIVITY_ID_1}/rating", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val postChanged = data.getTriviaById("0a1449a4bdb40abd5ae1e461")
        Truth.assertThat(postChanged.likes).isEqualTo(3)
        Truth.assertThat(postChanged.dislikes).isEqualTo(5)
    }

    @Test
    fun updateRatingLike_ratingToZero_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "rating" to 0
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.ACITIVITY_ID_1}/rating", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateRatingLike_ratingToNull_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "rating" to null
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.ACITIVITY_ID_1}/rating", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateRatingLike_ratingToEmptyString_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "rating" to ""
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.ACITIVITY_ID_1}/rating", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateRatingLike_ratingToDouble_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "rating" to 1.0
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/${testSetup.ACITIVITY_ID_1}/rating", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
}