package com.liceu.server.presentation

import com.liceu.server.domain.global.AUTH
import com.liceu.server.domain.global.AuthenticationException
import com.liceu.server.domain.global.DECRYPTION
import com.liceu.server.domain.global.NETWORK
import com.liceu.server.util.JWTAuth
import com.liceu.server.util.Logging
import com.liceu.server.util.NetworkUtils
import org.springframework.beans.factory.annotation.Autowired
import javax.servlet.http.HttpServletRequest

import javax.servlet.*
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletResponse

@WebFilter(urlPatterns = ["/v2/*"])
class JWTAuthenticationFilter : HttpFilter() {

    @Autowired
    lateinit var jwtAuth: JWTAuth
    @Autowired
    lateinit var netUtils: NetworkUtils

    companion object {
        const val HEADER_STRING = "Authorization"
    }

    val unprotected = listOf(
            "/v2/question"
    )

    override fun doFilter(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?) {
        request!!
        if ((request.servletPath == "/v2/login" && request.method == "POST") || unprotected.contains(request.servletPath)) {
            chain!!.doFilter(request, response)
            return
        }
        try {
            val token = request.getHeader(HEADER_STRING) ?: ""
            val beforeJWT = System.currentTimeMillis()
            val authentication = jwtAuth.authentication(token)
            val timeSpent = hashMapOf<String, Any>(
                    "time" to System.currentTimeMillis() - beforeJWT
            )
            if (authentication == null) {
                Logging.error(
                        "user_auth",
                        listOf(AUTH, NETWORK, DECRYPTION),
                        AuthenticationException("user sent invalid JWT"),
                        netUtils.networkData(request) + timeSpent
                )
                response!!.status = 401
                return
            } else {
                Logging.info("jwt_parsing", listOf(AUTH, DECRYPTION), timeSpent)
            }
            request.setAttribute("userId", authentication)
            chain!!.doFilter(request, response)
        } catch (e: Exception) {
            Logging.error(
                    "user_auth",
                    listOf(AUTH, NETWORK, DECRYPTION),
                    e,
                    netUtils.networkData(request)
            )
            response!!.status = 401
            return
        }

    }

}