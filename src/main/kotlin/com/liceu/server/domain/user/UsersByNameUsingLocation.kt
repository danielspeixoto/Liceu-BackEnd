package com.liceu.server.domain.user

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.game.GameRanking
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging
import java.lang.Exception
import java.text.Normalizer

class UsersByNameUsingLocation(
    private val userRepo: UserBoundary.IRepository,
    private val maxResults: Int,
    private val REGEX_UNACCENT: Regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
): UserBoundary.IGetUsersByNameUsingLocation {

    companion object{
        const val EVENT_NAME = "get_users_by_name_near_location"
        val TAGS = listOf(RETRIEVAL, USER , NAME, LOCATION)
    }

    override fun run(nameSearched: String, longitude: Double,latitude: Double,amount: Int): List<User> {
        if(amount == 0) {
            Logging.warn(UNCOMMON_PARAMS,TAGS, hashMapOf(
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
                            "action" to EVENT_NAME,
                            "requested" to amount,
                            "max_allowed" to maxResults
                    )
            )
        }
        try {
            if(nameSearched.length <= 3){
                throw OverflowSizeException("Name searched needs more than 3 characters")
            }
            var nameNormalized = Normalizer.normalize(nameSearched, Normalizer.Form.NFD)
            nameNormalized = REGEX_UNACCENT.replace(nameNormalized, "").trim()
            val usersRetrieved = userRepo.getUsersByNameUsingLocation(nameNormalized,longitude,latitude,finalAmount)
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "nameSearched" to nameSearched,
                    "longitude" to longitude,
                    "latitude" to latitude
            ))
            return usersRetrieved
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}