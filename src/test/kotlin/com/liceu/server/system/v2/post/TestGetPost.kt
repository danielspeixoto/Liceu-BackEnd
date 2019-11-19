package com.liceu.server.system.v2.post

import com.google.common.truth.Truth
import com.liceu.server.data.MongoPostRepository
import com.liceu.server.data.PostRepository
import com.liceu.server.system.TestSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.HashMap

class TestGetPost: TestSystem("/v2/post") {

    @Autowired
    lateinit var data: MongoPostRepository

    @Test
    fun getPost_postExist_returnOnePostApproved(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<HashMap<String, Any>>("$baseUrl/${testSetup.POST_ID_1}", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body["id"]).isEqualTo(testSetup.POST_ID_1)
        Truth.assertThat(body["type"]).isEqualTo("text")
        Truth.assertThat(body["description"]).isEqualTo("teste de texto")
        Truth.assertThat(body["statusCode"]).isEqualTo("approved")
    }

    @Test
    fun getPost_postExist_returnOnePostInReview(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<HashMap<String, Any>>("$baseUrl/${testSetup.POST_ID_3}", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body["id"]).isEqualTo(testSetup.POST_ID_3)
        Truth.assertThat(body["type"]).isEqualTo("video")
        Truth.assertThat(body["description"]).isEqualTo("teste de video")
        Truth.assertThat(body["statusCode"]).isEqualTo("inReview")
    }

    @Test
    fun getPost_postExist_returnOnePostDenied(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<HashMap<String, Any>>("$baseUrl/${testSetup.POST_ID_6}", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body["id"]).isEqualTo(testSetup.POST_ID_6)
        Truth.assertThat(body["type"]).isEqualTo("text")
        Truth.assertThat(body["description"]).isEqualTo("texto do texto, vindo do texto do texto de filosofia")
        Truth.assertThat(body["statusCode"]).isEqualTo("denied")
    }

    @Test
    fun getPost_multipleImagesPost_returnOnePostDenied(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<HashMap<String, Any>>("$baseUrl/${testSetup.POST_ID_7}", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body["id"]).isEqualTo(testSetup.POST_ID_7)
        Truth.assertThat(body["type"]).isEqualTo("image")
        Truth.assertThat(body["description"]).isEqualTo("teste de multipleImages")
        val defaultImage = body["image"] as HashMap<String,Any>
        Truth.assertThat(defaultImage["title"]).isEqualTo("teste1")
        Truth.assertThat(defaultImage["type"]).isEqualTo("png")
        Truth.assertThat(defaultImage["imageData"]).isEqualTo("www.minhaimagem1.com.br")
        val allImages = body["multipleImages"] as List<HashMap<String,Any>>
        Truth.assertThat(allImages[0]["title"]).isEqualTo("teste1")
        Truth.assertThat(allImages[0]["type"]).isEqualTo("png")
        Truth.assertThat(allImages[0]["imageData"]).isEqualTo("www.minhaimagem1.com.br")
        Truth.assertThat(allImages[1]["title"]).isEqualTo("teste2")
        Truth.assertThat(allImages[1]["type"]).isEqualTo("jpeg")
        Truth.assertThat(allImages[1]["imageData"]).isEqualTo("www.minhaimagem2.com.br")
        Truth.assertThat(body["statusCode"]).isEqualTo("denied")
    }

    @Test
    fun getPostsByDescriptions_descriptionMatch_returnListOfPosts(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<HashMap<String, Any>>>("$baseUrl?description=video legal&amount=3", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body.size).isEqualTo(2)
        Truth.assertThat(body[0]["description"]).isEqualTo("teste de video legal 3")
        Truth.assertThat(body[1]["description"]).isEqualTo("texto legal sobre texto de humanas")
    }

    @Test
    fun getPostsByDescriptions_descriptionDontMatch_returnListOfPosts(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<HashMap<String, Any>>>("$baseUrl?description=futebol&amount=10", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body).isEmpty()
    }

    @Test
    fun getPostsByDescriptionsUsingElasticSearch_descriptionMatch_returnListOfPosts(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<HashMap<String, Any>>>("$baseUrl?description=matematica exponencial grafico contas soma divisão logaritmica&amount=2", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        Truth.assertThat(body[0]["userId"]).isEqualTo(testSetup.USER_ID_ELASTIC_1)
        Truth.assertThat(body[0]["description"]).isEqualTo("Hoje trouxe fichas de matemática sobre progressão geométrica e aritmética, espero que gostem .#progressaoaritmetica #pa #pg #progressaogeometrica #matematica #enem #matematicaenem #studygrambr #studyblr #foconosestudos #studiesvvmat")
        val imagesFromPost1 = body[0]["multipleImages"] as List<HashMap<Any,String>>
        Truth.assertThat(imagesFromPost1[0]["title"]).isEqualTo("instagram")
        Truth.assertThat(imagesFromPost1[0]["type"]).isEqualTo("jpg")
        Truth.assertThat(imagesFromPost1[0]["imageData"]).isEqualTo("https://storage.googleapis.com/liceu-post-images-prod/instagram5dac8a80dc72fe4c7a16ac941571624388232.jpg")
        Truth.assertThat(imagesFromPost1[1]["title"]).isEqualTo("instagram")
        Truth.assertThat(imagesFromPost1[1]["type"]).isEqualTo("jpg")
        Truth.assertThat(imagesFromPost1[1]["imageData"]).isEqualTo("https://storage.googleapis.com/liceu-post-images-prod/instagram5dac8a80dc72fe4c7a16ac941571624388574.jpg")
        val imagesFromPost2 = body[1]["multipleImages"] as List<HashMap<Any,String>>
        Truth.assertThat(body[1]["userId"]).isEqualTo(testSetup.USER_ID_ELASTIC_2)
        Truth.assertThat(body[1]["description"]).isEqualTo("✔ Resumo de Média aritmética, moda e mediana. Matéria bem simples e que cai SMP no Enem. Então atenção !" +
                "➡ Espero que gostem .#resumomedicadofuturo #revisao #revisando #resumindo #importante #anotaai #matematica #enem #dica")
        Truth.assertThat(imagesFromPost2[0]["title"]).isEqualTo("instagram")
        Truth.assertThat(imagesFromPost2[0]["type"]).isEqualTo("jpg")
        Truth.assertThat(imagesFromPost2[0]["imageData"]).isEqualTo("https://storage.googleapis.com/liceu-post-images-prod/instagram5dab5072b8c43a430f9b6a9a1571694798294.jpg")
    }

    @Test
    fun getPostsByDescriptions_descriptionDesiredEmpty_returnListOfPosts(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(null, headers)
        val response = restTemplate.exchange<List<HashMap<String, Any>>>("$baseUrl?description=&amount=5", HttpMethod.GET, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

}