package com.example.sharedsocial_kmp.data.local

import com.example.sharedsocial_kmp.core.dispatchers.AppDispatchers
import com.example.sharedsocial_kmp.domain.model.User
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

/**
 * Implementazione concreta di [AuthPersistence].
 * * Questa classe orchestra l'accesso ai dati sensibili utilizzando un'astrazione di storage sicuro
 * e garantendo che tutte le operazioni di I/O e serializzazione avvengano sul dispatcher corretto.
 */
class AuthPersistenceImpl(
    private val secureStorage: SecureStorage,
    private val dispatchers: AppDispatchers
): AuthPersistence {

    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_DATA = "user_data"
    }

    /**
     * Persiste il token JWT utilizzando lo storage sicuro.
     * L'operazione viene eseguita sul dispatcher IO per evitare blocchi sul thread chiamante.
     */
    override suspend fun saveToken(token: String) = withContext(dispatchers.io) {
        secureStorage.saveString(KEY_JWT_TOKEN, token)
    }

    /**
     * Recupera il token di autenticazione persistito.
     * @return Il token JWT come [String] o null se non presente.
     */
    override suspend fun getToken(): String? = withContext(dispatchers.io) {
        secureStorage.getString(KEY_JWT_TOKEN)
    }

    /**
     * Serializza l'oggetto [User] in formato JSON prima di affidarlo allo storage sicuro.
     */
    override suspend fun saveUser(user: User) = withContext(dispatchers.io) {
        val userJson = Json.encodeToString(user)
        secureStorage.saveString(KEY_USER_DATA, userJson)
    }

    /**
     * Recupera e deserializza le informazioni utente.
     * Include un meccanismo di fallback (try-catch) per gestire eventuali corruzioni del formato JSON.
     */
    override suspend fun getUser(): User? = withContext(dispatchers.io) {
        val userJson = secureStorage.getString(KEY_USER_DATA) ?: return@withContext null

        runCatching {
            Json.decodeFromString<User>(userJson)
        }.getOrNull()
    }

    override suspend fun clear() = withContext(dispatchers.io) {
        secureStorage.clear()
    }

    /**
     * Determina se l'utente è autenticato verificando la presenza di un token di sessione.
     */
    override suspend fun isAuthenticated(): Boolean {
        return getToken() != null
    }
}