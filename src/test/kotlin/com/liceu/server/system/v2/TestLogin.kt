package com.liceu.server.system.v2

import com.google.common.truth.Truth.assertThat
import com.liceu.server.data.QuestionRepository
import com.liceu.server.data.UserRepository
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class TestLogin : TestSystem("/v2/login") {

    @Autowired
    lateinit var userRepo: UserRepository

    val validAccessToken = "EAAf4pgUyFpsBAPO6PS3iO8dycZCy6DBuZCS8nnxmYby0Evc3Qw4PaXwVcalPmYp5CRD0fOT4ovCITH0c2wG2ySXSyAGJH5OXzCHpzZAYM2AA05FMdQDQ0vhKb1ZBZCGe5kDa82XRSLKKhtFtimd63xRfLKLmxVogEze1k48vTnNsZBN2dNDJIalj0CTai5q2IsrntWYLiaVZBjSAYTImKfGM3EHxKVhWLta9USTdOJTggZDZD"

    @Test
    fun login_ValidAccessToken_CreatesUserOrLogsIn() {
        for(i in 1..3) {
            val headers = HttpHeaders()
            headers["API_KEY"] = "apikey"

            val body = hashMapOf<String, Any>(
                    "accessToken" to validAccessToken
            )
            val entity = HttpEntity(body, headers)
            val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

            assertThat(response.statusCode).isEqualTo(200)
            assertThat(response.headers).containsKey("Authorization")
            assertThat(response.headers["Authorization"]!!.size).isGreaterThan(10)

            // TODO Assert User exists
        }
    }

    @Test
    fun login_InvalidAccessToken_Unauthorized() {
    }

    @Test
    fun login_NoAccessToken_Unauthorized() {
        val headers = HttpHeaders()
        headers["API_KEY"] = "apikey"

        val body = hashMapOf<String, Any>()
        val entity = HttpEntity(body, headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        assertThat(response.headers).doesNotContainKey("Authorization")
    }

    @Test
    fun login_InvalidApiKey_Unauthorized() {
        val headers = HttpHeaders()
        headers["API_KEY"] = "wrongApiKey"

        val body = hashMapOf<String, Any>(
                "accessToken" to validAccessToken
        )
        val entity = HttpEntity(body, headers)
        val response = restTemplate.exchange<String>(baseUrl, HttpMethod.POST, entity)

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        assertThat(response.headers).doesNotContainKey("Authorization")
    }
}