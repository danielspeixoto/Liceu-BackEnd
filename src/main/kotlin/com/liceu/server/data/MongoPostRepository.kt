package com.liceu.server.data

import com.liceu.server.data.util.converters.toPost
import com.liceu.server.domain.post.*
import com.liceu.server.domain.user.User
import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.ZoneOffset
import java.util.*


@Repository
class MongoPostRepository(
        val template: MongoTemplate
): PostBoundary.IRepository{

    override fun insertPost(postToInsert: PostToInsert): String {
        val result = template.insert(MongoDatabase.MongoPost(
                ObjectId(postToInsert.userId),
                postToInsert.type,
                postToInsert.description,
                MongoDatabase.MongoPostImage(
                        postToInsert.image?.title,
                        postToInsert.image?.type,
                        postToInsert.image?.imageData
                ),
                MongoDatabase.MongoPostVideo(
                        postToInsert.video?.videoUrl,
                        MongoDatabase.MongoPostThumbnails(
                                postToInsert.video?.thumbnails?.high,
                                postToInsert.video?.thumbnails?.default,
                                postToInsert.video?.thumbnails?.medium
                        )
                ),
                postToInsert.multipleImages?.map {
                    MongoDatabase.MongoPostImage (
                            it.title,
                            it.type,
                            it.imageData
                    )
                },
                postToInsert.submissionDate,
                postToInsert.comments?.map { MongoDatabase.MongoComment(
                        ObjectId(it.id),
                        ObjectId(it.userId),
                        it.author,
                        it.comment
                ) },
                postToInsert.questions?.map { MongoDatabase.MongoPostQuestions(
                        it.question,
                        it.correctAnswer,
                        it.otherAnswers
                ) },
                null,
                postToInsert.approvalFlag
        ))
        return result.id.toHexString()
    }

    override fun getPostById(postId: String): Post {
        val match = Aggregation.match(Criteria("_id").isEqualTo(ObjectId(postId)))
        val agg = Aggregation.newAggregation(match)
        val results = template.aggregate(agg, MongoDatabase.POST_COLLECTION, MongoDatabase.MongoPost::class.java)
        val postRetrieved = results.map { toPost(it) }
        return postRetrieved[0]
    }

    override fun getPostsForFeed(user: User, date: Date, amount: Int, start: Int): List<Post> {
        if(user.following.isNullOrEmpty()){
            return emptyList()
        }
        val objectsIds = user.following!!.map {ObjectId(it)}
        val match = Aggregation.match(Criteria.where("userId").`in`(objectsIds)
                .and("submissionDate").lte(date))
        val sortByDate = Aggregation.sort(Sort.Direction.DESC, "submissionDate")
        val limitOfReturnedPosts = Aggregation.limit(amount.toLong())
        val skip = Aggregation.skip(start.toLong())
        val agg = Aggregation.newAggregation(match,sortByDate,skip,limitOfReturnedPosts)
        val results = template.aggregate(agg, MongoDatabase.POST_COLLECTION, MongoDatabase.MongoPost::class.java)
        return results.map { toPost(it) }
    }

    override fun getPostFromUser(userId: String,amount: Int,start: Int): List<Post> {
        val match = Aggregation.match(Criteria("userId").isEqualTo(ObjectId(userId))
                .and("approvalFlag").isEqualTo(true))
        val sortByDate = Aggregation.sort(Sort.Direction.DESC, "submissionDate")
        val skip = Aggregation.skip(start.toLong())
        val limit = Aggregation.limit(amount.toLong())
        val agg = Aggregation.newAggregation(match,sortByDate,skip,limit)
        val results = template.aggregate(agg, MongoDatabase.POST_COLLECTION, MongoDatabase.MongoPost::class.java)
        return results.map { toPost(it) }
    }

    override fun getPostsFromOwner(userId: String,amount: Int,start: Int): List<Post> {
        val match = Aggregation.match(Criteria("userId").isEqualTo(ObjectId(userId)))
        val sortByDate = Aggregation.sort(Sort.Direction.DESC, "submissionDate")
        val skip = Aggregation.skip(start.toLong())
        val limit = Aggregation.limit(amount.toLong())
        val agg = Aggregation.newAggregation(match,sortByDate,skip,limit)
        val results = template.aggregate(agg, MongoDatabase.POST_COLLECTION, MongoDatabase.MongoPost::class.java)
        return results.map { toPost(it) }
    }

    override fun getRandomPosts(amount: Int): List<Post> {
        if(amount == 0){
            return emptyList()
        }
        val match = Aggregation.match(Criteria("approvalFlag").isEqualTo(true))
        val sample = Aggregation.sample(amount.toLong())
        val agg = Aggregation.newAggregation(match,sample)
        val results = template.aggregate(agg, MongoDatabase.POST_COLLECTION, MongoDatabase.MongoPost::class.java)
        return results.map { toPost(it) }
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

    override fun updateDocumentPost(postId: String, title: String, type:String, documentURL: String): Long {
        val update = Update()
        val id = ObjectId()
        val documentToBeInserted = MongoDatabase.MongoPostDocument(
            id,
            title,
            type,
            documentURL
        )
        update.addToSet("documents", documentToBeInserted)
        val result = template.updateFirst(
                Query.query(Criteria.where("_id").isEqualTo(ObjectId(postId))),
                update,
                MongoDatabase.MongoPost::class.java
        )
        return result.modifiedCount
    }

    override fun updateLike(postId: String): Long {
        val update = Update()
        update.inc("likes",1)
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

    override fun countApprovedPosts(userId: String): Int {
        val query = Query()
        query.addCriteria(Criteria.where("userId").isEqualTo(ObjectId(userId))
                .and("approvalFlag").isEqualTo(true))
        return template.count(query,MongoDatabase.MongoPost::class.java).toInt()
    }

    override fun postExists(postId: String): Boolean {
        val query = Query()
        query.addCriteria(Criteria.where("_id").isEqualTo(ObjectId(postId)))
        val results = template.count(query,MongoDatabase.MongoPost::class.java).toInt()
        if(results > 0){
            return true
        }
        return false
    }
}