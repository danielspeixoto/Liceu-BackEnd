package com.liceu.server.acceptance

import com.google.common.truth.Truth
import com.google.gson.GsonBuilder
import com.liceu.server.data.UserRepository
import com.liceu.server.system.TestSystem
import com.liceu.server.util.JWTAuth
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class TestAcceptance : TestSystem("/v2/user") {

    lateinit var auth: String
    lateinit var userId: String
    @Autowired
    lateinit var jwtAuth: JWTAuth


    @BeforeEach
    fun login_ValidAccessToken_CreatesUserOrLogsIn() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        val body = hashMapOf<String, Any>(
                "accessToken" to testSetup.facebookAccessToken
        )
        val entity = HttpEntity(body, headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Truth.assertThat(response.headers).containsKey("Authorization")

        auth = response.headers["Authorization"]!![0]
        Truth.assertThat(auth.length).isGreaterThan(10)

        userId = jwtAuth.authentication(response.headers["Authorization"]!![0].toString())!!
    }


    @Test
    fun getRanking_Exists_returnTopGames() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = auth
        val entity = HttpEntity(null,headers)
        val response = restTemplate
                .exchange<List<java.util.HashMap<String, Any>>>(baseUrl + "?year=2019&month=8&amount=5", HttpMethod.GET,entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body.size).isEqualTo(5)
    }
}