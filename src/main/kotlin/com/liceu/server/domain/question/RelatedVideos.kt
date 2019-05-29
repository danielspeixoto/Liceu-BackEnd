package com.liceu.server.domain.question

import com.liceu.server.domain.video.Video
import com.liceu.server.domain.video.VideoBoundary
import com.liceu.server.util.Logging

class RelatedVideos(
        val videoRepo: VideoBoundary.IRepository,
        val maxResults: Int
): QuestionBoundary.IRelatedVideos {

    override fun run(id: String, start: Int, amount: Int): List<Video> {
        Logging.info(
                "question_related_videos",
                listOf("retrieval", "question", "video"),
                hashMapOf(
                        "amount" to amount,
                        "start" to start,
                        "questionId" to id
                )
        )
        var finalAmount = amount
        if(amount > maxResults) {
            finalAmount = maxResults
            Logging.warn(
                    "max_results_overflow",
                    listOf("overflow", "retrieval", "question", "video"),
                    hashMapOf(
                            "action" to "question_related_videos",
                            "requested" to amount,
                            "max_allowed" to maxResults
                    )
            )
        }
        return videoRepo.questionRelatedVideos(id, start, finalAmount)
    }
}