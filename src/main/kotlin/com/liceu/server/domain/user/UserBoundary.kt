package com.liceu.server.domain.user

import com.liceu.server.domain.challenge.Challenge


class UserBoundary {

    interface IAccessTokenResolver {
        fun data(accessToken: String): UserForm
    }

    interface IAccessTokenGoogleResolver{
        fun data(accessToken: String): UserForm
    }

    interface IRepository {
        fun save(user: UserForm): String
        fun getUserById(userId: String): User
        fun getChallengesFromUserById(userId: String): List<Challenge>
    }

    interface IUserById {
        @Throws(Error::class)
        fun run(userId: String): User
    }

    interface IChallengesFromUserById{
        fun run(userId: String): List<Challenge>
    }

    interface IMultipleAuthenticate {
        fun run(accessToken: String, method: String): String
    }

    @Deprecated("To be removed in next update")
    interface IAuthenticate {
        fun run(facebookAccessToken: String): String
    }
}