package com.liceu.server

import com.liceu.server.data.MongoQuestionRepository
import com.liceu.server.data.MongoTagRepository
import com.liceu.server.domain.question.AddTag
import com.liceu.server.domain.question.QuestionBoundary
import com.liceu.server.domain.question.Random
import com.liceu.server.domain.question.Videos
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
import org.springframework.stereotype.Component


@Configuration
@ComponentScan
@EnableMongoRepositories
class AppConfig: AbstractMongoConfiguration() {

    @Value(("\${mongo.uri:mongodb+srv://serverTest:G97r6PNcrXXMUJgy@cluster0-lf760.mongodb.net/test&retryWrites=true}"))
    lateinit var mongoURI: String

    @Value(("\${mongo.dbName:test}"))
    lateinit var mongoDBName: String

    fun mongoQuestionRepository() = MongoQuestionRepository(mongoTemplate())
    fun mongoTagRepository() = MongoTagRepository(mongoTemplate())

    fun clientUri() = MongoClientURI(mongoURI)

    override fun mongoClient(): MongoClient {
        return MongoClient(clientUri())
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
        return Random(mongoQuestionRepository(), 10)
    }

    @Bean
    fun addTag(): QuestionBoundary.IAddTag {
        return AddTag(mongoQuestionRepository(), mongoTagRepository())
    }

    @Bean
    fun videos(): QuestionBoundary.IVideos {
        return Videos(mongoQuestionRepository(), 10)
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