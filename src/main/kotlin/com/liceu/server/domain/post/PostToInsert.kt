package com.liceu.server.domain.post

import org.bson.types.ObjectId
import java.util.*

data class PostSubmission (
        val userId: String,
        val type: String,
        val description: String,
        val imageURL: Base64?,
        val video: PostVideo?,
        val questions: List<PostQuestions>?
)

data class PostToInsert(
        val userId: String,
        val type: String,
        val description: String,
        val imageURL: String?,
        val video: PostVideo?,
        val submissionDate: Date,
        val comments: List<PostComment>?,
        val questions: List<PostQuestions>?
)

data class Post(
        val id: String,
        val userId: String,
        val type: String,
        val description: String,
        val imageURL: String?,
        val video: PostVideo?,
        val submissionDate: Date,
        val comments: List<PostComment>?,
        val questions: List<PostQuestions>?
)

data class PostComment (
        var id: ObjectId,
        var userId: ObjectId,
        var author: String,
        var comment: String
)

data class PostVideo(
        val videoUrl: String?,
        var thumbnails: PostThumbnails?
)

data class PostThumbnails(
        var high: String?,
        var default: String?,
        var medium: String?
)

data class PostQuestions(
        var question: String,
        var correctAnswer: String,
        var otherAnswers: List<String>
)

