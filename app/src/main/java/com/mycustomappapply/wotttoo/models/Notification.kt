package com.mycustomappapply.wotttoo.models

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("_id") val id: String?,
    val likerUserId: String?,
    val likedQuoteId: String?,
    val userId: String?,
    val userPhoto: String?,
    val likeCount: Int,
    val read: Boolean = false
)
