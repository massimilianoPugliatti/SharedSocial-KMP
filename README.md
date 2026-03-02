# Kotlin Multiplatform Social App
![Mobile CI](https://github.com/massimilianoPugliatti/SharedSocial-KMP/actions/workflows/mobile-ci.yml/badge.svg)

Un'applicazione Social moderna costruita con **Kotlin Multiplatform (KMP)** e **Compose Multiplatform**. Il progetto segue rigorosi standard di **Clean Architecture** e gestione dello stato reattiva per garantire parità di feature e performance native tra Android e iOS.

## 🎯 Project Purpose
Questo repository è uno **showcase tecnico** progettato per dimostrare l'applicazione di standard industriali in contesti cross-platform. È il risultato di una ricerca su architetture scalabili, sicurezza dei dati e monitoraggio professionale (Observability).

L'obiettivo è illustrare la padronanza di:
* **Architetture Scalabili:** Implementazione di Clean Architecture in contesti multi-modulo.
* **Full-Stack Awareness:** Integrazione con un ecosistema backend complesso.
* **Observability & Native Integration:** Gestione dei servizi nativi e monitoraggio professionale.
* **Modern CI/CD:** Automazione della qualità del codice e della distribuzione degli artefatti.

## 🏗 Architettura & Design Patterns
* **Clean Architecture:** Separazione netta tra Domain, Data e UI layer per massimizzare la testabilità.
* **MVI (Model-View-Intent):** Gestione dello stato tramite un flusso unidirezionale (**UDF**) per stati atomici e prevedibili.
* **Reactive Navigation:** Sistema basato su `NavigationAction` tramite `Channels` e `Flow`, disaccoppiando la logica di presentazione dal framework di navigazione.
* **Repository Decorator Pattern:** Utilizzo del pattern Decorator per iniettare logica cross-cutting (Analytics, Push Token management) senza contaminare i repository core. 
* **Deterministic Threading:** Astrazione dei dispatcher tramite `AppDispatchers` per il pieno controllo della concorrenza sia in produzione che nei test.

## 🍎 Shared Native Services (Android & iOS)
Il progetto gestisce le funzionalità specifiche di piattaforma tramite astrazioni di dominio, garantendo performance e sicurezza native:

* **Firebase Ecosystem:** Integrazione nativa per **Analytics**, **Crashlytics** e **Push Notifications**. La gestione dei log è specchiata su **Logcat** (Android) e **NSLog** (iOS) per facilitare il debugging in tempo reale.
* **Native Secure Storage:** 
  * **iOS:** Implementazione in **Swift** basata su **Keychain**, garantendo il salvataggio dei token di sessione nel compartimento sicuro di sistema.
  * **Android:** Utilizzo di **Jetpack DataStore** integrato con **Google Tink** per crittografia hardware-backed (AES256_GCM).

## 🌐 Backend & API Ecosystem
L'applicazione si interfaccia con un servizio **RESTful** sviluppato in **Spring Boot 3.x** e ospitato su infrastruttura **Oracle Cloud (OCI)**. L'architettura è stata progettata seguendo standard di sicurezza e separazione delle responsabilità.

* **Infrastruttura:** Utilizzo di reti virtuali isolate (**VCN**) per garantire che il database **MySQL** non sia esposto pubblicamente, comunicando esclusivamente con il layer applicativo.
* **Sicurezza:** Gestione dell'autenticazione stateless tramite **JWT (JSON Web Token)** e comunicazioni protette via **HTTPS**.
* **API Documentation:** Il contratto tra client e server è formalizzato tramite lo standard **OpenAPI**, permettendo una validazione rapida degli endpoint tramite Swagger:
  👉 [**SocialMaster API Documentation**](https://socialmaster.ddns.net/swagger-ui/index.html)

## 🚀 CI/CD Pipeline (GitHub Actions)
Il progetto implementa un workflow di **Continuous Integration**:
* **Automated Testing:** Esecuzione di Unit Tests e UI Tests (Robolectric) su Java 21 ad ogni PR.
* **Build Verification:** Pipeline resiliente che valida la compilazione KMP su ogni commit senza esporre file di configurazione sensibili.
* **Multi-Platform Build:** Validazione parallela della compilazione Android (APK) e del framework nativo iOS.
* **Continuous Delivery:** Generazione automatica di artefatti (APK e iOS App) disponibili per il download immediato dalla tab *Actions*.

## 🧪 Testing Strategy (Pyramid Approach)
La suite di test garantisce affidabilità con il minimo overhead di esecuzione:
* **Verification of Threading Policy:** Test che validano il corretto cambio di contesto sui dispatcher IO.
* **UI Integration:** Utilizzo di **Robolectric** per testare i componenti **Compose Multiplatform** sulla JVM, garantendo la corretta interazione tra UI e Business Logic senza emulatore.
* **Flow Testing:** Validazione delle transizioni di stato del ViewModel e degli eventi di navigazione tramite **Turbine**.
* **Behavioral Mocking:** Utilizzo di **Mokkery** per il mocking type-safe dei servizi di dominio.
* **Stability:** Iniezione di `TestAppDispatchers` per eliminare il non-determinismo nei test asincroni.

## 🛠 Tech Stack
* **Logic:** [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
* **UI:** [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
* **DI:** [Koin](https://insert-koin.io/) (moduli platform-specific `androidModule` e `iosModule`).
* **Networking:** [Ktor](https://ktor.io/) (Client Auth, Logging, Content Negotiation).
* **Services:** [Firebase](https://firebase.google.com/) (Analytics, Crashlytics, Cloud Messaging).
* **Persistence:** [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) + [Google Tink](https://github.com/tink-crypto/tink-kotlin) (AES256_GCM hardware-backed).
* **Secure Storage:** Cifratura dei token tramite Google Tink su Android e **Keychain** su iOS.
* **Testing:** [Mokkery](https://github.com/lwasyl/Mokkery), [Turbine](https://github.com/cashapp/turbine), [Robolectric](https://robolectric.org/).

## 🗺 Roadmap
- [x] Setup Architetturale, BaseTest e CI/CD.
- [x] Implementazione Auth Flow (Login) con monitoraggio e servizi nativi.
- [x] **Secure Persistence:** Integrazione Tink (Android) e Keychain (iOS).
- [ ] **Next Step:** Sviluppo del Feed Social e integrazione Coil Multiplatform.

---
*Sviluppato da **Massimiliano Pugliatti*** *Mobile Developer & Technical Instructor*