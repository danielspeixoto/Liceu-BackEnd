package com.liceu.server.domain.user

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class UpdateDescription(
    private val userRepo: MongoUserRepository
):UserBoundary.IUpdateDescription {

    companion object {
        const val EVENT_NAME = "update_description_user"
        val TAGS = listOf(RETRIEVAL, USER , UPDATE, DESCRIPTION)
    }

    override fun run(userId: String, description: String) {
        try {
            if(description.length > 450){
                throw OverflowSizeException("Too many characters in user description")
            }
            Logging.info(EVENT_NAME,TAGS, hashMapOf(
                    "userId" to userId,
                    "description" to description
            ))
            userRepo.updateDescriptionFromUser(userId,description)
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }

    }
}