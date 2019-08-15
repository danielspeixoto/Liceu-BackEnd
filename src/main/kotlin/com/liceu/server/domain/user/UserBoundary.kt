package com.liceu.server.domain.user

import com.liceu.server.domain.challenge.Challenge
import com.mongodb.client.model.geojson.GeoJsonObjectType


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
        fun updateLocationFromUser(userId: String,longitude: Double,latitude: Double): Long
        fun updateSchoolFromUser(userId: String, school: String): Long
    }

    interface IUpdateSchool{
        fun run (userId: String, school: String)
    }

    interface IUpdateLocation {
        fun run (userId: String,longitude: Double,latitude: Double)
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