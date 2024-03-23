package com.mycustomappapply.wotttoo.di

import android.app.Application
import android.content.Context
import com.mycustomappapply.wotttoo.utils.Constants
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object UtilsModule {

    @Provides
    @Singleton
    fun provideUnsplash(
        @ApplicationContext context: Context
    ): UnsplashPhotoPicker {
        return UnsplashPhotoPicker.init(
            context.applicationContext as Application,
            Constants.UNSPLASH_ACCESS_KEY,
            Constants.UNSPLASH_SECRET_KEY
        )
    }

}