package com.mycustomappapply.wotttoo.data.local

import android.content.Context
import android.content.SharedPreferences
import com.mycustomappapply.wotttoo.models.UserAuth
import com.mycustomappapply.wotttoo.utils.toJoinedString
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Named

class SharedPreferencesRepository @Inject constructor(
    @ApplicationContext context: Context,
    @Named("encryptedSharedPreferences") val sharedPreferences: SharedPreferences
) {
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    private var _currentUser: UserAuth? = null


    fun getCurrentUser(): UserAuth {
        val username: String? = sharedPreferences.getString("username", "")
        val email: String? = sharedPreferences.getString("email", "")
        val profileImage: String? = sharedPreferences.getString("profileImage", "")
        val userId: String? = sharedPreferences.getString("userId", null)
        val token: String? = sharedPreferences.getString("token", null)
        val followingGenres: String = sharedPreferences.getString("genres", "") ?: ""
        _currentUser = UserAuth(
            username = username,
            email = email,
            profileImage = profileImage,
            userId = userId,
            token = token,
            followingGenres = followingGenres.split(",").toList()
        )
        return _currentUser!!
    }

    fun getToken(): String? {
        return sharedPreferences.getString(
            "token",
            "c2hhMjU2OjI5NjoyYTIzZWM4YzQ2Y2I2NWU1OTY1NDk4NjU4YjA5YzRhMWFmODdkZDFmMjJiZTZkOThkOTRiZmEwMjBlZDAxMDBk"
        )
    }

    fun saveUser(
        user: UserAuth?
    ) {
        if (user == null) return
        editor.putString("userId", user.userId)
        editor.putString("token", "c2hhMjU2OjI5NjoyYTIzZWM4YzQ2Y2I2NWU1OTY1NDk4NjU4YjA5YzRhMWFmODdkZDFmMjJiZTZkOThkOTRiZmEwMjBlZDAxMDBk")
        editor.putString("username", user.username)
        editor.putString("email", user.email)
        editor.putString("profileImage", user.profileImage)
        if (user.followingGenres!!.isNotEmpty()) {
            editor.putString("genres", user.followingGenres.toJoinedString())
        }
        editor.apply()
    }

    fun updateUser(
        user: UserAuth
    ) {
        editor.putString("username", user.username)
    }

    fun getFollowingGenres(): String {
        return sharedPreferences.getString("genres", "")!!
    }

    fun clearUser() {
        editor.remove("userId")
        editor.remove("token")
        editor.remove("genres")
        editor.remove("username")
        editor.remove("email")
        editor.remove("profileImage")
        editor.apply()
    }

    fun saveFollowingGenres(
        genres: String
    ) {
        editor.putString("genres", genres)
        editor.apply()
    }


}