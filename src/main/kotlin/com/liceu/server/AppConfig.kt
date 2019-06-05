package com.liceu.server

import com.liceu.server.data.MongoQuestionRepository
import com.liceu.server.data.MongoTagRepository
import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.question.AddTag
import com.liceu.server.domain.question.QuestionBoundary
import com.liceu.server.domain.question.Random
import com.liceu.server.domain.question.Videos
import com.liceu.server.domain.user.Authenticate
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.presentation.v2.JWTAuthenticationFilter
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.apache.catalina.connector.Connector
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.stereotype.Component

@Configuration
@ServletComponentScan
@EnableMongoRepositories
class AppConfig : AbstractMongoConfiguration() {

    @Value("\${mongo.uri}")
    lateinit var mongoURI: String

    @Value("\${mongo.dbName}")
    lateinit var mongoDBName: String

    val mongoQuestionRepository by lazy {
        MongoQuestionRepository(mongoTemplate())
    }
    val mongoTagRepository by lazy {
        MongoTagRepository(mongoTemplate())
    }
    val mongoUserRepository by lazy {
        MongoUserRepository(mongoTemplate())
    }

    val clientUri by lazy {
        MongoClientURI(mongoURI)
    }

    override fun mongoClient(): MongoClient {
        return MongoClient(clientUri)
    }

    override fun getDatabaseName(): String {
        return mongoDBName
    }

    @Bean
    override fun mongoTemplate(): MongoTemplate {
        return MongoTemplate(mongoClient(), mongoDBName)
    }

    @Bean
    fun random(): QuestionBoundary.IRandom {
        return Random(mongoQuestionRepository, 10)
    }

    @Bean
    fun addTag(): QuestionBoundary.IAddTag {
        return AddTag(mongoQuestionRepository, mongoTagRepository)
    }

    @Bean
    fun videos(): QuestionBoundary.IVideos {
        return Videos(mongoQuestionRepository, 10)
    }

    @Bean
    fun authenticate(): UserBoundary.IAuthenticate {
        return Authenticate(mongoUserRepository)
    }
}

@Component
class MyTomcatWebServerCustomizer : WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    override fun customize(factory: TomcatServletWebServerFactory) {
        factory.addConnectorCustomizers(TomcatConnectorCustomizer {
            connector -> connector.setAttribute("relaxedQueryChars", "[]")
        })
    }
}