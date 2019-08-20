package com.liceu.server.data

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.liceu.server.domain.aggregates.Picture
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.domain.user.UserForm
import org.springframework.beans.factory.annotation.Value
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

            val tokenResponse = GoogleAuthorizationCodeTokenRequest(
                    NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    "https://oauth2.googleapis.com/token",
                    clientId,
                    clientSecret,
                    authCode,
                    "")  // Specify the same redirect URI that you use with your web
                    // app. If you don't have a web version of your app, you can
                    // specify an empty string.
                    .execute()

            val idToken = tokenResponse.parseIdToken()
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
                    null
            )
        } catch (e: AuthenticationException) {
            throw AuthenticationException(e.message)
        } catch (e: Exception) {
            throw e
        }

    }
}