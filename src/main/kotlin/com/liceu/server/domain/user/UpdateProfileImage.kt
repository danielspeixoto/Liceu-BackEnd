package com.liceu.server.domain.user

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions
import com.liceu.server.domain.global.*
import com.liceu.server.domain.util.fileFunctions.FileFunctions
import com.liceu.server.util.Logging
import java.io.FileInputStream
import java.util.*

class UpdateProfileImage(
        private val userRepository: UserBoundary.IRepository,
        private val bucketName: String
): UserBoundary.IUpdateProfileImage {
    companion object{
        const val EVENT_NAME = "update_profile_image"
        val TAGS = listOf(UPDATE, PROFILE ,IMAGE)
    }


    //bucket connection
    val storage = StorageOptions.newBuilder()
            .setCredentials(ServiceAccountCredentials.fromStream(FileInputStream("googleImagesLiceu.json")))
            .build()
            .service

    override fun run(userId: String, imageData: String) {
        val imageTypes = hashMapOf(
                "image/jpeg" to "jpeg",
                "image/jpg" to "jpg",
                "image/png" to "png"
        )
        try {
            if(imageData.isBlank()){
                throw UnderflowSizeException ("ImageData can't be empty")
            }
            var fileExtension = FileFunctions.extractMimeType(imageData)
            if(fileExtension.isEmpty()){
                throw TypeMismatchException("this file type is not supported")
            }
            //verifying size of archive
            val formattedEncryptedBytes =imageData.substringAfterLast(",")
            if(FileFunctions.calculateFileSize(formattedEncryptedBytes) > 1e7){
                throw OverflowSizeException("image size is greater than 10MB")
            }

            //decoding and retrieving image type
            var contentType = fileExtension
            imageTypes.forEach {
                fileExtension = fileExtension.replace(it.key,it.value)
            }

            var userName = userRepository.getUserById(userId).name
            var fileName = "${userName}${userId}.${fileExtension}"
            val imageByteArray = Base64.getDecoder().decode(formattedEncryptedBytes)

            //uploading files to liceu test bucket
            val blobId = BlobId.of(bucketName, fileName)
            val blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build()
            val blob = storage.create(blobInfo, imageByteArray)
            val urlLink = "https://storage.googleapis.com/${bucketName}/${fileName}"

            Logging.info(EVENT_NAME, TAGS, hashMapOf(
                    "userId" to userId,
                    "imageUrl" to urlLink
            ))
            userRepository.updateProfileImage(userId,urlLink)
        } catch(e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}