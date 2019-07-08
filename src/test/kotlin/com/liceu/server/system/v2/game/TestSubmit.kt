package com.liceu.server.system.v2.game

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.liceu.server.data.GameRepository
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.Instant
import java.util.*

class TestSubmit: TestSystem("/v2/game") {

    @Autowired
    lateinit var gameRepo: GameRepository


    @Test
    fun submitGame_Valid_Success() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = dataSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "answers" to listOf(
                        hashMapOf(
                                "questionId" to dataSetup.QUESTION_ID_1,
                                "selectedAnswer" to 2,
                                "correctAnswer" to 1
                        ),
                        hashMapOf(
                                "questionId" to dataSetup.QUESTION_ID_3,
                                "selectedAnswer" to 1,
                                "correctAnswer" to 3
                        )
                ),
                "timeSpent" to 10
        ), headers)
        val before = Date.from(Instant.now())
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        val after = Date.from(Instant.now())

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!
        val id = body["id"] as String
        val insertedGame = gameRepo.findById(id).get()

        assertThat(insertedGame).isNotNull()
        assertThat(insertedGame.userId.toHexString()).isEqualTo(dataSetup.USER_ID_1)
        assertThat(insertedGame.timeSpent).isEqualTo(10)
        assertThat(insertedGame.answers.size).isEqualTo(2)
        assertThat(insertedGame.answers[0].questionId.toHexString()).isEqualTo(dataSetup.QUESTION_ID_1)
        assertThat(insertedGame.answers[0].selectedAnswer).isEqualTo(2)
        assertThat(insertedGame.answers[0].correctAnswer).isEqualTo(1)
        assertThat(insertedGame.answers[1].questionId.toHexString()).isEqualTo(dataSetup.QUESTION_ID_3)
        assertThat(insertedGame.answers[1].selectedAnswer).isEqualTo(1)
        assertThat(insertedGame.answers[1].correctAnswer).isEqualTo(3)
        assertThat(insertedGame.submissionDate).isGreaterThan(before)
        assertThat(insertedGame.submissionDate).isLessThan(after)
    }

    @Test
    fun submitGame_NoAccessTokenSupplied_Unauthorized() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        val entity = HttpEntity(hashMapOf<String, Any>(
                "answers" to listOf(
                        hashMapOf(
                                "questionId" to dataSetup.QUESTION_ID_1,
                                "selectedAnswer" to 2,
                                "correctAnswer" to 1
                        ),
                        hashMapOf(
                                "questionId" to dataSetup.QUESTION_ID_3,
                                "selectedAnswer" to 1,
                                "correctAnswer" to 3
                        )
                ),
                "timeSpent" to 10
        ), headers)

        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun submitGame_InvalidKey_BadRequest() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = dataSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf<String, Any>(
                "answer" to listOf(
                        hashMapOf(
                                "questionId" to dataSetup.QUESTION_ID_1,
                                "selectedAnswer" to 2,
                                "correctAnswer" to 1
                        ),
                        hashMapOf(
                                "questionId" to dataSetup.QUESTION_ID_3,
                                "selectedAnswer" to 1,
                                "correctAnswer" to 3
                        )
                ),
                "timeSpent" to 10
        ), headers)

        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitGame_InvalidAnswerKey_BadRequest() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = dataSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf<String, Any>(
                "answers" to listOf(
                        hashMapOf(
                                "questionId" to dataSetup.QUESTION_ID_1,
                                "sel" to 2,
                                "correctAnswer" to 1
                        ),
                        hashMapOf(
                                "questionId" to dataSetup.QUESTION_ID_3,
                                "selectedAnswer" to 1,
                                "correctAnswer" to 3
                        )
                ),
                "timeSpent" to 10
        ), headers)

        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

}