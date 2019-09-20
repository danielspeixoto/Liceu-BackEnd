package com.liceu.server.presentation.util.converters

import com.liceu.server.domain.post.Post
import com.liceu.server.domain.post.PostComment
import com.liceu.server.domain.post.PostQuestions
import com.liceu.server.domain.post.PostVideo
import java.util.*

data class PostResponse(
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

fun toPostResponse(post: Post): PostResponse {
    return PostResponse(
            post.id,
            post.userId,
            post.type,
            post.description,
            post.imageURL,
            post.video,
            post.submissionDate,
            post.comments,
            post.questions
    )
}