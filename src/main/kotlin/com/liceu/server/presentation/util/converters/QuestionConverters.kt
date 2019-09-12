package com.liceu.server.presentation.util.converters

import com.liceu.server.domain.question.Question
import com.liceu.server.domain.video.Video


data class QuestionResponse(
        val id: String,
        val view: String,
        val source: String,
        val variant: String,
        val edition: Int,
        val number: Int,
        val domain: String,
        val answer: Int,
        val tags: List<String>,
        val stage: Int,
        val width: Int,
        val height: Int
)


fun toQuestionResponse(question: Question): QuestionResponse {
    return QuestionResponse(
            question.id,
            question.imageURL,
            question.source,
            question.variant,
            question.edition,
            question.number,
            question.domain,
            question.answer,
            question.tags,
            question.stage,
            question.width,
            question.height
    )
}

fun toVideoResponse(video: Video): Map<String, Any> {
    return hashMapOf<String, Any>(
            "id" to video.id,
            "title" to video.title,
            "description" to video.description,
            "videoId" to video.videoId,
            "questionId" to video.questionId,
            "aspectRatio" to video.aspectRatio,
            "thumbnails" to hashMapOf(
                    "default" to video.thumbnail
            ),
            "channel" to hashMapOf(
                    "title" to video.channelTitle
            )
    )
}