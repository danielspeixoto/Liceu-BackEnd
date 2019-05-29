package com.liceu.server.domain.question

import com.liceu.server.domain.tag.TagBoundary
import com.liceu.server.util.Logging

class AddTag(
        val questionRepo: QuestionBoundary.IRepository,
        val tagRepo: TagBoundary.IRepository
): QuestionBoundary.IAddTag {

    override fun run(id: String, tag: String) {
        Logging.info(
                "add_tag",
                listOf("insertion", "tag", "question"),
                hashMapOf(
                        "questionId" to id,
                        "tagName" to tag
                )
        )
        try {
            questionRepo.addTag(id, tag)
            tagRepo.incrementCount(tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}