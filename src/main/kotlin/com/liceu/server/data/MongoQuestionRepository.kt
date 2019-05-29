package com.liceu.server.data

import com.liceu.server.domain.question.Question
import com.liceu.server.domain.question.QuestionBoundary
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.stereotype.Repository

@Repository
class MongoQuestionRepository(
        @Autowired val repo: QuestionRepository
) : QuestionBoundary.IRepository {

    override fun randomByTags(tags: List<String>, amount: Int): List<Question> {
        return listOf()
    }

    override fun addTag(id: String, tag: String) {
        println(repo.count())
    }

    @Document(collection="questions")
    data class MongoQuestion(
            val view: List<Byte>,
            val source: String,
            val variant: String,
            val edition: Int,
            val number: Int,
            val domain: String,
            val answer: Int,
            val tags: List<String>,
            val itemCode: String,
            val referenceId: String,
            val stage: Int,
            val width: Int,
            val height: Int
    ) {
        @Id
        lateinit var id: String
    }
}