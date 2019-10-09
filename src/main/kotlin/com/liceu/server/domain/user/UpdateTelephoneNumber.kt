package com.liceu.server.domain.user

import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class UpdateTelephoneNumber(
        private val userRepository: UserBoundary.IRepository,
        private val TELEPHONE_NUMBER_CHECK: Regex  = "(\\([1-9]{2}\\)|[1-9]{2})9[-\\.]?[0-9]{4}[-\\.]?[0-9]{4}".toRegex()
): UserBoundary.IUpdateTelephoneNumber{
    companion object {
        const val EVENT_NAME = "update_user_telephone_number"
        val TAGS = listOf (USER , UPDATE, TELEPHONE)
    }

    override fun run(userId: String, telephoneNumber: String) {
        try{
            if(telephoneNumber.isBlank()){
                throw UnderflowSizeException("Telephone can't be empty")
            }
            if(!TELEPHONE_NUMBER_CHECK.matches(telephoneNumber)){
                throw TypeMismatchException("Incorrect form of telephone number")
            }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to userId,
                    "telephoneNumber" to telephoneNumber
            ))
            userRepository.updateTelephoneNumber(userId,telephoneNumber)
        } catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}