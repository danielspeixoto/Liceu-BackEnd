package com.liceu.server.domain.question

import com.liceu.server.domain.exception.AlreadyExistsException
import com.liceu.server.domain.exception.ItemNotFoundException
import com.liceu.server.domain.video.Video

class QuestionBoundary {

    interface IRepository {

        @Throws(Error::class)
        fun randomByTags(tags: List<String>, amount: Int): List<Question>
        @Throws(AlreadyExistsException::class, ItemNotFoundException::class)
        fun addTag(id: String, tag: String)

    }

    interface IRandom {

        @Throws(Error::class)
        fun run(tags: List<String>, amount: Int): List<Question>

    }

    interface IAddTag {

        @Throws(AlreadyExistsException::class, ItemNotFoundException::class)
        fun run(id: String, tag: String)

    }

    interface IRelatedVideos {

        @Throws(Error::class)
        fun run(id: String, start: Int, count: Int): List<Video>

    }
}