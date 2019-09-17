package com.liceu.server.domain.util.challenge

import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.activities.ActivityToInsert
import com.liceu.server.domain.challenge.Challenge
import com.liceu.server.domain.challenge.GetChallenge
import com.liceu.server.domain.util.TimeStamp
import com.liceu.server.util.Logging


    fun challengeLogsAndActivityInsertion(challenge: Challenge,activityRepository: ActivityBoundary.IRepository) {
        Logging.info(
                GetChallenge.EVENT_NAME, GetChallenge.TAGS,
                hashMapOf(
                        "challengeId" to challenge.id,
                        "challengerId" to challenge.challenger,
                        "challengedId" to challenge.challenged,
                        "answersChallengerSize" to challenge.answersChallenger.size,
                        "answersChallengedSize" to challenge.answersChallenged.size
                )
        )
        activityRepository.insertActivity(ActivityToInsert(
                challenge.challenger,
                "challengeAccepted",
                hashMapOf(
                        "challengeId" to challenge.id,
                        "challengedId" to challenge.challenged!!
                ),
                TimeStamp.retrieveActualTimeStamp()
        ))
    }

