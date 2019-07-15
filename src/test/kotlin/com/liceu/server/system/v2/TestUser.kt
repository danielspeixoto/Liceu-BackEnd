package com.liceu.server.system.v2

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

class TestUser: TestSystem("/v2/user") {

//    @Autowired
//    lateinit var userRepo = UserRepository


    @Test
    fun userID_exists_returnUser(){

        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<HashMap<String, Any>>(baseUrl + "/39c54d325b75357a571d4cc2", HttpMethod.GET,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!

        Truth.assertThat(body["id"]).isEqualTo(testSetup.USER_ID_2)
        Truth.assertThat(body["name"]).isEqualTo("user2")
        Truth.assertThat(body["email"]).isEqualTo("user2@g.com")
        val userRetrieved = (body["picture"] as HashMap<String, Any>)
        Truth.assertThat(userRetrieved["url"]).isEqualTo("https://picture2.jpg")
        Truth.assertThat(userRetrieved["width"]).isEqualTo(200)
        Truth.assertThat(userRetrieved["height"]).isEqualTo(200)


    }

    @Test
    fun userID_notExits_returnUser(){

        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN

        val entity = HttpEntity(null,headers)

        val response = restTemplate
                .exchange<List<HashMap<String, Any>>>(baseUrl + "/222225357a571d4cc2", HttpMethod.GET,entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)



    }



}