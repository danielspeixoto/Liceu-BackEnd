package com.liceu.server.domain.question

import com.liceu.server.domain.global.*
import com.liceu.server.domain.video.Video
import com.liceu.server.util.Logging
import java.lang.Exception

class QuestionVideos(
        val videoRepo: QuestionBoundary.IRepository,
        val maxResults: Int
): QuestionBoundary.IVideos {

    companion object {
        const val EVENT_NAME = "question_videos"
        val TAGS = listOf(RETRIEVAL, QUESTION, VIDEO)
    }

    override fun run(id: String, start: Int, count: Int): List<Video> {
        var finalAmount = count
        if(count > maxResults) {
            finalAmount = maxResults
            Logging.warn(
                    MAX_RESULTS_OVERFLOW,
                    TAGS + listOf(OVERFLOW),
                    hashMapOf(
                            "action" to EVENT_NAME,
                            "requested" to count,
                            "max_allowed" to maxResults
                    )
            )
        }
        try {
            val result =  videoRepo.videos(id, start, finalAmount)
            Logging.info(
                    EVENT_NAME,
                    TAGS,
                    hashMapOf(
                            "count" to count,
                            "start" to start,
                            "questionId" to id,
                            "retrieved" to result.size
                    )
            )
            return result
        } catch (e: Exception) {
            Logging.error(EVENT_NAME, TAGS, e)
            throw e
        }

    }
}