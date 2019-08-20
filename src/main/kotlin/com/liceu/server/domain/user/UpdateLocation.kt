package com.liceu.server.domain.user

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging
import java.util.Locale
import kotlin.math.sqrt


class UpdateLocation(
    private val userRepository: MongoUserRepository
): UserBoundary.IUpdateLocation {


    companion object {
        const val EVENT_NAME = "update_location_user"
        val TAGS = listOf(RETRIEVAL, USER , UPDATE, LOCATION)
    }

    override fun run(userId: String,longitude: Double,latitude: Double) {
        val brazilianStatesCoordenates = hashMapOf(
                "AC" to Pair(-8.77, -70.55),
                "AL" to Pair(-9.71, -35.73),
                "AM" to Pair(-3.07, -61.66),
                "AP" to Pair(1.41, -51.77),
                "BA" to Pair(-12.96, -38.51),
                "CE" to Pair(-3.71, -38.54),
                "DF" to Pair(-15.83, -47.86),
                "ES" to Pair(-19.19, -40.34),
                "GO" to Pair(-16.64, -49.31),
                "MA" to Pair(-2.55, -44.30),
                "MT" to Pair(-12.64, -55.42),
                "MS" to Pair(-20.51, -54.54),
                "MG" to Pair(-18.10, -44.38),
                "PA" to Pair(-5.53, -52.29),
                "PB" to Pair(-7.06, -35.55),
                "PR" to Pair(-24.89, -51.55),
                "PE" to Pair(-8.28, -35.07),
                "PI" to Pair(-8.28, -43.68),
                "RJ" to Pair(-22.84, -43.15),
                "RN" to Pair(-5.22, -36.52),
                "RO" to Pair(-11.22, -62.80),
                "RS" to Pair(-30.01, -51.22),
                "RR" to Pair(1.89, -61.22),
                "SC" to Pair(-27.33, -49.44),
                "SE" to Pair(-10.90, -37.07),
                "SP" to Pair(-23.55, -46.64),
                "TO" to Pair(-10.25, -48.25)
        )
            try {
                var dist = Double.MAX_VALUE
                var state = ""
                brazilianStatesCoordenates.forEach {
                    var distanceBetweenPoints = sqrt((latitude-it.value.first)*(latitude-it.value.first) + (longitude-it.value.second)*(longitude-it.value.second));
                    if(distanceBetweenPoints < dist){
                        dist = distanceBetweenPoints
                        state = it.key
                    }
                }
                userRepository.updateLocationFromUser(userId,longitude,latitude,state)
            }catch (e: Exception){
                Logging.error(EVENT_NAME, TAGS,e)
                throw e
            }

    }
}