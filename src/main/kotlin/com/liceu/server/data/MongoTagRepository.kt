package com.liceu.server.data

import com.liceu.server.domain.tag.Tag
import com.liceu.server.domain.tag.TagBoundary
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
class MongoTagRepository(
        @Autowired val repo: TagRepository,
        @Autowired val template: MongoTemplate
): TagBoundary.IRepository {

    companion object {
        const val COLLECTION_NAME = "tag"
    }

    override fun incrementCount(tagName: String) {

    }

    override fun suggestions(query: String, minQuestions: Int): List<Tag> {
        return listOf()
    }

    @Document(collection = COLLECTION_NAME)
    data class MongoTag(
            var name: String,
            var amount: Int
    ) {
        @Id
        lateinit var id: String
    }
}