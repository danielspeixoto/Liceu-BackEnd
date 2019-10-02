package com.liceu.server.data.util.converters

import com.liceu.server.data.MongoDatabase
import com.liceu.server.domain.challenge.Challenge
import com.liceu.server.domain.trivia.PostComment
import com.liceu.server.domain.trivia.TriviaQuestion


fun toChallenge(answer: MongoDatabase.MongoChallenge): Challenge {
    return Challenge(
            answer.id.toString(),
            answer.challenger,
            answer.challenged,
            answer.answersChallenger,
            answer.answersChallenged,
            answer.scoreChallenger,
            answer.scoreChallenged,
            answer.triviaQuestionsUsed.map { toChallengeTrivia(it) },
            answer.submissionDate,
            answer.downloadChallenger,
            answer.downloadChallenged
    )
}

fun toChallengeTrivia(answer: MongoDatabase.MongoChallengeTrivia): TriviaQuestion {
    return TriviaQuestion(
            answer.id.toString(),
            answer.userId.toString(),
            answer.question,
            answer.correctAnswer,
            answer.wrongAnswer,
            answer.tags,
            answer.comments?.map {
                PostComment(
                        it.id.toHexString(),
                        it.userId.toHexString(),
                        it.author,
                        it.comment
                )
            },
            answer.likes,
            answer.dislikes
    )
}