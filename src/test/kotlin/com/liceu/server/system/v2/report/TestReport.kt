package com.liceu.server.system.v2.report

import com.google.common.truth.Truth
import com.liceu.server.data.ReportRepository
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.*

class TestReport: TestSystem("/v2/report") {

    @Autowired
    lateinit var userRepo : ReportRepository

    @Test
    fun submitReport_Valid_Success() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "message" to "esse e um teste ahahahah",
                "tags" to listOf(
                    "teste1",
                    "teste2",
                    "teste3ahahah"
                ),
                "params" to hashMapOf(
                        "oioi" to 2,
                        "oioioi" to "iha"
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!
        val id = body["id"] as String
        val insertedGame = userRepo.findById(id).get()

        Truth.assertThat(insertedGame.userId.toHexString()).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(insertedGame.message).isEqualTo("esse e um teste ahahahah")
        Truth.assertThat(insertedGame.tags.size).isEqualTo(3)
        Truth.assertThat(insertedGame.params.size).isEqualTo(2)
        Truth.assertThat(insertedGame.tags[0]).isEqualTo("teste1")
        Truth.assertThat(insertedGame.tags[1]).isEqualTo("teste2")
        Truth.assertThat(insertedGame.tags[2]).isEqualTo("teste3ahahah")
        Truth.assertThat(insertedGame.params["oioi"]).isEqualTo(2)
        Truth.assertThat(insertedGame.params["oioioi"]).isEqualTo("iha")

    }

    @Test
    fun submitReport_overflowTags__Invalid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "userId" to testSetup.USER_ID_1,
                "message" to "esse e um teste ahahahah",
                "tags" to listOf(
                        "teste1",
                        "teste2",
                        "teste3ahahah",
                        "teste4",
                        "teste5",
                        "teste6"
                ),
                "params" to hashMapOf(
                        "oioi" to 2,
                        "oioioi" to "iha"
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

    }

    @Test
    fun submitReport_overflowParams__Invalid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "userId" to testSetup.USER_ID_1,
                "message" to "esse e um teste ahahahah",
                "tags" to listOf(
                        "teste1",
                        "teste2",
                        "teste3ahahah",
                        "teste4",
                        "teste5"
                ),
                "params" to hashMapOf(
                        "oioi" to 2,
                        "oioioi" to "iha",
                        "oioi22" to 3,
                        "oio333i" to 4,
                        "oio11i" to 5,
                        "oioi22222" to 10
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

    }

    @Test
    fun submitReport_overflowMessage__Invalid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "userId" to testSetup.USER_ID_1,
                "message" to "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "tags" to listOf(
                        "teste1",
                        "teste2",
                        "teste3ahahah",
                        "teste4",
                        "teste5"
                ),
                "params" to hashMapOf(
                        "oioi" to 2,
                        "oioioi" to "iha",
                        "oioi22" to 3,
                        "oio333i" to 4,
                        "oio11i" to 5
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

    }

    @Test
    fun submitReport_tagsEmpty__Invalid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "userId" to testSetup.USER_ID_1,
                "message" to "2222222222",
                "tags" to null,
                "params" to hashMapOf(
                        "oioi" to 2,
                        "oioioi" to "iha",
                        "oioi22" to 3,
                        "oio333i" to 4
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

    }

    @Test
    fun submitReport_messageEmpty__Invalid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "userId" to testSetup.USER_ID_1,
                "message" to "",
                "tags" to listOf(
                        "teste1",
                        "teste2",
                        "teste3ahahah",
                        "teste4"
                ),
                "params" to hashMapOf(
                        "oioi" to 2,
                        "oioioi" to "iha",
                        "oioi22" to 3,
                        "oio333i" to 4
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

    }

    @Test
    fun submitReport_paramsEmpty__Invalid(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "userId" to testSetup.USER_ID_1,
                "message" to "2222",
                "tags" to listOf(
                        "teste1",
                        "teste2",
                        "teste3ahahah",
                        "teste4"
                ),
                "params" to null
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

    }


}