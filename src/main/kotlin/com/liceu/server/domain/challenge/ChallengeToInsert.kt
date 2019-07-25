package com.liceu.server.domain.challenge

import com.liceu.server.domain.trivia.TriviaQuestion


data class ChallengeToInsert (
        val challenger: String,
        val challenged: String?,
        val answersChallenger: List<String>,
        val answersChallenged: List<String>,
        val scoreChallenger: Int?,
        val scoreChallenged: Int?,
        val triviaQuestionsUsed: List<TriviaQuestion>
)

data class Challenge(
        val id: String,
        val challenger: String,
        val challenged: String?,
        val answersChallenger: List<String>,
        val answersChallenged: List<String>,
        val scoreChallenger: Int?,
        val scoreChallenged: Int?,
        val triviaQuestionsUsed: List<TriviaQuestion>
)
