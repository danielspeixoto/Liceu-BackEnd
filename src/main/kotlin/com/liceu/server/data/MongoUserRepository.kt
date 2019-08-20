package com.liceu.server.data

import com.liceu.server.domain.aggregates.Picture
import com.liceu.server.domain.challenge.Challenge
import com.liceu.server.domain.global.ItemNotFoundException
import com.liceu.server.domain.trivia.TriviaQuestion
import com.liceu.server.domain.user.User
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.domain.user.UserForm
import com.mongodb.client.model.geojson.CoordinateReferenceSystem
import com.mongodb.client.model.geojson.GeoJsonObjectType
import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Position
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.limit
import org.springframework.data.mongodb.core.aggregation.Aggregation.sort
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import java.io.StringReader
import org.springframework.data.mongodb.core.geo.GeoJsonPoint



@Repository
class MongoUserRepository(
        val template: MongoTemplate
) : UserBoundary.IRepository {

    override fun save(user: UserForm): String {
        val query = Query(Criteria("email").isEqualTo(user.email))
        val mongoUser = MongoDatabase.MongoUser(
                user.name, user.email,
                MongoDatabase.MongoPicture(
                        user.picture.url,
                        user.picture.width,
                        user.picture.height
                ),
                user.socialId,
                user.location,
                user.state,
                user.school,
                user.age,
                user.youtubeChannel,
                user.instagramProfile,
                user.description,
                user.website
        )
        val user = template.findOne<MongoDatabase.MongoUser>(query)
        if (user != null) {
            mongoUser.id = user.id
        }
        return template.save(mongoUser).id.toHexString()
    }

    override fun updateAgeFromUser(userId: String, age: Int): Long {
        val update = Update()
        update.set("age", age)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateYoutubeChannelFromUser(userId: String, youtubeChannel: String): Long {
        val update = Update()
        update.set("youtubeChannel", youtubeChannel)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateInstagramProfileFromUser(userId: String, instagramProfile: String): Long {
        val update = Update()
        update.set("instagramProfile", instagramProfile)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateDescriptionFromUser(userId: String, description: String): Long {
        val update = Update()
        update.set("description", description)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateWebsiteFromUser(userId: String, website: String): Long {
        val update = Update()
        update.set("website", website)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateLocationFromUser(userId: String,longitude: Double,latitude: Double,state: String): Long {
        val location = GeoJsonPoint(longitude,latitude)
        val update = Update()
        update.set("location", location)
        update.set("state", state)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateSchoolFromUser(userId: String, school: String): Long {
        val update = Update()
        update.set("school",school)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun getUserById(userId: String): User {
        val match = Aggregation.match(Criteria("_id").isEqualTo(ObjectId(userId)))
        val agg = Aggregation.newAggregation(match)
        val results = template.aggregate(agg, MongoDatabase.USER_COLLECTION, MongoDatabase.MongoUser::class.java)
        val userRetrieved = results.map {
            User(
                    it.id.toHexString(),
                    it.name,
                    it.email,
                    Picture(
                            it.picture.url,
                            it.picture.width,
                            it.picture.height
                    ),
                    it.location,
                    it.state,
                    it.school,
                    it.age,
                    it.youtubeChannel,
                    it.instagramProfile,
                    it.description,
                    it.website
            )
        }
        if (userRetrieved.isNotEmpty()) {
            return userRetrieved[0]
        } else {
            throw ItemNotFoundException()
        }
    }

    override fun getChallengesFromUserById(userId: String): List<Challenge> {
        val match = Aggregation.match(Criteria()
                .orOperator(
                        Criteria.where("challenger").isEqualTo(userId),
                        Criteria.where("challenged").isEqualTo(userId)
                )
                .and("answersChallenger").not().size(0))
        val sortByDate = sort(Sort.Direction.DESC, "submissionDate")
        val limitOfReturnedChallenges = limit(25)

        val agg = Aggregation.newAggregation(match,sortByDate,limitOfReturnedChallenges)
        val results = template.aggregate(agg, MongoDatabase.CHALLENGE_COLLECTION, MongoDatabase.MongoChallenge::class.java)
        val challengesRetrieved = results.map {
            Challenge(
                    it.id.toHexString(),
                    it.challenger,
                    it.challenged,
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
                    },
                    it.submissionDate
            )
        }
        return challengesRetrieved
    }


}

