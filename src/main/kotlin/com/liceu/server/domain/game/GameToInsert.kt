package com.liceu.server.domain.game

import java.util.*

data class GameSubmission(
        val userId: String,
        val answers: List<Answer>
)

data class Answer(
        val questionId: String,
        val correctAnswer: Int,
        val selectedAnswer: Int
) {
    override fun equals(other: Any?): Boolean {
        return other is Answer &&
                other.questionId == questionId &&
                other.correctAnswer == correctAnswer &&
                other.selectedAnswer == selectedAnswer
    }
}

data class GameToInsert(
        val userId: String,
        val answers: List<Answer>,
        val submissionDate: Date
) {

    override fun equals(other: Any?): Boolean {
        return other is GameToInsert &&
                other.userId == userId &&
                other.answers.toTypedArray().contentEquals(answers.toTypedArray()) &&
                other.submissionDate == submissionDate
    }

}

data class Game(
        val id: String,
        val userId: String,
        val answers: List<Answer>,
        val submissionDate: Date
) {
    override fun equals(other: Any?): Boolean {
        return other is Game &&
                other.id == id &&
                other.userId == userId &&
                other.answers.toTypedArray().contentEquals(answers.toTypedArray()) &&
                other.submissionDate == submissionDate
    }
}