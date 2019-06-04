package com.liceu.server.presentation.v2

import java.io.IOException

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

class JWTAuthenticationFilter : GenericFilterBean() {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) {

        val authentication = TokenAuthenticationService
                .getAuthentication(request as HttpServletRequest)



        println("auth")
        println(authentication)
//        println(request.)
        println()


        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)
    }

}