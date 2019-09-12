package com.liceu.server.domain.trivia

import com.liceu.server.data.MongoDatabase
import org.bson.types.ObjectId


data class TriviaQuestionToInsert(
        val userId: String,
        val question: String,
        val correctAnswer: String,
        val wrongAnswer: String,
        val tags: List<String>
    )

    data class TriviaQuestionSubmission(
        val userId: String,
        val question: String,
        val correctAnswer: String,
        val wrongAnswer: String,
        val tags: List<String>
    )

    data class TriviaQuestion(
        val id: String,
        val userId: String,
        val question: String,
        val correctAnswer: String,
        val wrongAnswer: String,
        val tags: List<String>,
        val comments: List<PostComment>?,
        val likes: Int?,
        val dislikes: Int?
    )


    data class PostComment (
            var id: String,
            var userId: String,
            var author: String,
            var comment: String
    )
