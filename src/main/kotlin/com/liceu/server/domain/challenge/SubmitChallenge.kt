package com.liceu.server.domain.challenge

import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.activities.ActivityToInsert
import com.liceu.server.domain.global.CHALLENGE
import com.liceu.server.domain.global.OverflowSizeException
import com.liceu.server.domain.global.POST
import com.liceu.server.domain.notification.AnswerChallengeNotification
import com.liceu.server.domain.notification.NotificationBoundary
import com.liceu.server.domain.trivia.TriviaBoundary
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.domain.util.dateFunctions.DateFunctions.retrieveActualTimeStamp
import com.liceu.server.util.Logging

class SubmitChallenge(
        private val challengeRepository: ChallengeBoundary.IRepository,
        private val triviaRepository: TriviaBoundary.IRepository,
        private val activityRepository: ActivityBoundary.IRepository,
        private val userRepository: UserBoundary.IRepository,
        private val firebaseNotifications: NotificationBoundary.INotifier
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
            val firstName = userRepository.getUserById(challengerId).name.split(" ")[0]
            val trivias = triviaRepository.randomQuestions(listOf(), 10)
            val challenge = challengeRepository.createChallenge(ChallengeToInsert(
                    challengerId,
                    challengedId,
                    listOf(),
                    listOf(),
                    null,
                    null,
                    trivias,
                    retrieveActualTimeStamp()
            ))
            activityRepository.insertActivity(ActivityToInsert(
                    challengedId,
                    "directChallenge",
                    hashMapOf(
                            "challengerId" to challengerId,
                            "challengeId" to challenge.id
                    ),
                    retrieveActualTimeStamp()
            ))
            val notification = AnswerChallengeNotification("Te desafiaram!", "${firstName} te desafiou!",challenge.id,challengerId)
            userRepository.getUserById(challengedId).fcmToken?.let { it1 -> firebaseNotifications.send(it1,notification) }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "challengerId" to challengerId,
                    "challengedId" to challengedId,
                    "challengeId" to challenge.id
            ))
            return challenge
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}