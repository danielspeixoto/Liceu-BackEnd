package com.liceu.server.system.v2.question

import com.google.common.truth.Truth
import com.liceu.server.DataSetup
import com.liceu.server.presentation.Response
import com.liceu.server.system.TestSystem
import com.liceu.server.util.getListResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class TestVideo : TestSystem("/v2/question") {

    private var headers = HttpHeaders()

    @BeforeEach
    override fun setup() {
        super.setup()
        headers = HttpHeaders()
        headers["API_KEY"] = "apikey"
    }

    fun videos(url: String): List<HashMap<String, Any>> {
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(url, HttpMethod.GET, entity)
        val body = response.body!!
        val data = body["data"]!! as List<HashMap<String, Any>>

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Truth.assertThat(body["errorCode"]).isEqualTo(null)

        return data
    }

    @Test
    fun videos_QuestionHasVideos_ReturnsThem() {
        val videos = videos("$baseUrl/${DataSetup.QUESTION_ID_1}/videos?amount=10").map { it["id"] }
        Truth.assertThat(videos).containsExactly(DataSetup.VIDEO_ID_3, DataSetup.VIDEO_ID_1).inOrder()
    }

    @Test
    fun videos_QuestionHasNoVideos_Empty() {
        val videos = videos("$baseUrl/${DataSetup.INVALID_ID}/videos?amount=10")
        Truth.assertThat(videos).isEmpty()
    }

    @Test
    fun videos_QuestionHasManyVideos_Paginates() {
        val data = videos("$baseUrl/${DataSetup.QUESTION_ID_1}/videos?start=1&amount=10")
        Truth.assertThat(data[0]["id"]).isEqualTo(DataSetup.VIDEO_ID_1)
    }

    @Test
    fun videos_QuestionHasManyVideos_LimitsAmount() {
        val data = videos("$baseUrl/${DataSetup.QUESTION_ID_1}/videos?amount=1").map { it["id"] }
        Truth.assertThat(data).containsExactly(DataSetup.VIDEO_ID_3)
    }

    @Test
    fun videos_NonExistentQuestion_Empty() {
        val data = videos("$baseUrl/${DataSetup.INVALID_ID}/videos?amount=10")
        Truth.assertThat(data).isEmpty()
    }


}