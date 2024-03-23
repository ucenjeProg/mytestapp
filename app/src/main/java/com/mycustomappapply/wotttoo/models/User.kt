package com.mycustomappapply.wotttoo.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserOrg(
    @SerializedName("_id") val id: String,
    val username: String?,
    val fullname: String?,
    val email: String?,
    val bio: String?,
    var followers: List<String>?,
    val followingUsers: List<String>?,
    val followingGenres: List<String>?,
    val quotes: List<Quote>?,
    val profileImage: String?,
    val totalQuoteCount: Int?
) : Serializable
