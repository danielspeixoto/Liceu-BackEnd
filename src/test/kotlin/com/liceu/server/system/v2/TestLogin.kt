package com.liceu.server.system.v2

import com.google.common.truth.Truth.assertThat
import com.liceu.server.data.UserRepository
import com.liceu.server.util.JWTAuth
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class TestLogin : TestSystem("/v2/login") {

    @Autowired
    lateinit var userRepo: UserRepository
    @Autowired
    lateinit var jwtAuth: JWTAuth

    val validAccessToken = "EAAf4pgUyFpsBACivxMMHar1zH1sfNxYOEO9VYLZCMPvmvJ7ZAyYhw8BfZCl5MR1QA0JhBhoeYB4f455CP1VHtnz2OwXlYYq8W9eQOXtQzsbFDUxwmFE7RlsfqabsXi5cMJ4k6iXdhMDEfXSJEF6Y7KoZBmZCHLw1mrZA0dZAgAm7O1GetwtZBim2IkVHfM2cAqlGwqybeQRZAagZDZD"

    @Test
    fun login_ValidAccessToken_CreatesUserOrLogsIn() {
        val userIds = arrayListOf<String>()
        var lastUserId = "lastUserId"
        for(i in 1..3) {
            val headers = HttpHeaders()
            headers["API_KEY"] = "apikey"

            val body = hashMapOf<String, Any>(
                    "accessToken" to validAccessToken
            )
            val entity = HttpEntity(body, headers)
            val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.headers).containsKey("Authorization")
            assertThat(response.headers["Authorization"]!![0].length).isGreaterThan(10)

            val userId = jwtAuth.authentication(response.headers["Authorization"]!![0].toString())!!
            userIds.add(userId)
            lastUserId = userId

            assertThat(userRepo.findById(userId).get()).isNotNull()
        }
        assertThat(userIds.size).isGreaterThan(2)
        userIds.forEach {
            assertThat(it).isEqualTo(lastUserId)
        }
    }

    @Test
    fun login_InvalidAccessToken_Unauthorized() {
        val headers = HttpHeaders()
        headers["API_KEY"] = "apikey"

        val body = hashMapOf<String, Any>(
                "accessToken" to "invalid"
        )
        val entity = HttpEntity(body, headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        assertThat(response.headers).doesNotContainKey("Authorization")
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