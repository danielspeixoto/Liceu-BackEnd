package com.liceu.server.domain.challenge

import com.liceu.server.data.MongoChallengeRepository
import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.activities.ActivityToInsert
import com.liceu.server.domain.global.ANSWERS
import com.liceu.server.domain.global.CHALLENGE
import com.liceu.server.domain.global.RETRIEVAL
import com.liceu.server.domain.global.UPDATE
import com.liceu.server.domain.util.TimeStamp
import com.liceu.server.util.Logging

class UpdateAnswers(
        private val challengeRepository: MongoChallengeRepository,
        private val activityRepository: ActivityBoundary.IRepository

): ChallengeBoundary.IUpdateAnswers{

    companion object {
        const val EVENT_NAME = "update_answers_challenge"
        val TAGS = listOf(RETRIEVAL, CHALLENGE, UPDATE, ANSWERS)
    }

    override fun run(challengeId: String,player: String,answers: List<String>) {
        val challenge = challengeRepository.findById(challengeId)
        var isChallenger = challenge.challenger == player
        try {
            var result = 0
            for(i in 0..(answers.size - 1)){
                if(challenge.triviaQuestionsUsed[i].correctAnswer == answers[i]){
                    result++
                }
            }
            challengeRepository.updateAnswers(challengeId,isChallenger,answers,result)
            if(challenge.challenged != null){
                activityRepository.insertActivity(ActivityToInsert(
                        challenge.challenger,
                        "challengeFinished",
                        hashMapOf(
                                "challengeId" to challenge.id,
                                "challengedId" to challenge.challenged
                        ),
                        TimeStamp.retrieveActualTimeStamp()
                ))
                activityRepository.insertActivity(ActivityToInsert(
                        challenge.challenged,
                        "challengeFinished",
                        hashMapOf(
                                "challengeId" to challenge.id,
                                "challengerId" to  challenge.challenger
                        ),
                        TimeStamp.retrieveActualTimeStamp()
                ))
            }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "challengeId" to challengeId,
                    "player" to player
            ))
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "challengeId" to challengeId,
                    "player" to player
            ))
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS, e)
            throw e
        }

    }
}