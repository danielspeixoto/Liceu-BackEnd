package com.liceu.server.domain.user

import com.liceu.server.domain.challenge.Challenge
import com.liceu.server.domain.global.CHALLENGE
import com.liceu.server.domain.global.RETRIEVAL
import com.liceu.server.domain.global.USER
import com.liceu.server.util.Logging
import java.lang.Exception

class ChallengesFromUserId (
        private val repo: UserBoundary.IRepository,
        private val historyAmount: Int
): UserBoundary.IChallengesFromUserById{

    companion object {
        const val EVENT_NAME = "challenges_from_user"
        val TAGS = listOf(RETRIEVAL, CHALLENGE, USER)
    }

    override fun run(userId: String): List<Challenge> {
        try {
            val challenges = repo.getChallengesFromUserById(userId,historyAmount)
            Logging.info(
                    EVENT_NAME,
                    TAGS,
                    hashMapOf(
                            "userId" to userId
                    ))
            return challenges
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS, e)
            throw e
        }
    }
}