package com.liceu.server.domain.trivia

import com.liceu.server.domain.global.*
import com.liceu.server.util.Logging

class TriviaRandomQuestions(val repo: TriviaBoundary.IRepository,val maxResults: Int): TriviaBoundary.IRandomQuestions {

    companion object {
        const val EVENT_NAME = "trivia_random_questions"
        val TAGS = listOf(RETRIEVAL, GAME, TRIVIA, QUESTION)
    }

    override fun run(tags: List<String>, amount: Int): List<TriviaQuestion> {
        if(amount == 0){
            Logging.info(UNCOMMON_PARAMS, TAGS, hashMapOf(
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
            val questions = repo.randomQuestions(tags,finalAmount)
            Logging.info(
                    EVENT_NAME,
                    TAGS,
                    hashMapOf(
                            "amount" to finalAmount,
                            "retrieved" to questions.size
                    )
            )
            return questions

        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}