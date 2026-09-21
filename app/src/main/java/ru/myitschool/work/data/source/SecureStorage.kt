package ru.myitschool.work.data.source

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import ru.myitschool.work.App

object SecureStorage {
    private const val FILE = "secure_store"
    private const val ACCESS = "access"
    private const val REFRESH = "refresh"
    private const val USERNAME = "username"

    private val prefs: SharedPreferences by lazy {
        val context = App.context
        val key = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            FILE,
            key,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    var access: String?
        get() = prefs.getString(ACCESS, null)
        set(value) = prefs.edit { putString(ACCESS, value) }

    var refresh: String?
        get() = prefs.getString(REFRESH, null)
        set(value) = prefs.edit { putString(REFRESH, value) }

    var username: String?
        get() = prefs.getString(USERNAME, null)
        set(value) = prefs.edit { putString(USERNAME, value) }

    fun clear() = prefs.edit { clear() }

    fun context(): Context = App.context
}
