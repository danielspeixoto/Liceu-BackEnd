package com.liceu.server.integration

import com.google.common.truth.Truth.assertThat
import com.liceu.server.DataSetup
import com.liceu.server.TestConfiguration
import com.liceu.server.data.MongoDatabase
import com.liceu.server.data.MongoPostRepository
import com.liceu.server.data.MongoUserRepository
import com.liceu.server.data.PostRepository
import com.liceu.server.domain.post.*
import khttp.post
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes=[TestConfiguration::class])
@ActiveProfiles("test")
@DataMongoTest
class TestPostRepositoryIntegration {

    @Autowired
    lateinit var data: MongoPostRepository
    @Autowired
    lateinit var postRepository: PostRepository
    @Autowired
    lateinit var userRepository: MongoUserRepository

    @Autowired
    lateinit var testSetup: DataSetup

    @BeforeEach
    fun setup() {
        testSetup.setup()
    }

    @Test
    fun insert_newTextPost_verifyPostCreated(){
        val newPost = data.insertPost(PostToInsert(
            "3a1449a4bdb40abd5ae1e431",
            "text",
            "Eu sou um teste de texto",
            null,
            null,
                null,
            Date.from(Instant.parse("2019-10-11T11:20:20.00Z")),
            null,
            listOf(
                PostQuestions(
                        "Qual o primeiro nome de Einstein?",
                        "Albert",
                        listOf(
                                "José","Albertonio","Lucas"
                        )
                ),
                PostQuestions(
                    "Qual o primeiro nome de Newton?",
                    "Isaac",
                    listOf(
                            "José","Albertonio","Albert"
                    )
                )
            ),
            true
        ))
        val postInserted = data.getPostById(newPost)
        assertThat(postInserted.userId).isEqualTo("3a1449a4bdb40abd5ae1e431")
        assertThat(postInserted.type).isEqualTo("text")
        assertThat(postInserted.description).isEqualTo("Eu sou um teste de texto")
        assertThat(postInserted.questions?.get(0)?.question).isEqualTo("Qual o primeiro nome de Einstein?")
        assertThat(postInserted.questions?.get(0)?.correctAnswer).isEqualTo("Albert")
        assertThat(postInserted.questions?.get(0)?.otherAnswers?.size).isEqualTo(3)
        assertThat(postInserted.questions?.get(1)?.question).isEqualTo("Qual o primeiro nome de Newton?")
        assertThat(postInserted.questions?.get(1)?.correctAnswer).isEqualTo("Isaac")
        assertThat(postInserted.questions?.get(1)?.otherAnswers?.size).isEqualTo(3)
    }

    @Test
    fun insert_newImagePost_verifyPostCreated(){
        val newPost = data.insertPost(PostToInsert(
                "3a1449a4bdb40abd5ae1e431",
                "image",
                "imagem legal",
                FormattedImage(
                        "divulgacao Curso de matematica",
                        "JPEG",
                        "www.minhaimagem.com"
                ),
                null,
                null,
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z")),
                null,
                null,
                true
        ))
        val postInserted = data.getPostById(newPost)
        assertThat(postInserted.userId).isEqualTo("3a1449a4bdb40abd5ae1e431")
        assertThat(postInserted.type).isEqualTo("image")
        assertThat(postInserted.description).isEqualTo("imagem legal")
        assertThat(postInserted.image?.title).isEqualTo("divulgacao Curso de matematica")
        assertThat(postInserted.image?.type).isEqualTo("JPEG")
        assertThat(postInserted.image?.imageData).isEqualTo("www.minhaimagem.com")
    }

    @Test
    fun insert_newVideoPost_verifyPostCreated(){
        val newPost = data.insertPost(PostToInsert(
                "3a1449a4bdb40abd5ae1e431",
                "video",
                "esse video fala sobre fisica",
                null,
                PostVideo(
                        "www.youtube.com/meuvideo",
                        PostThumbnails(
                                "high",
                                "default",
                                "medium"
                        )
                ),
                null,
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z")),
                null,
                listOf(
                        PostQuestions(
                                "Qual o primeiro nome de Einstein?",
                                "Albert",
                                listOf(
                                        "José","Albertonio","Lucas"
                                )
                        )
                ),
                true
        ))
        val postInserted = data.getPostById(newPost)
        assertThat(postInserted.userId).isEqualTo("3a1449a4bdb40abd5ae1e431")
        assertThat(postInserted.type).isEqualTo("video")
        assertThat(postInserted.video?.videoUrl).isEqualTo("www.youtube.com/meuvideo")
        assertThat(postInserted.description).isEqualTo("esse video fala sobre fisica")
        assertThat(postInserted.video?.thumbnails?.high).isEqualTo("high")
        assertThat(postInserted.video?.thumbnails?.default).isEqualTo("default")
        assertThat(postInserted.video?.thumbnails?.medium).isEqualTo("medium")
        assertThat(postInserted.questions?.get(0)?.question).isEqualTo("Qual o primeiro nome de Einstein?")
        assertThat(postInserted.questions?.get(0)?.correctAnswer).isEqualTo("Albert")
        assertThat(postInserted.questions?.get(0)?.otherAnswers?.size).isEqualTo(3)
    }

    @Test
    fun insert_newMultipleImagePost_verifyPostCreated(){
        val newPost = data.insertPost(PostToInsert(
                testSetup.USER_ID_1,
                "multipleImages",
                "imagens legais de historia",
                null,
                null,
                listOf(
                        FormattedImage(
                                "title 1",
                                "type 1",
                                "www.minhaimagem1.com.br"
                        ),
                        FormattedImage(
                                "title 2",
                                "type 2",
                                "www.minhaimagem2.com.br"
                        )
                ),
                Date.from(Instant.parse("2019-10-11T11:20:20.00Z")),
                null,
                null,
                true
        ))
        val postInserted = data.getPostById(newPost)
        assertThat(postInserted.userId).isEqualTo(testSetup.USER_ID_1)
        assertThat(postInserted.type).isEqualTo("multipleImages")
        assertThat(postInserted.description).isEqualTo("imagens legais de historia")
        assertThat(postInserted.multipleImages?.get(0)?.title).isEqualTo("title 1")
        assertThat(postInserted.multipleImages?.get(0)?.type).isEqualTo("type 1")
        assertThat(postInserted.multipleImages?.get(0)?.imageData).isEqualTo("www.minhaimagem1.com.br")
        assertThat(postInserted.multipleImages?.get(1)?.title).isEqualTo("title 2")
        assertThat(postInserted.multipleImages?.get(1)?.type).isEqualTo("type 2")
        assertThat(postInserted.multipleImages?.get(1)?.imageData).isEqualTo("www.minhaimagem2.com.br")
    }

    @Test
    fun getPosts_postsInserted_returnOnePost(){
        val date = Date.from(Instant.parse("2019-08-27T11:40:20.00Z"))
        val user = userRepository.getUserById(testSetup.USER_ID_4)
        val retrievedPosts = data.getPostsForFeed(user,date,10,0)
        assertThat(retrievedPosts?.size).isEqualTo(1)
        assertThat(retrievedPosts?.get(0)?.type).isEqualTo("text")
        assertThat(retrievedPosts?.get(0)?.description).isEqualTo("teste de texto")
        assertThat(retrievedPosts?.get(0)?.questions?.size).isEqualTo(2)
        assertThat(retrievedPosts?.get(0)?.questions?.get(0)?.question).isEqualTo("Qual o primeiro nome de Einstein?")
        assertThat(retrievedPosts?.get(0)?.questions?.get(1)?.question).isEqualTo("Qual o primeiro nome de Newton?")
    }

    @Test
    fun getPosts_postsInserted_returnMultiplesPost(){
        val date = Date.from(Instant.parse("2019-08-27T12:40:20.00Z"))
        val user = userRepository.getUserById(testSetup.USER_ID_3)
        val retrievedPosts = data.getPostsForFeed(user,date,10,0)
        assertThat(retrievedPosts?.size).isEqualTo(2)
        assertThat(retrievedPosts?.get(1).questions?.size).isEqualTo(1)
        val userIdsFromPosts = retrievedPosts?.map { it.userId }
        assertThat(userIdsFromPosts).containsExactly(testSetup.USER_ID_2,testSetup.USER_ID_4)
        assertThat(retrievedPosts?.get(0)?.type).isEqualTo("video")
        assertThat(retrievedPosts?.get(1)?.type).isEqualTo("text")
    }

    @Test
    fun getPosts_followingArrayEmpty_returnEmptyList(){
        val date = Date.from(Instant.parse("2019-08-27T11:40:20.00Z"))
        val user = userRepository.getUserById(testSetup.USER_ID_2)
        val retrievedPosts = data.getPostsForFeed(user,date,10,0)
        assertThat(retrievedPosts?.isEmpty())
    }

    @Test
    fun getMultiplePosts_randomPostsFromId_returnListOfPosts(){
        val postsRetrieved : MutableList<Post> = arrayListOf()
        val listOfIds = listOf(testSetup.POST_ID_1,testSetup.POST_ID_2,testSetup.POST_ID_4,testSetup.POST_ID_8)
        listOfIds.forEach { postsRetrieved.add(data.getPostById(it)) }
        assertThat(listOfIds.size).isEqualTo(4)
        assertThat(postsRetrieved.get(0).id).isEqualTo(testSetup.POST_ID_1)
        assertThat(postsRetrieved.get(1).id).isEqualTo(testSetup.POST_ID_2)
        assertThat(postsRetrieved.get(2).id).isEqualTo(testSetup.POST_ID_4)
        assertThat(postsRetrieved.get(3).id).isEqualTo(testSetup.POST_ID_8)
    }

    @Test
    fun getPosts_elasticPosts_ReturnListOfPosts(){
        val postsReceived1 = data.getPostById(testSetup.ELASTIC_POST_ID_1)
        assertThat(postsReceived1.multipleImages?.get(0)?.title).isEqualTo("instagram")
        assertThat(postsReceived1.multipleImages?.get(0)?.type).isEqualTo("jpg")
        assertThat(postsReceived1.multipleImages?.get(0)?.imageData).isEqualTo("https://storage.googleapis.com/liceu-post-images-prod/instagram5dac8a80dc72fe4c7a16ac941571624388232.jpg")
    }

    @Test
    fun getPostsFromUser_userExists_returnListOfPosts(){
        val retrievedPosts = data.getPostFromUser(testSetup.USER_ID_3,10,0)
        assertThat(retrievedPosts.size).isEqualTo(2)
        val idsFromPosts = retrievedPosts.map { it.id }
        assertThat(idsFromPosts).containsExactly(testSetup.POST_ID_5,testSetup.POST_ID_4).inOrder()
    }

    @Test
    fun getPostsFromUser_userExists_returnOnePost(){
        val retrievedPosts = data.getPostFromUser(testSetup.USER_ID_1,10,0)
        assertThat(retrievedPosts.size).isEqualTo(1)
    }

    @Test
    fun getRandomPosts_postsExists_returnListOfPosts(){
        val retrievedPosts = data.getRandomPosts(10)
        assertThat(retrievedPosts.size).isEqualTo(7)
    }

    @Test
    fun getRandomPosts_amountZero_returnEmptyList(){
        val retrievedPosts = data.getRandomPosts(0)
        assertThat(retrievedPosts).isEmpty()
    }

    @Test
    fun getPostById_postExists_returnOnePost(){
        val retrievedPost = data.getPostById(testSetup.POST_ID_4)
        assertThat(retrievedPost.id).isEqualTo(testSetup.POST_ID_4)
        assertThat(retrievedPost.userId).isEqualTo(testSetup.USER_ID_3)
        assertThat(retrievedPost.type).isEqualTo("text")
        assertThat(retrievedPost.description).isEqualTo("texto legal sobre texto de humanas")
        assertThat(retrievedPost.image?.title).isNull()
        assertThat(retrievedPost.image?.type).isNull()
        assertThat(retrievedPost.image?.imageData).isNull()
        assertThat(retrievedPost.video?.videoUrl).isNull()
        assertThat(retrievedPost.video?.thumbnails?.medium).isNull()
        assertThat(retrievedPost.video?.thumbnails?.high).isNull()
        assertThat(retrievedPost.video?.thumbnails?.default).isNull()
        assertThat(retrievedPost.comments).isNull()
        assertThat(retrievedPost.questions).isNull()
        assertThat(retrievedPost.documents).isNull()
    }

    @Test
    fun getPostsFromUser_userExists_returnOneApprovedPost(){
        val postsRetrieved = data.getPostFromUser(testSetup.USER_ID_5,10,0)
        assertThat(postsRetrieved.size).isEqualTo(1)
    }

    @Test
    fun getPostsFromOwner_userExists_returnListOfPosts(){
        val postsRetrieved = data.getPostsFromOwner(testSetup.USER_ID_5,10,0)
        assertThat(postsRetrieved.size).isEqualTo(2)
    }

    @Test
    fun getPostsByDescription_descriptionExists_returnListOfPosts(){
        val postsRetrieved = data.getPostsByDescription("texto",5)
        assertThat(postsRetrieved.size).isEqualTo(4)
        val idsFromPosts = postsRetrieved.map { it.id }
        assertThat(idsFromPosts).containsExactly(testSetup.POST_ID_4,testSetup.POST_ID_1,testSetup.POST_ID_2,testSetup.POST_ID_5).inOrder()
    }

    @Test
    fun getPostsByDescription_descriptionDontMatch_returnListOfPosts(){
        val postsRetrieved = data.getPostsByDescription("musica",5)
        assertThat(postsRetrieved).isEmpty()
    }

    @Test
    fun updateListOfComments_postExists_verifyCommentsFromPost(){
        val result1 = data.updateListOfComments(testSetup.POST_ID_1,testSetup.USER_ID_2,"user2","post interessante 1")
        val result2 = data.updateListOfComments(testSetup.POST_ID_1,testSetup.USER_ID_2,"user2","post interessante 2")
        val result3= data.updateListOfComments(testSetup.POST_ID_1,testSetup.USER_ID_2,"user2","post interessante 3")
        assertThat(result1).isEqualTo(1)
        assertThat(result2).isEqualTo(1)
        assertThat(result3).isEqualTo(1)
        val postChanged = data.getPostById(testSetup.POST_ID_1)
        assertThat(postChanged.comments?.size).isEqualTo(3)
        assertThat(postChanged.comments?.get(0)?.comment).isEqualTo("post interessante 1")
        assertThat(postChanged.comments?.get(1)?.comment).isEqualTo("post interessante 2")
    }

    @Test
    fun updateLikes_postExists_verifyLikesFromPost(){
        val result = data.updateLike(testSetup.POST_ID_2)
        assertThat(result).isEqualTo(1)
        val postChanged = data.getPostById(testSetup.POST_ID_2)
        assertThat(postChanged.likes).isEqualTo(1)
    }

    @Test
    fun deletePosts_postExists_verifyPosts() {
        val postsBefore = data.getPostFromUser(testSetup.USER_ID_3,10,0)
        assertThat(postsBefore.size).isEqualTo(2)
        val result = data.deletePost(testSetup.POST_ID_5, testSetup.USER_ID_3)
        assertThat(result?.id).isEqualTo(testSetup.POST_ID_5)
        val postsAfter = data.getPostFromUser(testSetup.USER_ID_3,10,0)
        assertThat(postsAfter.size).isEqualTo(1)
    }

    @Test
    fun deleteCommentInPost_postExists_verifyPost() {
        val postBefore = data.getPostById(testSetup.POST_ID_8)
        assertThat(postBefore.comments?.size).isEqualTo(2)
        val result = data.deleteCommentInPost(testSetup.POST_ID_8,testSetup.POST_COMMENT_ID_1,testSetup.USER_ID_1)
        assertThat(result).isEqualTo(1)
        val postAfter = data.getPostById(testSetup.POST_ID_8)
        assertThat(postAfter.comments?.size).isEqualTo(1)
    }

    @Test
    fun deleteCommentInPost_postExistsWrongUser_verifyPost() {
        val postBefore = data.getPostById(testSetup.POST_ID_8)
        assertThat(postBefore.comments?.size).isEqualTo(2)
        val result = data.deleteCommentInPost(testSetup.POST_ID_8,testSetup.POST_COMMENT_ID_2,testSetup.USER_ID_3)
        assertThat(result).isEqualTo(0)
        val postAfter = data.getPostById(testSetup.POST_ID_8)
        assertThat(postAfter.comments?.size).isEqualTo(2)
    }

    @Test
    fun deleteMultiplePosts_postExists_verifyPosts() {
        val postsBefore = data.getPostFromUser(testSetup.USER_ID_3,10,0)
        assertThat(postsBefore.size).isEqualTo(2)
        val result1 = data.deletePost(testSetup.POST_ID_4, testSetup.USER_ID_3)
        val result2 = data.deletePost(testSetup.POST_ID_5, testSetup.USER_ID_3)
        assertThat(result1?.id).isEqualTo(testSetup.POST_ID_4)
        assertThat(result2?.id).isEqualTo(testSetup.POST_ID_5)
        val postsAfter = data.getPostFromUser(testSetup.USER_ID_3,10,0)
        assertThat(postsAfter.size).isEqualTo(0)
    }

    @Test
    fun deletePosts_postExistsWrongUser_verifyThatPostIsNotDeleted() {
        val postsBefore = data.getPostFromUser(testSetup.USER_ID_3,10,0)
        assertThat(postsBefore.size).isEqualTo(2)
        val result = data.deletePost(testSetup.POST_ID_5, testSetup.USER_ID_1)
        assertThat(result).isNull()
        val postsAfter = data.getPostFromUser(testSetup.USER_ID_3, 10,0)
        assertThat(postsAfter.size).isEqualTo(2)
    }

    @Test
    fun updateDocument_postExists_verifyPost(){
        val result1 = data.updateDocumentPost(testSetup.POST_ID_1,"post interessante 1","pptx" ,"https://meuDocumento.pptx")
        val result2 = data.updateDocumentPost(testSetup.POST_ID_1,"post interessante 2","csv" ,"https://meuDocumento.csv")
        assertThat(result1).isEqualTo(1)
        assertThat(result2).isEqualTo(1)
        val postChanged = data.getPostById(testSetup.POST_ID_1)
        assertThat(postChanged.documents?.get(0)?.id).isNotNull()
        assertThat(postChanged.documents?.get(0)?.title).isEqualTo("post interessante 1")
        assertThat(postChanged.documents?.get(0)?.type).isEqualTo("pptx")
        assertThat(postChanged.documents?.get(0)?.documentURL).isEqualTo("https://meuDocumento.pptx")
        assertThat(postChanged.documents?.get(1)?.id).isNotNull()
        assertThat(postChanged.documents?.get(1)?.title).isEqualTo("post interessante 2")
        assertThat(postChanged.documents?.get(1)?.type).isEqualTo("csv")
        assertThat(postChanged.documents?.get(1)?.documentURL).isEqualTo("https://meuDocumento.csv")
    }

    @Test
    fun countApprovedPosts_userExists_verifyPostsAmount(){
        val result = data.countApprovedPosts(testSetup.USER_ID_3)
        assertThat(result).isEqualTo(2)
    }

    @Test
    fun countApprovedPosts_userExistsWithNoPostsApproved_verifyPostAmount(){
        val result = data.countApprovedPosts(testSetup.USER_ID_4)
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun postExists_postExists_verifyBoolean(){
        val result = data.postExists(testSetup.POST_ID_3)
        assertThat(result).isTrue()
    }

    @Test
    fun postExists_postDoesNotExists_verifyBoolean(){
        val result = data.postExists(testSetup.USER_ID_2)
        assertThat(result).isFalse()
    }

}