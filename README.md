# Kotlin Multiplatform Social App
![Mobile CI](https://github.com/massimilianoPugliatti/SharedSocial-KMP/actions/workflows/mobile-ci.yml/badge.svg)

Un'applicazione Social moderna costruita con **Kotlin Multiplatform (KMP)** e **Compose Multiplatform**. Il progetto segue rigorosi standard di **Clean Architecture** e gestione dello stato reattiva per garantire parità di feature e performance native tra Android e iOS.

## 🎯 Project Purpose
Questo repository è uno **showcase tecnico** progettato per dimostrare l'applicazione di standard industriali di alto livello. È il risultato di una ricerca su architetture scalabili, sicurezza dei dati e monitoraggio professionale (Observability).

L'obiettivo è illustrare la padronanza di:
* **Architetture Scalabili:** Implementazione di Clean Architecture in contesti multi-modulo.
* **Full-Stack Awareness:** Integrazione con un ecosistema backend complesso.
* **Observability & Native Integration:** Gestione dei servizi nativi e monitoraggio professionale.
* **Modern CI/CD:** Automazione della qualità del codice e della distribuzione degli artefatti.

## 🏗 Architettura & Design Patterns
* **Clean Architecture:** Separazione netta tra Domain, Data e UI layer per massimizzare la testabilità.
* **MVI (Model-View-Intent):** Gestione dello stato tramite un flusso unidirezionale (**UDF**) per stati atomici e prevedibili.
* **Reactive Navigation:** Sistema basato su `NavigationAction` tramite `Channels` e `Flow`, disaccoppiando la logica di presentazione dal framework [Voyager](https://voyager.adriel.cafe/).
* **Repository Decorator Pattern:** Utilizzo del pattern Decorator per iniettare logica di Analytics senza contaminare il repository core (SRP).
* **Deterministic Threading:** Astrazione dei dispatcher tramite `AppDispatchers` per il pieno controllo della concorrenza sia in produzione che nei test.

## 🍎 Shared Native Services (Android & iOS)
Il progetto gestisce le funzionalità specifiche di piattaforma tramite astrazioni di dominio:
* **Firebase Integration:** Implementazioni native per Analytics, Crashlytics e Push Notifications. Su iOS, lo stacktrace Kotlin viene mappato in `NSError` per una corretta analisi su Crashlytics.
* **Push Notifications:** Gestione dei token Firebase Messaging e sottoscrizione ai topic implementata via interop nativa.
* **Developer Experience (DX):** Mirroring degli eventi nel **Logcat** (Android) e tramite **NSLog** (iOS) durante il debug per validazione in tempo reale.

## 🌐 Backend & API Ecosystem
L'app si interfaccia con un ecosistema backend reale ospitato su **Oracle Cloud Infrastructure (OCI)**, progettato con un approccio orientato alla sicurezza e alla scalabilità.
* **Infrastructure:**
    * **Public VCN:** Ospita il server **Spring Boot 3.x** dietro un reverse proxy **Nginx**, con certificati **Let's Encrypt** (HTTPS) e gestione dinamica dell'host tramite **No-IP (DDNS)**.
    * **Private VCN:** Il database **MySQL** è isolato in una rete privata non accessibile dall'esterno, comunicando esclusivamente con il backend per garantire l'integrità del dato.
* **Security:** Gestione di sessioni stateless tramite **JWT**.
* **API Documentation:** Gli endpoint sono consultabili e testabili tramite la Swagger UI ufficiale:
  👉 [**SocialMaster API Documentation**](https://socialmaster.ddns.net/swagger-ui/index.html)

## 🚀 CI/CD Pipeline (GitHub Actions)
Il progetto implementa un workflow di **Continuous Integration** professionale:
* **Automated Testing:** Esecuzione di Unit Tests e UI Tests (Robolectric) su Java 21 ad ogni PR.
* **Build Verification:** Pipeline resiliente che valida la compilazione KMP su ogni commit senza esporre file di configurazione sensibili.
* **Multi-Platform Build:** Validazione parallela della compilazione Android (APK) e del framework nativo iOS.
* **Continuous Delivery:** Generazione automatica di artefatti (APK e iOS Framework) disponibili per il download immediato dalla tab *Actions*.

## 🧪 Testing Strategy (Pyramid Approach)
La suite di test garantisce affidabilità con il minimo overhead di esecuzione:
* **Verification of Threading Policy:** Test avanzati (es. in `LoginUseCaseTest`) che validano il corretto cambio di contesto sui dispatcher IO.
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
* **Secure Storage:** Cifratura dei token tramite Google Tink su Android e **Keychain** su iOS (via C-Interop).
* **Testing:** [Mokkery](https://github.com/lwasyl/Mokkery), [Turbine](https://github.com/cashapp/turbine), [Robolectric](https://robolectric.org/).

## 🗺 Roadmap
- [x] Setup Architetturale, BaseTest e CI/CD.
- [x] Implementazione Auth Flow (Login) con monitoraggio e servizi nativi.
- [x] **Secure Persistence:** Integrazione Tink (Android) e Keychain (iOS).
- [ ] **Next Step:** Sviluppo del Feed Social e integrazione Coil Multiplatform.

---
*Sviluppato da **Massimiliano Pugliatti*** *Mobile Developer & Technical Instructor*