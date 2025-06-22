package com.finshare.android.di

import com.finshare.android.data.network.ApiConstants
import com.finshare.android.data.network.FinShareApiService
import com.finshare.android.data.repository.FinShareRepositoryImpl
import com.finshare.android.domain.repository.FinShareRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header(ApiConstants.Headers.CONTENT_TYPE, ApiConstants.Headers.APPLICATION_JSON)
                    .header(ApiConstants.Headers.USER_ID, "android-user-123")
                
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFinShareApiService(retrofit: Retrofit): FinShareApiService {
        return retrofit.create(FinShareApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFinShareRepository(
        apiService: FinShareApiService
    ): FinShareRepository {
        return FinShareRepositoryImpl(apiService)
    }
}