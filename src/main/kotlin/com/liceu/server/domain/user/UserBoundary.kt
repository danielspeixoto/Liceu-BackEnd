package com.liceu.server.domain.user

import com.liceu.server.domain.challenge.Challenge
import java.util.*


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
        fun getUserBySocialId(socialId: String): User?
        fun getChallengesFromUserById(userId: String, amount: Int, start: Int): List<Challenge>
        fun getUsersByNameUsingLocation(nameSearched: String, latitude: Double?, longitude: Double?, amount: Int): List<User>
        fun getActiveUser(userId: String): User
        fun updateLocationFromUser(userId: String,longitude: Double,latitude: Double, state: String): Long
        fun updateSchoolFromUser(userId: String, school: String): Long
        fun updateAgeFromUser(userId: String, age: Int): Long
        fun updateYoutubeChannelFromUser(userId: String, youtubeChannel: String): Long
        fun updateInstagramProfileFromUser(userId: String, instagramProfile: String): Long
        fun updateDescriptionFromUser(userId: String, description: String): Long
        fun updateWebsiteFromUser(userId: String, website: String): Long
        fun updateAddProducerToFollowingList(userId: String,producerId: String): Long
        fun updateRemoveProducerToFollowingList(userId: String,producerId: String): Long
        fun updateAddUserToProducerFollowerList(userId: String,producerId: String): Long
        fun updateRemoveUserToProducerFollowerList(userId: String,producerId: String): Long
        fun updateProfileImage(userId: String, imageURL: String): Long
        fun updateFcmTokenFromUser(userId: String, fcmToken: String): Long
        fun updateLastAccess(userId: String, loginAccess: Date): Long
        fun updateDesiredCourse(userId: String, course: String): Long
        fun updateTelephoneNumber(userId: String, telephoneNumber: String): Long
        fun updatePostsAutomaticApprovalFlag (userId: String): Long
        fun updateAddPostToBeSaved(userId: String, postId: String): Long
        fun updateRemovePostSaved(userId: String, postId: String): Long
        fun userExists(userId: String): Boolean
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

    interface IUpdateProducerToBeFollowed {
        fun run (userId: String, producerId: String)
    }

    interface IUpdateProducerToBeUnfollowed {
        fun run (userId: String, producerId: String)
    }

    interface IUpdateProfileImage {
        fun run (userId: String, imageData: String)
    }

    interface IUpdateFcmToken {
        fun run (userId: String, fcmToken: String)
    }

    interface IUpdateLastAccess {
        fun run (userId: String)
    }

    interface IUpdateCourse {
        fun run (userId: String,course: String)
    }

    interface IUpdateTelephoneNumber {
        fun run (userId: String,telephoneNumber: String)
    }

    interface IUpdatePostToBeSaved {
        fun run (userId: String,postId: String)
    }

    interface IUpdateSavedPostToBeRemoved {
        fun run (userId: String,postId: String)
    }

    interface IUserById {
        @Throws(Error::class)
        fun run(userId: String): User
    }

    interface IChallengesFromUserById{
        fun run(userId: String, start: Int): List<Challenge>
    }

    interface IGetUsersByNameUsingLocation {
        fun run(nameSearched: String, longitude: Double,latitude: Double, amount: Int): List<User>
    }

    interface IMultipleAuthenticate {
        fun run(accessToken: String, method: String): String
    }

    @Deprecated("To be removed in next update")
    interface IAuthenticate {
        fun run(facebookAccessToken: String): String
    }
}