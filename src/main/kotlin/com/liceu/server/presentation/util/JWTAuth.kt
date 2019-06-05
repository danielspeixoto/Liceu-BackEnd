package com.liceu.server.presentation.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JWTAuth {

    val EXPIRATION_TIME: Long = 860000000
    @Value("\${auth.secret}")
    lateinit var SECRET: String
    @Value("\${auth.prefix}")
    lateinit var TOKEN_PREFIX: String

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