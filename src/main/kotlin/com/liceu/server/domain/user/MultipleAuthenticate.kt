package com.liceu.server.domain.user

import com.liceu.server.domain.global.AUTH
import com.liceu.server.domain.global.AuthenticationException
import com.liceu.server.domain.global.THIRD_PARTY
import com.liceu.server.util.Logging

class MultipleAuthenticate(
        val userRepo: UserBoundary.IRepository,
        val facebookApi: UserBoundary.IAccessTokenResolver,
        val googleApi: UserBoundary.IAccessTokenGoogleResolver
): UserBoundary.IMultipleAuthenticate {

    companion object {
        const val EVENT_NAME = "login"
        val TAGS = listOf(AUTH)
    }

    override fun run(accessToken: String, method: String): String {
        try {
            lateinit var id: String
            val timeBefore = System.currentTimeMillis()
            id = if(method == "facebook"){
                val user = facebookApi.data(accessToken)
                userRepo.save(user)
            }else{
                val user = googleApi.data(accessToken)
                userRepo.save(user)
            }
            Logging.info(Authenticate.EVENT_NAME, Authenticate.TAGS, hashMapOf(
                    "time" to System.currentTimeMillis() - timeBefore,
                    "userId" to id
            ))
            return id
        }catch (e: AuthenticationException) {
            Logging.error("oauth", listOf(AUTH, THIRD_PARTY), e, hashMapOf(
                    "accessToken" to accessToken
            ))
            throw e
        } catch (e: Exception) {
            Logging.error(EVENT_NAME, TAGS, e)
            throw e
        }
    }
}