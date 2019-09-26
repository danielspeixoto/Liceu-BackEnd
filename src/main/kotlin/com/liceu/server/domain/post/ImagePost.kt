package com.liceu.server.domain.post

import com.liceu.server.data.MongoPostRepository
import com.liceu.server.util.Logging
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.BlobId
import java.io.FileInputStream
import com.google.auth.oauth2.ServiceAccountCredentials
import com.liceu.server.domain.global.*
import com.liceu.server.domain.util.TimeStamp
import com.liceu.server.domain.util.fileFunctions.FileFunctions
import java.util.*


class ImagePost(
        private val postRepository: MongoPostRepository
): PostBoundary.IImagePost {
    companion object{
        const val EVENT_NAME = "text_post_submission"
        val TAGS = listOf(INSERTION, IMAGE, POST)
    }

    override fun run(post: PostSubmission): String {
        val imageTypes = hashMapOf(
                "image/jpeg" to "jpeg",
                "image/png" to "png",
                "application/pdf" to "pdf"
        )
        try {
            if(post.image?.title!!.isBlank()){
                throw UnderflowSizeException ("Title can't be empty")
            }
            if(post.image.imageData!!.isBlank()){
                throw UnderflowSizeException ("Image can't be empty")
            }
            var fileExtension = FileFunctions.extractMimeType(post.image.imageData)
            if(fileExtension.isEmpty()){
                throw TypeMismatchException("this file type is not supported")
            }
            //verifying size of archive
            val formattedEncryptedBytes = post.image.imageData.substringAfterLast(",")
            if(FileFunctions.calculateFileSize(formattedEncryptedBytes) > 1e7){
                throw OverflowSizeException("image is greater than 10MB")
            }

            //bucket connection
            val storage = StorageOptions.newBuilder()
                    .setCredentials(ServiceAccountCredentials.fromStream(FileInputStream("/home/ingoalmeida/Documentos/private/My Project 15866-cb6b3924d24a.json")))
                    .build()
                    .service


            val bucketName = "liceu-dev-test"

            //decoding and retrieving image type
            var contentType = fileExtension
            imageTypes.forEach {
                fileExtension = fileExtension.replace(it.key,it.value)
            }
            var fileName = "${post.image.title}.${fileExtension}"
            val imageByteArray = Base64.getDecoder().decode(formattedEncryptedBytes)

            //uploading files to liceu test bucket
            val blobId = BlobId.of(bucketName, fileName)
            val blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build()
            val blob = storage.create(blobInfo, imageByteArray)
            val urlLink = "https://storage.cloud.google.com/${bucketName}/${fileName}"

            Logging.info(EVENT_NAME,TAGS, hashMapOf(
                    "userId" to post.userId,
                    "type" to post.type,
                    "imageUrl" to urlLink
            ))

            return postRepository.insertPost(PostToInsert(
                    post.userId,
                    post.type,
                    post.description,
                    FormattedImage(
                            post.image.title,
                            fileExtension,
                            urlLink
                    ),
                    post.video,
                    TimeStamp.retrieveActualTimeStamp(),
                    null,
                    post.questions
            ))

        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }

}