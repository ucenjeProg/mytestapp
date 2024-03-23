package com.mycustomappapply.wotttoo.data.repositories

import com.mycustomappapply.wotttoo.data.network.AuthApi
import com.mycustomappapply.wotttoo.models.ArticleResponse
import com.mycustomappapply.wotttoo.models.CurrentUSerResponse
import com.mycustomappapply.wotttoo.models.UserAuth
import com.mycustomappapply.wotttoo.models.UsersResponse
import com.mycustomappapply.wotttoo.utils.Constants.KEY_EMAIL
import com.mycustomappapply.wotttoo.utils.Constants.KEY_FULL_NAME
import com.mycustomappapply.wotttoo.utils.Constants.KEY_PROFILE_IMAGE
import com.mycustomappapply.wotttoo.utils.Constants.KEY_USERNAME
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    val authApi: AuthApi
) {

    suspend fun authorizeWithGoogle(
        email: String,
        fullname: String,
        username: String,
        profileImage: String,
    ): Response<UserAuth> {
        val map: Map<String, String> = mapOf(
            KEY_EMAIL to email,
            KEY_FULL_NAME to fullname,
            KEY_USERNAME to username,
            KEY_PROFILE_IMAGE to profileImage
        )
        return authApi.authorizeWithGoogle(map)
    }

    suspend fun followOrUnFollowUser(
        userId: String
    ): Response<UsersResponse> = authApi.followOrUnFollowUser(userId)

    suspend fun saveFollowingGenres(
        genres: String
    ): Response<UserAuth> = authApi.saveFollowingGenres(genres)

    suspend fun updateUser(
        body: Map<String, String>
    ): Response<CurrentUSerResponse> = authApi.updateUser(body)

    suspend fun getUser(
        userId: String
    ): Response<CurrentUSerResponse> = authApi.getUser(userId)

    suspend fun getMoreUserQuotes(
        userId: String,
        page: Int
    ): Response<ArticleResponse> = authApi.getMoreUserQuotes(userId, page)

    suspend fun getUsers(): Response<UsersResponse> = authApi.getUsers()

}