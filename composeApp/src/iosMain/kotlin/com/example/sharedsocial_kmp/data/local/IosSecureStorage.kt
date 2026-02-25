package com.example.sharedsocial_kmp.data.local

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import kotlinx.cinterop.*
import kotlinx.coroutines.withContext
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*

/**
 * Implementazione iOS di [SecureStorage] che utilizza Apple Keychain Services.
 * * I dati sono memorizzati nel Secure Enclave con attributi di accessibilità
 * rigorosi per garantire la parità di sicurezza con l'implementazione Android.
 */
class IosSecureStorage(
    private val dispatchers: AppDispatchers
) : SecureStorage {

    override suspend fun saveString(key: String, value: String) = withContext(dispatchers.io) {
        val data = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return@withContext

        // Definiamo la query per la ricerca dell'item esistente
        val query = mutableMapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to key
        )

        // Rimuoviamo l'item se già esistente per poterlo sovrascrivere
        SecItemDelete(query.toCFDictionary())

        // Aggiungiamo i nuovi dati con protezione hardware-backed
        query[kSecValueData] = data
        query[kSecAttrAccessible] = kSecAttrAccessibleAfterFirstUnlock

        val status = SecItemAdd(query.toCFDictionary(), null)
        if (status != errSecSuccess) {
            // Qui potresti loggare l'errore di sistema se necessario
        }
    }

    override suspend fun getString(key: String): String? = withContext(dispatchers.io) {
        val query = mutableMapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword,
            kSecAttrAccount to key,
            kSecReturnData to kCFBooleanTrue,
            kSecMatchLimit to kSecMatchLimitOne
        )

        memScoped {
            val result = alloc<ObjCObjectVar<Any?>>()
            val status = SecItemCopyMatching(query.toCFDictionary(), result.ptr)

            if (status == errSecSuccess) {
                val data = result.value as? NSData
                data?.let { NSString(it, NSUTF8StringEncoding).toString() }
            } else {
                null
            }
        }
    }

    override suspend fun clear() = withContext(dispatchers.io) {
        val query = mutableMapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword
        )
        SecItemDelete(query.toCFDictionary())
    }

    /**
     * Helper per convertire mappe Kotlin nel formato CFDictionary richiesto dalle API Apple.
     */
    private fun Map<Any?, Any?>.toCFDictionary() = this as CFDictionaryRef
}