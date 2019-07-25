package com.liceu.server.data

import com.liceu.server.domain.trivia.TriviaBoundary
import com.liceu.server.domain.trivia.TriviaQuestion
import com.liceu.server.domain.trivia.TriviaQuestionToInsert
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Repository


@Repository
class MongoTriviaRepository(
     val template: MongoTemplate
): TriviaBoundary.IRepository {


    override fun insert(triviaQuestion: TriviaQuestionToInsert): String {
        val result = template.insert(MongoDatabase.MongoTriviaQuestion(
            ObjectId(triviaQuestion.userId),
            triviaQuestion.question,
            triviaQuestion.correctAnswer,
            triviaQuestion.wrongAnswer,
            triviaQuestion.tags
        ))
        return result.id.toHexString()
    }

    override fun randomQuestions(tags: List<String>, amount: Int): List<TriviaQuestion> {
        if(amount == 0){
            return emptyList()
        }
        var match = Aggregation.match(Criteria("tags").all(tags))
        if(tags.isEmpty()){
            match = Aggregation.match(Criteria())
        }
        val sample = Aggregation.sample(amount.toLong())
        val agg = Aggregation.newAggregation(match, sample)

        val results = template.aggregate(agg, MongoDatabase.TRIVIA_COLLECTION, MongoDatabase.MongoTriviaQuestion::class.java)
        return results.map {
            TriviaQuestion(
                    it.id.toHexString(),
                    it.userId.toString(),
                    it.question,
                    it.correctAnswer,
                    it.wrongAnswer,
                    it.tags
            )
        }
    }


    fun toTriviaQuestion(answer: MongoDatabase.MongoTriviaQuestion): TriviaQuestion{
        return TriviaQuestion(
                answer.id.toString(),
                answer.userId.toString(),
                answer.question,
                answer.correctAnswer,
                answer.wrongAnswer,
                answer.tags
        )
    }


}