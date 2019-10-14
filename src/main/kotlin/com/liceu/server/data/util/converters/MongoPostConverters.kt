package com.liceu.server.data.util.converters

import com.liceu.server.data.MongoDatabase
import com.liceu.server.domain.post.*


fun toPost(mongoPost: MongoDatabase.MongoPost): Post {
    return Post(
            mongoPost.id.toHexString(),
            mongoPost.userId.toHexString(),
            mongoPost.type,
            mongoPost.description,
            FormattedImage(
                    mongoPost.image?.title,
                    mongoPost.image?.type,
                    mongoPost.image?.imageURL
            ),
            PostVideo(
                    mongoPost.video?.videoUrl,
                    PostThumbnails(
                            mongoPost.video?.thumbnails?.high,
                            mongoPost.video?.thumbnails?.default,
                            mongoPost.video?.thumbnails?.medium
                    )
            ),
            mongoPost.submissionDate,
            mongoPost.comments?.map {
                PostComment(
                        it.id.toHexString(),
                        it.userId.toHexString(),
                        it.author,
                        it.comment
                )
            },
            mongoPost.questions?.map {
                PostQuestions(
                        it.question,
                        it.correctAnswer,
                        it.otherAnswers
                )
            },
            PostDocument(
                mongoPost.document?.id?.toHexString(),
                mongoPost.document?.title,
                mongoPost.document?.type,
                mongoPost.document?.documentURL
            ),
            mongoPost.approvalFlag,
            mongoPost.likes
    )
}