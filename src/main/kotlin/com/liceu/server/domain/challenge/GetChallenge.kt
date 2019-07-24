package com.liceu.server.domain.challenge

import com.liceu.server.data.MongoChallengeRepository
import com.liceu.server.data.MongoTriviaRepository

class GetChallenge(
        private val challengeRepository: MongoChallengeRepository,
        private val triviaRepository: MongoTriviaRepository
) : ChallengeBoundary.IGetChallenge {

    override fun run(userId: String): Challenge {
        challengeRepository.matchMaking(userId)?.let {
            return it
        }
        val trivias = triviaRepository.randomQuestions(listOf(), 5)
        val challengeId = challengeRepository.createChallenge(ChallengeToInsert(
                userId,
                null,
                listOf(),
                listOf(),
                null,
                null,
                trivias
        ))
        challengeRepository.findById(challengeId)?.let {
            return it
        }
//        TODO Customize exception for Id Not Found
        throw Exception()
    }
}