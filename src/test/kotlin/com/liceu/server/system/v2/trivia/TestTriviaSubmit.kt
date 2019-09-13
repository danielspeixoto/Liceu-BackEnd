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

class TestTriviaSubmit: TestSystem ("/v2/trivia"){
    @Autowired
    lateinit var triviaRepo: TriviaRepository

    @Test
    fun SubmitTrivia_Valid_Sucess(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
               "question" to "essa e uma questao de teste sobre matematica: Seno de 0?",
                "correctAnswer" to "0",
                "wrongAnswer" to "1",
                "tags" to listOf(
                        "matematica",
                        "angulos",
                        "trigonometria",
                        "algebra"
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!
        val id = body["id"] as String
        val insertedTriviaQuestion = triviaRepo.findById(id).get()

        Truth.assertThat(insertedTriviaQuestion.question).isEqualTo("essa e uma questao de teste sobre matematica: Seno de 0?")
        Truth.assertThat(insertedTriviaQuestion.correctAnswer).isEqualTo("0")
        Truth.assertThat(insertedTriviaQuestion.wrongAnswer).isEqualTo("1")

    }

    @Test
    fun submitTrivia_overflowQuestionCharacters_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "question" to "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "correctAnswer" to "0",
                "wrongAnswer" to "1",
                "tags" to listOf(
                        "matematica",
                        "angulos",
                        "trigonometria",
                        "algebra"
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitTrivia_overflowCorrectAnswer_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "question" to "oi? 1+1=?",
                "correctAnswer" to "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaa",
                "wrongAnswer" to "1",
                "tags" to listOf(
                        "matematica",
                        "angulos",
                        "trigonometria",
                        "algebra"
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitTrivia_overflowWrongAnswer_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "question" to "oi? 1+1=?",
                "correctAnswer" to "2",
                "wrongAnswer" to "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                        "aaaaaaaaaaaaa",
                "tags" to listOf(
                        "matematica",
                        "angulos",
                        "trigonometria",
                        "algebra"
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitTrivia_overflowTagsSize_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "question" to "oi? 1+1=?",
                "correctAnswer" to "2",
                "wrongAnswer" to "aaaaaa",
                "tags" to listOf(
                        "matematica",
                        "angulos",
                        "trigonometria",
                        "algebra",
                        "contas",
                        "quebrou-se"
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitTrivia_missMatchTypeTags_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "question" to "oi? 1+1=?",
                "correctAnswer" to "2",
                "wrongAnswer" to "aaaaaa",
                "tags" to listOf(
                        "matematica",
                        "angulos",
                        "trigonometria",
                        "algebra",
                        1
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitTrivia_emptyQuestionCharacters_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "question" to  null,
                "correctAnswer" to "0",
                "wrongAnswer" to "1",
                "tags" to listOf(
                        "matematica",
                        "angulos",
                        "trigonometria",
                        "algebra"
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitTrivia_emptyCorrectAnswer_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "question" to  null,
                "correctAnswer" to "0",
                "wrongAnswer" to "1",
                "tags" to listOf(
                        "matematica",
                        "angulos",
                        "trigonometria",
                        "algebra"
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }


    @Test
    fun submitTrivia_emptyWrongAnswer_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "question" to  null,
                "correctAnswer" to "0",
                "wrongAnswer" to "1",
                "tags" to listOf(
                        "matematica",
                        "angulos",
                        "trigonometria",
                        "algebra"
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitTrivia_missMatchTypeQuestion_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "question" to 1,
                "correctAnswer" to "2",
                "wrongAnswer" to "0",
                "tags" to listOf(
                        "matematica",
                        "angulos",
                        "trigonometria",
                        "algebra"
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

}