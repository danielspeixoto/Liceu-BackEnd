package com.liceu.server.domain.post

import com.liceu.server.data.MongoDatabase
import org.bson.types.ObjectId
import java.util.*

data class PostSubmission (
        val userId: String,
        val type: String,
        val text: String?,
        val image: MongoPostImage?,
        val video: MongoPostVideo?
)

data class PostToInsert(
        val userId: String,
        val text: String?,
        val type: String,
        val image: MongoPostImage?,
        val video: MongoPostVideo?,
        val submissionDate: Date
)


data class MongoPostImage(
        val imageURL: String,
        val description: String
)

data class MongoPostVideo(
        val videoUrl: String,
        val description: String,
        var thumbnails: MongoDatabase.Thumbnails
)

