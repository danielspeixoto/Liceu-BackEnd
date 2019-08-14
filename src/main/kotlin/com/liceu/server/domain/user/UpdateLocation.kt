package com.liceu.server.domain.user

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging



class UpdateLocation(
    private val userRepository: MongoUserRepository
): UserBoundary.IUpdateLocation {


    companion object {
        const val EVENT_NAME = "update_location_user"
        val TAGS = listOf(RETRIEVAL, USER , UPDATE, LOCATION)
    }

    override fun run(userId: String,longitude: Double,latitude: Double) {
            try {
                userRepository.updateLocationFromUser(userId,longitude,latitude)
            }catch (e: Exception){
                Logging.error(EVENT_NAME, TAGS,e)
                throw e
            }

    }
}