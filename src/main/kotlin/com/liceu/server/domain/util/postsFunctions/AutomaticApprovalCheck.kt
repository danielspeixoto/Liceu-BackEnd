package com.liceu.server.domain.util.postsFunctions

import com.liceu.server.domain.post.PostBoundary
import com.liceu.server.domain.user.UserBoundary


fun postsAutomaticApproval (postRepository: PostBoundary.IRepository,userRepository: UserBoundary.IRepository,userId: String,postsMinimumApproval: Int): Boolean? {
    var automaticApproval : Boolean? = null
    if (userRepository.getUserById(userId).postsAutomaticApproval != null && userRepository.getUserById(userId).postsAutomaticApproval!!){
        automaticApproval = true
    }else if(postRepository.countApprovedPosts(userId) >= postsMinimumApproval){
        userRepository.updatePostsAutomaticApprovalFlag(userId)
        automaticApproval = true
    }
    return automaticApproval
}
