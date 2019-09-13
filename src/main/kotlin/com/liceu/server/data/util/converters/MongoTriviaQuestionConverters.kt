package com.liceu.server.data.util.converters

import com.liceu.server.data.MongoDatabase
import com.liceu.server.domain.trivia.PostComment
import com.liceu.server.domain.trivia.TriviaQuestion


fun toTriviaQuestion(answer: MongoDatabase.MongoTriviaQuestion): TriviaQuestion {
    return TriviaQuestion(
            answer.id.toString(),
            answer.userId.toString(),
            answer.question,
            answer.correctAnswer,
            answer.wrongAnswer,
            answer.tags,
            answer.comments?.map {
                PostComment(
                        it.id.toHexString(),
                        it.userId.toHexString(),
                        it.author,
                        it.comment
                )
            },
            answer.likes,
            answer.dislikes
    )
}

