package com.liceu.server.data

import com.liceu.server.domain.challenge.Challenge
import com.liceu.server.domain.challenge.ChallengeBoundary
import com.liceu.server.domain.challenge.ChallengeToInsert
import com.liceu.server.domain.trivia.TriviaQuestion
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository


@Repository
class MongoChallengeRepository(
        val template: MongoTemplate
): ChallengeBoundary.IRepository  {

    override fun createChallenge(matchmaking: ChallengeToInsert): String {
        val result = template.insert(MongoDatabase.MongoChallenge(
                ObjectId(matchmaking.challenger),
                ObjectId(matchmaking.challenged),
                matchmaking.answersChallenger,
                matchmaking.answersChallenged,
                matchmaking.scoreChallenger,
                matchmaking.scoreChallenged,
                matchmaking.triviaQuestionsUsed.map { MongoDatabase.MongoTriviaQuestion(
                        ObjectId(it.userId),
                        it.question,
                        it.correctAnswer,
                        it.wrongAnswer,
                        it.tags
                )  }
        ))
        return result.id.toHexString()
    }

    override fun updateChallenge(): Challenge {
        //rever
        val match = Aggregation.match(Criteria.where("challenged").isEqualTo(null))
        val agg = Aggregation.newAggregation(match)
        val result= template.aggregate(agg,MongoDatabase.CHALLENGE_COLLECTION, MongoDatabase.MongoChallenge::class.java)
        val challengerRetrieved = result.map {
            Challenge(
                  it.id.toString(),
                  it.challenger.toString(),
                  it.challenged.toString(),
                  it.answersChallenger,
                  it.answersChallenged,
                  it.scoreChallenger,
                  it.scoreChallenged,
                  it.triviaQuestionsUsed.map { TriviaQuestion(
                          it.id.toString(),
                          it.userId.toString(),
                          it.question,
                          it.correctAnswer,
                          it.wrongAnswer,
                          it.tags
                  ) }
            )
        }
        if(challengerRetrieved.isNotEmpty()){
            return challengerRetrieved[0]
        }else{
            return null!!
        }
    }
}
