package com.liceu.server.data

import com.liceu.server.domain.post.PostBoundary
import com.liceu.server.domain.post.PostToInsert
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
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
                postToInsert.text,
                MongoDatabase.MongoPostImage(
                        postToInsert.image?.imageURL,
                        postToInsert.image?.description
                ),
                MongoDatabase.MongoPostVideo(
                        postToInsert.video?.videoUrl,
                        postToInsert.video?.description,
                        postToInsert.video?.thumbnails
                )

        ))
        return result.id.toHexString()
    }

    override fun getPosts(date: Date, amount: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}