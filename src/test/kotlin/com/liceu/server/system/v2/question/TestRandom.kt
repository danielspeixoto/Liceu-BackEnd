package com.liceu.server.system.v2.question

import com.google.common.truth.Truth
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class TestRandom : TestSystem("/v2/question") {

    private var headers = HttpHeaders()

    @BeforeEach
    override fun setup() {
        super.setup()
        headers = HttpHeaders()
        headers["API_KEY"] = apiKey
    }

    fun questions(url: String): List<HashMap<String, Any>> {
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange< List<HashMap<String, Any>>>(url, HttpMethod.GET, entity)
        val body = response.body!!
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        return body
    }

    @Test
    fun randomQuestion_WhenNoTagsSpecified_ReturnsAnyRandomly() {
        val ids = arrayListOf<String>()
        for (i in 1..20) {
            val data = questions("$baseUrl?amount=1")

            ids.add(data[0]["id"] as String)
        }
        Truth.assertThat(ids).containsAtLeast(dataSetup.QUESTION_ID_1, dataSetup.QUESTION_ID_2, dataSetup.QUESTION_ID_3)
    }

    @Test
    fun randomQuestion_TagsSpecified_Filters() {
        val data = questions("$baseUrl?amount=10&tags=primeira")
        Truth.assertThat(data[0]["id"]).isEqualTo(dataSetup.QUESTION_ID_1)
    }

    @Test
    fun randomQuestion_TagRecurrent_ReturnsAllWithTags() {
        val data = questions("$baseUrl?amount=10&tags=segunda")
        Truth.assertThat(data.map { it["id"] }).containsExactly(dataSetup.QUESTION_ID_1, dataSetup.QUESTION_ID_2)
    }

    @Test
    fun randomQuestion_NonExistentTag_ReturnsEmpty() {
        val data = questions("$baseUrl?amount=10&tags=negativo")
        Truth.assertThat(data).isEmpty()
    }

    @Test
    fun randomQuestion_ZeroAmount_ReturnsEmpty() {
        val data = questions("$baseUrl?amount=0&tags=segunda")
        Truth.assertThat(data).isEmpty()
        val data2 = questions("$baseUrl?tags=segunda")
        Truth.assertThat(data2).isEmpty()
    }

    @Test
    fun randomQuestion_ValidRequest_ValidData() {
        val data = questions("$baseUrl?amount=10&tags=primeira")
        val question = data[0]

        Truth.assertThat(question["id"]).isEqualTo(dataSetup.QUESTION_ID_1)
        Truth.assertThat(question["view"]).isEqualTo("https://url1.com")
        Truth.assertThat(question["source"]).isEqualTo("ENEM")
        Truth.assertThat(question["variant"]).isEqualTo("AMARELA")
        Truth.assertThat(question["edition"]).isEqualTo(2017)
        Truth.assertThat(question["number"]).isEqualTo(3)
        Truth.assertThat(question["domain"]).isEqualTo("matemática")
        Truth.assertThat(question["answer"]).isEqualTo(1)
        Truth.assertThat(question["tags"]).isEqualTo(listOf("primeira", "segunda"))
        Truth.assertThat(question["stage"]).isEqualTo(2)
        Truth.assertThat(question["width"]).isEqualTo(100)
        Truth.assertThat(question["height"]).isEqualTo(200)
    }

}