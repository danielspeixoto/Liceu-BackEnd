package com.liceu.server.presentation.util.converters

import com.liceu.server.data.MongoDatabase
import com.liceu.server.domain.post.*
import java.util.*

data class PostResponse(
        val id: String,
        val userId: String,
        val type: String,
        val description: String,
        val image: FormattedImage?,
        val video: PostVideo?,
        val multipleImages: List<FormattedImage>?,
        val submissionDate: Date,
        val comments: List<PostComment>?,
        val questions: List<PostQuestions>?
)

fun toPostResponse(post: Post): PostResponse {
    return PostResponse(
            post.id,
            post.userId,
            post.type,
            post.description,
            post.image,
            post.video,
            post.multipleImages,
            post.submissionDate,
            post.comments,
            post.questions
    )
}