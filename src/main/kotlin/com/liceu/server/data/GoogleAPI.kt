package com.liceu.server.data

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.liceu.server.domain.aggregates.Picture
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.domain.user.UserForm
import khttp.get
import java.util.*
import javax.naming.AuthenticationException

class GoogleAPI(
        val clientId: String,
        val clientSecret: String
) : UserBoundary.IAccessTokenGoogleResolver {

    override fun data(authCode: String): UserForm {
        if (authCode.isNullOrBlank()) {
            throw AuthenticationException("access token empty")
        }
        try {
            if (authCode.length > 150) {
                val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), JacksonFactory())
                        .setAudience(Collections.singletonList(clientId))
                        .build()
                val idToken = verifier.verify(authCode)
                val payload = idToken!!.payload
                val userId = payload.subject
                val email = payload.email
                val name = payload["name"] as String
                val pictureUrl = payload["picture"] as String
                return UserForm(
                        name,
                        email,
                        Picture(
                                pictureUrl,
                                200,
                                200
                        ),
                        userId,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )
            } else {
                val response = get("https://www.googleapis.com/oauth2/v3/userinfo", headers = mapOf(
                        "Authorization" to "Bearer $authCode"
                ))
                if(response.statusCode == 200) {
                    val data = response.jsonObject
                    return UserForm(
                            data["name"] as String,
                            data["email"] as String,
                            Picture(
                                    data["email"] as String,
                                    200,
                                    200
                            ),
                            data["sub"] as String,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                    )
                } else {
                    throw AuthenticationException(response.text)
                }
            }
        } catch (e: AuthenticationException) {
            throw AuthenticationException(e.message)
        } catch (e: Exception) {
            throw e
        }
    }
}