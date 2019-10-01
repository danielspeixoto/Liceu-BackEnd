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
            challengeRepository.matchMaking(userId)?.let {
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
                val notification = AnswerChallengeNotification("Te desafiaram!", "${firstName} te desafiou!",it.id,it.challenger)
                userRepository.getUserById(it.challenged).fcmToken?.let { it1 -> firebaseNotifications.send(it1,notification) }
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