package com.liceu.server.domain.video

data class Video(
        val id: String,
        val title: String,
        val description: String,
        val videoId: String,
        val questionId: String,
        val aspectRatio: Float,
        val thumbnail: String,
        val channelTitle: String
)