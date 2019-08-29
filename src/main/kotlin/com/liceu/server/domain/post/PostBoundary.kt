package com.liceu.server.domain.post

import com.liceu.server.domain.user.User
import java.util.*

class PostBoundary {

    interface IRepository{
        fun insertPost(postToInsert: PostToInsert): String
        fun getPostsForFeed(user:User, date: Date,amount: Int): List<Post>
        fun getPostById(postId: String): Post
        fun getPostFromUser(userId: String): List<Post>
        fun getRandomPosts(amount: Int): List<Post>
        fun updateListOfComments(postId: String,userId: String,author: String,comment: String): Long
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
        fun run(userId:String, date: Date, amount: Int): List<Post>?
    }

    interface IGetPostsFromUser{
        fun run(userId: String): List<Post>
    }

    interface IGetRandomPosts {
        fun run(amount: Int): List<Post>
    }

    interface IUpdateListOfComments{
        fun run(postId: String,userId: String,comment: String)
    }


}