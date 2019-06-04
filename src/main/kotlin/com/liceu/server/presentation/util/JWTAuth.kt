package com.liceu.server.presentation.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.util.*

object JWTAuth {

    // EXPIRATION_TIME = 10 dias
    val EXPIRATION_TIME: Long = 860000000
    val SECRET = "1234567890123456789012345678901212345678901234567890123456789012"
    val TOKEN_PREFIX = "12345678901234567890123456789012"
    val HEADER_STRING = "Authorization"

    val SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256

    fun sign(username: String): String {
        val jwt = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(SECRET.toByteArray()), SIGNATURE_ALGORITHM)
                .setSubject(username)
                .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .compact()
        return "$TOKEN_PREFIX$jwt"
    }

    fun authentication(token: String): String? {
        val claim = Jwts.parser()
                .setSigningKey(SECRET.toByteArray())
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))

        if (!claim.header.getAlgorithm().equals(SIGNATURE_ALGORITHM.value)) {
            return null
        }
        val userId = claim.body.subject
        if (userId != null) {
            return userId
        }

        return null
    }

}