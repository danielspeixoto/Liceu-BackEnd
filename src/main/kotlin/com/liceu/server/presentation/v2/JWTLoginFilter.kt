package com.liceu.server.presentation.v2

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTLoginFilter constructor(url: String, authManager: AuthenticationManager) : AbstractAuthenticationProcessingFilter(AntPathRequestMatcher(url)) {

    init {
        authenticationManager = authManager
    }

    @Throws(AuthenticationException::class, IOException::class, ServletException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {

        println("login ")
        println()
        println()

        val username = request.getParameter("username")
        val password = request.getParameter("password")
        val a = UsernamePasswordAuthenticationToken("admin", "password")

        println("login 2")
        println(a.isAuthenticated)
        println(a.credentials)
        println(a.details)
        println(a.principal)
        println()
        return authenticationManager.authenticate(a)
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain?,
            auth: Authentication) {

        println("successful auth")
        println(auth.name)
        println()
        TokenAuthenticationService.addAuthentication(response, auth.name)
    }

}