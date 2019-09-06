package com.liceu.server.system.v2.activities

import com.google.common.truth.Truth
import com.liceu.server.data.ActivityRepository
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.HashMap

class TestSubmission: TestSystem("/v2/activity") {

    @Autowired
    lateinit var activityRepo: ActivityRepository

    @Disabled
    @Test
    fun submitActivity_Valid_Success() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "followedUser",
                "params" to hashMapOf(
                    "followedBy" to "oi"
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!
        val id = body["id"] as String
        val insertedActivity = activityRepo.findById(id).get()

        Truth.assertThat(insertedActivity).isNotNull()
        Truth.assertThat(insertedActivity.userId.toHexString()).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(insertedActivity.type).isEqualTo("followedUser")
    }

    @Disabled
    @Test
    fun submitActivity_typeNull_throwError() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to null,
                "params" to hashMapOf(
                        "followedBy" to "oi"
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Disabled
    @Test
    fun submitActivity_typeEmpty_throwError() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "",
                "params" to hashMapOf(
                        "followedBy" to "oi"
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Disabled
    @Test
    fun submitActivity_paramsNull_throwError() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "followedUser",
                "params" to null
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

}
