package com.liceu.server.data.util.converters

import com.liceu.server.data.MongoDatabase
import com.liceu.server.domain.game.Answer
import com.liceu.server.domain.game.Game


fun toGame(doc: MongoDatabase.MongoGame): Game {
    return Game(
            doc.id.toHexString(),
            doc.userId.toHexString(),
            doc.answers.map { Answer(
                    it.questionId.toHexString(),
                    it.correctAnswer,
                    it.selectedAnswer
            ) },
            doc.submissionDate,
            doc.timeSpent,
            doc.score
    )
}