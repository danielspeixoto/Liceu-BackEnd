package com.liceu.server.domain.video

class VideoBoundary {

    interface IRepository {
        @Throws(Error::class)
        fun questionRelatedVideos(id: String, start: Int, count: Int): List<Video>
    }
}