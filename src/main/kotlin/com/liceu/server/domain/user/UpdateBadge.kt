package com.liceu.server.domain.user

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class UpdateBadge (
        private val userRepository: UserBoundary.IRepository
): UserBoundary.IUpdateBadge {
    companion object {
        const val EVENT_NAME = "update_user_badge"
        val TAGS = listOf (USER , UPDATE, BADGE)
    }

    override fun run(userId: String, badge: String) {
        try {
            if(badge.isBlank()) {
                throw UnderflowSizeException ("Badge can't be empty")
            }
            if(badge.length > 40){
                throw OverflowSizeException ("Badge is too large")
            }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to userId,
                    "badge" to badge
            ))
            userRepository.updateBadge(userId,badge)
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}