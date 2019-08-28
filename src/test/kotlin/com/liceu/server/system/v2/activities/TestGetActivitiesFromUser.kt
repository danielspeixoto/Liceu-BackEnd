package com.liceu.server.system.v2.activities

import com.google.common.truth.Truth
import com.liceu.server.data.ActivityRepository
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.HashMap

class TestGetActivitiesFromUser: TestSystem("/v2/activity") {

    @Autowired
    lateinit var activityRepo: ActivityRepository

    @Test
    fun getActivitiesFromUser_activitiesExists_returnListOfActivities(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<HashMap<String, Any>>>("$baseUrl/3a1449a4bdb40abd5ae1e431?amount=10", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body.size).isEqualTo(2)
        Truth.assertThat(body[0]["type"]).isEqualTo("TriviaFinished")
        Truth.assertThat(body[1]["type"]).isEqualTo("followedUser")
    }


    @Test
    fun getActivitiesFromUser_wrongUser_throwError(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<HashMap<String, Any>>>("$baseUrl/3a2b40abd5ae1e431?amount=10", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

}