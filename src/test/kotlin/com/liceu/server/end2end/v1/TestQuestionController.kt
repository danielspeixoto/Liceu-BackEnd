package com.liceu.server.end2end.v1

import com.google.common.truth.Truth.assertThat
import com.liceu.server.data.QuestionRepository
import com.liceu.server.data.VideoRepository
import com.liceu.server.presentation.v1.Response
import com.liceu.server.presentation.v1.STATUS_OK
import com.liceu.server.setup
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForObject
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableMongoRepositories
class TestQuestionController {

    @LocalServerPort
    private var port: Int = 0
    private var baseUrl: String = ""

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var questionRepo: QuestionRepository
    @Autowired
    lateinit var videoRepo: VideoRepository

    @BeforeEach
    fun setup() {
        baseUrl = "http://localhost:$port/questions/"
        setup(questionRepo, videoRepo)
    }

    @Test
    fun randomQuestion_WhenNoTagsSpecified_ReturnsAnyRandomly() {
        val ids = arrayListOf<String>()
        for (i in 1..20) {
            val response = randomQuestionGetResponse("$baseUrl?amount=1")
            assertThat(response.status).isEqualTo(STATUS_OK)
            assertThat(response.errorCode).isEqualTo(null)
            val data = response.data!!
            ids.add(data[0]["id"] as String)
        }
        assertThat(ids).containsAtLeast("id1", "id2", "id3")
    }

    @Test
    fun randomQuestion_TagsSpecified_Filters() {
        val response = randomQuestionGetResponse("$baseUrl?amount=10&tags[]=primeira")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data[0]["id"]).isEqualTo("id1")
    }

    @Test
    fun randomQuestion_TagRecurrent_ReturnsAllWithTags() {
        val response = randomQuestionGetResponse("$baseUrl?amount=10&tags[]=segunda")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data.map { it["id"] }).containsExactly("id1", "id2")
    }

    @Test
    fun randomQuestion_NonExistentTag_ReturnsEmpty() {
        val response = randomQuestionGetResponse("$baseUrl?amount=10&tags[]=negativo")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data).isEmpty()
    }

    @Test
    fun randomQuestion_ZeroAmount_ReturnsEmpty() {
        val response = randomQuestionGetResponse("$baseUrl?amount=0&tags[]=segunda")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data).isEmpty()
    }

    @Test
    fun randomQuestion_AmountNotSpecified_ReturnsEmpty() {
        val response = randomQuestionGetResponse("$baseUrl?tags[]=segunda")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data).isEmpty()
    }

//    @Test
//    fun randomQuestion_AmountTooBig_ReturnsMax() {
////        TODO Change Property
//        val response = randomQuestionGetResponse("$baseUrl?amount=2tags[]=segunda")
//        assertThat(response.status).isEqualTo(STATUS_OK)
//        assertThat(response.errorCode).isEqualTo(null)
//        val data = response.data!!
//        assertThat(data.size).isEqualTo(1)
//    }

    @Test
    fun randomQuestion_ValidRequest_ValidData() {
        val response = randomQuestionGetResponse("$baseUrl?amount=10&tags[]=primeira")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val question = response.data!![0]
        assertThat(question["id"]).isEqualTo("id1")
        assertThat(question["view"]).isEqualTo("ab")
        assertThat(question["source"]).isEqualTo("ENEM")
        assertThat(question["variant"]).isEqualTo("AMARELA")
        assertThat(question["edition"]).isEqualTo(2017)
        assertThat(question["number"]).isEqualTo(3)
        assertThat(question["domain"]).isEqualTo("matemática")
        assertThat(question["answer"]).isEqualTo(1)
        assertThat(question["tags"]).isEqualTo(listOf("primeira", "segunda"))
        assertThat(question["stage"]).isEqualTo(2)
        assertThat(question["width"]).isEqualTo(100)
        assertThat(question["height"]).isEqualTo(200)
    }

    fun randomQuestionGetResponse(url: String): Response<List<HashMap<String, Any>>> {
        val result = this.restTemplate
                .getForObject<HashMap<String, *>>(url)!!
        val status = result["status"]
        val errorCode = result["errorCode"]
        val data = result["data"]
        return Response(
                data as List<HashMap<String, Any>>,
                status as String,
                errorCode as Int?
        )
    }
}