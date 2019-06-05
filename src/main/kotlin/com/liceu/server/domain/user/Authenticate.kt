package com.liceu.server.domain.user

import com.liceu.server.data.FacebookAPI
import com.liceu.server.domain.global.*
import com.liceu.server.domain.question.Random
import com.liceu.server.util.Logging

class Authenticate(
        val userRepo: UserBoundary.IRepository,
        val facebookApi: UserBoundary.IAccessTokenResolver
): UserBoundary.IAuthenticate {

    companion object {
        const val EVENT_NAME = "login"
        val TAGS = listOf(AUTH)
    }

    override fun run(facebookAccessToken: String): String {
        try {
            val timeBefore = System.currentTimeMillis()
            val user = facebookApi.data(facebookAccessToken)
            val id = userRepo.save(user)
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "time" to System.currentTimeMillis() - timeBefore,
                    "userId" to id
            ))
            return id
        } catch (e: AuthenticationException) {
            Logging.error("oauth", listOf(AUTH, THIRD_PARTY), e, hashMapOf(
                    "accessToken" to facebookAccessToken
            ))
            throw e
        } catch (e: Exception) {
            Logging.error(EVENT_NAME, TAGS, e)
            throw e
        }
    }

}