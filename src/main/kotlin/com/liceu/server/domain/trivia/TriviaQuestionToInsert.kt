package com.liceu.server.domain.trivia

    data class TriviaQuestionToInsert(
        val question: String,
        val correctAnswer: String,
        val wrongAnswer: String
    )

    data class TriviaQuestionSubmission(
        val question: String,
        val correctAnswer: String,
        val wrongAnswer: String
    )

    data class TriviaQuestion(
        val question: String,
        val correctAnswer: String,
        val wrongAnswer: String
    )