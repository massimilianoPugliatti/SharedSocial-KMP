package com.example.sharedsocial_kmp.core.network

import com.example.sharedsocial_kmp.features.auth.data.local.AuthPersistence
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/**
 * Modulo Koin per la configurazione del client HTTP (Ktor).
 * Include la gestione globale della serializzazione, dei timeout e del
 * meccanismo di autenticazione Bearer tramite [Auth].
 */
val networkModule = module {
    single {
        val persistence: AuthPersistence = get()

        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.DEFAULT
            }

            /**
             * Configurazione dell'autenticazione Bearer.
             * Recupera il token dallo storage sicuro per ogni richiesta che lo richiede.
             */
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = persistence.getToken()
                        if (token != null) {
                            BearerTokens(accessToken = token, refreshToken = "")
                        } else null
                    }

                    /**
                     * Specifica quali richieste non devono includere l'header di autenticazione.
                     */
                    sendWithoutRequest { request ->
                        !request.url.pathSegments.contains("login") &&
                                !request.url.pathSegments.contains("createUtente")
                    }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 15000
            }

            defaultRequest {
                url("https://socialmaster.ddns.net/")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header("key", "MasterSviluppoFeb25")
            }
        }
    }
}