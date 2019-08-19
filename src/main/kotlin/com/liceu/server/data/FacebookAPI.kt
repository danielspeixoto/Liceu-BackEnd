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

    val toChange = hashMapOf<String,String>(
            "\\u00e1" to "á",
            "\\u00e9" to "é",
            "\\u00ed" to "í",
            "\\u00f3" to "ó",
            "\\u00fa" to "ú",

            "\\u00c1" to "Á",
            "\\u00c9" to "É",
            "\\u00cd" to "Í",
            "\\u00d3" to "Ó",
            "\\u00da" to "Ú",

            "\\u00e2" to "â",
            "\\u00ea" to "ê",
            "\\u00ee" to "î",
            "\\u00f4" to "ô",
            "\\u00fb" to "û",

            "\\u00c2" to "Â",
            "\\u00ca" to "Ê",
            "\\u00ce" to "Î",
            "\\u00d4" to "Ô",
            "\\u00db" to "Û",

            "\\u00e3" to "ã",
            "\\u1ebd" to "ẽ",
            "\\u0129" to "ĩ",
            "\\u00f5" to "õ",
            "\\u0169" to "ũ",

            "\\u00c3" to "Ã",
            "\\u00d5" to "Õ",

            "\\u00e7" to "ç",
            "\\u00c7" to "Ç"
    )

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
            var name = userMap["name"].toString().substring(1).dropLast(1)
            toChange.forEach{
                name = name.replace(it.key, it.value)
            }
            return UserForm(
                    name,
                    email,
                    Picture(
                            pictureData["url"].asString(),
                            pictureData["width"].asInt(),
                            pictureData["height"].asInt()
                            ),
                    userMap["id"].toString().substring(1).dropLast(1),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
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