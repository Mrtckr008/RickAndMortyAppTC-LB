package com.interview.data.utils

import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

suspend fun <T> retryApiCall(
    maxRetries: Int = 3,
    initialDelayMillis: Long = 500L,
    maxDelayMillis: Long = 8_000L,
    jitterMillis: Long = 250L,
    block: suspend () -> T
): T {
    require(maxRetries >= 0) { "maxRetries must be >= 0" }
    require(initialDelayMillis >= 0) { "initialDelayMillis must be >= 0" }
    require(maxDelayMillis >= 0) { "maxDelayMillis must be >= 0" }
    require(jitterMillis >= 0) { "jitterMillis must be >= 0" }

    var currentDelay = initialDelayMillis
    var lastThrowable: Throwable? = null

    repeat(maxRetries + 1) { attempt ->
        try {
            return block()
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) throw throwable

            lastThrowable = throwable

            val retryDelay = when (throwable) {
                is IOException -> currentDelay

                is HttpException -> {
                    val code = throwable.code()
                    when {
                        code == 429 -> {
                            val retryAfterSeconds = throwable.response()
                                ?.headers()
                                ?.get("Retry-After")
                                ?.toLongOrNull()

                            retryAfterSeconds?.times(1000) ?: currentDelay
                        }

                        code in 500..599 -> currentDelay
                        else -> null
                    }
                }

                else -> null
            }

            val isLastAttempt = attempt == maxRetries
            if (retryDelay == null || isLastAttempt) {
                throw throwable
            }

            val boundedDelay = retryDelay.coerceAtMost(maxDelayMillis)
            val jitter = if (jitterMillis > 0) Random.nextLong(0, jitterMillis + 1) else 0L

            delay(boundedDelay + jitter)
            currentDelay = (currentDelay * 2).coerceAtMost(maxDelayMillis)
        }
    }

    throw lastThrowable ?: IllegalStateException("Retry failed without a captured exception")
}
