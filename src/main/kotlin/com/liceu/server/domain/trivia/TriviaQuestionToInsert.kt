package com.liceu.server.domain.trivia

import org.bson.types.ObjectId

data class TriviaQuestionToInsert(
        val userId: String,
        val question: String,
        val correctAnswer: String,
        val wrongAnswer: String
    )

    data class TriviaQuestionSubmission(
        val userId: String,
        val question: String,
        val correctAnswer: String,
        val wrongAnswer: String
    )

    data class TriviaQuestion(
        val userId: String,
        val question: String,
        val correctAnswer: String,
        val wrongAnswer: String
    )