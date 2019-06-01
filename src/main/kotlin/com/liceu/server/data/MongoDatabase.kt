package com.liceu.server.data

import org.bson.types.Binary
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

class MongoDatabase {

    companion object {
        const val QUESTION_COLLECTION = "questions"
        const val VIDEO_COLLECTION = "relatedVideos"
        const val TAG_COLLECTION = "tag"
    }

    @Document(collection = MongoDatabase.VIDEO_COLLECTION)
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

    @Document(collection = MongoDatabase.QUESTION_COLLECTION)
    data class MongoQuestion(
            var view: ByteArray,
            var source: String,
            var variant: String,
            var edition: Int,
            var number: Int,
            var domain: String,
            var answer: Int,
            var tags: List<String>,
            var itemCode: String,
            var referenceId: String,
            var stage: Int,
            var width: Int,
            var height: Int
    ) {
        @Id
        lateinit var id: String
    }

    @Document(collection = MongoDatabase.TAG_COLLECTION)
    data class MongoTag(
            var name: String,
            var amount: Int
    ) {
        @Id
        lateinit var id: String
    }
}