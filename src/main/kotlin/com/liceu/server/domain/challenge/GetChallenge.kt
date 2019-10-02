package com.liceu.server.domain.challenge

import com.liceu.server.data.MongoChallengeRepository
import com.liceu.server.data.MongoTriviaRepository
import com.liceu.server.data.firebase.FirebaseNotifications
import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.activities.ActivityToInsert
import com.liceu.server.domain.global.CHALLENGE
import com.liceu.server.domain.global.RETRIEVAL
import com.liceu.server.domain.notification.AnswerChallengeNotification
import com.liceu.server.domain.notification.NotificationBoundary
import com.liceu.server.domain.trivia.TriviaBoundary
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.domain.util.TimeStamp
import com.liceu.server.domain.util.activitiesInsertion.activityInsertion
import com.liceu.server.util.Logging

class GetChallenge(
        private val challengeRepository: ChallengeBoundary.IRepository,
        private val triviaRepository: TriviaBoundary.IRepository,
        private val activityRepository: ActivityBoundary.IRepository,
        private val userRepository: UserBoundary.IRepository,
        private val firebaseNotifications: NotificationBoundary.INotifier
) : ChallengeBoundary.IGetChallenge {

    companion object {
        const val EVENT_NAME = "get_challenge"
        val TAGS = listOf(RETRIEVAL, CHALLENGE)
    }

    override fun run(userId: String): Challenge {
        try {
            val firstName = userRepository.getUserById(userId).name.split(" ")[0]
            challengeRepository.verifyDirectChallenges(userId)?.let {
                Logging.info(
                        EVENT_NAME, TAGS,
                        hashMapOf(
                                "challengeId" to it.id,
                                "challengerId" to it.challenger,
                                "challengedId" to it.challenged,
                                "answersChallengerSize" to it.answersChallenger.size,
                                "answersChallengedSize" to it.answersChallenged.size
                        )
                )
                activityInsertion(activityRepository, it.challenger,"challengeAccepted", hashMapOf(
                        "challengeId" to it.id,
                        "challengedId" to it.challenged!!
                    ))
                return it
            }
            val challenged = userRepository.getActiveUser()
            val trivias = triviaRepository.randomQuestions(listOf(), 4)
            val challenge = challengeRepository.createChallenge(ChallengeToInsert(
                    userId,
                    challenged?.id,
                    listOf(),
                    listOf(),
                    null,
                    null,
                    trivias,
                    TimeStamp.retrieveActualTimeStamp(),
                    true
            ))
            val notification = AnswerChallengeNotification("Te desafiaram!", "${firstName} te desafiou!",challenge.id,userId)
            userRepository.getUserById(challenged!!.id).fcmToken?.let { it1 -> firebaseNotifications.send(it1,notification) }
            Logging.info(
                    EVENT_NAME, TAGS ,
                    hashMapOf(
                            "challengeId" to challenge.id,
                            "challengerId" to challenge.challenger,
                            "challengedId" to challenge.challenged,
                            "answersChallengerSize" to challenge.answersChallenger.size,
                            "answersChallengedSize" to challenge.answersChallenged.size
                    )
            )
            return challenge
        } catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS, e)
            throw e
        }
    }


}