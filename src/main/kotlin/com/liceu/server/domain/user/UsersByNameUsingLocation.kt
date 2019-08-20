package com.liceu.server.domain.user

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging
import java.lang.Exception

class UsersByNameUsingLocation(
    private val userRepo: MongoUserRepository
): UserBoundary.IGetUsersByNameUsingLocation {

    companion object{
        const val EVENT_NAME = "get_users_by_name_near_location"
        val TAGS = listOf(RETRIEVAL, USER , NAME, LOCATION)
    }

    override fun run(userId: String, nameSearched: String): List<User> {
        try {
            if(nameSearched.length <= 3){
                throw OverflowSizeException("It needs more than 3 characters")
            }
            val requestingUser = userRepo.getUserById(userId)
            val usersRetrieved = userRepo.getUsersByNameUsingLocation(nameSearched,requestingUser.location?.y,requestingUser.location?.x)
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "requestingUser" to requestingUser,
                    "nameSearched" to nameSearched
            ))
            return usersRetrieved
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}