package com.liceu.server.domain.question

import com.liceu.server.domain.global.TagAlreadyExistsException
import com.liceu.server.domain.global.QuestionNotFoundException
import com.liceu.server.domain.video.Video

class QuestionBoundary {

    interface IRepository {

        @Throws(Error::class)
        fun randomByTags(tags: List<String>, amount: Int): List<Question>
        @Throws(Error::class)
        fun videos(id: String, start: Int, count: Int): MutableList<Video>
        fun getQuestionById(questionId: String): Question

    }

    interface IQuestionById {

        @Throws(Error::class)
        fun run(questionId: String): Question

    }

    interface IRandom {

        @Throws(Error::class)
        fun run(tags: List<String>, amount: Int): List<Question>

    }

    interface IVideos {

        @Throws(Error::class)
        fun run(id: String, start: Int, count: Int): List<Video>

    }
}