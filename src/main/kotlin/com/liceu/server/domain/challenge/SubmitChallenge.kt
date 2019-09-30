package com.liceu.server.domain.challenge

import com.liceu.server.data.ActivityRepository
import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.activities.ActivityToInsert
import com.liceu.server.domain.global.CHALLENGE
import com.liceu.server.domain.global.OverflowSizeException
import com.liceu.server.domain.global.POST
import com.liceu.server.domain.trivia.TriviaBoundary
import com.liceu.server.domain.util.TimeStamp
import com.liceu.server.domain.util.activitiesInsertion.activityInsertion
import com.liceu.server.util.Logging

class SubmitChallenge(
        private val challengeRepository: ChallengeBoundary.IRepository,
        private val triviaRepository: TriviaBoundary.IRepository,
        private val activityRepository: ActivityBoundary.IRepository
): ChallengeBoundary.ICreateChallenge {

    companion object {
        const val EVENT_NAME = "post_challenge"
        val TAGS = listOf(POST, CHALLENGE)
    }

    override fun run(challengerId: String, challengedId: String): Challenge {
        try {
            if(challengedId.isBlank()){
                throw OverflowSizeException ("challengedId cant't be null")
            }
            if(challengerId == challengedId){
                throw IllegalArgumentException("challengerId and challengedId can't be equal")
            }
            val trivias = triviaRepository.randomQuestions(listOf(), 10)
            val challengeId = challengeRepository.createChallenge(ChallengeToInsert(
                    challengerId,
                    challengedId,
                    listOf(),
                    listOf(),
                    null,
                    null,
                    trivias,
                    TimeStamp.retrieveActualTimeStamp()
            ))
            activityRepository.insertActivity(ActivityToInsert(
                    challengedId,
                    "directChallenge",
                    hashMapOf(
                            "challengerId" to challengerId,
                            "challengeId" to challengeId
                    ),
                    TimeStamp.retrieveActualTimeStamp()
            ))
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "challengerId" to challengerId,
                    "challengedId" to challengedId,
                    "challengeId" to challengeId
            ))
            return challengeId
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}