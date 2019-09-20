package com.liceu.server.domain.trivia

import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class UpdateRating(
        private val triviaRepository: TriviaBoundary.IRepository
): TriviaBoundary.IUpdateRating {
    companion object {
        const val EVENT_NAME = "trivia_question_rating_update"
        val TAGS = listOf(UPDATE, TRIVIA, RATING)
    }

    override fun run(questionId: String, rating: Int) {
        if(rating == 0) {
            Logging.warn(UNCOMMON_PARAMS, TAGS, hashMapOf(
                    "action" to EVENT_NAME,
                    "value" to rating
            ))
            throw OverflowSizeException ("Rating can't be zero")
        }
        if(rating > 1 || rating < -1){
            Logging.warn(MAX_RATING_OVERFLOW, TAGS + listOf(OVERFLOW), hashMapOf(
                    "questionId" to questionId,
                    "rating" to rating
            ))
        }
        try {
            val ratingNormalized = Integer.signum(rating)
            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "questionId" to questionId,
                    "rating" to ratingNormalized
            ))
            if(ratingNormalized == 1){
                triviaRepository.updateLike(questionId)
            }else if (ratingNormalized == -1){
                triviaRepository.updateDislike(questionId)
            }
            //maybe insert activity to show the question owner that someone liked his/her question -> maybe its too abusive
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}