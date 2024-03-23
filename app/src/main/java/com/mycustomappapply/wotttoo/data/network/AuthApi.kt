package com.mycustomappapply.wotttoo.data.network

import com.mycustomappapply.wotttoo.models.ArticleResponse
import com.mycustomappapply.wotttoo.models.CurrentUSerResponse
import com.mycustomappapply.wotttoo.models.User
import com.mycustomappapply.wotttoo.models.UserAuth
import com.mycustomappapply.wotttoo.models.UserRequest
import com.mycustomappapply.wotttoo.models.UsersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface AuthApi {

    // Users
    @GET("/joomlaTest/joomla/api/index.php/v1/users")
    suspend fun getUsers(
        @Header("Accept") acceptHeader: String = "application/vnd.api+json"
    ): Response<UsersResponse>


    @POST("/auth/google-signup")
    suspend fun authorizeWithGoogle(
        @Body signupBody: Map<String, String>
    ): Response<UserAuth>

    // Users
    @GET("/users/")
    suspend fun getUsersORg(
        @Query("search") searchQuery: String,
        @Query("page") currentPage: Int
    ): Response<UsersResponse>

    @GET("/joomlaTest/joomla/api/index.php/v1/users/{userId}")
    suspend fun getUser(
        @Path("userId") userId: String,
        @Header("Accept") acceptHeader: String = "application/vnd.api+json",
    ): Response<CurrentUSerResponse>

    @GET("/users/{userId}")
    suspend fun getMoreUserQuotes(
        @Path("userId") userId: String,
        @Query("page") page: Int
    ): Response<ArticleResponse>

    @POST("/users/{userId}/followers")
    suspend fun followOrUnFollowUser(
        @Path("userId") userId: String
    ): Response<UsersResponse>

    @POST("/joomlaTest/joomla/api/index.php/v1/users")
    suspend fun createUser(
        @Body body: UserRequest,
        @Header("Accept") acceptHeader: String = "application/vnd.api+json",
    ): Response<CurrentUSerResponse>

    @PATCH("/users/")
    suspend fun saveFollowingGenres(
        @Query("genres") genres: String,
    ): Response<UserAuth>

}