package com.liceu.server.presentation.util.converters

import com.liceu.server.domain.trivia.PostComment
import com.liceu.server.domain.trivia.TriviaQuestion

data class TriviaQuestionResponse(
        val id: String,
        val userId: String,
        val question: String,
        val correctAnswer: String,
        val wrongAnswer: String,
        val tags: List<String>,
        val comments: List<PostComment>?,
        val likes: Int?,
        val dislikes: Int?
)

fun toTriviaQuestionResponse(triviaQuestion: TriviaQuestion): TriviaQuestionResponse{
    return TriviaQuestionResponse(
            triviaQuestion.id,
            triviaQuestion.userId,
            triviaQuestion.question,
            triviaQuestion.correctAnswer,
            triviaQuestion.wrongAnswer,
            triviaQuestion.tags,
            triviaQuestion.comments,
            triviaQuestion.likes,
            triviaQuestion.dislikes
    )
}