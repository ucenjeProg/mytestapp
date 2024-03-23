package com.mycustomappapply.wotttoo.models

import com.google.gson.annotations.SerializedName


data class ArticleResponse(
    val data: MutableList<Quote>,
    val meta: MetaData? = null
)

data class PostArticleResponse(
    val data: Quote,
    val meta: MetaData? = null
)

data class ArticleAttributes(
    val id: Int,
    val asset_id: Int,
    val title: String,
    val alias: String,
    val state: Int,
    val access: Int,
    val created: String,
    val created_by: Int,
    val created_by_alias: String,
    val modified: String,
    val featured: Int,
    val language: String,
    val hits: Int,
    val publish_up: String,
    val publish_down: String?,
    val note: String,
    // val images: List<String>, // or a more complex structure if needed
    val metakey: String,
    val metadesc: String,
    // val metadata: Map<String, String>,
    val version: Int,
    val featured_up: String?,
    val featured_down: String?,
    val typeAlias: String,
    val text: String,
    val about_the_author: String,
    var liked: String,
    var likes: List<String>,
    val likes_count: String,
    @SerializedName("users-that-like-this-post") var usersThatLikeThisPost: List<String>,
    // val tags: List<String> // or Map<String, String> if your tags are key-value pairs
)

data class ArticleRelationships(
    val category: ArticleCategoryRelationship,
    val created_by: ArticleCreatedByRelationship
)

data class ArticleCategoryRelationship(
    val data: ArticleRelationshipData
)

data class ArticleCreatedByRelationship(
    val data: ArticleRelationshipData
)

data class ArticleRelationshipData(
    val type: String,
    val id: String
)

data class MetaData(
    val total_pages: Int
)
