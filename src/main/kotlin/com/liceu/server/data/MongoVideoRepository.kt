package com.liceu.server.data

import com.liceu.server.domain.video.Video
import com.liceu.server.domain.video.VideoBoundary
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class MongoVideoRepository(
        @Autowired val repo: VideoRepository,
        @Autowired val template: MongoTemplate
): VideoBoundary.IRepository {

    companion object {
        const val COLLECTION_NAME = "relatedVideos"
    }

    override fun questionRelatedVideos(id: String, start: Int, count: Int): List<Video> {
        val match = Aggregation.match(Criteria("questionId").isEqualTo(id))
        val sort = Aggregation.sort(Sort.Direction.DESC, "retrievalPosition")
        val skip = Aggregation.skip(start.toLong())
        val limit = Aggregation.limit(count.toLong())
        val agg = Aggregation.newAggregation(match, sort, skip, limit)

        val results = template.aggregate(agg, COLLECTION_NAME, MongoVideoRepository.MongoVideo::class.java)
        return results.map {
            Video(
                    it.id,
                    it.title,
                    it.description,
                    it.videoId,
                    it.questionId,
                    it.aspectRation,
                    it.thumbnails.default,
                    it.channel.title
            )
        }
    }

    @Document(collection = COLLECTION_NAME)
    data class MongoVideo(
            var title: String,
            var description: String,
            var videoId: String,
            var questionId: String,
            var aspectRation: Float,
            var thumbnails: Thumbnails,
            var channel: Channel,
            var retrievalPosition: Int
    ) {
        @Id
        lateinit var id: String
    }

    data class Thumbnails(
            var high: String,
            var default: String,
            var medium: String
    )

    data class Channel(
            var title: String,
            var id: String
    )
}