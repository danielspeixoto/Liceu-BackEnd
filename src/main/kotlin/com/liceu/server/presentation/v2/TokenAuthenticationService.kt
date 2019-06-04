package com.liceu.server.presentation.v2

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.security.core.Authentication

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import java.lang.Exception
import java.util.*

object TokenAuthenticationService {

    // EXPIRATION_TIME = 10 dias
    val EXPIRATION_TIME: Long = 860000000
    val SECRET = "1234567890123456789012345678901212345678901234567890123456789012"
    val TOKEN_PREFIX = "12345678901234567890123456789012"
    val HEADER_STRING = "Authorization"

    val SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256

    internal fun addAuthentication(response: HttpServletResponse, username: String) {
        println("add auth")
        val jwt = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(SECRET.toByteArray()), SIGNATURE_ALGORITHM)
                .setSubject(username)
                .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .compact()


        println(jwt)
        println()

        response.addHeader(HEADER_STRING, "$jwt")
    }

    internal fun getAuthentication(request: HttpServletRequest): Authentication? {
        val token = request.getHeader(HEADER_STRING)
        println("get auth")
        println(token)
        println()
        if (token != null) {
            val claim = Jwts.parser()
                    .setSigningKey(SECRET.toByteArray())
                    .parseClaimsJws(token)

            if(!claim.header.getAlgorithm().equals(SIGNATURE_ALGORITHM.value)) {
               throw Exception()
            }

            val userId = claim.body.subject

            println(userId)

            if (userId != null) {
                return UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList())
            }
        }
        return null
    }

}