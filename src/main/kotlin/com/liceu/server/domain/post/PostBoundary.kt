package com.liceu.server.domain.post

import com.liceu.server.domain.user.User
import java.util.*

class PostBoundary {

    interface IRepository{
        fun insertPost(postToInsert: PostToInsert): String
        fun getPosts(user:User, date: Date,amount: Int): List<Post>
        fun getPostById(postId: String): Post
    }

    interface ITextPost {
        fun run(post: PostSubmission): String
    }

    interface IImagePost {
        fun run(post: PostSubmission): String
    }

    interface IVideoPost {
        fun run(post: PostSubmission): String
    }

    interface IGetPosts {
        fun run(userId:String, day: Int, month: Int, year: Int, amount: Int): List<Post>
    }


}