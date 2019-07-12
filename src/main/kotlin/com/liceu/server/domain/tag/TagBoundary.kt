package com.liceu.server.domain.tag

class TagBoundary {

    interface IRepository {

        @Throws(Error::class)
        fun incrementCount(tagName: String)
        @Throws(Error::class)
        fun suggestions(query: String, minQuestions: Int): List<Tag>
    }

}