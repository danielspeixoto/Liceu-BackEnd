package com.liceu.server.presentation.v2

import com.liceu.server.domain.global.*
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.presentation.JWTAuthenticationFilter
import com.liceu.server.util.JWTAuth
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.Exception
import javax.servlet.http.HttpServletRequest

@Deprecated("Use LoginControllerV2 instead")
class LoginController(
        @Autowired val authUser: UserBoundary.IAuthenticate
) {

    @Autowired
    lateinit var jwtAuth: JWTAuth

    @Autowired
    lateinit var netUtils: NetworkUtils

    @PostMapping
    fun login(
            @RequestBody body: HashMap<String, String>,
            request: HttpServletRequest
    ): ResponseEntity<Void> {
        val eventName = "login_post"
        val eventTags = listOf(CONTROLLER, NETWORK, AUTH)
        val networkData = netUtils.networkData(request)
        Logging.info(eventName, eventTags, data = networkData + hashMapOf<String, Any>(
                "version" to 2
        ))

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
            ResponseEntity(null, headers, HttpStatus.OK)
        } catch (e: Exception) {
//            TODO Check for another types of exceptions for custom handling
            Logging.error(
                    eventName,
                    eventTags,
                    e, data = networkData
            )
            ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
    }
}