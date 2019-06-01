package com.liceu.server.domain.question

import com.liceu.server.domain.global.*
import com.liceu.server.domain.tag.TagBoundary
import com.liceu.server.util.Logging


class AddTag(
        val questionRepo: QuestionBoundary.IRepository,
        val tagRepo: TagBoundary.IRepository
): QuestionBoundary.IAddTag {

    companion object {
        const val EVENT_NAME = "add_tag"
    val TAGS = listOf(INSERTION, TAG, QUESTION)
    }

    override fun run(id: String, tag: String) {
        Logging.info(
                EVENT_NAME,
                TAGS,
                hashMapOf(
                        "questionId" to id,
                        "tagName" to tag
                )
        )
        try {
            questionRepo.addTag(id, tag)
            tagRepo.incrementCount(tag)
        } catch (e: ItemNotFoundException) {
            Logging.info(
                    QUESTION + NOT_FOUND,
                    TAGS,
                    hashMapOf(
                            "questionId" to id
                    )

            )
        } catch (e: AlreadyExistsException) {
            Logging.info(
                    TAG + ALREADY_EXISTS,
                    TAGS,
                    hashMapOf(
                            "questionId" to id,
                            "tagName" to tag
                    )

            )
        } catch (e: Exception) {
            Logging.error(EVENT_NAME, TAGS, e)
            throw Exception()
        }
    }

}