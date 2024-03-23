package com.mycustomappapply.wotttoo.models

import java.io.Serializable

data class UsersResponseOrg(
    val message: String,
    val users: List<UserOrg>
)

data class UsersResponse(
    val `data`: List<User>,
    val links: Links? = null,
) : Serializable

data class CurrentUSerResponse(
    val `data`: Data,
    val links: Links
) : Serializable


data class Data(
    val attributes: Attributes,
    val id: String,
    val type: String
)

data class Attributes(
    val block: Int,
    val email: String,
    val group_count: Int,
    val group_names: String,
    val id: Int,
    val lastResetTime: Any,
    val lastvisitDate: String,
    val name: String,
    val registerDate: String,
    val resetCount: Int,
    val sendEmail: Int,
    val username: String,


    val fullname: String?,
    val bio: String?,
    var followers: List<String>?,
    val followingUsers: List<String>?,
    val followingGenres: List<String>?,
    val quotes: List<Quote>?,
    val profileImage: String?,
    val totalQuoteCount: Int?
) : Serializable


data class User(
    val attributes: Attributes? = null,
    val id: String,
    val type: String,
    val username: String,
    val fullname:String,
    val bio: String
) : Serializable


data class Links(
    val self: String
)

