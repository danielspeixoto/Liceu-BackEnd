package com.liceu.server.presentation.v2

import com.liceu.server.presentation.util.JWTAuth
import javax.servlet.http.HttpServletRequest

import javax.servlet.*
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletResponse

@WebFilter(urlPatterns = ["/v2/*"])
class JWTAuthenticationFilter : HttpFilter() {

    override fun doFilter(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?) {
        request!!
        if(request.servletPath == "/v2/login" && request.method == "POST") {
            chain!!.doFilter(request, response)
            return
        }
        val token = request.getHeader(JWTAuth.HEADER_STRING)
        val authentication = JWTAuth
                .authentication(token)

        if (authentication == null) {
            (response as HttpServletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED)
            return
        }
        request.setAttribute("userId", authentication)
        chain!!.doFilter(request, response)
    }

}