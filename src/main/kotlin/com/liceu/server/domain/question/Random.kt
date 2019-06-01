package com.liceu.server.domain.question

import com.liceu.server.domain.global.MAX_RESULTS_OVERFLOW
import com.liceu.server.domain.global.OVERFLOW
import com.liceu.server.domain.global.QUESTION
import com.liceu.server.domain.global.RETRIEVAL
import com.liceu.server.util.Logging
import java.lang.Exception

class Random(val repo: QuestionBoundary.IRepository, val maxResults: Int): QuestionBoundary.IRandom {

    companion object {
        const val EVENT_NAME = "random_questions"
        val TAGS = listOf(RETRIEVAL, QUESTION)
    }

    override fun run(tags: List<String>, amount: Int): List<Question> {
        Logging.info(
                EVENT_NAME,
                TAGS,
                hashMapOf(
                        "amount" to amount,
                        "tagNames" to tags
                )
        )
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
            return repo.randomByTags(tags, finalAmount)
        } catch (e: Exception) {
            Logging.error(EVENT_NAME, TAGS, e)
            throw Exception()
        }
    }
}