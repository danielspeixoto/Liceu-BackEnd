package com.liceu.server.presentation.converters

import com.liceu.server.domain.game.Answer
import com.liceu.server.domain.game.Game

data class GameResponse(
        val id: String,
        val userId: String,
        val answers: List<AnswerResponse>,
        val submissionDate: String,
        val timeSpent: Int
)

data class AnswerResponse(
        val questionId: String,
        val correctAnswer: Int,
        val selectedAnswer: Int
)


object GameConverters {

    fun toGameResponse(game: Game): GameResponse {
        return GameResponse (
                game.id,
                game.userId,
                game.answers.map { toAnswerResponse(it) },
                game.submissionDate.toString(),
                game.timeSpent
        )
    }

    fun toAnswerResponse(answer: Answer): AnswerResponse {
        return AnswerResponse(
                answer.questionId,
                answer.correctAnswer,
                answer.selectedAnswer
        )
    }


}