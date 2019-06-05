package com.liceu.server.domain.user

class UserBoundary {

    interface IAccessTokenResolver {
        fun data(accessToken: String): UserForm
    }

    interface IRepository {
        fun save(user: UserForm): String
    }

    interface IAuthenticate {

        fun run(facebookAccessToken: String): String

    }
}