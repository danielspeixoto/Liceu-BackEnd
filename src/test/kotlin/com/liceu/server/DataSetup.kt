package com.liceu.server

import com.liceu.server.data.MongoDatabase
import com.liceu.server.data.QuestionRepository
import com.liceu.server.data.TagRepository
import com.liceu.server.data.VideoRepository

fun setup(questionRepo: QuestionRepository, videoRepo: VideoRepository, tagRepo: TagRepository) {
    questionRepo.deleteAll()
    videoRepo.deleteAll()
    tagRepo.deleteAll()
    val q1 = MongoDatabase.MongoQuestion(
            "ab",
            "ENEM",
            "AMARELA",
            2017,
            3,
            "matemática",
            1,
            listOf("primeira", "segunda"),
            "12345",
            "referenceId",
            2,
            100,
            200
    )
    q1.id = "id1"
    questionRepo.insert(q1)
    val q2 = MongoDatabase.MongoQuestion(
            "ef",
            "ENEM",
            "AMARELA",
            2016,
            5,
            "linguagens",
            1,
            listOf("segunda"),
            "54321",
            "referenceId2",
            2,
            100,
            200
    )
    q2.id = "id2"
    questionRepo.insert(q2)
    val q3 = MongoDatabase.MongoQuestion(
            "cd",
            "ENEM",
            "AZUL",
            2015,
            15,
            "linguagens",
            1,
            listOf(),
            "54321",
            "referenceId3",
            1,
            100,
            200
    )
    q3.id = "id3"
    questionRepo.insert(q3)

    val item1 = MongoDatabase.MongoVideo(
            "primeira",
            "primeiro video",
            "videoId1",
            "id1",
            1.1f,
            MongoDatabase.Thumbnails(
                    "highQuality",
                    "defaultQuality",
                    "mediumQuality"
            ),
            MongoDatabase.Channel(
                    "channelTitle",
                    "channelId"
            ),
            3
    )
    item1.id = "id1"
    videoRepo.insert(item1)
    val item2 = MongoDatabase.MongoVideo(
            "segundo",
            "segundo video",
            "videoId3",
            "id2",
            1.1f,
            MongoDatabase.Thumbnails(
                    "highQuality",
                    "defaultQuality",
                    "mediumQuality"
            ),
            MongoDatabase.Channel(
                    "channelTitle",
                    "channelId"
            ),
            2
    )
    item2.id = "id2"
    videoRepo.insert(item2)
    val item3 = MongoDatabase.MongoVideo(
            "terceiro",
            "terceiro video",
            "videoId2",
            "id1",
            1.3f,
            MongoDatabase.Thumbnails(
                    "highQuality",
                    "defaultQuality",
                    "mediumQuality"
            ),
            MongoDatabase.Channel(
                    "channelTitle",
                    "channelId"
            ),
            1
    )
    item3.id = "id3"
    videoRepo.insert(item3)

    val tag1 = MongoDatabase.MongoTag(
            "primeira",
            1
    )
    tag1.id = "id1"
    tagRepo.insert(tag1)
    val tag2 = MongoDatabase.MongoTag(
            "segunda",
            2
    )
    tag2.id = "id2"
    tagRepo.insert(tag2)
}