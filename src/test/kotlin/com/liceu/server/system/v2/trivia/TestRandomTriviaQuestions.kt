package com.liceu.server.system.v2.trivia

import com.google.common.truth.Truth
import com.liceu.server.data.TriviaRepository
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.HashMap

class TestRandomTriviaQuestions: TestSystem("/v2/trivia") {

    @Autowired
    lateinit var triviaRepo: TriviaRepository

    fun questions(url: String): List<HashMap<String, Any>> {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange< List<HashMap<String, Any>>>(url, HttpMethod.GET, entity)
        val body = response.body!!
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        return body
    }

    //@Test
    fun randomTriviaQuestions_requestOnlyOne_returnsExistent(){
        val data = questions("$baseUrl?tags=historia&amount=5")
        Truth.assertThat(data[0]["id"]).isEqualTo(testSetup.QUESTION_TRIVIA_ID_5)
    }

    //@Test
    fun randomTriviaQuestions_requestFourQuestions_returnsExistent(){
        val data = questions("$baseUrl?tags=matematica&amount=5")
        Truth.assertThat(data.map { it["id"] }).containsExactly(testSetup.QUESTION_TRIVIA_ID_1, testSetup.QUESTION_TRIVIA_ID_2, testSetup.QUESTION_TRIVIA_ID_3,
                testSetup.QUESTION_TRIVIA_ID_4)
    }

    //@Test
    fun randomTriviaQuestions_requestEmpty_returnsNoExistent(){
        val data = questions("$baseUrl?tags=fisica&amount=5")
        Truth.assertThat(data).isEmpty()
    }

    @Test
    fun randomTriviaQuestions_requestAmountZero_returnsEmpty(){
        val data = questions("$baseUrl?tags=matematica&amount=0")
        Truth.assertThat(data).isEmpty()
    }

    //@Test
    fun randomTriviaQuestions_requestAmountZeroThanCheckAmountFour_returnsEmpty(){
        val data1 = questions("$baseUrl?tags=matematica&amount=0")
        Truth.assertThat(data1).isEmpty()
        val data2 = questions("$baseUrl?tags=matematica&amount=4")
        Truth.assertThat(data2.map { it["id"] }).containsExactly(testSetup.QUESTION_TRIVIA_ID_1, testSetup.QUESTION_TRIVIA_ID_2, testSetup.QUESTION_TRIVIA_ID_3,
                testSetup.QUESTION_TRIVIA_ID_4)
    }

}