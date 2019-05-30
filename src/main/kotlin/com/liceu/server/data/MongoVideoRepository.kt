package com.liceu.server.data

import com.liceu.server.domain.video.Video
import com.liceu.server.domain.video.VideoBoundary
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.mapping.Document
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
        return listOf()
    }

    @Document(collection = COLLECTION_NAME)
    data class MongoVideo(
            var title: String,
            var description: String,
            var videoId: String,
            var questionId: String,
            var aspectRation: Float,
            var thumbnails: Thumbnails,
            var channel: Channel
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