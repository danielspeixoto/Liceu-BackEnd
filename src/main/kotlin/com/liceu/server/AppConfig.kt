package com.liceu.server

import com.mongodb.MongoClient
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import com.mongodb.WriteConcern
import com.mongodb.Mongo
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.MongoTemplate


@Configuration
@ComponentScan
@EnableMongoRepositories
class AppConfig: AbstractMongoConfiguration() {

    override fun mongoClient(): MongoClient {
        val client = MongoClient("127.0.0.1", 27017)
        println(client.getDatabase("b").getCollection("questions").count())
        return client
    }

    override fun getDatabaseName(): String {
        return "b"
    }

    @Bean
    override fun mongoTemplate(): MongoTemplate {
        return MongoTemplate(mongoClient(), databaseName)
    }

//    @Autowired
//    lateinit var repo: QuestionRepository
//
//    @Bean
//    fun data(): QuestionBoundary.IRepository {
//        return MongoQuestionRepository()
//    }
//    @Bean
//    fun random(): QuestionBoundary.IRandom {
//        return Random()
//    }
}