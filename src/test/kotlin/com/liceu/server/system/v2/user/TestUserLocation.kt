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
import java.util.HashMap

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

    @Test
    fun getUsersByNameUsingLocation_userExists_returnListOfUsers(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null, headers)
        val response = restTemplate
                .exchange<List<HashMap<String, Any>>>("$baseUrl?nameRequired=user&longitude=-44.30&latitude=-2.55&amount=15", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body.size).isEqualTo(3)
    }

    @Test
    fun getUsersByNameUsingLocation_userExists_returnListOfUser(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null, headers)
        val response = restTemplate
                .exchange<List<HashMap<String, Any>>>("$baseUrl?nameRequired=man i&longitude=-44.30&latitude=-2.55&amount=15", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body.size).isEqualTo(1)
        Truth.assertThat(body[0]["name"]).isEqualTo("manitos1")
    }

    @Test
    fun getUsersByNameUsingLocationWithAccentuation_userExists_returnListOfUser(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null, headers)
        val response = restTemplate
                .exchange<List<HashMap<String, Any>>>("$baseUrl?nameRequired= mán i&longitude=-44.30&latitude=-2.55&amount=15", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body.size).isEqualTo(1)
        Truth.assertThat(body[0]["name"]).isEqualTo("manitos1")
    }

    @Test
    fun getUsersByNameUsingLocation_emptyUser_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null, headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl?nameRequired=&longitude=-44.30&latitude=-2.55&amount=15", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun getUsersByNameUsingLocation_amountZero_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null, headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl?nameRequired=1231&longitude=-44.30&latitude=-2.55&amount=0", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)

    }

    @Test
    fun getUsersByNameUsingLocation_latitudeAsString_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null, headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl?nameRequired=1231&longitude=-44.30&latitude=oi&amount=10", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun getUsersByNameUsingLocation_longitudeAsString_throwException(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null, headers)
        val response = restTemplate
                .exchange<Void>("$baseUrl?nameRequired=1231&longitude=ahah&latitude=-32.53&amount=10", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }


}