package com.liceu.server.system.v2.Trivia

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

class TestUpdateCommentsTrivia: TestSystem("v2/trivia") {
    @Autowired
    lateinit var triviaRepo: TriviaRepository

    @Autowired
    lateinit var data: MongoTriviaRepository

    @Test
    fun updateListOfComments_validComment_verifyPost(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "userId" to testSetup.USER_ID_1,
                "comment" to "comentando na questão"
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/0a1449a4bdb40abd5ae1e461/comment", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val postChanged = data.getTriviaById("0a1449a4bdb40abd5ae1e461")
        Truth.assertThat(postChanged.comments?.size).isEqualTo(1)
        Truth.assertThat(postChanged.comments?.get(0)?.comment).isEqualTo("comentando na questão")
    }

    @Test
    fun updateListOfComments_nullComment_throwError(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "userId" to testSetup.USER_ID_1,
                "comment" to null
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/0a1449a4bdb40abd5ae1e461/comment", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun updateListOfComments_emptyComment_throwError(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "userId" to testSetup.USER_ID_1,
                "comment" to ""
        ), headers)
        val response = restTemplate.exchange<Void>("$baseUrl/0a1449a4bdb40abd5ae1e461/comment", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

}