package com.liceu.server.domain.post

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions
import com.liceu.server.domain.global.*
import com.liceu.server.domain.user.UserBoundary
import com.liceu.server.domain.util.dateFunctions.DateFunctions
import com.liceu.server.domain.util.fileFunctions.FileFunctions
import com.liceu.server.util.Logging
import java.io.FileInputStream
import java.lang.Exception
import java.util.*

class UpdateDocument(
    private val postRepository: PostBoundary.IRepository,
    private val bucketName: String
): PostBoundary.IUpdateDocument {

    companion object{
        const val EVENT_NAME = "post_document_update"
        val TAGS = listOf(UPDATE, POST, DOCUMENT)
    }

    //bucket connection
    val storage = StorageOptions.newBuilder()
            .setCredentials(ServiceAccountCredentials.fromStream(FileInputStream("googleImagesLiceu.json")))
            .build()
            .service

    override fun run(postId: String, userId: String,documents: List<PostDocumentSubmission>) {
        try {
            val documentTypes = hashMapOf(
                    "image/jpeg" to "jpeg",
                    "image/png" to "png",
                    "application/pdf" to "pdf",
                    "application/msword" to "doc",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document" to "docx",
                    "application/vnd.ms-powerpoint" to "ppt",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation" to "pptx",
                    "application/vnd.ms-excel" to "xls",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" to "xlsx",
                    "text/plain" to "txt"
            )

            if(postRepository.getPostById(postId).userId != userId){
                throw AuthenticationException("user attempting to change other user properties")
            }

            documents.forEach {

                if(it.title!!.isBlank()){
                    throw UnderflowSizeException ("Title can't be empty")
                }
                if(it.title.length > 150){
                    throw OverflowSizeException ("Title too much characters")
                }
                if(it.documentData!!.isBlank()){
                    throw UnderflowSizeException ("Document can't be empty")
                }
                var fileExtension = FileFunctions.extractDocumentsMimeType(it.documentData)
                if(fileExtension.isEmpty()){
                    throw TypeMismatchException("this file type is not supported")
                }
                //verifying size of archive
                val formattedEncryptedBytes = it.documentData.substringAfterLast(",")
                if(FileFunctions.calculateFileSize(formattedEncryptedBytes) > 1e7){
                    throw OverflowSizeException("image is greater than 10MB")
                }

                //decoding and retrieving image type
                var contentType = fileExtension
                documentTypes.forEach {
                    fileExtension = fileExtension.replace(it.key,it.value)
                }
                var timeStamp = DateFunctions.retrieveActualTimeStamp().toString().replace("\\s".toRegex(), "")
                var fileName = "${it.title}${userId}${timeStamp}.${fileExtension}"
                val imageByteArray = Base64.getDecoder().decode(formattedEncryptedBytes)

                //uploading files to liceu test bucket
                val blobId = BlobId.of(bucketName, fileName)
                val blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build()
                val blob = storage.create(blobInfo, imageByteArray)
                val urlLink = "https://storage.googleapis.com/${bucketName}/${fileName}"

                Logging.info(EVENT_NAME, TAGS, hashMapOf(
                        "postId" to postId,
                        "documentType" to fileExtension,
                        "documentUrl" to urlLink
                ))
                postRepository.updateDocumentPost(postId,it.title,fileExtension,urlLink)
            }

        }catch (e: Exception){
            Logging.error(EVENT_NAME, TAGS,e)
            throw e
        }
    }
}