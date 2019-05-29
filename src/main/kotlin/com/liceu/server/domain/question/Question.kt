package com.liceu.server.domain.question

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