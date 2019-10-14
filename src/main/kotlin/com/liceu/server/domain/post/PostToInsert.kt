package com.liceu.server.domain.post

import java.util.*

data class PostSubmission (
        val userId: String,
        val type: String,
        val description: String,
        val image: PostImage?,
        val video: PostVideo?,
        val multipleImages: List<PostImage>?,
        val questions: List<PostQuestions>?
)

data class PostToInsert(
        val userId: String,
        val type: String,
        val description: String,
        val image: FormattedImage?,
        val video: PostVideo?,
        val multipleImages: List<FormattedImage>?,
        val submissionDate: Date,
        val comments: List<PostComment>?,
        val questions: List<PostQuestions>?,
        val approvalFlag: Boolean?
)

data class Post(
        val id: String,
        val userId: String,
        val type: String,
        val description: String,
        val image: FormattedImage?,
        val video: PostVideo?,
        val multipleImages: List<FormattedImage>?,
        val submissionDate: Date,
        val comments: List<PostComment>?,
        val questions: List<PostQuestions>?,
        val document: PostDocument?,
        val approvalFlag: Boolean?,
        val likes: Int?
)


data class PostComment (
        var id: String,
        var userId: String,
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

data class PostImage(
        var title: String?,
        var type: String?,
        val imageData: String?
)

data class FormattedImage(
        var title: String?,
        var type: String?,
        val imageData: String?
)

data class PostDocument(
        var id: String?,
        val title: String?,
        val type: String?,
        val documentURL: String?
)


