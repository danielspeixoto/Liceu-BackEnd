package com.liceu.server.domain.question

import com.liceu.server.domain.global.ID
import com.liceu.server.domain.global.QUESTION
import com.liceu.server.domain.global.RETRIEVAL
import com.liceu.server.util.Logging

class QuestionById(val repo: QuestionBoundary.IRepository): QuestionBoundary.IQuestionById {

    companion object {
        const val EVENT_NAME = "qustion_from_id"
        val TAGS = listOf(RETRIEVAL, QUESTION, ID)
    }

    override fun run(questionId: String): Question {
        try {
            val question = repo.getQuestionById(questionId)
            Logging.info(
                    EVENT_NAME,
                    TAGS,
                    hashMapOf(
                            "questionId" to questionId
                    )
            )
            return question
        } catch (e: Exception) {
            Logging.error(EVENT_NAME,TAGS, e)
            throw e
        }
    }
}