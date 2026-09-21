package ru.myitschool.work.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import ru.myitschool.work.App
import ru.myitschool.work.data.dto.PlaceDto
import ru.myitschool.work.data.dto.UserDto

object CacheDataSource {
    private val Context.store: DataStore<Preferences> by preferencesDataStore(name = "cache")
    private val INFO = stringPreferencesKey("info")
    private val BOOKING = stringPreferencesKey("booking")
    private val ATTEMPTS = intPreferencesKey("login_attempts")
    private val LOCK_UNTIL = longPreferencesKey("login_lock_until")

    private val store get() = App.context.store
    private val json get() = NetworkDataSource.json

    suspend fun saveInfo(dto: UserDto) {
        store.edit { it[INFO] = json.encodeToString(dto) }
    }

    suspend fun readInfo(): UserDto? = store.data.first()[INFO]
        ?.let { runCatching { json.decodeFromString<UserDto>(it) }.getOrNull() }

    suspend fun saveBooking(dto: Map<String, List<PlaceDto>>) {
        store.edit { it[BOOKING] = json.encodeToString(dto) }
    }

    suspend fun readBooking(): Map<String, List<PlaceDto>>? = store.data.first()[BOOKING]
        ?.let { runCatching { json.decodeFromString<Map<String, List<PlaceDto>>>(it) }.getOrNull() }

    suspend fun readAttempts(): Int = store.data.first()[ATTEMPTS] ?: 0

    suspend fun saveAttempts(value: Int) {
        store.edit { it[ATTEMPTS] = value }
    }

    suspend fun readLockUntil(): Long = store.data.first()[LOCK_UNTIL] ?: 0L

    suspend fun saveLockUntil(value: Long) {
        store.edit { it[LOCK_UNTIL] = value }
    }

    suspend fun clear() {
        store.edit { it.clear() }
    }
}
