package com.liceu.server.data

import org.springframework.data.mongodb.repository.MongoRepository

interface QuestionRepository : MongoRepository<MongoDatabase.MongoQuestion, String>
interface UserRepository : MongoRepository<MongoDatabase.MongoUser, String>
interface TagRepository : MongoRepository<MongoDatabase.MongoTag, String>
interface VideoRepository : MongoRepository<MongoDatabase.MongoVideo, String>