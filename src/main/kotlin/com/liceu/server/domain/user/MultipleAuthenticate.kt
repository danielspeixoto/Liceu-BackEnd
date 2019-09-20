package com.liceu.server.domain.user

import com.liceu.server.domain.global.AUTH
import com.liceu.server.domain.global.AuthenticationException
import com.liceu.server.domain.global.THIRD_PARTY
import com.liceu.server.domain.util.user.logsUserAuthentication
import com.liceu.server.util.Logging

class MultipleAuthenticate(
        val userRepo: UserBoundary.IRepository,
        val facebookApi: UserBoundary.IAccessTokenResolver,
        val googleApi: UserBoundary.IAccessTokenGoogleResolver
) : UserBoundary.IMultipleAuthenticate {

    companion object {
        const val EVENT_NAME = "login"
        val TAGS = listOf(AUTH)
    }

    override fun run(accessToken: String, method: String): String {
        try {
            val timeBefore = System.currentTimeMillis()
            val user = if (method == "google") {
                googleApi.data(accessToken)
            } else {
                facebookApi.data(accessToken)
            }
            userRepo.getUserBySocialId(user.socialId)?.let {
                logsUserAuthentication(EVENT_NAME,TAGS,it.id,timeBefore)
                return it.id
            }
            val id = userRepo.save(user)
            logsUserAuthentication(EVENT_NAME,TAGS,id,timeBefore)
            return id

        } catch (e: AuthenticationException) {
            Logging.error("oauth", listOf(AUTH, THIRD_PARTY), e, hashMapOf(
                    "accessToken" to accessToken,
                    "method" to method
            ))
            throw e
        } catch (e: Exception) {
            Logging.error(EVENT_NAME, TAGS, e, hashMapOf(
                    "accessToken" to accessToken,
                    "method" to method
            ))
            throw e
        }
    }
}