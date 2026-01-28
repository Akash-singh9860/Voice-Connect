package com.app.voiceconnect.di

import com.app.voiceconnect.data.remote.TwilioApi
import com.app.voiceconnect.data.repository.VoiceRepositoryImpl
import com.app.voiceconnect.domain.repository.VoiceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Hilt module that provides singleton-scoped dependencies for the application.
 * This includes network clients, API services, and repository implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides a [Retrofit] instance configured with a base URL and Gson converter.
     * The base URL points to the backend server responsible for Twilio token generation.
     */
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://lizbeth-uncoroneted-articulately.ngrok-free.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides an implementation of the [TwilioApi] interface using the provided [Retrofit] instance.
     */
    @Provides
    @Singleton
    fun provideTwilioApi(retrofit: Retrofit): TwilioApi {
        return retrofit.create(TwilioApi::class.java)
    }

    /**
     * Provides the [VoiceRepository] implementation.
     * Maps the [VoiceRepositoryImpl] to its interface [VoiceRepository].
     */
    @Provides
    @Singleton
    fun provideVoiceRepository(repo: VoiceRepositoryImpl): VoiceRepository {
        return repo
    }
}
