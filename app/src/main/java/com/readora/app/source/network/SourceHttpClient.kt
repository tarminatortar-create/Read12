package com.readora.app.source.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

object SourceHttpClient {
    
    /** Default shared client (500ms rate limit, 3 retries). */
    val client: OkHttpClient = buildClient(minDelayMs = 500L)

    /**
     * Phase 54 — Build a per-source OkHttpClient with a custom minimum
     * delay between requests to the same domain. Sources with generous
     * APIs can lower this; scraper-style sources should raise it.
     *
     * @param minDelayMs  Minimum milliseconds between requests to the same host.
     * @param maxRetries  Number of retries on 429 / 503.
     */
    fun buildClientFor(minDelayMs: Long = 500L, maxRetries: Int = 3): OkHttpClient =
        buildClient(minDelayMs, maxRetries)

    private fun buildClient(minDelayMs: Long = 500L, maxRetries: Int = 3): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(RetryInterceptor(maxRetries = maxRetries))
            .addInterceptor(RateLimitInterceptor(minDelayMs = minDelayMs))
            .build()
}

/**
 * Retries requests on 429 (Too Many Requests) or 503 (Service Unavailable)
 */
class RetryInterceptor(private val maxRetries: Int) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var response: Response? = null
        var tryCount = 0
        var success = false

        while (!success && tryCount < maxRetries) {
            try {
                response = chain.proceed(request)
                if (response.code == 429 || response.code == 503) {
                    // Exponential backoff
                    Thread.sleep((1000 * Math.pow(2.0, tryCount.toDouble())).toLong())
                    response.close()
                    tryCount++
                } else {
                    success = true
                }
            } catch (e: Exception) {
                tryCount++
                if (tryCount >= maxRetries) {
                    throw IOException("Failed after $maxRetries retries", e)
                }
                Thread.sleep((1000 * Math.pow(2.0, tryCount.toDouble())).toLong())
            }
        }
        
        return response ?: throw IOException("Request failed")
    }
}

/**
 * Phase 54 — Rate limiting per domain.
 * @param minDelayMs  Configurable minimum delay (ms) between requests to the same host.
 */
class RateLimitInterceptor(private val minDelayMs: Long = 500L) : Interceptor {
    private val hostLastRequestMap = mutableMapOf<String, Long>()

    @Synchronized
    override fun intercept(chain: Interceptor.Chain): Response {
        val host = chain.request().url.host
        val now = System.currentTimeMillis()
        val lastRequest = hostLastRequestMap[host] ?: 0L

        if (now - lastRequest < minDelayMs) {
            Thread.sleep(minDelayMs - (now - lastRequest))
        }

        hostLastRequestMap[host] = System.currentTimeMillis()
        return chain.proceed(chain.request())
    }
}
