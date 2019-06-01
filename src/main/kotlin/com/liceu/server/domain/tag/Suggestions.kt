package com.liceu.server.domain.tag

import com.liceu.server.domain.global.RETRIEVAL
import com.liceu.server.domain.global.TAG
import com.liceu.server.domain.question.Random
import com.liceu.server.util.Logging
import java.lang.Exception

class Suggestions(val tagRepo: TagBoundary.IRepository): TagBoundary.ISuggestions {

    companion object {
        const val EVENT_NAME = "tag_suggestions"
        val TAGS = listOf(RETRIEVAL, TAG)
    }

    override fun run(query: String, minQuestions: Int): List<Tag> {
        Logging.info(
                EVENT_NAME,
                TAGS,
                hashMapOf(
                        "minQuestions" to minQuestions,
                        "query" to query
                )
        )
        try {
            return tagRepo.suggestions(query, minQuestions)
        } catch (e: Exception) {
            Logging.error(EVENT_NAME, TAGS, e)
            throw Exception()
        }
    }
}