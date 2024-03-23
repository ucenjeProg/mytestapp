package com.mycustomappapply.wotttoo.data.network

import com.mycustomappapply.wotttoo.models.ArticleResponse
import com.mycustomappapply.wotttoo.models.BasicResponse
import com.mycustomappapply.wotttoo.models.NotificationsResponse
import com.mycustomappapply.wotttoo.models.PostArticleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MainApi {

    // Quotes
    @GET("/joomlaTest/joomla/api/index.php/v1/content/articles")
    suspend fun getQuotes(
        @Header("Accept") acceptHeader: String = "application/vnd.api+json"
    ): Response<ArticleResponse>


    @POST("/joomlaTest/joomla/api/index.php/v1/content/articles")
    suspend fun postQuote(
        @Header("Accept") acceptHeader: String = "application/vnd.api+json",
        @Body body: Map<String, String>
    ): Response<PostArticleResponse>


    @GET("/quotes/{quoteId}")
    suspend fun getSingleQuote(
        @Path("quoteId") quoteId: String
    ): Response<ArticleResponse>

    @GET("/quotes/by-genre")
    suspend fun getQuotesByGenre(
        @Query("genre") genre: String,
        @Query("page") page: Int
    ): Response<ArticleResponse>


    @PATCH("/quotes/{quoteId}")
    suspend fun likeOrDislikeQuote(
        @Path("quoteId") quoteId: String,
    ): Response<ArticleResponse>

    @PUT("/quotes/{quoteId}")
    suspend fun updateQuote(
        @Path("quoteId") quoteId: String,
        @Body quote: Map<String, String>
    ): Response<ArticleResponse>

    @DELETE("/quotes/{quoteId}")
    suspend fun deleteQuote(
        @Path("quoteId") quoteId: String
    ): Response<ArticleResponse>

    @POST("/reports/quote")
    suspend fun reportQuote(
        @Body reportBody: Map<String, String>
    ): Response<BasicResponse>

    @POST("/reports/user")
    suspend fun reportUser(
        @Body reportBody: Map<String, String>
    ): Response<BasicResponse>

    @POST("/reports/book")
    suspend fun reportBook(
        @Body reportBody: Map<String, String>
    ): Response<BasicResponse>

    @GET("/notifications/")
    suspend fun getNotifications(
        @Query("page") page: Int
    ): Response<NotificationsResponse>

    @DELETE("/notifications/")
    suspend fun clearNotifications(): Response<NotificationsResponse>
}


