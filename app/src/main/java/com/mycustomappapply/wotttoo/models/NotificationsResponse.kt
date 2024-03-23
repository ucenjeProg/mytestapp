package com.mycustomappapply.wotttoo.models

data class NotificationsResponse(
    val message: String? = null,
    val notifications: List<Notification>?
)
