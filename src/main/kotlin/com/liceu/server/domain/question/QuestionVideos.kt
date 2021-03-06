package com.liceu.server.domain.question

import com.liceu.server.domain.global.*
import com.liceu.server.domain.video.Video
import com.liceu.server.util.Logging
import net.logstash.logback.encoder.org.apache.commons.lang.ArrayUtils.indexOf
import net.logstash.logback.encoder.org.apache.commons.lang.ArrayUtils.removeElement
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
            var result =  videoRepo.videos(id, start, finalAmount)
            var resultToIterate = result.toMutableList()
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
            //process to remove Bernoulli from videos retrieved
            if(result.size > 0){
                for(i in 0..(resultToIterate.size-1)){
                    if(resultToIterate[i].title.toUpperCase().contains("BERNOULLI")  || resultToIterate[i].channelTitle.toUpperCase().contains("BERNOULLI")){
                        result.remove(resultToIterate[i])
                    }
                }
            }
            return result
        } catch (e: Exception) {
            Logging.error(EVENT_NAME, TAGS, e)
            throw e
        }

    }
}