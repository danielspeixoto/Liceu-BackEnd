package com.liceu.server.domain.user


class UserBoundary {

    interface IAccessTokenResolver {
        fun data(accessToken: String): UserForm
    }

    interface IRepository {
        fun save(user: UserForm): String
        fun getUserById(userId: String): User
    }

    interface IUserById {

        @Throws(Error::class)
        fun run(userId: String): User

    }

    interface IAuthenticate {

        fun run(facebookAccessToken: String): String

    }
}