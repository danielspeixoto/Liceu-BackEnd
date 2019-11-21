package com.liceu.server

import com.liceu.server.data.*
import com.liceu.server.data.firebase.FirebaseNotifications
import com.liceu.server.data.search.SearchRepository
import com.liceu.server.domain.activities.ActivityBoundary
import com.liceu.server.domain.activities.GetActivitiesFromUser
import com.liceu.server.domain.challenge.*
import com.liceu.server.domain.report.ReportBoundary
import com.liceu.server.domain.report.SubmitReport
import com.liceu.server.domain.game.GameBoundary
import com.liceu.server.domain.game.GameRanking
import com.liceu.server.domain.game.SubmitGame
import com.liceu.server.domain.post.*
import com.liceu.server.domain.question.QuestionBoundary
import com.liceu.server.domain.question.QuestionById
import com.liceu.server.domain.question.RandomQuestions
import com.liceu.server.domain.question.QuestionVideos
import com.liceu.server.domain.trivia.*
import com.liceu.server.domain.user.*
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
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

    @Value("\${elasticsearch.elasticCluster}")
    lateinit var elasticCluster: String

    @Value("\${elasticsearch.elasticUser}")
    lateinit var elasticUser: String

    @Value("\${elasticsearch.elasticPassword}")
    lateinit var elasticPassword: String

    @Value("\${elasticsearch.elasticPort}")
    var elasticPort: Int = 9243

    @Value("\${elasticsearch.elasticScheme}")
    lateinit var elasticScheme: String

    @Bean
    fun mongoQuestionRepository() = MongoQuestionRepository(mongoTemplate())

    @Bean
    fun mongoUserRepository() = MongoUserRepository(mongoTemplate())

    @Bean
    fun mongoGameRepository() = MongoGameRepository(mongoTemplate())

    @Bean
    fun mongoReportRepository() = MongoReportRepository(mongoTemplate())

    @Bean
    fun mongoTriviaRepository() = MongoTriviaRepository(mongoTemplate())

    @Bean
    fun mongoChallengeRepository() = MongoChallengeRepository(mongoTemplate())

    @Bean
    fun mongoPostRepository() = MongoPostRepository(mongoTemplate())

    @Bean
    fun mongoActivityRepository() = MongoActivityRepository(mongoTemplate())

    @Bean
    fun facebookAPI() = FacebookAPI()

    @Bean
    fun googleAPI() = GoogleAPI(googleClientId, googleClientSecret)

    @Bean
    fun firebaseNotifications() = FirebaseNotifications(firebaseCloudMessagingKey)

    val restClientBuilder by lazy {
        restClientBuilder()
    }

    val restHighLevelClient by lazy {
        restHighLevelClient()
    }

    @Bean
    override fun mongoTemplate(): MongoTemplate {
        return MongoTemplate(mongoClient(), mongoDBName)
    }

    @Bean
    fun restClientBuilder(): RestClientBuilder {
        val credentialsProvider = BasicCredentialsProvider()
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                UsernamePasswordCredentials(elasticUser, elasticPassword)
        )
        return RestClient.builder(
                HttpHost(elasticCluster, elasticPort, elasticScheme)
        )
                .setHttpClientConfigCallback { httpClientBuilder ->
                    httpClientBuilder
                            .setDefaultCredentialsProvider(credentialsProvider)
                }
    }

    @Bean
    fun restHighLevelClient(): RestHighLevelClient {
        return RestHighLevelClient(restClientBuilder)
    }

    @Bean(destroyMethod = "close")
    fun restClient(): RestClient {
        return restHighLevelClient().lowLevelClient
    }

    @Value("\${google.clientId}")
    lateinit var googleClientId: String
    @Value("\${google.clientSecret}")
    lateinit var googleClientSecret: String


    val clientUri by lazy {
        MongoClientURI(mongoURI)
    }

    override fun mongoClient(): MongoClient {
        return MongoClient(clientUri)
    }

    override fun getDatabaseName(): String {
        return mongoDBName
    }


    @Value("\${firebase.cloudMessaging}")
    lateinit var firebaseCloudMessagingKey: String
}

@Component
class MyTomcatWebServerCustomizer : WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    override fun customize(factory: TomcatServletWebServerFactory) {
        factory.addConnectorCustomizers(TomcatConnectorCustomizer { connector ->
            connector.setAttribute("relaxedQueryChars", "[]")
        })
    }
}