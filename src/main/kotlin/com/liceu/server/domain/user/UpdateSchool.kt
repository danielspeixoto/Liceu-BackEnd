package com.liceu.server.domain.user

import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging
import java.text.Normalizer


class UpdateSchool(
     private val userRepository:  UserBoundary.IRepository,
     private val REGEX_UNACCENT: Regex = "\\p{InCombiningDiacriticalMarks}+".toRegex() //REGEX to remove special characters and accentuation

): UserBoundary.IUpdateSchool {

    companion object {
        const val EVENT_NAME = "update_school_user"
        val TAGS = listOf(RETRIEVAL, USER , UPDATE, SCHOOL)
    }

    override fun run(userId: String, school: String) {
        val changeSchoolsName = hashMapOf(
                "MARISTA PATAMARES" to "MARISTA",
                "ANTONIO VIEIRA" to "VIEIRA",
                "SAO PAULO" to "SAOPAULO"
        )
        val commomPrefixes = arrayListOf(
                "COLEGIO","ESCOLA","CURSINHO","CURSO"
        )

        try {
            if(school.length > 300){
                throw OverflowSizeMessageException("Too many characters in school name")
            }
            var schoolNormalized = Normalizer.normalize(school, Normalizer.Form.NFD)
            schoolNormalized = REGEX_UNACCENT.replace(schoolNormalized, "").toUpperCase().trim()
            commomPrefixes.forEach {
                if(schoolNormalized.contains(it)){
                    schoolNormalized = schoolNormalized.removePrefix(it).trim()
                }
            }
            changeSchoolsName.forEach {
                schoolNormalized = schoolNormalized.replace(it.key,it.value)
            }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to userId,
                    "school" to schoolNormalized
            ))
            userRepository.updateSchoolFromUser(userId,schoolNormalized)
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}