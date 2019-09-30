package com.liceu.server.domain.user

import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class UpdateFcmToken(
        private val userRepository: UserBoundary.IRepository
): UserBoundary.IupdateFcmToken {
    companion object {
        const val EVENT_NAME = "update_fcmToken"
        val TAGS = listOf(USER , UPDATE, FCMTOKEN)
    }
    override fun run(userId: String, fcmToken: String) {
        try {
            if(fcmToken.isBlank()){
                throw UnderflowSizeException("FcmToken can't be null")
            }
            if(fcmToken.length > 250){
                throw OverflowSizeException("FcmToken is too long")
            }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to userId,
                    "fcmToken" to fcmToken
            ))
            userRepository.updateFcmTokenFromUser(userId,fcmToken)

        }catch (e: Exception){
            Logging.error(EVENT_NAME,TAGS,e)
            throw e
        }
    }
}