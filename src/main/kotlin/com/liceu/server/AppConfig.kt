package com.liceu.server

import com.liceu.server.data.*
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

    @Value("\${google.imageBucket}")
    lateinit var googleImageBucket: String

    val mongoQuestionRepository by lazy {
        MongoQuestionRepository(mongoTemplate())
    }
    val mongoUserRepository by lazy {
        MongoUserRepository(mongoTemplate())
    }

    val mongoGameRepository by lazy {
        MongoGameRepository(mongoTemplate())
    }

    val mongoReportRepository by lazy{
        MongoReportRepository(mongoTemplate())
    }

    val mongoTriviaRepository by lazy{
        MongoTriviaRepository(mongoTemplate())
    }

    val mongoChallengeRepository by lazy{
        MongoChallengeRepository(mongoTemplate())
    }

    val mongoPostRepository by lazy {
        MongoPostRepository(mongoTemplate())
    }

    val mongoActivityRepository by lazy {
        MongoActivityRepository(mongoTemplate())
    }

    val facebookAPI by lazy {
        FacebookAPI()
    }

    @Value("\${google.clientId}")
    lateinit var googleClientId: String
    @Value("\${google.clientSecret}")
    lateinit var googleClientSecret: String

    val googleAPI by lazy {
        GoogleAPI(googleClientId, googleClientSecret)
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
        return RandomQuestions(mongoQuestionRepository, 30)
    }

    @Bean
    fun videos(): QuestionBoundary.IVideos {
        return QuestionVideos(mongoQuestionRepository, 30)
    }

    @Bean
    fun authenticate(): UserBoundary.IAuthenticate {
        return Authenticate(mongoUserRepository, facebookAPI)
    }

    @Bean
    fun multipleAuthenticate(): UserBoundary.IMultipleAuthenticate{
        return MultipleAuthenticate(mongoUserRepository,facebookAPI,googleAPI)
    }

    @Bean
    fun submitGame(): GameBoundary.ISubmit {
        return SubmitGame(mongoGameRepository)
    }

    @Bean
    fun ranking(): GameBoundary.IGameRanking{
        return GameRanking(mongoGameRepository,50)
    }

    @Bean
    fun getUserById(): UserBoundary.IUserById{
        return UserById(mongoUserRepository)
    }

    @Bean
    fun getUsersByNameFromLocation(): UserBoundary.IGetUsersByNameUsingLocation{
        return UsersByNameUsingLocation(mongoUserRepository, 30)
    }

    @Bean
    fun getChallengesFromUserById(): UserBoundary.IChallengesFromUserById{
        return ChallengesFromUserId(mongoUserRepository)
    }

    @Bean
    fun updateLocationFromUser(): UserBoundary.IUpdateLocation{
        return UpdateLocation(mongoUserRepository)
    }

    @Bean
    fun updateSchoolFromUser(): UserBoundary.IUpdateSchool{
        return UpdateSchool(mongoUserRepository)
    }

    @Bean
    fun updateAge(): UserBoundary.IUpdateAge{
        return UpdateAge(mongoUserRepository)
    }

    @Bean
    fun updateYoutubeChannel(): UserBoundary.IUpdateYoutubeChannel{
        return UpdateYoutubeChannel(mongoUserRepository)
    }

    @Bean
    fun updateInstagramProfile(): UserBoundary.IUpdateInstagramProfile{
        return UpdateInstagramProfile(mongoUserRepository)
    }

    @Bean
    fun updateDescription(): UserBoundary.IUpdateDescription{
        return UpdateDescription(mongoUserRepository)
    }

    @Bean
    fun updateWebsite(): UserBoundary.IUpdateWebsite{
        return UpdateWebsite(mongoUserRepository)
    }

    @Bean
    fun updateProducerToBeFollowed(): UserBoundary.IUpdateProducerToBeFollowed {
        return UpdateProducerToBeFollowed(mongoUserRepository,mongoActivityRepository)
    }

    @Bean
    fun updateProducerToBeUnfollowed(): UserBoundary.IupdateProducerToBeUnfollowed {
        return UpdateProducerToBeUnfollowed(mongoUserRepository)
    }

    @Bean
    fun getQuestionById(): QuestionBoundary.IQuestionById{
        return QuestionById(mongoQuestionRepository)
    }

    @Bean
    fun submitReport(): ReportBoundary.ISubmit{
        return SubmitReport(mongoReportRepository)
    }

    @Bean
    fun submitTriviaQuestion(): TriviaBoundary.ISubmit{
        return SubmitTriviaQuestion(mongoTriviaRepository)
    }

    @Bean
    fun randomTriviaQuestions(): TriviaBoundary.IRandomQuestions{
        return TriviaRandomQuestions(mongoTriviaRepository, 5)
    }

    @Bean
    fun updateCommentsTrivia(): TriviaBoundary.IUpdateListOfComments {
        return UpdateCommentsTrivia(mongoTriviaRepository,mongoUserRepository)
    }

    @Bean
    fun updateRatingTrivia(): TriviaBoundary.IUpdateRating{
        return UpdateRating(mongoTriviaRepository)
    }

    @Bean
    fun submitChallenge(): ChallengeBoundary.ICreateChallenge{
        return SubmitChallenge(mongoChallengeRepository,mongoTriviaRepository,mongoActivityRepository)
    }

    @Bean
    fun getChallenge(): ChallengeBoundary.IGetChallenge{
        return GetChallenge(mongoChallengeRepository,mongoTriviaRepository,mongoActivityRepository)
    }

    @Bean
    fun getDirectChallenge(): ChallengeBoundary.IAcceptDirectChallenge{
        return AcceptDirectChallenge(mongoChallengeRepository,mongoActivityRepository)
    }

    @Bean
    fun UpdateAnswers(): ChallengeBoundary.IUpdateAnswers{
        return UpdateAnswers(mongoChallengeRepository,mongoActivityRepository)
    }

    @Bean
    fun textPost(): PostBoundary.ITextPost{
        return TextPost(mongoPostRepository)
    }

    @Bean
    fun imagePost(): PostBoundary.IImagePost{
        return ImagePost(mongoPostRepository,googleImageBucket)
    }

    @Bean
    fun videoPost(): PostBoundary.IVideoPost{
        return VideoPost(mongoPostRepository)
    }


    @Bean
    fun getPosts(): PostBoundary.IGetPosts{
        return GetPosts(mongoPostRepository,mongoUserRepository,30)
    }

    @Bean
    fun getPostsFromUser(): PostBoundary.IGetPostsFromUser{
        return GetPostsFromUser(mongoPostRepository)
    }

    @Bean
    fun getRandomPosts(): PostBoundary.IGetRandomPosts{
        return GetRandomPosts(mongoPostRepository,20)
    }

    @Bean
    fun updateComments(): PostBoundary.IUpdateListOfComments {
        return UpdateComments(mongoPostRepository,mongoUserRepository)
    }

    @Bean
    fun deletePost(): PostBoundary.IDeletePost{
        return DeletePosts(mongoPostRepository)
    }

    @Bean
    fun getActivitiesFromUser(): ActivityBoundary.IGetActivitiesFromUser{
        return GetActivitiesFromUser(mongoActivityRepository,50)
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