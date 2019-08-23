//package com.liceu.server.system.v2
//
//import com.google.common.truth.Truth.assertThat
//import com.liceu.server.data.UserRepository
//import com.liceu.server.util.JWTAuth
//import com.liceu.server.system.TestSystem
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.web.client.exchange
//import org.springframework.http.HttpEntity
//import org.springframework.http.HttpHeaders
//import org.springframework.http.HttpMethod
//import org.springframework.http.HttpStatus
//
//class TestLogin : TestSystem("/v2/login") {
//
//    @Autowired
//    lateinit var userRepo: UserRepository
//    @Autowired
//    lateinit var jwtAuth: JWTAuth
//
//    val facebookAccessToken = "EAAf4pgUyFpsBAPqmyAC2kZBpOOEDZCz1EQlV5svVLuxyOCGlhFVcbOBMdkfu6i4Fxuq9LAA51gnsBv2ZALVZADl2gPZAVNhwmsftjVuId1BqcAufkrKDeKhuGAQauboYUlKUOR1BTyYDjkvcho7ZAAgADZAYrlLkJHszaxZBPFWE7QDzL1EzAzpKtHO9K9fnwo7SWMCuakQw1V81zZBHZAAzgh"
//    val googleServerAuthCode = "4/nQF47iLi0DqRPU39i-TGqNgEwp9tHcTk1Nlym51k7E2zuHhroqQZY3c7JHUfFK_TPOHjwFCS1NqNqPcv4kAn128"
//
//    @Test
//    fun login_ValidAccessToken_CreatesUserOrLogsIn() {
//        val userIds = arrayListOf<String>()
//        var lastUserId = "lastUserId"
//        for(i in 1..3) {
//            val headers = HttpHeaders()
//            headers["API_KEY"] = "apikey"
//
//            val body = hashMapOf<String, Any>(
//                    "accessToken" to facebookAccessToken
//            )
//            val entity = HttpEntity(body, headers)
//            val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
//
//            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
//            assertThat(response.headers).containsKey("Authorization")
//            assertThat(response.headers["Authorization"]!![0].length).isGreaterThan(10)
//
//            val userId = jwtAuth.authentication(response.headers["Authorization"]!![0].toString())!!
//            userIds.add(userId)
//            lastUserId = userId
//
//            assertThat(userRepo.findById(userId).get()).isNotNull()
//        }
//        assertThat(userIds.size).isGreaterThan(2)
//        userIds.forEach {
//            assertThat(it).isEqualTo(lastUserId)
//        }
//    }
//
//    @Test
//    fun multipleLogin_facebookLoginPassingMethodParam_CreatesUserOrLogsIn() {
//        val userIds = arrayListOf<String>()
//        var lastUserId = "lastUserId"
//        for(i in 1..3) {
//            val headers = HttpHeaders()
//            headers["API_KEY"] = "apikey"
//
//            val body = hashMapOf<String, Any>(
//                    "accessToken" to facebookAccessToken,
//                    "method" to "facebook"
//            )
//            val entity = HttpEntity(body, headers)
//            val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
//
//            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
//            assertThat(response.headers).containsKey("Authorization")
//            assertThat(response.headers["Authorization"]!![0].length).isGreaterThan(10)
//
//            val userId = jwtAuth.authentication(response.headers["Authorization"]!![0].toString())!!
//            userIds.add(userId)
//            lastUserId = userId
//
//            assertThat(userRepo.findById(userId).get()).isNotNull()
//        }
//        assertThat(userIds.size).isGreaterThan(2)
//        userIds.forEach {
//            assertThat(it).isEqualTo(lastUserId)
//        }
//    }
//
//    @Test
//    fun multipleLogin_googleLoginPassingMethodParam_CreatesUserOrLogsIn() {
//        val userIds = arrayListOf<String>()
//        val headers = HttpHeaders()
//        headers["API_KEY"] = "apikey"
//
//        val body = hashMapOf<String, Any>(
//                "accessToken" to googleServerAuthCode,
//                "method" to "google"
//        )
//        val entity = HttpEntity(body, headers)
//        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
//
//        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
//        assertThat(response.headers).containsKey("Authorization")
//        assertThat(response.headers["Authorization"]!![0].length).isGreaterThan(10)
//
//        val userId = jwtAuth.authentication(response.headers["Authorization"]!![0].toString())!!
//        userIds.add(userId)
//
//        assertThat(userRepo.findById(userId).get()).isNotNull()
//    }
//
//    @Test
//    fun login_InvalidAccessToken_Unauthorized() {
//        val headers = HttpHeaders()
//        headers["API_KEY"] = "apikey"
//
//        val body = hashMapOf<String, Any>(
//                "accessToken" to "invalid"
//        )
//        val entity = HttpEntity(body, headers)
//        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
//
//        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
//        assertThat(response.headers).doesNotContainKey("Authorization")
//    }
//
//    @Test
//    fun login_NoAccessToken_Unauthorized() {
//        val headers = HttpHeaders()
//        headers["API_KEY"] = "apikey"
//
//        val body = hashMapOf<String, Any>()
//        val entity = HttpEntity(body, headers)
//        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
//
//        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
//        assertThat(response.headers).doesNotContainKey("Authorization")
//    }
//
//    @Test
//    fun login_InvalidApiKey_Unauthorized() {
//        val headers = HttpHeaders()
//        headers["API_KEY"] = "wrongApiKey"
//
//        val body = hashMapOf<String, Any>(
//                "accessToken" to facebookAccessToken
//        )
//        val entity = HttpEntity(body, headers)
//        val response = restTemplate.exchange<String>(baseUrl, HttpMethod.POST, entity)
//
//        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
//        assertThat(response.headers).doesNotContainKey("Authorization")
//    }
//}