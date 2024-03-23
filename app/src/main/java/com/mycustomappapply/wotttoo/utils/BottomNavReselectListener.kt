package com.mycustomappapply.wotttoo.utils

interface BottomNavReselectListener {
    fun itemReselected(screen: Screen?)
}

enum class Screen { Home, Search, Notifications, MyProfile }