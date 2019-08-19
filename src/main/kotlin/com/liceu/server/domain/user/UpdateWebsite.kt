package com.liceu.server.domain.user

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class UpdateWebsite(
    private val userRepo: MongoUserRepository
): UserBoundary.IUpdateWebsite {

    companion object {
        const val EVENT_NAME = "update__user"
        val TAGS = listOf(RETRIEVAL, USER , UPDATE, WEBSITE)
    }

    override fun run(userId: String, website: String) {
        try {
            if(website.length > 150){
                throw OverflowSizeException ("Too many characters in website URL")
            }
            Logging.info(EVENT_NAME,TAGS, hashMapOf(
                    "userId" to userId,
                    "website" to website
            ))
            userRepo.updateWebsiteFromUser(userId,website)
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}
