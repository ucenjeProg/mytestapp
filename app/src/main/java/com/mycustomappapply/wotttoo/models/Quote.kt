package com.mycustomappapply.wotttoo.models

import java.io.Serializable

data class Quote(
    val id: String,
    val type: String,
    val attributes: ArticleAttributes,
    val relationships: ArticleRelationships
) : Serializable
