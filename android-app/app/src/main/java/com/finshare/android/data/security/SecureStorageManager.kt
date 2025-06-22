package com.finshare.android.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureStorageManager @Inject constructor(
    private val context: Context
) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val encryptedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            "finshare_secure_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun storeAuthToken(token: String) {
        encryptedPrefs.edit()
            .putString(KEY_AUTH_TOKEN, token)
            .apply()
    }

    fun getAuthToken(): String? {
        return encryptedPrefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun clearAuthToken() {
        encryptedPrefs.edit()
            .remove(KEY_AUTH_TOKEN)
            .apply()
    }

    fun storeBiometricEnabled(enabled: Boolean) {
        encryptedPrefs.edit()
            .putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
            .apply()
    }

    fun isBiometricEnabled(): Boolean {
        return encryptedPrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    fun storeUserPreferences(key: String, value: String) {
        encryptedPrefs.edit()
            .putString(key, value)
            .apply()
    }

    fun getUserPreference(key: String, defaultValue: String = ""): String {
        return encryptedPrefs.getString(key, defaultValue) ?: defaultValue
    }

    fun storeLastSyncTimestamp(timestamp: Long) {
        encryptedPrefs.edit()
            .putLong(KEY_LAST_SYNC, timestamp)
            .apply()
    }

    fun getLastSyncTimestamp(): Long {
        return encryptedPrefs.getLong(KEY_LAST_SYNC, 0L)
    }

    fun clearAllData() {
        encryptedPrefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_LAST_SYNC = "last_sync_timestamp"
    }
}