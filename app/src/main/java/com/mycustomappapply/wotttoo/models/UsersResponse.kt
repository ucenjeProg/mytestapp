package com.mycustomappapply.wotttoo.models

import java.io.Serializable

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
    val id: Int,
    val block: Int? = null,
    val email: String? = null,
    val group_count: Int? = null,
    val group_names: String? = null,
    val lastResetTime: Any? = null,
    val lastvisitDate: String? = null,
    val name: String? = null,
    val registerDate: String? = null,
    val resetCount: Int? = null,
    val sendEmail: Int? = null,
    val username: String? = null,
    val fullname: String? = null,
    val bio: String? = null,
    var followers: List<String>? = null,
    val followingUsers: List<String>? = null,
    val followingGenres: List<String>? = null,
    val quotes: List<Quote>? = null,
    val profileImage: String? = null,
    val totalQuoteCount: Int? = null
) : Serializable


data class User(
    val id: String? = null,
    val attributes: Attributes? = null,
    val type: String? = null,
    val username: String? = null,
    val fullname: String? = null,
    val bio: String? = null
) : Serializable


data class Links(
    val self: String
)

