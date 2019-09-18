package com.liceu.server.domain.user

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.domain.report.SubmitReport
import com.liceu.server.util.Logging

class UpdateInstagramProfile(
    private val userRepo: UserBoundary.IRepository
): UserBoundary.IUpdateInstagramProfile {

    companion object {
        const val EVENT_NAME = "update_instagram_profile_user"
        val TAGS = listOf(RETRIEVAL, USER , UPDATE, INSTAGRAM)
    }


    override fun run(userId: String, instagramProfile: String) {
        try {
            if(instagramProfile.length > 35){
                throw OverflowSizeException ("Too many characters in instagram profile name")
            }
            var instagramProfileNormalized = instagramProfile
            if(instagramProfile.contains("@")){
                instagramProfileNormalized = instagramProfile.trim().removePrefix("@")
            }
            instagramProfileNormalized = instagramProfileNormalized.trim().toLowerCase()
            Logging.info(EVENT_NAME,TAGS, hashMapOf(
                    "userId" to userId,
                    "instagramProfile" to instagramProfileNormalized
            ))
            userRepo.updateInstagramProfileFromUser(userId,instagramProfileNormalized)
        }catch (e: Exception){
            Logging.error(EVENT_NAME,TAGS,e)
            throw  e
        }
    }
}