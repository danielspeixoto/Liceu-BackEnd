package com.liceu.server.domain.post

import com.liceu.server.data.MongoPostRepository
import com.liceu.server.domain.global.IMAGE
import com.liceu.server.domain.global.INSERTION
import com.liceu.server.domain.global.POST
import com.liceu.server.util.Logging
import org.apache.commons.codec.binary.Base64;
import com.google.cloud.storage.BucketInfo
import com.google.api.gax.paging.Page
import com.google.cloud.storage.Acl
import com.google.cloud.storage.Acl.User
import com.google.cloud.storage.Blob
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Bucket.BucketSourceOption
import com.google.cloud.storage.Storage.BlobGetOption
import com.google.cloud.storage.StorageException
import org.bouncycastle.crypto.tls.ConnectionEnd.client
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.LinkedList
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.nio.charset.StandardCharsets.UTF_8
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.BlobId
import java.nio.file.Files.readAllBytes
import java.io.FileInputStream
import com.google.auth.oauth2.ServiceAccountCredentials
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


class ImagePost(
        private val postRepository: MongoPostRepository
): PostBoundary.IImagePost {
    companion object{
        const val EVENT_NAME = "text_post_submission"
        val TAGS = listOf(INSERTION, IMAGE, POST)
    }

    override fun run(post: PostSubmission): String {
        try {
            //val storage = StorageOptions.getDefaultInstance().service

            val storage = StorageOptions.newBuilder()
                    .setCredentials(ServiceAccountCredentials.fromStream(FileInputStream("/home/ingoalmeida/Documentos/private/My Project 15866-cb6b3924d24a.json")))
                    .build()
                    .service

            val bucketName = "liceu-dev-test"
            val fileName = "image_test_1.jpeg"

            //uploading files to liceu test bucket
            val blobId = BlobId.of(bucketName, fileName)
            val blobInfo = BlobInfo.newBuilder(blobId).build()
            val filePath = "/home/ingoalmeida/Downloads/anonymouse.jpeg" //image test
            val encodedFile = File(filePath).readBytes() //file to array of bytes
            val blob = storage.create(blobInfo, encodedFile)

            //restoring data from bucket
            val content = storage.readAllBytes(blobId)
            val urlLink = "https://storage.cloud.google.com/${bucketName}/${fileName}"
            val contentString = String(content, UTF_8)
            println(contentString)

            Logging.info(EVENT_NAME,TAGS, hashMapOf(
                    "userId" to post.userId,
                    "type" to post.type,
                    "imageUrl" to urlLink
            ))
            return "oi"
        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}