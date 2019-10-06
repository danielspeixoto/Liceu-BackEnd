package com.liceu.server.data

import com.liceu.server.data.util.converters.toChallenge
import com.liceu.server.domain.challenge.Challenge
import com.liceu.server.domain.challenge.ChallengeBoundary
import com.liceu.server.domain.challenge.ChallengeToInsert
import com.liceu.server.domain.global.ItemNotFoundException
import com.liceu.server.domain.trivia.PostComment
import com.liceu.server.domain.trivia.TriviaQuestion
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository


@Repository
class MongoChallengeRepository(
        private val template: MongoTemplate
) : ChallengeBoundary.IRepository {

    override fun createChallenge(matchmaking: ChallengeToInsert): Challenge {
        val result = template.insert(MongoDatabase.MongoChallenge(
                matchmaking.challenger,
                matchmaking.challenged,
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
                            it.tags,
                            it.comments?.map {
                                MongoDatabase.MongoComment(
                                        ObjectId(it.id),
                                        ObjectId(it.userId),
                                        it.author,
                                        it.comment
                                )
                            },
                            it.likes,
                            it.dislikes
                    )
                },
                matchmaking.submissionDate
        ))
        return toChallenge(result)
    }

    override fun matchMaking(challengedId: String): Challenge? {
        val result = template.findAndModify(
                Query.query(Criteria
                        .where("challenged").`is`(null)
                        .and("challenger").ne(challengedId)
                        .and("answersChallenger").not().size(0)
                ),
                Update.update("challenged", challengedId),
                MongoDatabase.MongoChallenge::class.java
        )
        result?.let {
            return Challenge(
                    it.id.toHexString(),
                    it.challenger,
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
                                triviaQuestion.tags,
                                triviaQuestion.comments?.map {
                                    PostComment(
                                            it.id.toHexString(),
                                            it.userId.toHexString(),
                                            it.author,
                                            it.comment
                                    )
                                },
                                triviaQuestion.likes,
                                triviaQuestion.dislikes
                        )
                    },
                    it.submissionDate,
                    it.downloadChallenger,
                    it.downloadChallenged
            )
        }
        return null
    }

    override fun verifyDirectChallenges(challengedId: String): Challenge? {
        val result = template.findOne(
                Query.query(Criteria
                        .where("challenged").isEqualTo(challengedId)
                        .and("answersChallenged").size(0)
                        .and("downloadChallenged").`is`(false)
                ),
                MongoDatabase.MongoChallenge::class.java
        )
        result?.let {
            return updateDownloadAttribute(result.id.toHexString())
        }
        return null
    }

    override fun updateAnswers(challengeId: String, isChallenger: Boolean, answers: List<String>, score: Int): Long {
        var player = "Challenger"
        if (!isChallenger) {
            player = "Challenged"
        }
        val update = Update()
        update.set("answers" + player, answers)
        update.set("score" + player, score)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(challengeId))),
                update,
                MongoDatabase.MongoChallenge::class.java
        )
        return result.modifiedCount
    }

    override fun findById(challengeId: String): Challenge {
        val match = Aggregation.match(Criteria("_id").isEqualTo(ObjectId(challengeId)))
        val agg = Aggregation.newAggregation(match)
        val results = template.aggregate(agg, MongoDatabase.CHALLENGE_COLLECTION, MongoDatabase.MongoChallenge::class.java)
        val challengeRetrieved = results.map { toChallenge(it) }
        if (challengeRetrieved.isNotEmpty()) {
            return challengeRetrieved[0]
        } else {
            throw ItemNotFoundException()
        }
    }

    override fun findDirectChallengesById(challengeId: String): Challenge? {
        val result = template.findOne(
                Query.query(Criteria
                        .where("_id").isEqualTo(ObjectId(challengeId))
                            .and("downloadChallenged").`is`(false)
                ),
                MongoDatabase.MongoChallenge::class.java
        )
        result?.let {
            return updateDownloadAttribute(result.id.toHexString())
        }
        return null
    }

    override fun updateDownloadAttribute(challengeId: String): Challenge {
        val update = Update()
        update.set("downloadChallenged", true)
        template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(challengeId))),
                update,
                MongoDatabase.MongoChallenge::class.java
        )
        val newChallenge = template.findOne(Query.query(Criteria.where("_id").isEqualTo(ObjectId(challengeId))),
                MongoDatabase.MongoChallenge::class.java
        )
        return toChallenge(newChallenge!!)
    }
}
