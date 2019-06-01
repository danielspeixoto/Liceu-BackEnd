package com.liceu.server.domain.question

import com.liceu.server.domain.global.TagAlreadyExistsException
import com.liceu.server.domain.global.QuestionNotFoundException
import com.liceu.server.domain.video.Video

class QuestionBoundary {

    interface IRepository {

        @Throws(Error::class)
        fun randomByTags(tags: List<String>, amount: Int): List<Question>
        @Throws(TagAlreadyExistsException::class, QuestionNotFoundException::class)
        fun addTag(id: String, tag: String)
        @Throws(Error::class)
        fun videos(id: String, start: Int, count: Int): List<Video>

    }

    interface IRandom {

        @Throws(Error::class)
        fun run(tags: List<String>, amount: Int): List<Question>

    }

    interface IAddTag {

        @Throws(TagAlreadyExistsException::class, QuestionNotFoundException::class)
        fun run(id: String, tag: String)

    }

    interface IVideos {

        @Throws(Error::class)
        fun run(id: String, start: Int, count: Int): List<Video>

    }
}