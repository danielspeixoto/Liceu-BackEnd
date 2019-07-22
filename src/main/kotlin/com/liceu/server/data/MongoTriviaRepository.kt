package com.liceu.server.data

import com.liceu.server.domain.trivia.TriviaBoundary
import com.liceu.server.domain.trivia.TriviaQuestion
import com.liceu.server.domain.trivia.TriviaQuestionToInsert
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository


@Repository
class MongoTriviaRepository(
    val template: MongoTemplate
): TriviaBoundary.IRepository {


    override fun insert(triviaQuestion: TriviaQuestionToInsert): String {
        val result = template.insert(MongoDatabase.MongoTriviaQuestion(
            triviaQuestion.question,
            triviaQuestion.correctAnswer,
            triviaQuestion.wrongAnswer
        ))
        return result.id.toHexString()
    }

    fun toTriviaQuestion(answer: MongoDatabase.MongoTriviaQuestion): TriviaQuestion{
        return TriviaQuestion(
                answer.question,
                answer.correctAnswer,
                answer.wrongAnswer
        )
    }


}