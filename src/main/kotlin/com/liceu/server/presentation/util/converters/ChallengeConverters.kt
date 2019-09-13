package com.liceu.server.presentation.util.converters

import com.liceu.server.domain.challenge.Challenge
import com.liceu.server.domain.trivia.TriviaQuestion


data class ChallengeResponse(
        val id: String,
        val challenger: String,
        val challenged: String?,
        val answersChallenger: List<String>,
        val answersChallenged: List<String>,
        val scoreChallenger: Int?,
        val scoreChallenged: Int?,
        val triviaQuestionsUsed: List<TriviaQuestion>
)


fun toChallengeResponse(challenge: Challenge): ChallengeResponse {
    return ChallengeResponse(
            challenge.id,
            challenge.challenger,
            challenge.challenged,
            challenge.answersChallenger,
            challenge.answersChallenged,
            challenge.scoreChallenger,
            challenge.scoreChallenged,
            challenge.triviaQuestionsUsed
    )
}
