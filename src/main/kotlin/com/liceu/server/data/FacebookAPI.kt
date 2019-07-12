package com.liceu.server.data

import com.liceu.server.domain.aggregates.Picture
import com.liceu.server.domain.global.AUTH
import com.liceu.server.domain.global.THIRD_PARTY
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.domain.user.UserForm
import com.liceu.server.util.Logging
import com.restfb.DefaultFacebookClient
import com.restfb.Parameter
import com.restfb.Version
import com.restfb.exception.FacebookOAuthException
import javax.naming.AuthenticationException
import com.restfb.json.JsonObject
import java.lang.Exception

class FacebookAPI : UserBoundary.IAccessTokenResolver {

    override fun data(accessToken: String): UserForm {
        if (accessToken == "") {
            throw AuthenticationException("access token empty")
        }
        try {
            val client = DefaultFacebookClient(accessToken, Version.VERSION_3_3)
            val userMap = client.fetchObject(
                    "me", JsonObject::class.java,
                    Parameter.with("fields", "id,name,email"))

            val pictureData = client.fetchObject("me/picture",
                    JsonObject::class.java,
                    Parameter.with("redirect", "false"),
                    Parameter.with("type", "large"))["data"] as JsonObject
            var email = ""
            if(userMap.contains("email")) {
                email = userMap["email"].toString().substring(1).dropLast(1)
            }
            return UserForm(
                    userMap["name"].toString().substring(1).dropLast(1),
                    email,
                    Picture(
                            pictureData["url"].asString(),
                            pictureData["width"].asInt(),
                            pictureData["height"].asInt()
                            ),
                    userMap["id"].toString().substring(1).dropLast(1)
            )
        } catch (e: FacebookOAuthException) {
            throw AuthenticationException(e.message)
        } catch (e: javax.naming.AuthenticationException) {
            throw AuthenticationException(e.message)
        } catch (e: Exception) {
            throw e
        }

    }
}