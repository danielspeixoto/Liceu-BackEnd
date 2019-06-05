package com.liceu.server.domain.user

import com.liceu.server.data.FacebookAPI

class Authenticate(
        val userRepo: UserBoundary.IRepository,
        val facebookApi: FacebookAPI
): UserBoundary.IAuthenticate {

    override fun run(facebookAccessToken: String): String {
        try {
            val user = facebookApi.data(facebookAccessToken)
            val id = userRepo.save(user)
        } catch (e: Exception) {

        }
    }

}