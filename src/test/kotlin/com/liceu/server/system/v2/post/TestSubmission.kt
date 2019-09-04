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
                "description" to "esse e um teste aha",
                "hasQuestions" to "true",
                "questions" to listOf(
                        hashMapOf(
                                "question" to "O triangulo que tem lados iguais é?",
                                "correctAnswer" to "Equilátero",
                                "otherAnswers" to listOf(
                                        "Anormal", "Perfeito"
                                )
                        ),
                        hashMapOf(
                                "question" to "O triangulo que tem todos os lados diferentes é?",
                                "correctAnswer" to "Escaleno",
                                "otherAnswers" to listOf(
                                        "Anormal", "Doido"
                                )
                        )
                )
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
        Truth.assertThat(insertedPost.questions?.size).isEqualTo(2)
        Truth.assertThat(insertedPost.questions?.get(0)?.question).isEqualTo("O triangulo que tem lados iguais é?")
        Truth.assertThat(insertedPost.questions?.get(0)?.correctAnswer).isEqualTo("Equilátero")
        Truth.assertThat(insertedPost.questions?.get(0)?.otherAnswers?.size).isEqualTo(2)
        Truth.assertThat(insertedPost.questions?.get(0)?.otherAnswers).containsExactly("Anormal", "Perfeito")
        Truth.assertThat(insertedPost.questions?.get(1)?.question).isEqualTo("O triangulo que tem todos os lados diferentes é?")
        Truth.assertThat(insertedPost.questions?.get(1)?.correctAnswer).isEqualTo("Escaleno")
        Truth.assertThat(insertedPost.questions?.get(1)?.otherAnswers?.size).isEqualTo(2)
        Truth.assertThat(insertedPost.questions?.get(1)?.otherAnswers).containsExactly("Anormal", "Doido")
    }

    @Test
    fun submitVideoPost_Valid_Success() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to "esse e um teste aha",
                "videoUrl" to "https://www.youtube.com/watch?v=8vefLpfozPA",
                "hasQuestions" to "true",
                "questions" to listOf(
                            hashMapOf(
                                    "question" to "O triangulo que tem lados iguais é?",
                                    "correctAnswer" to "Equilátero",
                                    "otherAnswers" to listOf(
                                            "Anormal", "Perfeito"
                                    )
                            ),
                            hashMapOf(
                                    "question" to "O triangulo que tem todos os lados diferentes é?",
                                    "correctAnswer" to "Escaleno",
                                    "otherAnswers" to listOf(
                                            "Anormal", "Doido"
                                    )
                            )
                )
        ),headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!
        val id = body["id"] as String
        val insertedPost = postRepo.findById(id).get()

        Truth.assertThat(insertedPost).isNotNull()
        Truth.assertThat(insertedPost.userId.toHexString()).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(insertedPost.type).isEqualTo("video")
        Truth.assertThat(insertedPost.video?.videoUrl).isEqualTo("https://www.youtube.com/watch?v=8vefLpfozPA")
        Truth.assertThat(insertedPost.video?.thumbnails?.high).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/hqdefault.jpg")
        Truth.assertThat(insertedPost.video?.thumbnails?.default).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/default.jpg")
        Truth.assertThat(insertedPost.video?.thumbnails?.medium).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/mqdefault.jpg")
        Truth.assertThat(insertedPost.questions?.size).isEqualTo(2)
        Truth.assertThat(insertedPost.questions?.get(0)?.question).isEqualTo("O triangulo que tem lados iguais é?")
        Truth.assertThat(insertedPost.questions?.get(0)?.correctAnswer).isEqualTo("Equilátero")
        Truth.assertThat(insertedPost.questions?.get(0)?.otherAnswers?.size).isEqualTo(2)
        Truth.assertThat(insertedPost.questions?.get(0)?.otherAnswers).containsExactly("Anormal", "Perfeito")
    }

    @Test
    fun submitVideoPost_questionsEmpty_sucess(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to "teste teste",
                "videoUrl" to "https://www.youtube.com/watch?v=8vefLpfozPA",
                "hasQuestions" to "false"
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        val id = body["id"] as String
        val insertedPost = postRepo.findById(id).get()
        Truth.assertThat(insertedPost.userId.toHexString()).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(insertedPost.type).isEqualTo("video")
        Truth.assertThat(insertedPost.video?.videoUrl).isEqualTo("https://www.youtube.com/watch?v=8vefLpfozPA")
        Truth.assertThat(insertedPost.video?.thumbnails?.high).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/hqdefault.jpg")
        Truth.assertThat(insertedPost.video?.thumbnails?.default).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/default.jpg")
        Truth.assertThat(insertedPost.video?.thumbnails?.medium).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/mqdefault.jpg")
        Truth.assertThat(insertedPost.questions).isEmpty()
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
                "hasQuestions" to "false"
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
                "hasQuestions" to "false"
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
                "hasQuestions" to "false"
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
                "hasQuestions" to "false"
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitVideoPost_hasQuestionsToInt_sucess() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to "teste teste",
                "videoUrl" to "https://www.youtube.com/watch?v=8vefLpfozPA",
                "hasQuestions" to 1
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

    }

}