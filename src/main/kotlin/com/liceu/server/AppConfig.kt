package com.liceu.server

import com.liceu.server.data.*
import com.liceu.server.data.elasticsearch.ElasticSearchFinder
import com.liceu.server.data.firebase.FirebaseNotifications
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

    @Value("\${google.documentBucket}")
    lateinit var googleDocumentBucket: String

    @Value("\${values.triviaAmount}")
    var triviaAmount: Int = 4

    @Value("\${values.challengeHistoryAmount}")
    var challengeHistoryAmount: Int = 10

    @Value("\${slack.slackReportWebhook}")
    lateinit var reportWebhookURL: String

    @Value("\${values.reportTagsAmount}")
    var reportTagsAmount: Int = 30

    @Value("\${values.reportParamsAmount}")
    var reportParamsAmount: Int = 30

    @Value("\${values.reportMessageLength}")
    var reportMessageLength: Int = 200

    @Value("\${values.postsNumberApproval}")
    var postsNumberApproval: Int = 10

    @Value("\${values.postFinderAmount}")
    var postFinderAmount: Int = 20

    @Value("\${values.postSavedAmount}")
    var postSavedAmount: Int = 20

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

    val firebaseNotifications by lazy {
        FirebaseNotifications(firebaseCloudMessagingKey)
    }

    val elasticSearchFinder by lazy {
        ElasticSearchFinder(mongoPostRepository,restClientBuilder)
    }

    val restClientBuilder by lazy {
        restClientBuilder()
    }

    @Bean
    fun elasticSearchFinder(): ElasticSearchFinder {
        return ElasticSearchFinder(mongoPostRepository,restClientBuilder)
    }

    @Bean
    fun restClientBuilder(): RestClientBuilder{
        val credentialsProvider = BasicCredentialsProvider()
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                UsernamePasswordCredentials(elasticUser, elasticPassword)
        )
        return RestClient.builder(
                HttpHost(elasticCluster, elasticPort, elasticScheme)
        )
                .setHttpClientConfigCallback { httpClientBuilder -> httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider)
                }
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


    @Value("\${firebase.cloudMessaging}")
    lateinit var firebaseCloudMessagingKey: String

    @Bean
    fun firebaseNotifications(): FirebaseNotifications {
        return FirebaseNotifications(firebaseCloudMessagingKey)
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
        return ChallengesFromUserId(mongoUserRepository,challengeHistoryAmount)
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
    fun updateProducerToBeUnfollowed(): UserBoundary.IUpdateProducerToBeUnfollowed {
        return UpdateProducerToBeUnfollowed(mongoUserRepository)
    }

    @Bean
    fun updateProfileImage(): UserBoundary.IUpdateProfileImage {
        return UpdateProfileImage(mongoUserRepository,googleImageBucket)
    }

    @Bean
    fun updateFcmToken(): UserBoundary.IUpdateFcmToken {
        return UpdateFcmToken(mongoUserRepository)
    }

    @Bean
    fun updateLastAccess(): UserBoundary.IUpdateLastAccess {
        return UpdateLastAccess(mongoUserRepository,mongoActivityRepository)
    }

    @Bean
    fun updateDesiredCourse(): UserBoundary.IUpdateCourse {
        return UpdateDesiredCourse (mongoUserRepository)
    }

    @Bean
    fun updateTelephoneNumber(): UserBoundary.IUpdateTelephoneNumber {
        return UpdateTelephoneNumber(mongoUserRepository)
    }

    @Bean
    fun updateAddToSavedPosts(): UserBoundary.IUpdatePostToBeSaved {
        return UpdatePostToBeSaved(mongoUserRepository,mongoPostRepository)
    }

    @Bean
    fun updateRemoveFromSavedPosts(): UserBoundary.IUpdateSavedPostToBeRemoved {
        return UpdatePostSavedToBeRemoved(mongoUserRepository)
    }

    @Bean
    fun getPostsSaved(): UserBoundary.IGetSavedPosts {
        return GetPostsSaved(mongoUserRepository,postSavedAmount)
    }

    @Bean
    fun getQuestionById(): QuestionBoundary.IQuestionById{
        return QuestionById(mongoQuestionRepository)
    }

    @Bean
    fun submitReport(): ReportBoundary.ISubmit{
        return SubmitReport(mongoReportRepository,reportWebhookURL,reportTagsAmount,reportMessageLength,reportParamsAmount)
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
        return SubmitChallenge(mongoChallengeRepository,mongoTriviaRepository,mongoActivityRepository,mongoUserRepository,firebaseNotifications,triviaAmount)
    }

    @Bean
    fun getChallenge(): ChallengeBoundary.IGetChallenge{
        return GetChallenge(mongoChallengeRepository,mongoTriviaRepository,mongoActivityRepository,mongoUserRepository,firebaseNotifications,triviaAmount)
    }

    @Bean
    fun getDirectChallenge(): ChallengeBoundary.IAcceptDirectChallenge{
        return AcceptDirectChallenge(mongoChallengeRepository,mongoActivityRepository)
    }

    @Bean
    fun UpdateAnswers(): ChallengeBoundary.IUpdateAnswers{
        return UpdateAnswers(mongoChallengeRepository,mongoActivityRepository,mongoUserRepository,firebaseNotifications)
    }

    @Bean
    fun textPost(): PostBoundary.ITextPost{
        return TextPost(mongoPostRepository,mongoUserRepository,postsNumberApproval)
    }

    @Bean
    fun imagePost(): PostBoundary.IImagePost{
        return ImagePost(mongoPostRepository,googleImageBucket,mongoUserRepository,postsNumberApproval)
    }

    @Bean
    fun multipleImagesPost(): PostBoundary.IMultipleImagesPosts{
        return MultipleImagesPost(mongoPostRepository,googleImageBucket,mongoUserRepository,postsNumberApproval)
    }

    @Bean
    fun videoPost(): PostBoundary.IVideoPost{
        return VideoPost(mongoPostRepository,mongoUserRepository,postsNumberApproval)
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
    fun getPostById(): PostBoundary.IGetPostById {
        return GetPostById(mongoPostRepository)
    }

    @Bean
    fun getPostByDescription(): PostBoundary.IGetPostsByDescription {
        return GetPostsByDescription(postFinderAmount,elasticSearchFinder)
    }

    @Bean
    fun updateComments(): PostBoundary.IUpdateListOfComments {
        return UpdateComments(mongoPostRepository,mongoUserRepository)
    }

    @Bean
    fun updateDocument(): PostBoundary.IUpdateDocument {
        return UpdateDocument(mongoPostRepository,googleDocumentBucket)
    }

    @Bean
    fun updatePostRating(): PostBoundary.IUpdateRating {
        return UpdatePostRating(mongoPostRepository)
    }

    @Bean
    fun deletePost(): PostBoundary.IDeletePost{
        return DeletePosts(mongoPostRepository)
    }

    @Bean
    fun deleteCommentPost(): PostBoundary.IDeleteCommentPost {
        return DeleteCommentPost(mongoPostRepository)
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