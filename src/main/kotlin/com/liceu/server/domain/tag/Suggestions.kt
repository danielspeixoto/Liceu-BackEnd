package com.liceu.server.domain.tag

import com.liceu.server.util.Logging

class Suggestions(val tagRepo: TagBoundary.IRepository): TagBoundary.ISuggestions {
    override fun run(query: String, minQuestions: Int): List<Tag> {
        Logging.info(
                "tag_suggestions",
                listOf("retrieval", "tag"),
                hashMapOf(
                        "minQuestions" to minQuestions,
                        "query" to query
                )
        )
        return tagRepo.suggestions(query, minQuestions)
    }
}