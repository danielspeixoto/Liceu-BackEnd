package com.liceu.server.presentation.util.converters

import com.liceu.server.domain.aggregates.Picture
import com.liceu.server.domain.challenge.Challenge
import com.liceu.server.domain.trivia.TriviaQuestion
import com.liceu.server.domain.user.User


    data class UserResponse(
            val id: String,
            val name: String,
            val email: String,
            val picture: Picture,
            val state: String?,
            val school: String?,
            val age: Int?,
            val youtubeChannel: String?,
            val instagramProfile: String?,
            val description: String?,
            val website: String?,
            val amountOfFollowers: Int,
            val amountOfFollowing: Int,
            val following: Boolean
    )


    fun toUserResponse(user: User, id: String): UserResponse {
        var amountOfFollowers = 0
        var isFollowing = false
        if(user.followers != null) {
            amountOfFollowers = user.followers.size
            isFollowing = user.followers.contains(id)
        }
        var amountOfFollowing = 0
        if(user.following != null) {
            amountOfFollowing = user.following.size
        }


        return UserResponse (
                user.id,
                user.name,
                user.email,
                user.picture,
                user.state,
                user.school,
                user.age,
                user.youtubeChannel,
                user.instagramProfile,
                user.description,
                user.website,
                amountOfFollowers,
                amountOfFollowing,
                isFollowing
        )
    }
