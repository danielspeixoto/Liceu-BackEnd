package com.liceu.server.domain.challenge

import com.liceu.server.domain.global.AuthenticationException
import com.liceu.server.domain.global.CHALLENGE
import com.liceu.server.domain.global.DIRECT
import com.liceu.server.domain.global.RETRIEVAL
import com.liceu.server.util.Logging

class AcceptDirectChallenge(
        private val challengeRepository: ChallengeBoundary.IRepository
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
                throw AuthenticationException ("userId authenticated isn't the challenged user")
            }
            //maybe put activity insertion to notify challenger
            return result
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }

    }
}