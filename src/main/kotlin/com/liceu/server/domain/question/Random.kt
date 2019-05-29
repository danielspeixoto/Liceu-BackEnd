package com.liceu.server.domain.question

import com.liceu.server.util.Logging
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class Random(val repo: QuestionBoundary.IRepository, val maxResults: Int): QuestionBoundary.Random {

    override fun run(tags: List<String>, amount: Int): List<Question> {
        Logging.info(
                "request_random_questions",
                listOf("retrieval", "question"),
                hashMapOf(
                        "amount" to amount,
                        "tagNames" to tags
                )
        )
        var finalAmount = amount
        if(amount > maxResults) {
            finalAmount = maxResults
            Logging.warn(
                    "max_results_overflow",
                    listOf("overflow", "retrieval", "question"),
                    hashMapOf(
                            "action" to "request_random_questions",
                            "requested" to amount,
                            "max_allowed" to maxResults
                    )
            )
        }
        return repo.randomByTags(tags, finalAmount)
    }
}