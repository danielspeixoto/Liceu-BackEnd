package com.liceu.server.domain.challenge

import com.liceu.server.data.MongoChallengeRepository
import com.liceu.server.data.MongoTriviaRepository
import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.global.CHALLENGE
import com.liceu.server.domain.global.RETRIEVAL
import com.liceu.server.domain.util.TimeStamp
import com.liceu.server.domain.util.challenge.challengeLogsAndActivityInsertion
import com.liceu.server.util.Logging

class GetChallenge(
        private val challengeRepository: MongoChallengeRepository,
        private val triviaRepository: MongoTriviaRepository,
        private val activityRepository: ActivityBoundary.IRepository
) : ChallengeBoundary.IGetChallenge {

    companion object {
        const val EVENT_NAME = "get_challenge"
        val TAGS = listOf(RETRIEVAL, CHALLENGE)
    }

    override fun run(userId: String): Challenge {
        try {
            challengeRepository.verifyDirectChallenges(userId)?.let {
                challengeLogsAndActivityInsertion(EVENT_NAME,TAGS,it,activityRepository)
                return it
            }
            challengeRepository.matchMaking(userId)?.let {
                challengeLogsAndActivityInsertion(EVENT_NAME,TAGS,it,activityRepository)
                return it
            }
            val trivias = triviaRepository.randomQuestions(listOf(), 10)
            val challengeId = challengeRepository.createChallenge(ChallengeToInsert(
                    userId,
                    null,
                    listOf(),
                    listOf(),
                    null,
                    null,
                    trivias,
                    TimeStamp.retrieveActualTimeStamp()
            ))
            Logging.info(
                    EVENT_NAME, TAGS ,
                    hashMapOf(
                            "challengeId" to challengeId.id,
                            "challengerId" to challengeId.challenger,
                            "challengedId" to challengeId.challenged,
                            "answersChallengerSize" to challengeId.answersChallenger.size,
                            "answersChallengedSize" to challengeId.answersChallenged.size
                    )
            )
            return challengeId
        } catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS, e)
            throw e
        }
    }


}