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
        fun updateAgeFromUser(userId: String, age: Int): Long
        fun updateYoutubeChannelFromUser(userId: String, youtubeChannel: String): Long
        fun updateInstagramProfileFromUser(userId: String, instagramProfile: String): Long
        fun updateDescriptionFromUser(userId: String, description: String): Long
        fun updateWebsiteFromUser(userId: String, website: String): Long
    }

    interface IUpdateAge {
        fun run (userId: String, day: Int,month: Int, year: Int)
    }

    interface IUpdateYoutubeChannel {
        fun run (userId: String, youtubeChannel: String)
    }

    interface IUpdateInstagramProfile {
        fun run (userId: String, instagramProfile: String)
    }

    interface IUpdateDescription {
        fun run (userId: String, description: String)
    }

    interface IUpdateWebsite {
        fun run(userId: String, website: String)
    }

    interface IUpdateSchool {
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