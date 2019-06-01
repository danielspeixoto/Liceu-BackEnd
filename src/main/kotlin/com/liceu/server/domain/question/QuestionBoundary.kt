package com.liceu.server.domain.question

import com.liceu.server.domain.global.AlreadyExistsException
import com.liceu.server.domain.global.ItemNotFoundException
import com.liceu.server.domain.video.Video

class QuestionBoundary {

    interface IRepository {

        @Throws(Error::class)
        fun randomByTags(tags: List<String>, amount: Int): List<Question>
        @Throws(AlreadyExistsException::class, ItemNotFoundException::class)
        fun addTag(id: String, tag: String)
        @Throws(Error::class)
        fun videos(id: String, start: Int, count: Int): List<Video>

    }

    interface IRandom {

        @Throws(Error::class)
        fun run(tags: List<String>, amount: Int): List<Question>

    }

    interface IAddTag {

        @Throws(AlreadyExistsException::class, ItemNotFoundException::class)
        fun run(id: String, tag: String)

    }

    interface IVideos {

        @Throws(Error::class)
        fun run(id: String, start: Int, count: Int): List<Video>

    }
}