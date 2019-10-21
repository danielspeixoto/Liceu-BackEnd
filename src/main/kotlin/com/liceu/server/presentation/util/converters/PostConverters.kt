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
        val questions: List<PostQuestions>?,
        val documents: List<PostDocument>?,
        val statusCode: String?,
        val likes: Int?
)

fun toPostResponse(post: Post): PostResponse {
    return PostResponse(
            post.id,
            post.userId,
            postTypeManager(post.type),
            post.description,
            post.image,
            post.video,
            post.multipleImages,
            post.submissionDate,
            commentsManager(post.comments),
            post.questions,
            post.documents,
            statusCodeManager(post.approvalFlag),
            likesManager(post.likes)
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

fun postTypeManager(type: String) : String {
    if(type == "multipleImages"){
        return "image"
    }
    return type
}

fun commentsManager(comments: List<PostComment>?): List<PostComment> {
    if(comments == null){
        return emptyList()
    }
    return comments
}

fun likesManager(likes: Int?): Int {
    if(likes == null){
        return 0
    }
    return likes
}