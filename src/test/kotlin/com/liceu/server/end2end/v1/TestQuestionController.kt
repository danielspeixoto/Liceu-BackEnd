package com.liceu.server.end2end.v1

import com.google.common.truth.Truth.assertThat
import com.liceu.server.data.QuestionRepository
import com.liceu.server.data.TagRepository
import com.liceu.server.data.VideoRepository
import com.liceu.server.domain.global.ALREADY_EXISTS
import com.liceu.server.domain.global.ERROR
import com.liceu.server.domain.global.NOT_FOUND
import com.liceu.server.presentation.v1.Response.Companion.ALREADY_EXISTS_ERROR_CODE
import com.liceu.server.presentation.v1.Response.Companion.NOT_FOUND_ERROR_CODE
import com.liceu.server.presentation.v1.Response.Companion.STATUS_ERROR
import com.liceu.server.presentation.v1.Response.Companion.STATUS_OK
import com.liceu.server.presentation.v1.Response.Companion.UNKNOWN_ERROR_CODE
import com.liceu.server.presentation.v1.Response.Companion.VALIDATION_ERROR_CODE
import com.liceu.server.setup
import com.liceu.server.util.getListResponse
import com.liceu.server.util.postResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
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
    @Autowired
    lateinit var tagRepo: TagRepository

    @BeforeEach
    fun setup() {
        baseUrl = "http://localhost:$port/questions/"
        setup(questionRepo, videoRepo, tagRepo)
    }

    @Test
    fun randomQuestion_WhenNoTagsSpecified_ReturnsAnyRandomly() {
        val ids = arrayListOf<String>()
        for (i in 1..20) {
            val response = getListResponse(restTemplate, "$baseUrl?amount=1")
            assertThat(response.status).isEqualTo(STATUS_OK)
            assertThat(response.errorCode).isEqualTo(null)
            val data = response.data!!
            ids.add(data[0]["id"] as String)
        }
        assertThat(ids).containsAtLeast("id1", "id2", "id3")
    }

    @Test
    fun randomQuestion_TagsSpecified_Filters() {
        val response = getListResponse(restTemplate, "$baseUrl?amount=10&tags[]=primeira")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data[0]["id"]).isEqualTo("id1")
    }

    @Test
    fun randomQuestion_TagRecurrent_ReturnsAllWithTags() {
        val response = getListResponse(restTemplate, "$baseUrl?amount=10&tags[]=segunda")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data.map { it["id"] }).containsExactly("id1", "id2")
    }

    @Test
    fun randomQuestion_NonExistentTag_ReturnsEmpty() {
        val response = getListResponse(restTemplate, "$baseUrl?amount=10&tags[]=negativo")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data).isEmpty()
    }

    @Test
    fun randomQuestion_ZeroAmount_ReturnsEmpty() {
        val response = getListResponse(restTemplate, "$baseUrl?amount=0&tags[]=segunda")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data).isEmpty()
    }

    @Test
    fun randomQuestion_AmountNotSpecified_ReturnsEmpty() {
        val response = getListResponse(restTemplate, "$baseUrl?tags[]=segunda")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data).isEmpty()
    }

//    @Test
//    fun randomQuestion_AmountTooBig_ReturnsMax() {
////        TODO Change Property
//        val response = getListResponse(restTemplate, "$baseUrl?amount=2tags[]=segunda")
//        assertThat(response.status).isEqualTo(STATUS_OK)
//        assertThat(response.errorCode).isEqualTo(null)
//        val data = response.data!!
//        assertThat(data.size).isEqualTo(1)
//    }

    @Test
    fun randomQuestion_ValidRequest_ValidData() {
        val response = getListResponse(restTemplate, "$baseUrl?amount=10&tags[]=primeira")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val question = response.data!![0]
        assertThat(question["id"]).isEqualTo("id1")
        assertThat(question["view"]).isEqualTo("YWI=")
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

    @Test
    fun videos_QuestionHasVideos_ReturnsThem() {
        val response = getListResponse(restTemplate, "$baseUrl/id1/relatedVideos?amount=10")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val videos = response.data!!.map { it["id"] }
        assertThat(videos).containsExactly("id3", "id1").inOrder()
    }

    @Test
    fun videos_QuestionHasNoVideos_Empty() {
        val response = getListResponse(restTemplate, "$baseUrl/id0/relatedVideos?amount=10")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        assertThat(response.data).isEmpty()
    }

    @Test
    fun videos_QuestionHasManyVideos_Paginates() {
        val response = getListResponse(restTemplate, "$baseUrl/id1/relatedVideos?start=1&amount=10")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data[0]["id"]).isEqualTo("id1")
    }

    @Test
    fun videos_QuestionHasManyVideos_LimitsAmount() {
        val response = getListResponse(restTemplate, "$baseUrl/id1/relatedVideos?amount=1")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!.map { it["id"] }
        assertThat(data).containsExactly("id3")
    }

    @Test
    fun videos_NonExistentQuestion_Empty() {
        val response = getListResponse(restTemplate, "$baseUrl/id0/relatedVideos?amount=10")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data).isEmpty()
    }


    @Test
    fun addTag_QuestionDoesNotHaveThatTag_Success() {
        val response = postResponse(restTemplate, "$baseUrl/id2/tags", hashMapOf(
                "name" to "primeira"
        ))
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val questionTags = questionRepo.findById("id2").get().tags
        assertThat(questionTags).containsExactly("segunda", "primeira").inOrder()

    }

    @Test
    fun addTag_TagDoesNotExists_Fail() {
        val response = postResponse(restTemplate, "$baseUrl/id2/tags", hashMapOf(
                "name" to "zero"
        ))
        assertThat(response.status).isEqualTo(ERROR)
        assertThat(response.errorCode).isEqualTo(NOT_FOUND_ERROR_CODE)
    }

    @Test
    fun addTag_QuestionHasTag_Fail() {
        val response = postResponse(restTemplate, "$baseUrl/id1/tags", hashMapOf(
                "name" to "primeira"
        ))
        assertThat(response.status).isEqualTo(ERROR)
        assertThat(response.errorCode).isEqualTo(ALREADY_EXISTS_ERROR_CODE)
        val questionTags = questionRepo.findById("id1").get().tags
        assertThat(questionTags).containsExactly("primeira", "segunda").inOrder()
    }

    @Test
    fun addTag_QuestionDoesNotExists_Fail() {
        val response = postResponse(restTemplate, "$baseUrl/id0/tags", hashMapOf(
                "name" to "primeira"
        ))
        assertThat(response.status).isEqualTo(ERROR)
        assertThat(response.errorCode).isEqualTo(NOT_FOUND_ERROR_CODE)
    }

    @Test
    fun addTag_NoBody_Fail() {
        val response = postResponse(restTemplate, "$baseUrl/id1/tags", hashMapOf())
        assertThat(response.status).isEqualTo(STATUS_ERROR)
        assertThat(response.errorCode).isEqualTo(VALIDATION_ERROR_CODE)
        val questionTags = questionRepo.findById("id1").get().tags
        assertThat(questionTags).containsExactly("primeira", "segunda").inOrder()

    }
}