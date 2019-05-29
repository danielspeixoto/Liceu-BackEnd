package com.liceu.server.data

import com.liceu.server.domain.question.Question
import com.liceu.server.domain.tag.Tag
import com.liceu.server.domain.video.Video
import org.springframework.data.mongodb.repository.MongoRepository

interface QuestionRepository : MongoRepository<MongoQuestionRepository.MongoQuestion, String> {

}
interface TagRepository : MongoRepository<Tag, String>
interface RelatedVideoRepository : MongoRepository<Video, String>