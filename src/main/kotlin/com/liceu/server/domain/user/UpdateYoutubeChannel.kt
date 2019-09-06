package com.liceu.server.domain.user

import com.liceu.server.data.MongoUserRepository
import com.liceu.server.domain.global.*
import com.liceu.server.domain.report.SubmitReport
import com.liceu.server.util.Logging

class UpdateYoutubeChannel(
        private val userRepo: UserBoundary.IRepository
): UserBoundary.IUpdateYoutubeChannel {

    companion object {
        const val EVENT_NAME = "update_youtube_channel_user"
        val TAGS = listOf(RETRIEVAL, USER , UPDATE, YOUTUBE)
    }

    override fun run(userId: String, youtubeChannel: String) {
        try {
            if(youtubeChannel.length > 100){
                throw OverflowSizeException ("Too many characters in youtube channel name")
            }
            Logging.info(EVENT_NAME,TAGS, hashMapOf(
                    "userId" to userId,
                    "youtubeChannel" to youtubeChannel
            ))
            userRepo.updateYoutubeChannelFromUser(userId,youtubeChannel)
            //maybe verify if string contains -> www.youtube.com.br/
        }catch (e: Exception){
            Logging.error(EVENT_NAME,TAGS,e)
            throw e
        }
    }
}