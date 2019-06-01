package com.liceu.server.data

import com.liceu.server.domain.global.TagNotFoundException
import com.liceu.server.domain.tag.Tag
import com.liceu.server.domain.tag.TagBoundary
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.*
import org.springframework.stereotype.Repository

@Repository
class MongoTagRepository(
        val template: MongoTemplate
): TagBoundary.IRepository {

    companion object {
        const val COLLECTION_NAME = "tag"
    }

    override fun incrementCount(tagName: String) {
        val result = template.updateFirst(
                Query.query(Criteria.where("name").isEqualTo(tagName)),
                Update().inc("amount", 1),
                COLLECTION_NAME
        )
        if(result.matchedCount < 1) {
            throw TagNotFoundException()
        }
    }

    override fun suggestions(query: String, minQuestions: Int): List<Tag> {
        val result = template.find(
                Query.query(
                        Criteria.where("name").regex(query)
                                .and("amount").gte(minQuestions)
                ),
                MongoDatabase.MongoTag::class.java,
                COLLECTION_NAME
        )
        return result.map {
            Tag(
                    it.id,
                    it.name,
                    it.amount
            )
        }
    }
}