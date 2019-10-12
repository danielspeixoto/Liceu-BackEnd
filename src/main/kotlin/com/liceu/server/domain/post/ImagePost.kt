package com.liceu.server.domain.post

import com.liceu.server.data.MongoPostRepository
import com.liceu.server.util.Logging
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.BlobId
import java.io.FileInputStream
import com.google.auth.oauth2.ServiceAccountCredentials
import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.domain.util.dateFunctions.DateFunctions
import com.liceu.server.domain.util.dateFunctions.DateFunctions.retrieveActualTimeStamp
import com.liceu.server.domain.util.fileFunctions.FileFunctions
import com.liceu.server.domain.util.postsFunctions.postsAutomaticApproval
import java.util.*


class ImagePost(
        private val postRepository: PostBoundary.IRepository,
        private val bucketName: String,
        private val userRepository: UserBoundary.IRepository,
        private val postsMinimumApproval: Int
): PostBoundary.IImagePost {
    companion object{
        const val EVENT_NAME = "text_post_submission"
        val TAGS = listOf(INSERTION, IMAGE, POST)
    }

    //bucket connection
    val storage = StorageOptions.newBuilder()
            .setCredentials(ServiceAccountCredentials.fromStream(FileInputStream("googleImagesLiceu.json")))
            .build()
            .service

    override fun run(post: PostSubmission): String {
        val imageTypes = hashMapOf(
                "image/jpeg" to "jpeg",
                "image/png" to "png"
        )
        try {
            if(post.image?.title!!.isBlank()){
                throw UnderflowSizeException ("Title can't be empty")
            }
            if(post.image?.title!!.length > 150){
                throw OverflowSizeException ("Title too much characters")
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

            //decoding and retrieving image type
            var contentType = fileExtension
            imageTypes.forEach {
                fileExtension = fileExtension.replace(it.key,it.value)
            }
            var timeStamp = DateFunctions.retrieveActualTimeStamp().toString().replace("\\s".toRegex(), "")
            var fileName = "${post.image.title}${post.userId}${timeStamp}.${fileExtension}"
            val imageByteArray = Base64.getDecoder().decode(formattedEncryptedBytes)

            //uploading files to liceu test bucket
            val blobId = BlobId.of(bucketName, fileName)
            val blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build()
            val blob = storage.create(blobInfo, imageByteArray)
            val urlLink = "https://storage.googleapis.com/${bucketName}/${fileName}"

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
                    retrieveActualTimeStamp(),
                    null,
                    post.questions,
                    postsAutomaticApproval(postRepository,userRepository,post.userId,postsMinimumApproval)
            ))

        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }

}