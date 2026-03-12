package com.example.sharedsocial_kmp.platform

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.features.auth.data.local.SecureStorage
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementazione Android di [com.example.sharedsocial_kmp.features.auth.data.local.SecureStorage] basata su Google Tink e Jetpack DataStore.
 * * Utilizza lo schema AES256_GCM hardware-backed tramite Android Keystore per garantire
 * la massima protezione dei token di autenticazione e dei dati sensibili.
 */
class AndroidSecureStorage(
    private val context: Context,
    private val dataStore: DataStore<Preferences>,
    private val dispatchers: AppDispatchers
) : SecureStorage {

    companion object {
        private const val MASTER_KEY_URI = "android-keystore://social_app_master_key"
        private const val KEYSET_NAME = "tink_keyset"
        private const val PREF_FILE_NAME = "tink_pref_file"
    }

    private val aead: Aead by lazy {
        AeadConfig.register()
        AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
            .getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    }

    override suspend fun saveString(key: String, value: String) {
        withContext(dispatchers.io) {
            val prefKey = stringPreferencesKey(key)


            val encryptedValue = aead.encrypt(value.encodeToByteArray(), null)
            val base64Value = Base64.encodeToString(encryptedValue, Base64.DEFAULT)

            dataStore.edit { it[prefKey] = base64Value }
        }
    }

    override suspend fun getString(key: String): String? = withContext(dispatchers.io) {
        val prefKey = stringPreferencesKey(key)
        val base64Value =
            dataStore.data.map { it[prefKey] }.firstOrNull() ?: return@withContext null

        runCatching {
            val encryptedValue = Base64.decode(base64Value, Base64.DEFAULT)
            val decrypted = aead.decrypt(encryptedValue, null)
            String(decrypted)
        }.getOrNull()
    }

    override suspend fun clear() {
        withContext(dispatchers.io) {
            dataStore.edit { it.clear() }
        }
    }
}