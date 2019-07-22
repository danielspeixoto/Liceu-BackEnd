package com.liceu.server.data

import org.springframework.data.mongodb.repository.MongoRepository

interface QuestionRepository : MongoRepository<MongoDatabase.MongoQuestion, String>
interface UserRepository : MongoRepository<MongoDatabase.MongoUser, String> {
    fun findByEmail(email: String): MongoDatabase.MongoUser
}
interface VideoRepository : MongoRepository<MongoDatabase.MongoVideo, String>
interface GameRepository: MongoRepository<MongoDatabase.MongoGame, String>
interface ReportRepository: MongoRepository<MongoDatabase.MongoReport, String>
interface TriviaRepository: MongoRepository<MongoDatabase.MongoTriviaQuestion, String>