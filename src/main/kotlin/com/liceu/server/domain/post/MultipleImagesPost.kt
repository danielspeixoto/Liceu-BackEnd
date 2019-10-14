package com.liceu.server.domain.post

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions
import com.liceu.server.domain.global.*
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.domain.util.dateFunctions.DateFunctions
import com.liceu.server.domain.util.fileFunctions.FileFunctions
import com.liceu.server.domain.util.postsFunctions.postsAutomaticApproval
import com.liceu.server.util.Logging
import java.io.FileInputStream
import java.util.*

class MultipleImagesPost(
        private val postRepository: PostBoundary.IRepository,
        private val bucketName: String,
        private val userRepository: UserBoundary.IRepository,
        private val postsMinimumApproval: Int
): PostBoundary.IMultipleImagesPosts {

    companion object{
        const val EVENT_NAME = "multiple_image_post_submission"
        val TAGS = listOf(INSERTION, MULTIPLE ,IMAGE, POST)
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
        var formattedImages: MutableList<FormattedImage> = arrayListOf()
        try {
            post.multipleImages?.forEach {
                if(it.title!!.isBlank()){
                    throw UnderflowSizeException ("Title can't be empty")
                }
                if(it.title!!.length > 150){
                    throw OverflowSizeException ("Title too much characters")
                }
                if(it.imageData!!.isBlank()){
                    throw UnderflowSizeException ("Image can't be empty")
                }
                var fileExtension = FileFunctions.extractMimeType(it.imageData)
                if(fileExtension.isEmpty()){
                    throw TypeMismatchException("this file type is not supported")
                }
                //verifying size of archive
                val formattedEncryptedBytes = it.imageData.substringAfterLast(",")
                if(FileFunctions.calculateFileSize(formattedEncryptedBytes) > 1e7){
                    throw OverflowSizeException("image is greater than 10MB")
                }

                //decoding and retrieving image type
                var contentType = fileExtension
                imageTypes.forEach { entry ->
                    fileExtension = fileExtension.replace(entry.key,entry.value)
                }
                var timeStamp = DateFunctions.retrieveActualTimeStamp().toString().replace("\\s".toRegex(), "")
                var fileName = "${it.title}${post.userId}${timeStamp}.${fileExtension}"
                val imageByteArray = Base64.getDecoder().decode(formattedEncryptedBytes)

                //uploading files to liceu test bucket
                val blobId = BlobId.of(bucketName, fileName)
                val blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build()
                val blob = storage.create(blobInfo, imageByteArray)
                val urlLink = "https://storage.googleapis.com/${bucketName}/${fileName}"


                Logging.info(ImagePost.EVENT_NAME, ImagePost.TAGS, hashMapOf(
                        "userId" to post.userId,
                        "type" to post.type,
                        "imageUrl" to urlLink
                ))

                formattedImages.add(FormattedImage(
                        it.title,
                        fileExtension,
                        urlLink
                ))
            }

            return postRepository.insertPost(PostToInsert(
                    post.userId,
                    post.type,
                    post.description,
                    formattedImages[0],
                    post.video,
                    formattedImages.toList(),
                    DateFunctions.retrieveActualTimeStamp(),
                    null,
                    post.questions,
                    postsAutomaticApproval(postRepository,userRepository,post.userId,postsMinimumApproval)
            ))

        }catch (e: Exception){
            Logging.error(EVENT_NAME,TAGS,e)
            throw e
        }
    }
}