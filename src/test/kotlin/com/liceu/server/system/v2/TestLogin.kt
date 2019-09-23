package com.liceu.server.system.v2

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.liceu.server.data.UserRepository
import com.liceu.server.util.JWTAuth
import com.liceu.server.system.TestSystem
import org.bson.types.ObjectId
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

    @Test
    fun login_ValidFacebookAccessToken_CreatesUserOrLogsIn() {
        val userIds = arrayListOf<String>()
        var lastUserId = "lastUserId"
        for(i in 1..3) {
            val headers = HttpHeaders()
            headers["API_KEY"] = apiKey
            val body = hashMapOf<String, Any>(
                    "accessToken" to testSetup.facebookAccessToken
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
    fun multipleLogin_facebookLoginPassingMethodParam_CreatesUserOrLogsIn() {
        val userIds = arrayListOf<String>()
        var lastUserId = "lastUserId"
        for(i in 1..3) {
            val headers = HttpHeaders()
            headers["API_KEY"] = apiKey
            val body = hashMapOf<String, Any>(
                    "accessToken" to testSetup.facebookAccessToken,
                    "method" to "facebook"
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
    fun login_ValidFacebookAccessToken_DontChangeUserRegistered() {
        val userIds = arrayListOf<String>()
        var lastUserId = "lastUserId"
        for(i in 1..3) {
            val headers = HttpHeaders()
            headers["API_KEY"] = apiKey
            val body = hashMapOf<String, Any>(
                    "accessToken" to testSetup.facebookAccessToken,
                    "method" to "facebook"
            )
            val entity = HttpEntity(body, headers)
            val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.headers).containsKey("Authorization")
            assertThat(response.headers["Authorization"]!![0].length).isGreaterThan(10)
            val userId = jwtAuth.authentication(response.headers["Authorization"]!![0].toString())!!
            userIds.add(userId)
            lastUserId = userId
            val user = userRepo.findById(userId).get()
            assertThat(user).isNotNull()
            assertThat(user.id).isEqualTo(ObjectId(testSetup.FACEBOOK_ID))
            assertThat(user.email).isEqualTo("sulwxcmqrp_1567856463@tfbnw.net")
            assertThat(user.facebookId).isEqualTo("115992013112781")
            assertThat(user.age).isEqualTo(17)
            assertThat(user.school).isEqualTo("Colégio legal do face")
            assertThat(user.youtubeChannel).isEqualTo("souOFace")
            assertThat(user.instagramProfile).isEqualTo("faceSchool")
            assertThat(user.description).isEqualTo("face estudando")
        }
        assertThat(userIds.size).isGreaterThan(2)
        userIds.forEach {
            assertThat(it).isEqualTo(lastUserId)
        }
    }


//    @Test
    fun multipleLogin_googleLoginPassingMethodParam_CreatesUserOrLogsIn() {
        val userIds = arrayListOf<String>()
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        val body = hashMapOf<String, Any>(
                "accessToken" to googleServerAuthCode,
                "method" to "google"
        )
        val entity = HttpEntity(body, headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.headers).containsKey("Authorization")
        assertThat(response.headers["Authorization"]!![0].length).isGreaterThan(10)

        val userId = jwtAuth.authentication(response.headers["Authorization"]!![0].toString())!!
        userIds.add(userId)

        assertThat(userRepo.findById(userId).get()).isNotNull()
    }

    @Test
    fun login_InvalidAccessToken_Unauthorized() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
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
        headers["API_KEY"] = apiKey
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
                "accessToken" to testSetup.facebookAccessToken
        )
        val entity = HttpEntity(body, headers)
        val response = restTemplate.exchange<String>(baseUrl, HttpMethod.POST, entity)

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        assertThat(response.headers).doesNotContainKey("Authorization")
    }
}