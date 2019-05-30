package com.liceu.server.presentation.v1

import com.liceu.server.domain.question.Question
import com.liceu.server.domain.question.QuestionBoundary
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions")
class QuestionController(
        @Autowired val random: QuestionBoundary.IRandom,
        @Autowired val addTag: QuestionBoundary.IAddTag,
        @Autowired val videos: QuestionBoundary.IVideos
) {

    @RequestMapping("/")
    fun get(
            @RequestParam(value="tags[]", defaultValue="") tags: List<String>,
            @RequestParam(value="amount", defaultValue="0") amount: Int
    ): Response<List<Question>> {
        return Response(random.run(tags, amount))
    }

}