package com.mycustomappapply.wotttoo.data.repositories

import com.mycustomappapply.wotttoo.data.local.SharedPreferencesRepository
import com.mycustomappapply.wotttoo.data.network.MainApi
import com.mycustomappapply.wotttoo.models.ArticleResponse
import com.mycustomappapply.wotttoo.models.BasicResponse
import com.mycustomappapply.wotttoo.models.NotificationsResponse
import com.mycustomappapply.wotttoo.models.PostArticleResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    val shPref: SharedPreferencesRepository,
    val mainApi: MainApi
) {

    suspend fun getQuotes(
        page: Int
    ): Response<ArticleResponse> {
        val genres: String = shPref.getFollowingGenres()
        return mainApi.getQuotes()
    }

    suspend fun getSingleQuote(
        quoteId: String
    ): Response<ArticleResponse> = mainApi.getSingleQuote(quoteId)

    suspend fun reportQuote(
        reportBody: Map<String, String>
    ): Response<BasicResponse> = mainApi.reportQuote(reportBody)

    suspend fun reportUser(
        reportBody: Map<String, String>
    ): Response<BasicResponse> = mainApi.reportUser(reportBody)

    suspend fun reportBook(
        reportBody: Map<String, String>
    ): Response<BasicResponse> = mainApi.reportBook(reportBody)

    suspend fun getQuotesByGenre(
        genre: String,
        page: Int
    ): Response<ArticleResponse> = mainApi.getQuotesByGenre(genre, page)

    suspend fun postQuote(
        quote: Map<String, String>
    ): Response<PostArticleResponse> = mainApi.postQuote(body = quote)

    suspend fun likeOrDislikeQuote(
        quoteId: String
    ): Response<ArticleResponse> = mainApi.likeOrDislikeQuote(quoteId)

    suspend fun updateQuote(
        quoteId: String,
        quote: Map<String, String>
    ): Response<ArticleResponse> {
        return mainApi.updateQuote(quoteId, quote)
    }

    suspend fun deleteQuote(
        quoteId: String
    ): Response<ArticleResponse> = mainApi.deleteQuote(quoteId)

    suspend fun getNotifications(
        currentPage: Int = 1
    ): Response<NotificationsResponse> = mainApi.getNotifications(currentPage)

    suspend fun clearNotifications(): Response<NotificationsResponse> = mainApi.clearNotifications()

}