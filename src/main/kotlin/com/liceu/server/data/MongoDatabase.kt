package com.liceu.server.data

import com.liceu.server.domain.user.UpdateDesiredCourse
import com.liceu.server.domain.user.UserBoundary
import com.restfb.types.Post
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
        const val ACTIVITIES_COLLECTION = "activities"
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
            var followers: List<ObjectId>?,
            var following: List<ObjectId>?,
            var fcmToken: String?=null,
            var lastAccess: Date?=null,
            var desiredCourse: String?=null,
            var telephoneNumber: String?=null,
            var savedPosts: List<ObjectId>?=null,
            val postsAutomaticApproval: Boolean?=false,
            val isFounder: Boolean?=false
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
    ) {
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
            val tags: List<String>,
            val comments: List<MongoComment>?,
            val likes: Int?,
            val dislikes: Int?,
            val approvalFlag: Boolean?=null
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
            val tags: List<String>,
            val comments: List<MongoComment>?,
            val likes: Int?,
            val dislikes: Int?
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
            val submissionDate: Date?,
            val downloadChallenger: Boolean?=false,
            val downloadChallenged: Boolean?=false
    ){
        @Id
        lateinit var id: ObjectId
    }

    @Document(collection = MongoDatabase.POST_COLLECTION)
    data class MongoPost(
        val userId: ObjectId,
        val type: String,
        val description: String,
        val image: MongoPostImage?,
        val video: MongoPostVideo?,
        val multipleImages: List<MongoPostImage>?,
        val submissionDate: Date,
        val comments: List<MongoComment>?,
        val questions: List<MongoPostQuestions>?,
        val document: MongoPostDocument?,
        val approvalFlag: Boolean?=null,
        val likes: Int?=null

        ){
        @Id
        lateinit var id: ObjectId
    }

    data class MongoPostDocument(
            var id: ObjectId?,
            val title: String?,
            val type: String?,
            val documentURL: String?
    )

    data class MongoPostVideo(
            val videoUrl: String?,
            val thumbnails: MongoPostThumbnails?
    )

    data class MongoPostImage(
            var title: String?,
            var type: String?,
            val imageURL: String?
    )

    data class MongoPostThumbnails(
            var high: String?,
            var default: String?,
            var medium: String?
    )

    data class MongoComment(
            var id: ObjectId,
            var userId: ObjectId,
            var author: String,
            var comment: String
    )

    data class MongoPostQuestions(
            var question: String,
            var correctAnswer: String,
            var otherAnswers: List<String>
    )



    @Document(collection = MongoDatabase.ACTIVITIES_COLLECTION)
    data class MongoActivities(
        val userId: ObjectId,
        val type: String,
        val params: HashMap<String,Any>,
        val submissionDate: Date
    ){
        @Id
        lateinit var id: ObjectId
    }

}