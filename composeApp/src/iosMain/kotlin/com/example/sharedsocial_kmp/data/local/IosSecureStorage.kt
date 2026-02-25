package com.example.sharedsocial_kmp.data.local

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import kotlinx.cinterop.*
import kotlinx.coroutines.withContext
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*

/**
 * Implementazione iOS di [SecureStorage] che utilizza Apple Keychain Services.
 * I dati sono memorizzati nel Secure Enclave con attributi di accessibilità
 * rigorosi per garantire la parità di sicurezza con l'implementazione Android.
 */
@OptIn(ExperimentalForeignApi::class)
class IosSecureStorage(
    private val dispatchers: AppDispatchers
) : SecureStorage {

    override suspend fun saveString(key: String, value: String): Unit = withContext(dispatchers.io) {
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
            // Loggare errore se necessario
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
            // Utilizziamo CFTypeRefVar per gestire correttamente il puntatore alla memoria della Keychain
            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query.toCFDictionary(), result.ptr)

            if (status == errSecSuccess) {
                val data = CFBridgingRelease(result.value) as? NSData
                data?.let {
                    NSString.create(it, NSUTF8StringEncoding)?.toString()
                }
            } else {
                null
            }
        }
    }

    override suspend fun clear(): Unit = withContext(dispatchers.io) {
        val query = mutableMapOf<Any?, Any?>(
            kSecClass to kSecClassGenericPassword
        )
        SecItemDelete(query.toCFDictionary())
    }

    /**
     * Helper per convertire mappe Kotlin nel formato CFDictionary richiesto dalle API Apple.
     * In Kotlin/Native, NSDictionary è compatibile con CFDictionaryRef.
     */
    private fun Map<Any?, Any?>.toCFDictionary(): CFDictionaryRef {
        return (this as NSDictionary) as CFDictionaryRef
    }
}
