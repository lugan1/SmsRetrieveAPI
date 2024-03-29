package com.example.smsretriverstudy.data

import com.example.smsretriverstudy.BuildConfig
import com.example.smsretriverstudy.data.source.AuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout((1000 * 30), TimeUnit.MILLISECONDS)
            .readTimeout((1000 * 30), TimeUnit.MILLISECONDS)

        if(BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit
        = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.LOCAL_URL)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()



    @Singleton
    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService
        = retrofit.create(AuthService::class.java)
}