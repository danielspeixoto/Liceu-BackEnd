package com.liceu.server.data

import com.liceu.server.domain.game.Answer
import com.liceu.server.domain.game.Game
import com.liceu.server.domain.game.GameToInsert
import com.liceu.server.domain.game.GameBoundary
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class MongoGameRepository(
        val template: MongoTemplate
): GameBoundary.IRepository {

    override fun insert(game: GameToInsert): String {
        val result = template.insert(MongoDatabase.MongoGame(
                ObjectId(game.userId),
                game.answers.map { MongoDatabase.MongoAnswer(
                        ObjectId(it.questionId),
                        it.correctAnswer,
                        it.selectedAnswer
                ) },
                game.submissionDate
        ))
        return result.id.toHexString()
    }

    fun toGame(doc: MongoDatabase.MongoGame): Game {
        return Game(
                doc.id.toHexString(),
                doc.userId.toHexString(),
                doc.answers.map { Answer(
                        it.questionId.toHexString(),
                        it.correctAnswer,
                        it.selectedAnswer
                ) },
                doc.submissionDate
        )
    }
}