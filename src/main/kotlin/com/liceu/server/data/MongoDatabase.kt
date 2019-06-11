package com.liceu.server.data

import com.liceu.server.domain.game.Answer
import org.bson.types.Binary
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

class MongoDatabase {

    companion object {
        const val QUESTION_COLLECTION = "questions"
        const val VIDEO_COLLECTION = "relatedVideos"
        const val TAG_COLLECTION = "tag"
        const val USER_COLLECTION = "user"
        const val GAME_COLLECTION = "game"
    }

    @Document(collection = MongoDatabase.VIDEO_COLLECTION)
    data class MongoVideo(
            var title: String,
            var description: String,
            var videoId: String,
            var questionId: ObjectId,
            var aspectRation: Float?,
            var thumbnails: Thumbnails,
            var channel: Channel,
            var retrievalPosition: Int
    ) {
        @Id
        lateinit var id: ObjectId
    }

    data class Thumbnails(
            var high: String,
            var default: String,
            var medium: String
    )

    data class Channel(
            var title: String
    ) {
        @Id
        lateinit var id: String

        constructor(title: String, id: String) : this(title) {
            this.id = id
        }
    }

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
            var height: Int,
            var imageURL: String
    ) {
        @Id
        lateinit var id: ObjectId
    }

    @Document(collection = MongoDatabase.TAG_COLLECTION)
    data class MongoTag(
            var name: String,
            var amount: Int
    ) {
        @Id
        lateinit var id: ObjectId
    }

    data class MongoPicture(
            var url: String,
            var width: Int,
            var height: Int
    )

    @Document(collection = MongoDatabase.USER_COLLECTION)
    data class MongoUser(
            var name: String,
            @Indexed(unique=true) var email: String,
            var picture: MongoPicture,
            var facebookId: String
    ) {
        @Id
        lateinit var id: ObjectId


    }

    data class MongoAnswer(
            val questionId: String,
            val correctAnswer: Int,
            val selectedAnswer: Int
    )

    @Document(collection = MongoDatabase.GAME_COLLECTION)
    data class MongoGame(
            val userId: ObjectId,
            val answers: List<MongoAnswer>,
            @Indexed val submissionDate: Date
    ) {
        @Id
        lateinit var id: ObjectId
    }
}