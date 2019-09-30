package com.liceu.server.util

import khttp.get
import org.json.JSONArray
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class FacebookTestUsers {
    @Value("\${facebook.accessToken}")
    lateinit var facebookAccessToken: String
    val hasAccess: Boolean by lazy {
        facebookAccessToken.length > 10
    }
    val userAccessToken: String by lazy {
        if (facebookAccessToken.length > 10) {
            val facebookTestUsers = get("https://graph.facebook.com/v4.0/2243716772402843/accounts/test-users?access_token=$facebookAccessToken")
                    .jsonObject["data"] as JSONArray
            for (i in 0..facebookTestUsers.length()) {
                val it = facebookTestUsers.getJSONObject(i)
                if (it["id"] == "115992013112781") {
                    return@lazy it["access_token"] as String
                }
            }
        }
        ""
    }
}