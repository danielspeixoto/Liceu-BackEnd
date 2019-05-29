package com.liceu.server.domain.question

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "questions")
data class Question(
        val id: String,
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
)