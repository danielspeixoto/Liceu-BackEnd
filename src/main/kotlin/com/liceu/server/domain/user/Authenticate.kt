package com.liceu.server.domain.user

class Authenticate(
        val userRepo: UserBoundary.IRepository
): UserBoundary.IAuthenticate {

    override fun run(facebookAccessToken: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}