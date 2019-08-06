package com.liceu.server.data

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.domain.user.UserForm
import org.springframework.security.oauth2.common.util.OAuth2Utils.CLIENT_ID
import java.util.*
import java.util.Collections.singletonList
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.liceu.server.domain.aggregates.Picture
import java.lang.Exception
import javax.naming.AuthenticationException


class GoogleAPI: UserBoundary.IAccessTokenGoogleResolver {

    override fun data(accessToken: String): UserForm {
        if(accessToken.isNullOrBlank()){
            throw AuthenticationException("access token empty")
        }
        try{
            val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(singletonList("156873606478-o6d7apmu90crek80omv9d44hop4j38gn.apps.googleusercontent.com"))
                    .build()

            val idToken = verifier.verify(accessToken)
            val payload = idToken!!.getPayload()
            val userId = payload.getSubject()
            val email = payload.getEmail()
            val emailVerified = java.lang.Boolean.valueOf(payload.getEmailVerified()!!)!!
            val name = payload.get("name") as String
            val pictureUrl = payload.get("picture") as String
            val givenName = payload.get("given_name") as String
            return UserForm(
                    name,
                    email,
                    Picture(
                            pictureUrl,
                            200,
                            200
                    ),
                    userId
            )
        }catch (e: AuthenticationException){
            throw AuthenticationException(e.message)
        }catch (e: Exception){
            throw e
        }

    }
}