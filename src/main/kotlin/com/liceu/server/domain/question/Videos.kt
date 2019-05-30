package com.liceu.server.domain.question

import com.liceu.server.domain.video.Video
import com.liceu.server.util.Logging

class Videos(
        val videoRepo: QuestionBoundary.IRepository,
        val maxResults: Int
): QuestionBoundary.IVideos {

    override fun run(id: String, start: Int, count: Int): List<Video> {
        Logging.info(
                "question_related_videos",
                listOf("retrieval", "question", "video"),
                hashMapOf(
                        "count" to count,
                        "start" to start,
                        "questionId" to id
                )
        )
        var finalAmount = count
        if(count > maxResults) {
            finalAmount = maxResults
            Logging.warn(
                    "max_results_overflow",
                    listOf("overflow", "retrieval", "question", "video"),
                    hashMapOf(
                            "action" to "question_related_videos",
                            "requested" to count,
                            "max_allowed" to maxResults
                    )
            )
        }
        return videoRepo.videos(id, start, finalAmount)
    }
}