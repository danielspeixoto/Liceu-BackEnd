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
                    MongoDatabase.MongoTriviaQuestion(
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

    override fun matchMaking(challengedId: String): Challenge? {
        val result = template.findAndModify(
//                TODO Check if challenger has answered their questions
                Query.query(Criteria.where("challenged").ne(null)),
                Update.update("challenged", challengedId),
                MongoDatabase.MongoChallenge::class.java
        )
        result?.let {
            return Challenge(
                    it.id.toHexString(),
                    it.challenger.toHexString(),
                    it.challenged?.toHexString(),
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
}
