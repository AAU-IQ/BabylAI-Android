/*
 * TokenClient.kt
 * BabylAI Android Example App - Token fetching implementation
 *
 * Created by Ahmed Raad on 08/08/2025.
 */

package iq.aau.babylai.android.BabylAI

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Request model for token API
 */
@Serializable
data class TokenRequest(
    val apiKey: String,
    val tenantId: String
)

/**
 * Response model for token API
 */
@Serializable
data class TokenResponse(
    val token: String
)

/**
 * HTTP client for fetching authentication tokens
 */
object TokenClient {
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Fetch authentication token from the API
     */
    suspend fun fetchToken(apiKey: String, tenantId: String, baseUrl: String): String = withContext(Dispatchers.IO) {
        try {
            println("🔑 Starting token fetch...")
            
            val requestBody = TokenRequest(
                apiKey = apiKey,
                tenantId = tenantId
            )
            
            val jsonBody = json.encodeToString(TokenRequest.serializer(), requestBody)
            
            val request = Request.Builder()
                .url("${baseUrl}Auth/client/get-token")
                .post(
                    jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
                )
                .addHeader("Content-Type", "application/json")
                .build()
            
            val response = httpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                println("❌ HTTP error: ${response.code}")
                return@withContext ""
            }
            
            val responseBody = response.body?.string() ?: ""
            val tokenResponse = json.decodeFromString<TokenResponse>(responseBody)
            
            println("✅ Token fetched successfully")
            tokenResponse.token
            
        } catch (e: Exception) {
            println("❌ Error during token fetch: ${e.message}")
            e.printStackTrace()
            ""
        }
    }
}
