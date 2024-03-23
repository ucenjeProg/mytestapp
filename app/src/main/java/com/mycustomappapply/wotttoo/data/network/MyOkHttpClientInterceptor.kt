package com.mycustomappapply.wotttoo.data.network

import com.mycustomappapply.wotttoo.data.local.SharedPreferencesRepository
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import javax.inject.Inject

class MyOkHttpClientInterceptor @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : Interceptor {

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer"
        const val TOKEN = "c2hhMjU2OjI5NjoyMmZhOWQ5OWFmNGVlNjUyZTRhOTQxMzE3NDc2NjQ0MWE3NjI4ZTg4N2Y4ODM3MjI2ZjZmYjA1MzM1NTRiNjBh"
    }

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val mediaType: MediaType? = "text/plain".toMediaTypeOrNull()
        val body: RequestBody = RequestBody.create(mediaType, "")

        val request: Request = chain.request()
        val token: String? = sharedPreferencesRepository.getToken()

        val updatedRequest: Request = request.newBuilder()
            .addHeader(HEADER_AUTHORIZATION, "$BEARER $TOKEN")
            .build()

        return chain.proceed(updatedRequest)
    }
}