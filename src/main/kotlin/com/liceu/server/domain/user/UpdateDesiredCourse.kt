package com.liceu.server.domain.user

import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class UpdateDesiredCourse (
        private val userRepository: UserBoundary.IRepository
): UserBoundary.IUpdateCourse {
    companion object {
        const val EVENT_NAME = "update_user_course"
        val TAGS = listOf (USER , UPDATE, COURSE)
    }

    override fun run(userId: String, course: String) {
        try {
            if(course.isBlank()){
                throw UnderflowSizeException("Course can't be empty")
            }
            if(course.length > 100){
                throw OverflowSizeException("Course length is too big")
            }
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to userId,
                    "course" to course
            ))
            userRepository.updateDesiredCourse(userId,course)
        }catch (e: Exception) {
            Logging.error(EVENT_NAME,TAGS,e)
            throw e
        }
    }
}