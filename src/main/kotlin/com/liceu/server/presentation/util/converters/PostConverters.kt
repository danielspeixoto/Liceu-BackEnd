package com.liceu.server.presentation.util.converters

import com.liceu.server.domain.post.*
import java.util.*

data class PostResponse(
        val id: String,
        val userId: String,
        val type: String,
        val description: String,
        val image: FormattedImage?,
        val video: PostVideo?,
        val submissionDate: Date,
        val comments: List<PostComment>?,
        val questions: List<PostQuestions>?,
        val statusCode: String?
)

fun toPostResponse(post: Post): PostResponse {
    return PostResponse(
            post.id,
            post.userId,
            post.type,
            post.description,
            post.image,
            post.video,
            post.submissionDate,
            post.comments,
            post.questions,
            statusCodeManager(post.approvalFlag)
    )
}

fun statusCodeManager(approvalFlag: Boolean?): String {
    return if(approvalFlag == null){
        "inReview"
    } else {
        when (approvalFlag){
            false -> "denied"
            true -> "approved"
        }
    }
}