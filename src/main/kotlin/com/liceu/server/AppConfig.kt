package com.liceu.server

import com.liceu.server.data.MongoQuestionRepository
import com.liceu.server.data.MongoTagRepository
import com.liceu.server.data.QuestionRepository
import com.liceu.server.domain.question.AddTag
import com.liceu.server.domain.question.QuestionBoundary
import com.liceu.server.domain.question.Random
import com.liceu.server.domain.question.Videos
import com.liceu.server.util.Logging
import com.mongodb.MongoClient
import org.apache.catalina.connector.Connector
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Component
import org.springframework.web.filter.CommonsRequestLoggingFilter




@Configuration
@ComponentScan
@EnableMongoRepositories
class AppConfig: AbstractMongoConfiguration() {

//    @Autowired lateinit var repo: QuestionRepository
    val mongoTemplate = MongoTemplate(MongoClient("127.0.0.1", 27017), "b")
    val mongoQuestionRepository = MongoQuestionRepository(mongoTemplate)
    val mongoTagRepository = MongoTagRepository(mongoTemplate)

    override fun mongoClient(): MongoClient {
        val client = MongoClient("127.0.0.1", 27017)
        return client
    }

    override fun getDatabaseName(): String {
        return "b"
    }

    @Bean
    override fun mongoTemplate(): MongoTemplate {
        return mongoTemplate
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
    fun requestLoggingFilter(): CommonsRequestLoggingFilter {
        val loggingFilter = CommonsRequestLoggingFilter()
        loggingFilter.setIncludeClientInfo(true)
        loggingFilter.setIncludeQueryString(true)
        loggingFilter.setIncludePayload(true)
        return loggingFilter
    }

}

@Component
class MyTomcatWebServerCustomizer : WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    override fun customize(factory: TomcatServletWebServerFactory) {
        factory.addConnectorCustomizers(object : TomcatConnectorCustomizer {
            override fun customize(connector: Connector) {
                connector.setAttribute("relaxedQueryChars", "[]")
            }
        })
    }
}