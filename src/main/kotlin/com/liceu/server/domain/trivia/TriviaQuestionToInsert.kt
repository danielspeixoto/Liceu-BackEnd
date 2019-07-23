package com.liceu.server.domain.trivia


data class TriviaQuestionToInsert(
        val userId: String,
        val question: String,
        val correctAnswer: String,
        val wrongAnswer: String,
        val tags: List<String>
    )

    data class TriviaQuestionSubmission(
        val userId: String,
        val question: String,
        val correctAnswer: String,
        val wrongAnswer: String,
        val tags: List<String>

    )

    data class TriviaQuestion(
        val id: String,
        val userId: String,
        val question: String,
        val correctAnswer: String,
        val wrongAnswer: String,
        val tags: List<String>

    )