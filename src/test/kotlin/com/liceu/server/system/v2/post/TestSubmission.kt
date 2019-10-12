package com.liceu.server.system.v2.post

import com.google.common.truth.Truth
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

class TestSubmission: TestSystem("/v2/post") {

    @Autowired
    lateinit var postRepo: PostRepository

    @Test
    fun submitTextPost_Valid_Success() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "text",
                "description" to "esse e um teste de texto aha esse e um teste de texto aha esse e um teste de texto aha esse e um teste de texto aha",
                "hasQuestions" to "true",
                "questions" to listOf(
                        hashMapOf(
                                "question" to "O triangulo que tem lados iguais é?",
                                "correctAnswer" to "Equilátero",
                                "otherAnswers" to listOf(
                                        "Anormal", "Perfeito"
                                )
                        ),
                        hashMapOf(
                                "question" to "O triangulo que tem todos os lados diferentes é?",
                                "correctAnswer" to "Escaleno",
                                "otherAnswers" to listOf(
                                        "Anormal", "Doido"
                                )
                        )
                )
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!
        val id = body["id"] as String
        val insertedPost = postRepo.findById(id).get()

        Truth.assertThat(insertedPost).isNotNull()
        Truth.assertThat(insertedPost.userId.toHexString()).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(insertedPost.type).isEqualTo("text")
        Truth.assertThat(insertedPost.description).isEqualTo("esse e um teste de texto aha esse e um teste de texto aha esse e um teste de texto aha esse e um teste de texto aha")
        Truth.assertThat(insertedPost.questions?.size).isEqualTo(2)
        Truth.assertThat(insertedPost.questions?.get(0)?.question).isEqualTo("O triangulo que tem lados iguais é?")
        Truth.assertThat(insertedPost.questions?.get(0)?.correctAnswer).isEqualTo("Equilátero")
        Truth.assertThat(insertedPost.questions?.get(0)?.otherAnswers?.size).isEqualTo(2)
        Truth.assertThat(insertedPost.questions?.get(0)?.otherAnswers).containsExactly("Anormal", "Perfeito")
        Truth.assertThat(insertedPost.questions?.get(1)?.question).isEqualTo("O triangulo que tem todos os lados diferentes é?")
        Truth.assertThat(insertedPost.questions?.get(1)?.correctAnswer).isEqualTo("Escaleno")
        Truth.assertThat(insertedPost.questions?.get(1)?.otherAnswers?.size).isEqualTo(2)
        Truth.assertThat(insertedPost.questions?.get(1)?.otherAnswers).containsExactly("Anormal", "Doido")
        Truth.assertThat(insertedPost.approvalFlag).isEqualTo(true)
    }

    @Test
    fun submitVideoPost_Valid_Success() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to "esse e um teste de video aha esse e um teste de video aha esse e um teste de video aha esse e um teste de video aha",
                "videoUrl" to "https://www.youtube.com/watch?v=8vefLpfozPA",
                "hasQuestions" to "true",
                "questions" to listOf(
                            hashMapOf(
                                    "question" to "O triangulo que tem lados iguais é?",
                                    "correctAnswer" to "Equilátero",
                                    "otherAnswers" to listOf(
                                            "Anormal", "Perfeito"
                                    )
                            ),
                            hashMapOf(
                                    "question" to "O triangulo que tem todos os lados diferentes é?",
                                    "correctAnswer" to "Escaleno",
                                    "otherAnswers" to listOf(
                                            "Anormal", "Doido"
                                    )
                            )
                )
        ),headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val body = response.body!!
        val id = body["id"] as String
        val insertedPost = postRepo.findById(id).get()

        Truth.assertThat(insertedPost).isNotNull()
        Truth.assertThat(insertedPost.userId.toHexString()).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(insertedPost.type).isEqualTo("video")
        Truth.assertThat(insertedPost.video?.videoUrl).isEqualTo("https://www.youtube.com/watch?v=8vefLpfozPA")
        Truth.assertThat(insertedPost.video?.thumbnails?.high).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/hqdefault.jpg")
        Truth.assertThat(insertedPost.video?.thumbnails?.default).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/default.jpg")
        Truth.assertThat(insertedPost.video?.thumbnails?.medium).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/mqdefault.jpg")
        Truth.assertThat(insertedPost.questions?.size).isEqualTo(2)
        Truth.assertThat(insertedPost.questions?.get(0)?.question).isEqualTo("O triangulo que tem lados iguais é?")
        Truth.assertThat(insertedPost.questions?.get(0)?.correctAnswer).isEqualTo("Equilátero")
        Truth.assertThat(insertedPost.questions?.get(0)?.otherAnswers?.size).isEqualTo(2)
        Truth.assertThat(insertedPost.questions?.get(0)?.otherAnswers).containsExactly("Anormal", "Perfeito")
        Truth.assertThat(insertedPost.approvalFlag).isEqualTo(true)
    }

    @Test
    fun submitVideoPost_questionsEmpty_success(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to "teste de video teste de video teste de video teste de video teste de video teste de video teste de video teste de video",
                "videoUrl" to "https://www.youtube.com/watch?v=8vefLpfozPA",
                "hasQuestions" to "false"
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        val id = body["id"] as String
        val insertedPost = postRepo.findById(id).get()
        Truth.assertThat(insertedPost.userId.toHexString()).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(insertedPost.type).isEqualTo("video")
        Truth.assertThat(insertedPost.video?.videoUrl).isEqualTo("https://www.youtube.com/watch?v=8vefLpfozPA")
        Truth.assertThat(insertedPost.video?.thumbnails?.high).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/hqdefault.jpg")
        Truth.assertThat(insertedPost.video?.thumbnails?.default).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/default.jpg")
        Truth.assertThat(insertedPost.video?.thumbnails?.medium).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/mqdefault.jpg")
        Truth.assertThat(insertedPost.questions).isEmpty()
        Truth.assertThat(insertedPost.approvalFlag).isEqualTo(true)
    }


    @Test
    fun submitVideoPost_questionsEmpty_successWithNoAutomaticApproval(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_4_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to "teste de video teste de video teste de video teste de video teste de video teste de video teste de video teste de video",
                "videoUrl" to "https://www.youtube.com/watch?v=8vefLpfozPA",
                "hasQuestions" to "false"
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        val id = body["id"] as String
        val insertedPost = postRepo.findById(id).get()
        Truth.assertThat(insertedPost.userId.toHexString()).isEqualTo(testSetup.USER_ID_4)
        Truth.assertThat(insertedPost.type).isEqualTo("video")
        Truth.assertThat(insertedPost.video?.videoUrl).isEqualTo("https://www.youtube.com/watch?v=8vefLpfozPA")
        Truth.assertThat(insertedPost.video?.thumbnails?.high).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/hqdefault.jpg")
        Truth.assertThat(insertedPost.video?.thumbnails?.default).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/default.jpg")
        Truth.assertThat(insertedPost.video?.thumbnails?.medium).isEqualTo("http://i.ytimg.com/vi/8vefLpfozPA/mqdefault.jpg")
        Truth.assertThat(insertedPost.questions).isEmpty()
        Truth.assertThat(insertedPost.approvalFlag).isEqualTo(null)
    }


    @Test
    fun submitImagePost_jpegImage_success(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "image",
                "description" to "teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem",
                "imageTitle" to "essa_foto_representa_algo_legal",
                "imageData" to "data:image/jpeg;base64,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",
                "hasQuestions" to "false"
        ), headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        val id = body["id"] as String
        val insertedPost = postRepo.findById(id).get()
        Truth.assertThat(insertedPost.userId.toHexString()).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(insertedPost.type).isEqualTo("image")
        Truth.assertThat(insertedPost.description).isEqualTo("teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem")
        Truth.assertThat(insertedPost.image?.title).isEqualTo("essa_foto_representa_algo_legal")
        Truth.assertThat(insertedPost.image?.imageURL).isNotNull()
        Truth.assertThat(insertedPost.questions).isEmpty()
        Truth.assertThat(insertedPost.approvalFlag).isEqualTo(true)
    }

    @Test
    fun submitImagePost_pngImage_success(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "image",
                "description" to "teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem PNG",
                "imageTitle" to "essa_foto_epresenta_algo_sobre_donuts",
                "imageData" to "data:image/png;base64,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",
                "hasQuestions" to "false"
        ), headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        val id = body["id"] as String
        val insertedPost = postRepo.findById(id).get()
        Truth.assertThat(insertedPost.userId.toHexString()).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(insertedPost.type).isEqualTo("image")
        Truth.assertThat(insertedPost.description).isEqualTo("teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem PNG")
        Truth.assertThat(insertedPost.image?.title).isEqualTo("essa_foto_epresenta_algo_sobre_donuts")
        Truth.assertThat(insertedPost.image?.imageURL).isNotNull()
        Truth.assertThat(insertedPost.questions).isEmpty()
        Truth.assertThat(insertedPost.approvalFlag).isEqualTo(true)
    }

    @Test
    fun submitImagePost_pngImageWithAnotherBase64Compression_success(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "image",
                "description" to "teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem PNG",
                "imageTitle" to "camera",
                "imageData" to "data:image/png;base64,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",
                "hasQuestions" to "false"
        ), headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val body = response.body!!
        val id = body["id"] as String
        val insertedPost = postRepo.findById(id).get()
        Truth.assertThat(insertedPost.userId.toHexString()).isEqualTo(testSetup.USER_ID_1)
        Truth.assertThat(insertedPost.type).isEqualTo("image")
        Truth.assertThat(insertedPost.description).isEqualTo("teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem PNG")
        Truth.assertThat(insertedPost.image?.title).isEqualTo("camera")
        Truth.assertThat(insertedPost.image?.imageURL).isNotNull()
        Truth.assertThat(insertedPost.questions).isEmpty()
        Truth.assertThat(insertedPost.approvalFlag).isEqualTo(true)
    }


    @Test
    fun submitImagePost_imageDataToNull_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "image",
                "description" to "teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem PNG",
                "imageTitle" to null,
                "imageData" to "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAABrVBMVEX////wa7wAAAD2pdbTqXDBVpf5b8P0bb/XrHLcsHX6b8TwaLv2p9fdsXXWrHLGWJv/Dw//rN/sabm9VJTKWp7aYau2kmHPXKLhZLAAwkDdYq2nhlnNpG2+mGWaRXns7OyHbEhvWTt4YEAPDAhmLVDh4eHIyMg3LB2ti1yFO2iYelHEnWjV1dWzUIz0j8x6xP+Dg4NPPyonHxXygsaQQHF8N2FYJ0Wrq6uZmZm+vr4mJiZVRC10XT1kUDWfn59FRUVdXV0kEBz/9QBKITp5eXnyfMMArzo9GzA1NTVQUFBra2vZkr0tHifzjsv1mtEsIxcAgCr02gBzuf/Dg6qsdJYATRmPgAD/6gAAmjPPuQCikQAyFicAaiMXChIeHh6LXXlVOUppRltGcJwAWR0ZKDi9qQBclM7ZDAxuBgYAhiwAnjRmWwBGPwDgyAAnP1d2T2cAEwAwTWuPCAjFCwuqCQkdAABnpue1a5hThrsAMxAAHgctSWU+ZIw0LwCDdQBXTgBPBAQ8AwEAGgYAPxUgHQAtAgJVBQVLRAAXAxcMGCFpBgbMCwueCQkmAgKFBwcknDUuAAAgAElEQVR4nO1d+VtTSdY2RXJvbiQJZIFAQggECEmIGCQBRJRNUMEF9xZx61bb7mnt1U97tJfR6Rln6b/5O+dU1c3ds4jLPI/nhx4HQm699+xLVR048Ik+0Sf6RJ/oE32iT/SJPpEbDY9PTU0sLExMTEyND3/oxewfDY9PzM2vnz7FbHTqzPzC+Ide3lvQ8NTC8cPLR+3ALLQ8P/Whl9oeActaQ2ak9YkPveyWaOr4mVN5Fwgz1aVjuVJpcrKvbwiobzJdyo0szhg+cfhjl9eJM07A8oWl0VJ6KBPWNC3IKUIUjMA/4WdDpREd5omPmZFzt6zIFhGZPxjWEJPflQBoOFNaFH926mPFuCDxzSyN5krpvrKfWOaFTBLwUtPC/vKI+IbTH6MTGT4tVlcYAU0DfEGC1xQcYAtnJkvHqjMm9q8vfGwgFxzMyuJoeijoARNVsJzOLbrYpaPzH5PVcTQwgqUlhGlCGUHGBcvp0aqb0ZUaefxDAxM03MztVXPpcjCMplSD/2SG0rmlguUj23dvX750aQvo0qXLt+/oP5//0OCQpvTl3Lm81dUL1LUFq7y7bcaQL1SBCja2AbZLW730Z4Lw31u35Z9/eMs6J5d6d6uxSFpm15aRGw60/eTypa5e418Z/37r9h596vAHBnhYLveS00IJ5pNtO7g7tzm/ncA1/vgyYTz9QQEuixXvbbkvlnPzyd07QHefgL41xab/ZdftDwxxWGZDe80X26BWsOl/dgm//syHAtiwMe0s2vMViP/Trf9v7xZ+/9yHAXhcB7i1DwAvb++BWQVr1d3d3XXu3LlDQOfOdXVziB8kxCE3T8HW5bYEz5lAGimw3f6/k+pBA/lOHvo/+PGJ949vnNYzWoX/PHl7gN29txkGaRPrjG0mFZ+RDobq8JCF9w2QB6LpHL51K8BuTi2jA6E8e7LCTk9NoSwCxkrIBFFNwVOOvmeA6xSlDA3ZrQxAO3f2CNDZc13NUNJ7OHTEh8KojNE7OzOO9itrhqgU33dswwPRqj+YtylhdxctWOjQEUTpDBPRnRPoOIxoNlvbxRBmOM8qZkGNvWenyCU0Fw5jVn6317TsI3LBXIUOHjx51gayWwhmAx0XRlVVQv27AGWYsYRqYmLtvZpTklDWp2klq6vv7vIZlyxRSlZ2i/8A606awRlggrQuHxhnKyY5VfvhUe8rleJhDEhocMjqCbvPOS6as/IIODf0ckesrLOSUgdBXWbmH4Z235ut4WFMToNE1qqEIKEeC28oZzNSV1jewkOfOvu+NHGeAE6G/f5gwayE3d0nmy++NYrV9tYGTXroU5Ps/bh9yiQKmaDfr6Gr3zaKaCsAwZiozT/lU0KK9WOhlfcBkTuJEZRQAsjaAqgqSipaqfQrTr8DKxqCDzj9ToCukfSceqcAx+kZJZDQiL9qsTJNAapqhbt0VrPBUEPJ7Ngu/c797+P8r9+lueFecAgkNFimeNuQ83obGcSXhT948+Les2ePWdKiYqH4Jn7d81evfnaALykm0pj8O3OLVI45TypYYlaA5ywAVdWkcWoStOje02mgi39l/SaEamqNsSuPNgaQGHNFqMBrmJl5h4kUGdFFxJdZtAHsMgFUFTUZqFQqiZRYrhJg7MXF6QcvvsA/rJtAKFHAtzGw8ejm51c+f87GXBGqGJtmCOI7qRRTvWk0HAn6c4TvjimUMeOTCgdg+E8AxIPppwhvtz6WNXMQfvdoYOOK+IMxN3xCESf95/Fj76CZus4DUa08yldy2xSMGq1MqLLH2Mtfbty/f//Gr6yIcCD3eTr9DMxIvxpSzG5AHWTsq4HrkBNm+2NA7raUK+Ko5i+8E4gEMJ0pVTm+bVNdrftsA6Dqg2T16s7q6rWdnWurO2wNlgyB5rPpB4z123wcfD7LLiDASqi5n1TA2hY0f5AWsb+p1DiJ6IxsDuUtNQujlYntsl93Vnd+eY0f/BbyPJUQ3gOASScIGKpcQfDN4PlEkpgBQ0B2YN9S/qnDlumJ7cu9lnzQuIg1dnX12lXUwNpY/Z+7lOVRhm5N+KSpVQNgZaOtAISPojdGW7eEX7g/1bcF63DIE1vdt9uYLymMXbv2K1sJKEgy9AJnn7VwCX4ST9LvVSXmIL3OENGaU0C1XxCHTxiw7W3fvbxlL+aaYxny62DxrUu2RqMKuXhWTLbEPP2v0EZj29EfHtkXiKLce+f2pS1RrrXCswdrSmXzn7NNlw0u4szE3DpkX7VYi/yjP0N/kdMml/z+8Og+QOQAbzvwzQDQFqxBBG1joGK1k2qCreMjJkBIEm2wUUGDF86xAnAx99YQh7lf96qEdne1kjApldpsQDXjACk9unx4gkLBSusQKQ0uhZdYNSwgvo1FRR2864mv+5BDUcaWAXLVZEWzM4/xsGcO85U2IKLTn9HCVawxcEHt3C9inH2nAwYqlWwloRpMDbBr4/rNvLUEqiixfuDIMspKv5su2uSbXCKgy7OyX5ibjmNU/GNPfM7pkijr1qM6RqXOrkPS8CjP6lZtVCAAwrIas34LeBCCNlvLDprtMqVQ5WCaVcFrhMlpdJhpIAsNoYuFmd3dZ50ElFbw28Mfvv+dsTXpCLAO+PPn1zG43rR9PDTG5uFZRYuzHARnAqqL3Qq2FjD+Uq0wDN1ATvvA9VO9Nt8ZwqMmFm5dNgei51wtTIz93gP08EuQSgkxgSt9vjHwOUWpFm7tQhDNdk0/x3fya56pMfZq49ErEAijR4GoibGR8BAaG1EN66h2M2VKH7bMDO0+5G5BsZry25c/AsZGtQIL2SC81weuMJurxLh02ZwUIsD7q7+ymMr+BmnxxitzSEshYAmYOEQjOhgsr3eA8LCRhb132A0DYO+ChTJLNbE/e3p+NwifinnuVwN/sxsVNbrHVlLGHwCEz1bvs01FAUv06sLGwAUTRF6vSffxgliw3KFbNBnSXvZydee15GLTikxITVV22d97ev5u9ANKgn078BXbDNk+b4lLwfbeX4U3Oqj6lCxWpx4NPGIsZvgAuZ90gfHJscmODOq4SSx7t+GZO6Ky1gygWHWR/dTTY3r3wJGboIrRJlEauM9fAOAK6Z4aigGcm8DFNcObCVFdsUBpBkTh6PlvtYsQLamhUgh6eHX1M+KqreTkc85cQ7Psj55/m0oy6h44RpZthhCyk2/Ynm5cFMg4L4AGV4zWhpdOz2vERTKo7c5qYIPe5CC22Y3Vr9klczLIn5ZMOGIEP/AjqKLhV2Do889Z3BshsPDG6kuT3sX22FcbZp9Jjh+7X3wQMN9+bHPUFrAxtvMN/MxW9yUP7xyTMPaH2Q2gArmXQ8Vn1tjOZ6xm1FYI01+BnJqYH+KhIBanQRX78N/tIWSWUhOO7fwCr9ZYkeFPT7LT885FCHLblsqoojYLQWPs19Wrlu5oqA5mGGyr6U1ECeKSH10GRahtuQwyNOYopnePwZMv2Wx9Pzt8YIKxuC1lait5N37fVVAH2w9/fmSNFtTYGmFMk+NHr9hO+W2K2UaAep+g9NjNhLILzgg+P5YMYd2iXUQ2hBUw239hlq9Bx2hXYPopY4v+oD+CctpOz2bBAeFl9tmOU08lhTOuWO7Yq9fX6u2ks64I/2FF6FP6sw66rvSvcG3UeOGmjVxxziGvuMzuX3MquaupFbaM+yz4YGzg7bgICG98YwlT6eeO9lpVuU2tlrVMe8bGFaE9chY1YDGqPO74CctSPd9BrL3XJNmYo4y/9eDthAPCLfb1Zy5tE6rjn5mbGh+fslg8MzYl5Ev19yeSMdVdYdVkbayl8rD8vNDGargdJk44Zb+9OM2ccF6YqlZ4dZCZQg/TQkCVxvbEh9haLe5mZz3QO5MySEZ1tNQGE2lriG2csvt2zQUgriuUqmSLRWvdV191ssisNJZ8a8srv71C9ibfcnjKe9l2hEe8V6RiUcXRHoSiazZ8SGB59wejQgnVsZZ9It8ccsmGsNPHJyW+/GIuPZlOl0ojcq/FWmB/MIZQGfFLWwrAh/nD7TOxHQ3LqCJIZoVcORwuVwtaJBjWNH9a9Ol2K2r7IO1/EEI7kG/R1vCRINtQrD1tamUpoUGasGBLQ+GgllnCxkqkb6Q0BCCH5F682cH2MCqxaMpWDEmJL2vF1qDpXbRPxRqCblVtKT6DKLuyJjwy4gM9KUC+E8Qq50wuAxxdksJa8bXsIJQUJoe28FE4jVbmwjBiK43a3YXMm2Dd0WxxbKyYapLoKYmaAJAOQwIAXzmT1jArDw6VENpSGVCPSqtTs7HF5VtncbvXKWNVQ5D4ouYI0dsH03ZTww2NGorX5aLsDzEsxJfdFR+jEbFIjuVLhI/vOAyWZihFD/pLsrNcbGEcTE3tslsTuEjbwyUTmwanaGeOaX5mTRC5oQlFIUh6fO/B04sX77nXI9RQUrKPjfRRkurPpINB09ZDbZJXA8PBoWP8o3tNYxksPR4GMTvqFFyJgaKm1hTtTF8kDJZ3z9zLRkODJaAXT6ennz548OCxaz0ilJRsrpYigm9BNKBhvtE5IjfGEj/TLH8sk+buI+sNEc3JwoGpoyDTDo8W/YSmYnoLG3T+YM4qpmholDr74un00+/4N7mM9igpOUwzWubwIlq4nM6NVAuF84XC4kguXda0Bj/LuIO0L5ym1GTWVms0IVwBgPO2Npb8baUlMcV4JgePz1j9RfeRg/CSHl+8+IKxlVo2W+l3XIwMhFlBSmUwmJZuoUGLubIOUguXsNcSJMPq1T4GzzoPAFcG3T7Dv3u5uZCW5WyleSgI3hEAfMx2E0ooZB//5ACF+6v2Sen051z2wZ7PZTQhrxp1knjH00NQU+wojn87+HtOobVWxPQozeTAyjBSN01wo5w/nf4OpxDU4m7dcTiGx4fo9QQ+LdeAdGL5zPoZ08EfS2VRDeSdJA7RNTeEbHcBkgLXXqMsMXqLqRRSfwSH1E3FqIPYL3iDVXlqNbOA/WUrssQnBFDrE57gzJyx6j41p2+LXuQYg300RMIhuvnZ0B4DS19311SpiJ7WFJN7bsQt/gJNqRJfo8E6ZY3xOS1HgNWMrmCj9MC80x6C8XkhvKNkUrUCzjrxpq69hMHXPwiOYs4t/6RPJFjz2HQZPsL9V9iCEBtqYkgZxOXmwFc4KKIYh0h5T2gkLDek8yli90Bxgp9QkMfCdaTERUdDr1F0tmFJdmYi7xVmUN8NrZpX+RuVQzQD8iZjau7HrADEjefgMLKVbFZMbOGQIdZL/JyFET4I6lmmneIDSbixIUIDa9yGu1QSqCPswULu81HQPTZGY52UN3QobjMiPGl8nbFd9mpj4MLPXNB4iZGmnI4BwDSPYQhgs2bCBLe8QbRsxER67p5LfaOSdXUUOkKcnvSYBMctoRnOwvMWKTU/S4X3eWFgYOP6o+vXn9M7p5mSahhHI9MR0X1uZSCbxjrPQ6ha4g+mwmfRRRWbRa7IQ2Si+3N1NeS9DiNCa0MG7dZNnLEYuMBFh9HbAT9ajYh1tjZBQOPxM5mItE8R/P+OvqgpEQ/7PP2FroaUwxm8hUPXMMaDl2/RvgsW5oLaCE2BRFDWWp0CobLQjB7GkZy62FNvIktQCntp/7CuhhrZcoPHd0jwFTUwW9/d2xxDIaUNLZHgJDsfFK6m9UYJlU2qmoRIDc8mMbgzQmxHTWpeioh630fekDalGXJgW1eNf6WKI6Q0I9pPrk2b4bsx0BG2cWAHQTymQyR76uUV3BCi5pQpjHJ7EhoaLim0n2LbaEqbTCegyOKkEhdy1mZjnSCmpaDS08c8swxHoqgtSFLuZsPBruW5GlJO+sTNlDp8+RomXWBfMCCi1Ku9mUHyGnpCRX5fGhuvrVD2RRTCJAJuWzFPSXXgu7aM+WGzPU0oZsEgf0Ha+SaRkwPh/M6IlFOy5Dz9VFP1vWKLQ7a4iBEI4GfcQ1NaJiGcsahhk0oi7Q0sBTNcSIfa00JOuLe/LFgYDFelJiqUjbU0nkm2AAwlTvK7mRomEgu/hl97x3nMS0nZU0MKSfvg1Y8GhZC2PcGDcroY5tHQJOkSlYFi7DvaiiK10j5u3FhElr+koLupGW+oO3lDx6g0tcv2Bq0PISuWAYQ5uVWgXYC8HYRMjPSBIKG3oj6dyt5cnL74hV4ymR0rVhxeMb16rJ6EhUN1fsNTxAhShILZGzYMDYS/X7MVW8UZX18mwnmoeeiBB02Rv6HXW9XI1MXEN9+bvviYO0iFZ/G7FSdGKiJeodTW2dAt6KqAxsxU8z7Y+JqXq7+wlOXLyVlAOkFPCHaihgfIznFDNcPCNKxGwSCGh19cvPiGbGuM/brzzf2vGVuxb0HhaiidlbMxnZNxN70FY7m0YWhC/3y5esNWSSAeIjYs8qK17mSEfk6IEFiKDEWnPP5W1RpI6gNqn6vs9c7q6urOVYeSFa1haDTDAzLnBAodPhfSnEVIG2oItg0QWmspXA9BA5eC/PV0Ml4+LAwdRER9QZQiORIQKrIX099hBkMo/nL1m9VvXpvHpnxcDWfSaEyx2Lvs+Ih5iRBTJ6OQGpJD+J6d+7Y8lMdLtJmlc4RYBUN3DCFNKUihv2QTvNanT8nYKPExTEO/vnbtL9bJF1LDSXIXS27u4rBEWLYIKaihVG14i5/t2IrBVCFJ8/oODe90hPAwt4VksEiMZGwKzujF9Bc0KIX7VaJrLL9z7aXZS/LEgnhIYbHjEyQPrUIKaqgGxCgQYLm6+tIWF8d0V+plyprQnLR0bFEjk69niQp7M31P7yKooSzLX9uhGVuzGA2VCKGrQ5R6qFmaFqCGu3oojJvT7tuVYE9sKvN3bmn01EbLz2hkTPV6DegG2JqiDggg/rr6mWl4lcJuyCtAkihyd0xO50T0m7EIaTeI5p9/COsF33QfmGg9IGdMSnjn3oJHHBGKiiMUmuqtH1jAs4tG3QiN0RSqEWGdsfPhYxj6E/9dEWZE8mLqyoRqrKfnv8JHxLiEmPWcZGSSQ0SPv/xWCJcgPhoyIsQ64V/ZrEEoQS3+Ypq/whR8CZJn1BPkv2P+jVKCr2CEmU+DOKRk2fc9D8W4k4Jz2DsvLRANithh1NZACAHNEI3gN0w2vkHTdhR1cI3tGvUQTeloOI8xQ9AV4RTnA6rhHZOvOKiusB96vhRMVDZxI+xr88kAIZ656N60k506UxJhDpZh5qFP9Vn2YqqKzxSe0isWQVWfG0IZeTv4CpASYKJgW2yF/XLNcnwMVkuFqaEHdGJqhAyBIcj5zXrIMXpSijsLlKOIK0LMnkoR+r1RDTFzAtH8sYftCY8R22TWaQhU9IIwpph7LXeAUI8a/UEuae5tJheEo2SLPWIOiu7JEtnPLGG/gZimpIQUDaabI6zpmwP82khniqgXUWRNsZ2qKdVKzzPN78nDW5h+kBp1mYTUR93Hnv8YHFTIEtUgwrxsqlU7E9N8w6dyg95WvQ2DNl5jcLc0WPKG1GzUjJCn9+Dwen702CtB+VPQUKhr/6AcNAMl2bmiVbRT+OaTGBQ5RtxTYBITTD7ztpZMU4RZJotl4A9HXF+iB2HQeD4tJR1cji3R9iI8AeZ8mU9GuCPEZ/itPOSJE2TXgNB9z5LMLkgLRjAqavtYrlvU3hwN6zl4vR2Eg2MNO+UatVGSP6SZ9FDWoBT2e8/3HsaNDnSgejnocZp2r7bJxAlyaDNsMiLtcbPtNSZKohCVGumt80PIIWqmGQVZ7E6xP3v+8FB9UU/08xQ9Um5fE3GEIQOhTCEsE7hme8DMCPEVVxv1bJen4GvU0NbqXSe9BsV+f8h2PSrtvCbM5Ysnoe2ZU5SfEQ3fT1mqUrN9fGaE0brUE3y42+sld4ElErmtSz8difbDeW07wxFWev2AMCy6K+2EbkwP+0uy5tqWs0gGUEypveNexSB3MaPR6S8WNURjWfM8YoXcBcZcgFATS2xjBzLm96MaiSf6NFzDXlu9mWQgsSJsDaZvbq18MqYkIXJHbKPn1GRYmYp5tLgqk8aw9TSROjOic4lxTdjBlNLYruvs7mA8OitW4JWC44MmI/gOJBNbn3ymSYW+ICZfQzKPbrVeQ901nl7y/hUWBC0ZqBIorgGXVtZqjvXgVDyQ2CWfTCGN24N5QY/8CWmi17Z0K1EHFifwcrw1wEOb1voXR5neeYKAFAxWxmJKVT6H9fPz5zj/seegoYAwmqWoxsMdHpCNfNrNh3La1gYEPt9Z1vpEcV471ipEbFkU9EmqfJVXaQx2jbZT3vyKjqwb2LhpDfuRYvFAIEDLD3pm4GcovOeTGJd6DSxsZa+OSp2wvqDsyfMuZHODusz0cIQowuOqRutAof3cAxsXPr9y5eajry44xgKAMIFtjWDYs4pCc21iAzhAbDyjUq9XeGMLkutB5w6XeoRuQOgr6DWplk5WI4BDphFpvfXEvzfJfv6qcWYdE2kV7RTT37sKLIxiva3Pqwcs4nv9MC19No9P/e1lk2pI7cevcWlZdtNdFgUx7iAhenv+E5zxRoAkpA01VOPAweu4Z2FQjfVXilmcXsTTMsHH7+lF1ARAxGXm3HMnImFwhYRJIKBiw3ynCfqco4dvOUeoB7t679I7LunjPwTRo9c2nrcD9FML2NDf4plRJcQ5hwKkKrRZ7qc/GBsUCPsBIVrTqtcsxgG+50lababXZCE3WgZ9Wlg/ceoEHn004RwzHjzb3fsE/2xJpnnia1xr4Hx/jgWgoY0vVj84tjtmHGlTkgDlXz/09BgyukFQxCgau/PeeQ21uPguATKFwi7jHNuy9DHD8+bxQbJCKs1Fd3f10oUbOR0in4plp51s6gSdaDtTNgPkkw6mCobFz6Pl+RPg/fs/XzZiOzSmUT7C63kqtmxx+cUBPvJVKgG0U6fOrB8+c8ush0qM75xcGcumQiclxJI+/6MN8UHh01YnPMFnL/NBK0B8J17jNLhR7WHPw3+R1jTUBd2FOKbW00WdEgG0X2xylwG+qgxm+TaKvbGK8RQoNDw//fbHb/ireuKIhDjZGFSLjAoTuD4hXcfwhH45zaQFINWRmMfxIkqF/ben50+wPNmooaWvRlER+UvzAkiakTE8qzFDhzFhLDXoMwkMRDK//9jD6UfQ+/pZCdGgXFq5sR/hxPKZ5cZJfnnDsJd4KJ03c9kDIdZue/7FdvvNp2WSqeFbOb3D4cYQrTATnlO56EcA4A/ff/mv70HxUXIQIpmbjOGWLm1oidlppCQG/QxWpozG9W6v+5SZUmc/9PyH1W0+WTc1zeIo1ijtUqa95gWQ3uhvcsXfA1ZWREFFv2hafETTzz4VVC3hpPWQ+Uo2Sr/Zdq9HuEgHUTmNEafiwuc3Ky6ggpTlA1G63Lc3ExP7N9nKbH8s1p8Fe/Pjf1gthBAxumn4DM4eLTKZG1ks0M6gSb8W8UdyabOn57HUtmdOE6LY0GFXRkoa02a1hamGNeWpYtE716YWiUpqSrZ6UD0IFrXLbFAlBLwsL0y7u+geS8144Vww3FcQAD1zmlhtb8UpptIRNg3289hqlE9FMW25uK76KhWcsgOI/JKmnBWiLrPhvlJuZGQkl0sP8Xs7w36xf+1ul0tOo6fgDifSI6EeUimjaZFv3mAIydZEjY/xniYXv0SIZFAXrc5OaJvJ7lRHS6VRqaSUtDmxUMluzno+OxkQPGyKEJ2+HISkbLIyaHhMa4OCOsR8n42NwYz1XsAG3eWH+zmdN4nOfMVDnCj0pkpG85QUE1IpTtioKupxAzzmp5/YoOtTzBAv06KXMmaM4bQrPn6Cr/0oKnq3dYbRokeFMS7zp+YZKe11DtL4D01fjQWkrcEO1A9NPKSEeKS7d4vvbRrRt+E1znNv3JMo7tXbfnKJ88/lmhNljZEVdC27Jwkh+sOmAMklzmCHJshLZpvRpPiWGPuj5/sWK7UAsavrCWfOyBAYTTCfWplv+TUdB44w6b9ddPGTSzQD6c2tKYToZtkDAZE+tVLDRFuDvYchsfEiKjBRANNy0wsh9m6Jiw+rpXS6NCoU0LqHWi+wd5874npHC25OnYPcxzkmV4mFlAK3VMLEJUHaA2kUVXbjcf7ilBp7+LD1hglxsfeS9XLHbet52d3iYqtzePWT+9cpScwzTzh3hqkQFYjWWjGlSLgZaQgnIrjPzwa4mIK2OzQR3VlKEAGj8Z7ObctFl3g10rlDZ5veH+TjW7lBTp12WMdIRgNYkmttGwSWaxZLWPckYzob5ZEbBEyQtZjfoZLymCcAi0oYt4Q+bt+28K+76+zJFu8P8lHeNA/pnStAOsShxX4Q1r9yPHiDf9WiXExDK39vjCuIh846F2h1iA1rsrXV1WvF5652zhAh9TtuFyLJQaqXtQaQmFjlp/Sju4jGyQcqwENzm1QNsC9emMaxrBB959zulmsbHxmC4XFrPVhNxQ0AW56KRCaOYBqFPZK1aIDEFPXwB3O1fQ9nWz1tDyijA8buDvCRMV84YG3Z9HOACYr7Wx+hJyYeY6O0k2wlEYjTA4rsYY9xgACc1L3pF01CgIO+s5ZLAtG6nGwfH1UUjh+4ZWy7qYNCQgOU+7ZzojCWFdPnIVUcRYcYIDGFoO17ENMGExXGLj5tvlnw4MGTh+Slj3QloLdb8LkcierDkOPEAUNjUU1FuZcI8K34bR2ZjPH3DOT4Sxh7Y0+AyrFYImlMtMErfTH9uJXhLLpN9MjZQ+AW3C/Nk6TEAm77fan7I9Ve4Ism5LU2bR6YjIENVsipwh7gYgqW88eevzcmzGuohS0PhbR2r5zqQ364nY+hZtf4r1TIljDlTcSL4iCcfNvD5fhXmcljZXKIXExxturLhssF22oYv94fwkba6xv2SXIdIm3oVFP98XggGo0X5Ul4nYyZLVDsFuQOEd4WiSk8f1d/uMoeT/+1k92e7oRny964tvoPzwOMYoJ9Wf24ow5vYztNWRQePVAHhNzpq3uDH8kAAAcxSURBVD7DeU4p9h2oYcsbIH2xpk3IUJHlv1n97KXXvVapBGlfoLZiCAY7uz6AuusZjbuLQMBuUDjCFoWUDq6pVzwvdFKy7OW1a187nyFE8Dj7QD71Y5qOYcLZ6fXyuD2hQK0V6gnYnoebA1+0OOeKJ+VewSZ80T1Uh898u7PzunHpgA2f9O7imB9WTWvlNkI1O50Q9pRlUUyTtieyL6YfeMVsxs9W6BCGC996XH0Eluuba6/Bjjq3mGX0EkgI74AHFdFUS+e3eJCcUjcP2x4B24rW2MWLTt7CqesfY+zC9Q08gsFtsz28hF9Wf3E52kTmuCChce4eSlhmoVLZcscAuT2dOS8UMW4tQeFGjwf2qFRVKvUxW55K04vs+SM898UZIh7lveMyVarGojpAOgV1lNcp254us5E8DagScGIitZ+te2aVfnrHNnOvxuJo3l8NbLgclZRiX6/ecA5ydQaKDIKJSdtwhwPXRjrKEXIxte2sTI6NWX+G/cvDEBE5gFDVUKrOniNE6y5UHwkpnmzv8Bs9f9B7oKLpSPPub3v3qjjrk6xpIG7bHmtTOIgcb43jpciOK/WpoRq7MvDI6bgndRbsjPO8XlTiC0TJCc7IEuxbyyjShGBigiA2C2AA4GnqX7kevBaqs0cDVxw6WsDDl6+dRiBiOj4+E6Q3OKnn0NEmMjMdF5rIX6Q3RHDrpw8M32IrAfc+PCiv8yUQOMPq8GIMAIWfF7syaOym7YstnIjfM74iRMULIrjsWyjXLh5NfKiCp9o4xQmK44RnAyCVQ/MF/YwQXNb+3GXJD+VaS/DHeJxbqmxCgGi72t72qcpKG+fPRA0sRCUckS1qmhXpNFyzEj8or84h2mMbSXRf3Lzn2Yf8cy5nEjp9tGFFuYwuFkRnnOzovsioAeJmnF5o3I2NoEjjB+T+r32hhh9EwjWUC/xEuEim85TCkYRbnBVsjKacz224dWDK7aCujihmAEh9Jf0c2/amrFshcaLjZkVgDCRjNpApENK5fc35DQwkX6/vbaMTC/f5nnU55rOWTUQFyP5BE0q8r2TY6QzjTsmohFwLpRUNtnOYWMu0LHPO3dmoMHBxM0oVY+LmV3S2TEYZpa6LZCHtod3nG4GR9FE0iHAqCWnF4/Fof4pntWqsNubqKZpdbOHwF1EjQEx65RQMXX+0X47CSHMNiGxzNtBYALCSmx73QowyCB/yOdy34w7QZEdp04/fYGXa3hvXEk0xIxkYSSC9DqwSp6duZj3rNCYyySi5CrHDlCa19s8TWui0CeNm1oARfMig+xm/7M2zey8es9YOJicy4Ys2Nt+91cWVLRCfW55ZFEfI7hUDRoyBpAvGEPtiGujiszctHExOFDUjHJNCymeX38Ht8TqNc+ef00cNayaMcWeMSpG9+e67ew+mp++1NHOkmgHSti26tJID3H8zaiLOxnxJnxmtRZvzURzU/IaukW8KUU2YAZK7p2O26FDOfbtW3Y3Edd0zJb88zLqYMGN0MDqKGoslAOZ300+bbZ5UYxYOyi1NkSAJzv5cqu5NC0INc8ES/9dK1vTW486GVVVia+yvCNGzLdEftwAM0F2tmSA/uvedc5CT8I35crjE/7UWN734eCLlJKx4A+6L6Weue3xVH29LOAhpVUzEvWMdNJCobpTCwZwUVfOqnD2kssvuPXMolEPIE0slo3EHfLzIPUkquJ8JU3OaIwldDIYz3ObsViz6E4/aMxC8ktJQZMVYLhZLDSb7AZwTOiQKF0hC9zmdaE4Ujef7wtpQwYmNnJMWcVV9FdybhcgQWAJdjCs2oqjeR9vXhLA14uo4EgxqXB3XHBYYjyeSKYq8JfliyLNAE2C6jOr3Rb1LP+9K/IKv/GRYy5AlX4lbLT0HGQ8k+okS0aY8s3BQdnrfRTLREvFi42JZE5u4rMr4dpSoCBFdfneRaFOa4i5xNBLmR0hnbcrYKTUGSZbfqwm1k8iNSxrdA85qbw0xipSozAoNPPwB+SdoXOy0K/F9sZvtSSqiAe2sSMpms7PFMf2+veUPjY6TiOPyJe4aTcG4J7hEoDI7VpfXX9np1PuLYZqRiHFm+IbKvWIzjMi4StEDG9L6B3EQrnTcvLpaPOECkivZ2KYzKsm89bmPCx6RBePaLIA08ZJMSDxbq++ZPjhTHcmVSunJnLyE7UPbTg9aOGUGuTJWzMajCU7xStZgQThVR0tDfg2vLCvn5Hah9Y8XH9LUGWan3d3dFdsPCyOlsty5Njkq2fcx+IamNHfChsZC+UXcZInwNC0zmWvs9TrxnvLbt6bh+VNu4IBzfZlwGC/SK/elR2XVDuno/P8A+xo0PLdshzezmEMaHVmqFmbMvzp1/OPWPmeaOu6A0oFOHV74n+KemcYX5r1gHl0/PvE/jK5B4xNzh9eXT5wSWnfr1Inl9fm5if9FwfxEn+gTfaJP9Ik+0cdG/w+il+77enEn4wAAAABJRU5ErkJggg==",
                "hasQuestions" to "false"
        ), headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitImagePost_imageTitleToNull_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "image",
                "description" to "teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem PNG",
                "imageTitle" to "essa_foto_epresenta_algo_sobre_donuts",
                "imageData" to null,
                "hasQuestions" to "false"
        ), headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitImagePost_emptyImageTitle_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "image",
                "description" to "teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem PNG",
                "imageTitle" to "",
                "imageData" to "data:image/png;base64,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",
                "hasQuestions" to "false"
        ), headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitImagePost_emptyImageData_throwBadRequest(){
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "image",
                "description" to "teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem teste de imagem PNG",
                "imageTitle" to "essa_foto_epresenta_algo_sobre_donuts",
                "imageData" to "",
                "hasQuestions" to "false"
        ), headers)
        val response = restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitVideoPost_descriptionNull_throwBadRequest() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to null,
                "videoUrl" to "www.youtube.com/lalal",
                "hasQuestions" to "false"
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
    @Test
    fun submitVideoPost_descriptionEmptyString_throwBadRequest() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to "",
                "videoUrl" to "www.youtube.com/lalal",
                "hasQuestions" to "false"
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
    @Test
    fun submitVideoPost_videoUrlEmpty_throwBadRequest() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to "teste teste",
                "videoUrl" to "",
                "hasQuestions" to "false"
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitVideoPost_defaultThumbnailEmpty_throwBadRequest() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to "teste teste",
                "videoUrl" to "www.youtube.com/lalal",
                "hasQuestions" to "false"
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun submitVideoPost_hasQuestionsToInt_throwBadRequest() {
        val headers = HttpHeaders()
        headers["API_KEY"] = apiKey
        headers["Authorization"] = testSetup.USER_1_ACCESS_TOKEN
        val entity = HttpEntity(hashMapOf(
                "type" to "video",
                "description" to "teste teste",
                "videoUrl" to "https://www.youtube.com/watch?v=8vefLpfozPA",
                "hasQuestions" to 1
        ), headers)
        val response =
                restTemplate.exchange<HashMap<String, Any>>(baseUrl, HttpMethod.POST, entity)
        Truth.assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
}