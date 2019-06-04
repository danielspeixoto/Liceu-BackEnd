package com.liceu.server.system.v1

import com.google.common.truth.Truth.assertThat
import com.liceu.server.*
import com.liceu.server.presentation.v1.Response.Companion.STATUS_OK
import com.liceu.server.system.TestSystem
import com.liceu.server.util.*
import org.junit.jupiter.api.Test

class TestQuestion : TestSystem("question") {

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
        assertThat(ids).containsAtLeast(DataSetup.QUESTION_ID_1, DataSetup.QUESTION_ID_2, DataSetup.QUESTION_ID_3)
    }

    @Test
    fun randomQuestion_TagsSpecified_Filters() {
        val response = getListResponse(restTemplate, "$baseUrl?amount=10&tags[]=primeira")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data[0]["id"]).isEqualTo(DataSetup.QUESTION_ID_1)
    }

    @Test
    fun randomQuestion_TagRecurrent_ReturnsAllWithTags() {
        val response = getListResponse(restTemplate, "$baseUrl?amount=10&tags[]=segunda")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data.map { it["id"] }).containsExactly(DataSetup.QUESTION_ID_1, DataSetup.QUESTION_ID_2)
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
        assertThat(question["id"]).isEqualTo(DataSetup.QUESTION_ID_1)
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
        val response = getListResponse(restTemplate, "$baseUrl/${DataSetup.QUESTION_ID_1}/relatedVideos?amount=10")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val videos = response.data!!.map { it["id"] }
        assertThat(videos).containsExactly(DataSetup.VIDEO_ID_3, DataSetup.VIDEO_ID_1).inOrder()
    }

    @Test
    fun videos_QuestionHasNoVideos_Empty() {
        val response = getListResponse(restTemplate, "$baseUrl/${DataSetup.INVALID_ID}/relatedVideos?amount=10")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        assertThat(response.data).isEmpty()
    }

    @Test
    fun videos_QuestionHasManyVideos_Paginates() {
        val response = getListResponse(restTemplate, "$baseUrl/${DataSetup.QUESTION_ID_1}/relatedVideos?start=1&amount=10")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data[0]["id"]).isEqualTo(DataSetup.VIDEO_ID_1)
    }

    @Test
    fun videos_QuestionHasManyVideos_LimitsAmount() {
        val response = getListResponse(restTemplate, "$baseUrl/${DataSetup.QUESTION_ID_1}/relatedVideos?amount=1")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!.map { it["id"] }
        assertThat(data).containsExactly(DataSetup.VIDEO_ID_3)
    }

    @Test
    fun videos_NonExistentQuestion_Empty() {
        val response = getListResponse(restTemplate, "$baseUrl/${DataSetup.INVALID_ID}/relatedVideos?amount=10")
        assertThat(response.status).isEqualTo(STATUS_OK)
        assertThat(response.errorCode).isEqualTo(null)
        val data = response.data!!
        assertThat(data).isEmpty()
    }


}