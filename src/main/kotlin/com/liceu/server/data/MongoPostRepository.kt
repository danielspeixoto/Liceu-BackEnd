package com.liceu.server.data

import com.liceu.server.domain.post.*
import com.liceu.server.domain.user.User
import com.mongodb.client.model.Filters.elemMatch
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
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
                postToInsert.imageURL,
                MongoDatabase.MongoPostVideo(
                        postToInsert.video?.videoUrl,
                        MongoDatabase.MongoPostThumbnails(
                            postToInsert.video?.thumbnails?.high,
                            postToInsert.video?.thumbnails?.default,
                            postToInsert.video?.thumbnails?.medium
                        )
                ),
                postToInsert.submissionDate

        ))
        return result.id.toHexString()
    }

    override fun getPostById(postId: String): Post {
        val match = Aggregation.match(Criteria("_id").isEqualTo(ObjectId(postId)))
        val agg = Aggregation.newAggregation(match)
        val results = template.aggregate(agg, MongoDatabase.POST_COLLECTION, MongoDatabase.MongoPost::class.java)
        val postRetrieved = results.map {
            Post(
                it.userId.toHexString(),
                it.type,
                it.description,
                it.imageURL,
                PostVideo(
                    it.video?.videoUrl,
                    PostThumbnails(
                        it.video?.thumbnails?.high,
                        it.video?.thumbnails?.default,
                        it.video?.thumbnails?.medium
                    )
                ),
                it.submissionDate
            )
        }
        return postRetrieved[0]
    }

    override fun getPosts(user: User, date: Date, amount: Int): List<Post> {
        //val match = Aggregation.match(elemMatch("_id")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}