package com.liceu.server.data

import org.springframework.data.mongodb.repository.MongoRepository

interface QuestionRepository : MongoRepository<MongoQuestionRepository.MongoQuestion, String>
interface TagRepository : MongoRepository<MongoTagRepository.MongoTag, String>
interface VideoRepository : MongoRepository<MongoVideoRepository.MongoVideo, String>