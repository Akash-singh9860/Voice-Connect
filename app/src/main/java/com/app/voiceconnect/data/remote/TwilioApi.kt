package com.app.voiceconnect.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for fetching Twilio access tokens from the backend.
 * These tokens are required to authenticate the Twilio Voice SDK.
 */
interface TwilioApi {
    /**
     * Retrieves an access token for the specified user identity.
     *
     * @param identity The unique identifier for the user (e.g., username or email).
     * @return A [TokenResponse] containing the JWT access token.
     */
    @GET("access_token.php")
    suspend fun getAccessToken(@Query("user") identity: String): TokenResponse
}
