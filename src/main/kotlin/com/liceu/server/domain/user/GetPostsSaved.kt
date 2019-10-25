package com.liceu.server.domain.user

import com.liceu.server.domain.game.GameRanking
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class GetPostsSaved(
        private val userRepository: UserBoundary.IRepository,
        private val maxResults: Int
): UserBoundary.IGetSavedPosts {
    companion object {
        const val EVENT_NAME = "get_posts_saved"
        val TAGS = listOf(RETRIEVAL, USER , POST, SAVED)
    }
    override fun run(userId: String,amount: Int,start: Int): List<String>? {
        if(amount == 0) {
            Logging.warn(UNCOMMON_PARAMS, TAGS, hashMapOf(
                    "action" to EVENT_NAME,
                    "value" to amount
            ))
        }
        var finalAmount = amount
        if(amount > maxResults) {
            finalAmount = maxResults
            Logging.warn(
                    MAX_RESULTS_OVERFLOW,
                    TAGS + listOf(OVERFLOW),
                    hashMapOf(
                            "requested" to amount,
                            "max_allowed" to maxResults
                    )
            )
        }
        try {
            if(userId.isBlank()){
                throw UnderflowSizeException("userId can't be null")
            }
            if(!userRepository.userExists(userId)){
                throw ItemNotFoundException("user requested don't exists")
            }
            Logging.info(EVENT_NAME,TAGS, hashMapOf(
                    "userId" to userId,
                    "amount" to finalAmount,
                    "start" to start
            ))
            return userRepository.getPostsSaved(userId,finalAmount,start)
        }catch (e: Exception){
            Logging.error(EVENT_NAME,TAGS,e)
            throw e
        }
    }
}