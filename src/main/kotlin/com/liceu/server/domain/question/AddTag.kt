package com.liceu.server.domain.question

import com.liceu.server.domain.exception.AlreadyExistsException
import com.liceu.server.domain.exception.ItemNotFoundException
import com.liceu.server.domain.tag.TagBoundary
import com.liceu.server.util.Logging


class AddTag(
        val questionRepo: QuestionBoundary.IRepository,
        val tagRepo: TagBoundary.IRepository
): QuestionBoundary.IAddTag {

    companion object {
        const val EVENT_NAME = "add_tag"
        val TAGS = listOf("insertion", "tag", "question")
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
                    "question_not_found",
                    TAGS,
                    hashMapOf(
                            "questionId" to id
                    )

            )
        } catch (e: AlreadyExistsException) {
            Logging.info(
                    "tag_already_exists",
                    TAGS,
                    hashMapOf(
                            "questionId" to id,
                            "tagName" to tag
                    )

            )
        } catch (e: Exception) {
            Logging.error(
                    EVENT_NAME + "_error",
                    listOf("unknown", "error") + TAGS,
                    hashMapOf(
                            "stackTrace" to e.stackTrace
                    )

            )
        }
    }

}