package com.liceu.server.domain.tag

class TagBoundary {

    interface IRepository {

        @Throws(Error::class)
        fun incrementCount(tagName: String)
        @Throws(Error::class)
        fun suggestions(query: String, minQuestions: Int): List<Tag>
    }

    interface ISuggestions {
        @Throws(Error::class)
        fun run(query: String, minQuestions: Int): List<Tag>
    }
}