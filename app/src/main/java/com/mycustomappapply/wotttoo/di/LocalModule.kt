package com.mycustomappapply.wotttoo.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object LocalModule {

    @Provides
    @Named("themeSharedPreferences")
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences = context.getSharedPreferences("theme", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    @Named(("encryptedSharedPreferences"))
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {

        val masterKey: MasterKey =
            MasterKey.Builder(context).setKeyGenParameterSpec(MasterKeys.AES256_GCM_SPEC).build()

        return EncryptedSharedPreferences.create(
            /* context = */ context,
            /* fileName = */ "user",
            /* masterKey = */ masterKey,
            /* prefKeyEncryptionScheme = */ EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            /* prefValueEncryptionScheme = */ EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


}