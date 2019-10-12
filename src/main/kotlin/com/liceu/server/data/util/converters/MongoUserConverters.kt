package com.liceu.server.data.util.converters

import com.liceu.server.data.MongoDatabase
import com.liceu.server.domain.aggregates.Picture
import com.liceu.server.domain.user.User


fun toUser(mongoUser: MongoDatabase.MongoUser): User {
    return User(
            mongoUser.id.toHexString(),
            mongoUser.name,
            mongoUser.email,
            Picture(
                    mongoUser.picture.url,
                    mongoUser.picture.width,
                    mongoUser.picture.height
            ),
            mongoUser.location,
            mongoUser.state,
            mongoUser.school,
            mongoUser.age,
            mongoUser.youtubeChannel,
            mongoUser.instagramProfile,
            mongoUser.description,
            mongoUser.website,
            mongoUser.followers?.map { it.toHexString() },
            mongoUser.following?.map { it.toHexString() },
            mongoUser.fcmToken,
            mongoUser.lastAccess,
            mongoUser.desiredCourse,
            mongoUser.telephoneNumber,
            mongoUser.postsAutomaticApproval
    )
}
