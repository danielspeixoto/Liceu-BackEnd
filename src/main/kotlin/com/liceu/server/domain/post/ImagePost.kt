package com.liceu.server.domain.post

import com.liceu.server.data.MongoPostRepository
import com.liceu.server.domain.global.IMAGE
import com.liceu.server.domain.global.INSERTION
import com.liceu.server.domain.global.POST
import com.liceu.server.util.Logging
import java.util.*
import com.sun.mail.util.BASE64DecoderStream


import java.nio.charset.StandardCharsets.UTF_8
import javax.imageio.ImageIO
import java.io.ByteArrayInputStream
import java.awt.image.BufferedImage
import org.apache.commons.codec.binary.Base64;
import java.io.File


//class ImagePost(
//        private val postRepository: MongoPostRepository
//): PostBoundary.IImagePost {
//
//    companion object{
//        const val EVENT_NAME = "text_post_submission"
//        val TAGS = listOf(INSERTION, IMAGE, POST)
//    }
//
//    override fun run(userId: String, type: String, description: String, image: Base64): String {
//        try {
////            val storage = StorageOptions.getDefaultInstance().getService()
////            val blobId = BlobId.of("bucket-liceu", "blob_name")
////            val blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build()
////            val blob = storage.create(blobInfo, image)
////            val storage = StorageOptions.getDefaultInstance().service
////            val bucket = storage.get(bucketName) ?: error("Bucket $bucketName does not exist.")
//            Logging.info(EVENT_NAME,TAGS, hashMapOf(
//                    "userId" to userId,
//                    "type" to type
//                    //"imageUrl" to imageUrl
//            ))
//            return "oi"
//        }catch (e: Exception){
//            Logging.error(EVENT_NAME, TAGS,e)
//            throw e
//        }
//    }
//}