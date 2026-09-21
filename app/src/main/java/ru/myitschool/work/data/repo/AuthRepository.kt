package ru.myitschool.work.data.repo

import ru.myitschool.work.data.source.ApiException
import ru.myitschool.work.data.source.CacheDataSource
import ru.myitschool.work.data.source.NetworkDataSource
import ru.myitschool.work.data.source.SecureStorage

object AuthRepository {
    const val MAX_ATTEMPTS = 5
    const val LOCK_MILLIS = 60_000L

    suspend fun login(username: String, password: String): Result<Unit> {
        val lockLeft = lockLeftMillis()
        if (lockLeft > 0) return Result.failure(LockedException(lockLeft))
        return runCatching {
            NetworkDataSource.login(username, password)
        }.map { tokens ->
            SecureStorage.access = tokens.access
            SecureStorage.refresh = tokens.refresh
            SecureStorage.username = username
            CacheDataSource.saveAttempts(0)
            CacheDataSource.saveLockUntil(0)
        }.recoverCatching { error ->
            val mapped = if (error is ApiException.Server && error.message?.contains("credentials", true) == true) {
                ApiException.Unauthorized()
            } else error
            if (mapped is ApiException.Unauthorized) {
                val attempts = CacheDataSource.readAttempts() + 1
                CacheDataSource.saveAttempts(attempts)
                if (attempts >= MAX_ATTEMPTS) {
                    val until = System.currentTimeMillis() + LOCK_MILLIS
                    CacheDataSource.saveLockUntil(until)
                    CacheDataSource.saveAttempts(0)
                    throw LockedException(LOCK_MILLIS)
                }
            }
            throw mapped
        }
    }

    suspend fun lockLeftMillis(): Long {
        val until = CacheDataSource.readLockUntil()
        return (until - System.currentTimeMillis()).coerceAtLeast(0)
    }

    fun isAuthorized(): Boolean = SecureStorage.access != null

    fun accessToken(): String? = SecureStorage.access

    fun username(): String? = SecureStorage.username

    suspend fun refreshTokens(): Boolean {
        val refresh = SecureStorage.refresh ?: return false
        return runCatching { NetworkDataSource.refresh(refresh) }
            .onSuccess {
                SecureStorage.access = it.access
                SecureStorage.refresh = it.refresh
            }
            .isSuccess
    }

    suspend fun logout() {
        SecureStorage.clear()
        CacheDataSource.clear()
    }

    class LockedException(val leftMillis: Long) : Exception("Много запросов")
}
