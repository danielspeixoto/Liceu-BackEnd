package com.liceu.server.presentation.v2

import org.springframework.beans.factory.annotation.Value
import javax.servlet.FilterChain
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebFilter(urlPatterns = ["/v2/*"])
class ApiKeyAuthorizationFilter : HttpFilter() {

    companion object {
        const val HEADER_API_KEY = "API_KEY"
    }

    @Value("\${client.api_key}")
    lateinit var apiKey: String

    override fun doFilter(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?) {
        request!!
        val key = request.getHeader(HEADER_API_KEY)
        if (key != apiKey) {
            response!!.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            return
        }
        chain!!.doFilter(request, response)
    }


}