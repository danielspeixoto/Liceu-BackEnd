package com.liceu.server.data

import com.liceu.server.domain.trivia.PostComment
import com.liceu.server.domain.trivia.TriviaBoundary
import com.liceu.server.domain.trivia.TriviaQuestion
import com.liceu.server.domain.trivia.TriviaQuestionToInsert
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
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
            triviaQuestion.tags,
            null,
            null,
            null
        ))
        return result.id.toHexString()
    }

    override fun randomQuestions(tags: List<String>, amount: Int): List<TriviaQuestion> {
        if(amount == 0){
            return emptyList()
        }
        val sample = Aggregation.sample(amount.toLong() + 5)
        val agg = Aggregation.newAggregation(sample)

        val results = template.aggregate(agg, MongoDatabase.TRIVIA_COLLECTION, MongoDatabase.MongoTriviaQuestion::class.java)
        return results.map {toTriviaQuestion(it)}
    }

    override fun updateListOfComments(questionId: String, userId: String, author: String, comment: String): Long {
        val update = Update()
        val id = ObjectId()
        val commentToBeInserted = MongoDatabase.MongoComment(
                id,
                ObjectId(userId),
                author,
                comment
        )
        update.addToSet("comments",commentToBeInserted)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(questionId))),
                update,
                MongoDatabase.MongoTriviaQuestion::class.java
        )
        return result.modifiedCount
    }

    override fun getTriviaById(questionId: String): TriviaQuestion {
        val match = Aggregation.match(Criteria.where("_id").isEqualTo(ObjectId(questionId)))
        val agg = Aggregation.newAggregation(match)
        val result = template.aggregate(agg,MongoDatabase.TRIVIA_COLLECTION,MongoDatabase.MongoTriviaQuestion::class.java)
        val retrievedQuestion = result.map {toTriviaQuestion(it)}
        return retrievedQuestion[0]
    }

    override fun updateLike(questionId: String): Long {
        val update = Update()
        update.inc("likes",1)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(questionId))),
                update,
                MongoDatabase.MongoTriviaQuestion::class.java
        )
        return result.modifiedCount
    }

    override fun updateDislike(questionId: String): Long {
        val update = Update()
        update.inc("dislikes",1)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(questionId))),
                update,
                MongoDatabase.MongoTriviaQuestion::class.java
        )
        return result.modifiedCount
    }

    fun toTriviaQuestion(answer: MongoDatabase.MongoTriviaQuestion): TriviaQuestion{
        return TriviaQuestion(
                answer.id.toString(),
                answer.userId.toString(),
                answer.question,
                answer.correctAnswer,
                answer.wrongAnswer,
                answer.tags,
                answer.comments?.map {
                    PostComment(
                            it.id,
                            it.userId,
                            it.author,
                            it.comment
                    )
                },
                answer.likes,
                answer.dislikes
        )
    }


}