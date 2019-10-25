package com.liceu.server.data

import com.liceu.server.data.util.converters.toUser
import com.liceu.server.domain.challenge.Challenge
import com.liceu.server.domain.global.ItemNotFoundException
import com.liceu.server.domain.post.Post
import com.liceu.server.domain.trivia.PostComment
import com.liceu.server.domain.trivia.TriviaQuestion
import com.liceu.server.domain.user.User
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.domain.user.UserForm
import com.liceu.server.domain.util.dateFunctions.DateFunctions.lastTwoWeeks
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.*
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.findOne
import org.springframework.stereotype.Repository
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.query.*
import java.util.*


@Repository
class MongoUserRepository(
        val template: MongoTemplate
) : UserBoundary.IRepository {

    override fun save(user: UserForm): String {
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
                user.website,
                user.followers?.map { ObjectId(it) },
                user.following?.map { ObjectId(it) }
        )
        return template.save(mongoUser).id.toHexString()
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

    override fun updateAddUserToProducerFollowerList(userId: String, producerId: String): Long {
        val update = Update()
        update.addToSet("followers", ObjectId(userId))
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(producerId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateRemoveUserToProducerFollowerList(userId: String, producerId: String): Long {
        val update = Update()
        update.pull("followers", ObjectId(userId))
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(producerId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateAddProducerToFollowingList(userId: String, producerId: String): Long {
        val update = Update()
        update.addToSet("following", ObjectId(producerId))
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateRemoveProducerToFollowingList(userId: String, producerId: String): Long {
        val update = Update()
        update.pull("following", ObjectId(producerId))
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateProfileImage(userId: String, imageURL: String): Long {
        val update = Update()
        update.set("picture.$.url", imageURL)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateFcmTokenFromUser(userId: String, fcmToken: String): Long {
        val update = Update()
        update.set("fcmToken", fcmToken)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateLastAccess(userId: String, loginAccess: Date): Long {
        val update = Update()
        update.set("lastAccess",loginAccess)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateDesiredCourse(userId: String, course: String): Long {
        val update = Update()
        update.set("desiredCourse",course)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateTelephoneNumber(userId: String, telephoneNumber: String): Long {
        val update = Update()
        update.set("telephoneNumber",telephoneNumber)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updatePostsAutomaticApprovalFlag(userId: String): Long {
        val update = Update()
        update.set("postsAutomaticApproval",true)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateAddPostToBeSaved(userId: String, postId: String): Long {
        val update = Update()
        update.addToSet("savedPosts",ObjectId(postId))
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(userId))),
                update,
                MongoDatabase.MongoUser::class.java
        )
        return result.modifiedCount
    }

    override fun updateRemovePostSaved(userId: String, postId: String): Long {
        val update = Update()
        update.pull("savedPosts",ObjectId(postId))
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
        val userRetrieved = results.map { toUser(it) }
        if (userRetrieved.isNotEmpty()) {
            return userRetrieved[0]
        } else {
            throw ItemNotFoundException()
        }
    }

    override fun getUserBySocialId(socialId: String): User? {
        val result = template.findOne(
                Query.query(Criteria.where("facebookId").isEqualTo(socialId)),
                MongoDatabase.MongoUser::class.java
        )
        result?.let {
            return toUser(it)
        }
        return null
    }


    override fun userExists(userId: String): Boolean {
        val match = Aggregation.match(Criteria("_id").isEqualTo(ObjectId(userId)))
        val agg = Aggregation.newAggregation(match)
        val results = template.aggregate(
                agg, MongoDatabase.USER_COLLECTION, MongoDatabase.MongoUser::class.java)
        return results.count() > 0
    }

    override fun getChallengesFromUserById(userId: String, amount: Int, start: Int): List<Challenge> {
        val match = Aggregation.match(Criteria()
                .orOperator(
                        Criteria.where("challenger").isEqualTo(userId),
                        Criteria.where("challenged").isEqualTo(userId)
                )
                .and("answersChallenger").not().size(0))
        val sortByDate = sort(Sort.Direction.DESC, "submissionDate")
        val limitOfReturnedChallenges = limit(amount.toLong())
        val skip = Aggregation.skip(start.toLong())
        val agg = Aggregation.newAggregation(match,sortByDate,skip,limitOfReturnedChallenges)
        val results = template.aggregate(agg, MongoDatabase.CHALLENGE_COLLECTION, MongoDatabase.MongoChallenge::class.java)
        return results.map {
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
    }

    override fun getUsersByNameUsingLocation(nameSearched: String, longitude: Double?, latitude: Double?, amount: Int): List<User> {
        val match: MatchOperation
        val geoMatch: GeoNearOperation?
        val agg: Aggregation
        val sample = Aggregation.sample(amount.toLong())
        if(latitude != null && longitude != null){
            val location = GeoJsonPoint(longitude,latitude)
            geoMatch = Aggregation.geoNear(NearQuery.near(location), "distance")
            match = Aggregation.match(Criteria("name")
                    .regex(nameSearched, "ix"))
            agg = Aggregation.newAggregation(geoMatch,match,sample)
        } else{
            match =  Aggregation.match(Criteria("name")
                    .regex(nameSearched,"ix"))
            agg = Aggregation.newAggregation(match,sample)
        }
        val results = template.aggregate(agg, MongoDatabase.USER_COLLECTION, MongoDatabase.MongoUser::class.java)
        return results.map { toUser(it) }
    }

    override fun getActiveUser(userId: String): User {
        val query = Query()
        var match: MatchOperation
        val sample: SampleOperation = sample(1)
        val sort: SortOperation
        val limits: LimitOperation
        val agg: Aggregation
        query.addCriteria(Criteria.where("lastAccess").ne(null))
        val results = template.count(query,MongoDatabase.MongoUser::class.java).toInt()

        if(results <= 200){
            match = match(Criteria("_id").ne(ObjectId(userId)))
            sort = sort(Sort.Direction.DESC, "_id")
            limits = limit(200)
            agg = newAggregation(match,sort,sample,limits)
        }else{
            match = match(Criteria("lastAccess").gte(lastTwoWeeks())
                    .and("_id").ne(ObjectId(userId)))
            agg = newAggregation(match,sample)
        }
        val usersRetrieved = template.aggregate(agg, MongoDatabase.USER_COLLECTION, MongoDatabase.MongoUser::class.java)
        val users = usersRetrieved.map { toUser(it) }
        return users[0]
    }

    override fun getPostsSaved(userId: String, amount: Int, start: Int): List<String>? {
        val result = template.findOne(Query(Criteria("_id")
                .isEqualTo(ObjectId(userId)))
                .limit(amount)
                .skip(start.toLong()),
                MongoDatabase.MongoUser::class.java,
                MongoDatabase.USER_COLLECTION)
        return result?.savedPosts?.map { it.toHexString() }
    }
}

