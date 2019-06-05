package com.liceu.server.presentation.v2

import com.liceu.server.domain.global.*
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.presentation.util.JWTAuth
import com.liceu.server.presentation.util.networkData
import com.liceu.server.presentation.v1.Response
import com.liceu.server.util.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.Exception
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/v2/login")
class LoginController(
        @Autowired val authUser: UserBoundary.IAuthenticate
) {

    @Autowired
    lateinit var jwtAuth: JWTAuth

    @PostMapping
    fun login(
            @RequestBody body: HashMap<String, String>,
            request: HttpServletRequest
    ): ResponseEntity<Response<Void>> {
        val eventName = "login_post"
        val eventTags = listOf(NETWORK, AUTH)
        val networkData = networkData(request)
        Logging.info(eventName, eventTags, data = networkData)

        val accessToken = body["accessToken"] ?: ""
        return try {
            val userId = authUser.run(accessToken)
            val headers = HttpHeaders()
            val beforeJWT = System.currentTimeMillis()
            headers[JWTAuthenticationFilter.HEADER_STRING] = jwtAuth.sign(userId)
            val timeSpent = hashMapOf<String, Any>(
                    "time" to System.currentTimeMillis() - beforeJWT
            )
            Logging.info("jwt_sign", listOf(AUTH, ENCRYPTION), timeSpent)
            ResponseEntity(Response(), headers, HttpStatus.OK)
        } catch (e: Exception) {
            Logging.error(
                    eventName,
                    eventTags,
                    e, data = networkData
            )
            ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
    }
}