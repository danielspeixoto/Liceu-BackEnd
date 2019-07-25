package com.liceu.server.data

import com.liceu.server.domain.challenge.Challenge
import com.liceu.server.domain.challenge.ChallengeBoundary
import com.liceu.server.domain.challenge.ChallengeToInsert
import com.liceu.server.domain.trivia.TriviaQuestion
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository


@Repository
class MongoChallengeRepository(
        private val template: MongoTemplate
) : ChallengeBoundary.IRepository {

    override fun createChallenge(matchmaking: ChallengeToInsert): String {
        val result = template.insert(MongoDatabase.MongoChallenge(
                ObjectId(matchmaking.challenger),
                ObjectId(matchmaking.challenged),
                matchmaking.answersChallenger,
                matchmaking.answersChallenged,
                matchmaking.scoreChallenger,
                matchmaking.scoreChallenged,
                matchmaking.triviaQuestionsUsed.map {
                    MongoDatabase.MongoChallengeTrivia(
                            ObjectId(it.id),
                            ObjectId(it.userId),
                            it.question,
                            it.correctAnswer,
                            it.wrongAnswer,
                            it.tags
                    )
                }
        ))
        return result.id.toHexString()
    }

    override fun findById(id: String): Challenge? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun matchMaking(challengedId: String): Challenge? {
        val result = template.findAndModify(
                Query.query(Criteria.where("challenged").`is`(null) .and("answersChallenger").not().size(0)),
                Update.update("challenged", ObjectId(challengedId)),
                MongoDatabase.MongoChallenge::class.java
        )
        result?.let {
            return Challenge(
                    it.id.toHexString(),
                    it.challenger.toHexString(),
                    challengedId,
                    it.answersChallenger,
                    it.answersChallenged,
                    it.scoreChallenger,
                    it.scoreChallenged,
                    it.triviaQuestionsUsed.map { triviaQuestion ->
                        TriviaQuestion(
                                triviaQuestion.id.toHexString(),
                                triviaQuestion.userId.toHexString(),
                                triviaQuestion.question,
                                triviaQuestion.correctAnswer,
                                triviaQuestion.wrongAnswer,
                                triviaQuestion.tags
                        )
                    }
            )
        }
        return null
    }

    fun toChallenge(answer: MongoDatabase.MongoChallenge): Challenge {
        return Challenge(
                answer.id.toString(),
                answer.challenger.toString(),
                answer.challenged.toString(),
                answer.answersChallenger,
                answer.answersChallenged,
                answer.scoreChallenger,
                answer.scoreChallenged,
                answer.triviaQuestionsUsed.map { toChallengeTrivia(it) }
        )
    }

    fun toChallengeTrivia(answer: MongoDatabase.MongoChallengeTrivia): TriviaQuestion{
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
