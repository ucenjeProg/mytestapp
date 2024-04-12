package com.mycustomappapply.wotttoo.models

class UserMessage {
    var nickname: String? = null
    var profileUrl: String? = null
    var id: String? = null
    var message: String
    var createdAt: String? = null

    constructor(
        nickname: String?,
        profileUrl: String?,
        id: String?,
        message: String,
        createdAt: String?
    ) {
        this.nickname = nickname
        this.profileUrl = profileUrl
        this.id = id
        this.message = message
        this.createdAt = createdAt
    }

    constructor(
        message: String
    ) {
        this.message = message
    }
}
