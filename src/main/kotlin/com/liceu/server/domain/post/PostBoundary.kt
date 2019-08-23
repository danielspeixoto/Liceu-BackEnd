package com.liceu.server.domain.post

import java.util.*

class PostBoundary {

    interface IRepository{
        fun insertPost(postToInsert: PostToInsert): String
        fun getPosts(date: Date,amount: Int)
    }



}