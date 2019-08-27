package com.liceu.server.data

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.index.GeospatialIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.sql.Timestamp
import java.util.*
import kotlin.collections.HashMap

class MongoDatabase {

    companion object {
        const val QUESTION_COLLECTION = "questions"
        const val VIDEO_COLLECTION = "relatedVideos"
        const val TAG_COLLECTION = "tag"
        const val USER_COLLECTION = "user"
        const val GAME_COLLECTION = "game"
        const val REPORT_COLLECTION = "report"
        const val TRIVIA_COLLECTION = "trivia"
        const val CHALLENGE_COLLECTION = "challenge"
        const val POST_COLLECTION = "post"
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

    data class MongoPicture(
            var url: String,
            var width: Int,
            var height: Int
    )

    @Document(collection = MongoDatabase.USER_COLLECTION)
    data class MongoUser(
            var name: String,
            var email: String,
            var picture: MongoPicture,
            @Indexed(unique=true) var facebookId: String,
            @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE) var location: GeoJsonPoint?,
            var state: String?,
            var school: String?,
            var age: Int?,
            var youtubeChannel: String?,
            var instagramProfile: String?,
            var description: String?,
            var website: String?,
            var amountOfFollowers: Int?,
            var following: List<String>?
    ) {
        @Id
        lateinit var id: ObjectId


    }

    data class MongoAnswer(
            val questionId: ObjectId,
            val correctAnswer: Int,
            val selectedAnswer: Int
    )

    @Document(collection = MongoDatabase.GAME_COLLECTION)
    data class MongoGame(
            val userId: ObjectId,
            val answers: List<MongoAnswer>,
            @Indexed val submissionDate: Date,
            val timeSpent: Int,
            val score: Int?
    ) :Comparable<MongoGame>{
        override fun compareTo(other: MongoGame): Int {
            //funcao de comparacao - answers, lista de resposta - timespent, tempo de jogo -
            var countO1 = 0
            for(item in this.answers){
                if(item.correctAnswer == item.selectedAnswer){
                    countO1 ++
                }
            }
            var countO2 = 0
            for(item in other.answers){
                if(item.correctAnswer == item.selectedAnswer){
                    countO2 ++
                }
            }
            if(countO1 == countO2){
                return this.timeSpent - other.timeSpent
            }
            return countO2 - countO1
        }

        @Id
        lateinit var id: ObjectId

    }

    @Document(collection = MongoDatabase.REPORT_COLLECTION)
    data class MongoReport(
            val userId: ObjectId,
            val message: String,
            val tags: List<String>,
            val params: HashMap<String,Any>,
            val submissionDate: Date
    ) {
        @Id
        lateinit var id: ObjectId
    }

    @Document(collection = MongoDatabase.TRIVIA_COLLECTION)
    data class MongoTriviaQuestion(
            val userId: ObjectId,
            val question: String,
            val correctAnswer: String,
            val wrongAnswer: String,
            val tags: List<String>
    ) {
        @Id
        lateinit var id: ObjectId
    }



    data class MongoChallengeTrivia(
            val id: ObjectId,
            val userId: ObjectId,
            val question: String,
            val correctAnswer: String,
            val wrongAnswer: String,
            val tags: List<String>
    )



    @Document(collection = MongoDatabase.CHALLENGE_COLLECTION)
    data class MongoChallenge(
            val challenger: String,
            val challenged: String?,
            val answersChallenger: List<String>,
            val answersChallenged: List<String>,
            val scoreChallenger: Int?,
            val scoreChallenged: Int?,
            val triviaQuestionsUsed: List<MongoChallengeTrivia>,
            val submissionDate: Date?
    ){
        @Id
        lateinit var id: ObjectId
    }

    @Document(collection = MongoDatabase.POST_COLLECTION)
    data class MongoPost(
        val userId: ObjectId,
        val type: String,
        val description: String,
        val imageURL: String?,
        val video: MongoPostVideo?,
        val submissionDate: Date
    ){
        @Id
        lateinit var id: ObjectId
    }

//    data class MongoPostImage(
//            val imageURL: String?,
//            val description: String?
//    )

    data class MongoPostVideo(
            val videoUrl: String?,
            //val description: String?,
            val thumbnails: MongoPostThumbnails?
    )

    data class MongoPostThumbnails(
            var high: String?,
            var default: String?,
            var medium: String?
    )

}