package com.liceu.server.data

import com.liceu.server.domain.post.*
import com.liceu.server.domain.trivia.TriviaQuestion
import com.liceu.server.domain.user.User
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.remove
import org.springframework.stereotype.Repository
import java.math.BigInteger
import java.security.Timestamp
import java.time.Instant
import java.time.ZoneOffset
import java.util.*
import java.text.SimpleDateFormat



@Repository
class MongoPostRepository(
        val template: MongoTemplate
): PostBoundary.IRepository{

    override fun insertPost(postToInsert: PostToInsert): String {
        val result = template.insert(MongoDatabase.MongoPost(
                ObjectId(postToInsert.userId),
                postToInsert.type,
                postToInsert.description,
                postToInsert.imageURL,
                MongoDatabase.MongoPostVideo(
                        postToInsert.video?.videoUrl,
                        MongoDatabase.MongoPostThumbnails(
                            postToInsert.video?.thumbnails?.high,
                            postToInsert.video?.thumbnails?.default,
                            postToInsert.video?.thumbnails?.medium
                        )
                ),
                postToInsert.submissionDate,
                postToInsert.comments?.map { MongoDatabase.MongoComment(
                        ObjectId(postToInsert.userId+Date.from(Instant.now().atOffset(ZoneOffset.ofHours(-3)).toInstant()).toString()),
                        it.userId,
                        it.author,
                        it.comment
                ) },
                postToInsert.questions?.map { MongoDatabase.MongoPostQuestions(
                        it.question,
                        it.correctAnswer,
                        it.otherAnswers
                ) }
        ))
        return result.id.toHexString()
    }

    override fun getPostById(postId: String): Post {
        val match = Aggregation.match(Criteria("_id").isEqualTo(ObjectId(postId)))
        val agg = Aggregation.newAggregation(match)
        val results = template.aggregate(agg, MongoDatabase.POST_COLLECTION, MongoDatabase.MongoPost::class.java)
        val postRetrieved = results.map {toPost(it)}
        return postRetrieved[0]
    }

    override fun getPostsForFeed(user: User, date: Date, amount: Int): List<Post> {
        if(user.following.isNullOrEmpty()){
            return emptyList()
        }
        val objectsIds = user.following!!.map {ObjectId(it)}
        val match = Aggregation.match(Criteria.where("userId").`in`(objectsIds)
                .and("submissionDate").lte(date))
        val sortByDate = Aggregation.sort(Sort.Direction.DESC, "submissionDate")
        val limitOfReturnedPosts = Aggregation.limit(amount.toLong())
        val agg = Aggregation.newAggregation(match,sortByDate,limitOfReturnedPosts)
        val results = template.aggregate(agg, MongoDatabase.POST_COLLECTION, MongoDatabase.MongoPost::class.java)
        return results.map {toPost(it)}
    }

    override fun getPostFromUser(userId: String): List<Post> {
        val match = Aggregation.match(Criteria("userId").isEqualTo(ObjectId(userId)))
        val sortByDate = Aggregation.sort(Sort.Direction.DESC, "submissionDate")
        val agg = Aggregation.newAggregation(match,sortByDate)
        val results = template.aggregate(agg, MongoDatabase.POST_COLLECTION, MongoDatabase.MongoPost::class.java)
        return results.map {toPost(it)}
    }

    override fun getRandomPosts(amount: Int): List<Post> {
        if(amount == 0){
            return emptyList()
        }
        val sample = Aggregation.sample(amount.toLong())
        val agg = Aggregation.newAggregation(sample)

        val results = template.aggregate(agg, MongoDatabase.POST_COLLECTION, MongoDatabase.MongoPost::class.java)
        return results.map {toPost(it)}
    }

    override fun updateListOfComments(postId: String, userId: String,author: String ,comment: String): Long {
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
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(postId))),
                update,
                MongoDatabase.MongoPost::class.java
        )
        return result.modifiedCount
    }

    override fun deletePost(postId: String, userId: String): Post? {
        val result = template.findAndRemove(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(postId))
                        .and("userId").isEqualTo(ObjectId(userId))),
                MongoDatabase.MongoPost::class.java
        )
        return result?.let { toPost(it) }
    }

    fun toPost(mongoPost: MongoDatabase.MongoPost): Post {
        return Post(
                mongoPost.id.toHexString(),
                mongoPost.userId.toHexString(),
                mongoPost.type,
                mongoPost.description,
                mongoPost.imageURL,
                PostVideo(
                        mongoPost.video?.videoUrl,
                        PostThumbnails(
                                mongoPost.video?.thumbnails?.high,
                                mongoPost.video?.thumbnails?.default,
                                mongoPost.video?.thumbnails?.medium
                        )
                ),
                mongoPost.submissionDate,
                mongoPost.comments?.map {
                    PostComment(
                            it.id,
                            it.userId,
                            it.author,
                            it.comment
                    )
                },
                mongoPost.questions?.map {
                    PostQuestions(
                            it.question,
                            it.correctAnswer,
                            it.otherAnswers
                    )
                }
        )
    }
}