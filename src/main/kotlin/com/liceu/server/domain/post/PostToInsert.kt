package com.liceu.server.domain.post

import java.util.*

data class PostSubmission (
        val userId: String,
        val type: String,
        val description: String,
        val imageURL: Base64?,
        val video: PostVideo?
)

data class PostToInsert(
        val userId: String,
        val type: String,
        val description: String,
        val imageURL: String?,
        val video: PostVideo?,
        val submissionDate: Date
)

data class Post(
        val id: String,
        val userId: String,
        val type: String,
        val description: String,
        val imageURL: String?,
        val video: PostVideo?,
        val submissionDate: Date
)


//data class PostImage(
//        val imageURL: String?,
//        val description: String?
//)

data class PostVideo(
        val videoUrl: String?,
        //val description: String?,
        var thumbnails: PostThumbnails?
)

data class PostThumbnails(
        var high: String?,
        var default: String?,
        var medium: String?
)

