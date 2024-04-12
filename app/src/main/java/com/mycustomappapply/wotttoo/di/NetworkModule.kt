package com.mycustomappapply.wotttoo.di

import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.mycustomappapply.wotttoo.data.network.AuthApi
import com.mycustomappapply.wotttoo.data.network.MainApi
import com.mycustomappapply.wotttoo.data.network.MyOkHttpClientInterceptor
import com.mycustomappapply.wotttoo.utils.Constants.API_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    fun provideOkHttpClient(
        myOkHttpClientInterceptor: MyOkHttpClientInterceptor,
    ): OkHttpClient {

        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val networkFlipperPlugin: NetworkFlipperPlugin by lazy {
            NetworkFlipperPlugin()
        }

        return OkHttpClient.Builder()
            .addInterceptor(myOkHttpClientInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(FlipperOkhttpInterceptor(networkFlipperPlugin))
            .build()
    }

    @Provides
    @Singleton
    fun provideMainRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(gsonConverterFactory)
        .baseUrl(API_URL)
        .client(client)
        .build()

    @Provides
    @Singleton
    fun provideMainApi(
        retrofit: Retrofit
    ): MainApi = retrofit.create(MainApi::class.java)


    @Provides
    @Singleton
    fun provideAuthApi(
        retrofit: Retrofit
    ): AuthApi = retrofit.create(AuthApi::class.java)

}