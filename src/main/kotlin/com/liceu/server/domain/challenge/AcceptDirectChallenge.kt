package com.liceu.server.domain.challenge

import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.global.AuthenticationException
import com.liceu.server.domain.global.CHALLENGE
import com.liceu.server.domain.global.DIRECT
import com.liceu.server.domain.global.RETRIEVAL
import com.liceu.server.domain.util.activitiesInsertion.activityInsertion
import com.liceu.server.util.Logging

class AcceptDirectChallenge(
        private val challengeRepository: ChallengeBoundary.IRepository,
        private val activityRepository: ActivityBoundary.IRepository
): ChallengeBoundary.IAcceptDirectChallenge {

    companion object {
        const val EVENT_NAME = "accept_direct_challenge"
        val TAGS = listOf(RETRIEVAL,DIRECT,CHALLENGE)
    }

    override fun run(challengeId: String,userId: String): Challenge {
        try {
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "challengeId" to challengeId
            ))
            val result = challengeRepository.findById(challengeId)
            if (result.challenged != userId){
                throw AuthenticationException ("user authenticated isn't the challenged user")
            }
            Logging.info(
                    EVENT_NAME, TAGS, hashMapOf(
                            "challengeId" to result.id,
                            "challengerId" to result.challenger,
                            "challengedId" to result.challenged,
                            "answersChallengerSize" to result.answersChallenger.size,
                            "answersChallengedSize" to result.answersChallenged.size
                    )
            )
            activityInsertion(activityRepository, result.challenger,"challengeAccepted", hashMapOf(
                    "challengeId" to result.id,
                    "challengedId" to result.challenged!!
            ))

            return result
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }

    }
}