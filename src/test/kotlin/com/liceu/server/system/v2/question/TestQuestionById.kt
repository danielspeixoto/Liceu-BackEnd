package com.liceu.server.system.v2.question

import com.google.common.truth.Truth
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.HashMap

class TestQuestionById: TestSystem("/v2/question") {

    @Test
    fun questionID_exists_returnQuestion(){

        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<HashMap<String, Any>>(baseUrl + "/0a1449a4bdb40abd5ae1e431", HttpMethod.GET,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!

        Truth.assertThat(body["id"]).isEqualTo(testSetup.QUESTION_ID_1)
        Truth.assertThat(body["source"]).isEqualTo("ENEM")
        Truth.assertThat(body["variant"]).isEqualTo("AMARELA")
        Truth.assertThat(body["edition"]).isEqualTo(2017)
        Truth.assertThat(body["number"]).isEqualTo(3)
        Truth.assertThat(body["domain"]).isEqualTo("matemática")
        Truth.assertThat(body["answer"]).isEqualTo(1)
        val questionRetrieved = (body["tags"] as List<String>)
        Truth.assertThat(questionRetrieved[0]).isEqualTo("primeira")
        Truth.assertThat(questionRetrieved[1]).isEqualTo("segunda")
        Truth.assertThat(body["stage"]).isEqualTo(2)
        Truth.assertThat(body["width"]).isEqualTo(100)
        Truth.assertThat(body["height"]).isEqualTo(200)
        Truth.assertThat(body["view"]).isEqualTo("https://url1.com")

    }



    @Test
    fun questionID_notExits_returnError(){

        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<HashMap<String, Any>>(baseUrl + "/0a1449a4bdb40abd5ae1e432", HttpMethod.GET,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)



    }


}