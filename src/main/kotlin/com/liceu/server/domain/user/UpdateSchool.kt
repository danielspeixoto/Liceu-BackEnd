package com.liceu.server.domain.user

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging
import java.text.Normalizer


class UpdateSchool(
     private val userRepository:  MongoUserRepository,
     private val REGEX_UNACCENT: Regex = "\\p{InCombiningDiacriticalMarks}+".toRegex()
): UserBoundary.IUpdateSchool {

    companion object {
        const val EVENT_NAME = "update_school_user"
        val TAGS = listOf(RETRIEVAL, USER , UPDATE, SCHOOL)
    }

    override fun run(userId: String, school: String) {
        val changeSchoolsName = hashMapOf(
                "MARISTA PATAMARES" to "MARISTA",
                "ANTONIO VEIRA" to "VIEIRA",
                "SAO PAULO" to "SAOPAULO"
        )
        try {
            var schoolNormalized = Normalizer.normalize(school, Normalizer.Form.NFD)
            schoolNormalized = REGEX_UNACCENT.replace(schoolNormalized, "").toUpperCase().trim()
            if(schoolNormalized.substring(0,7) == "COLEGIO"){
                schoolNormalized = schoolNormalized.substring(7,schoolNormalized.length).trim()
            }
            else if(schoolNormalized.substring(0,6) == "ESCOLA"){
                schoolNormalized = schoolNormalized.substring(6,schoolNormalized.length).trim()
            }
            else if(schoolNormalized.substring(0,8) == "CURSINHO"){
                schoolNormalized = schoolNormalized.substring(8,schoolNormalized.length).trim()
            }
            else if(schoolNormalized.substring(0,5) == "CURSO"){
                schoolNormalized = schoolNormalized.substring(5,schoolNormalized.length).trim()
            }
            changeSchoolsName.forEach {
                schoolNormalized = schoolNormalized.replace(it.key,it.value)
            }
            userRepository.updateSchoolFromUser(userId,schoolNormalized)
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}