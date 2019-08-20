package com.liceu.server.system.v2.user

import com.google.common.truth.Truth
import com.liceu.server.data.UserRepository
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class TestUserLocation: TestSystem("/v2/user") {

    @Autowired
    lateinit var userRepo : UserRepository


    @Test
    fun updateLocationFromUser_userExist_noReturn(){

        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "longitude" to -38.527639,
                        "latitude" to -12.997278
                )
                ,headers)

        val response = restTemplate.exchange<Void>(baseUrl+"/3a1449a4bdb40abd5ae1e431/locale", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val result = userRepo.findById(testSetup.USER_ID_1).get()
        Truth.assertThat(result.location?.x).isEqualTo(-38.527639)
        Truth.assertThat(result.location?.y).isEqualTo(-12.997278)
        Truth.assertThat(result.state).isEqualTo("BA")
    }

    @Test
    fun updateLocationFromUser_userExist_throwValidationException() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(
                hashMapOf(
                        "longitude" to 1,
                        "latitude" to 90.233
                )
                , headers)

        val response = restTemplate.exchange<Void>(baseUrl + "/3a1449a4bdb40abd5ae1e431/locale", HttpMethod.PUT, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

}