# Kotlin Multiplatform Social App
![Mobile CI](https://github.com/massimilianoPugliatti/SharedSocial-KMP/actions/workflows/mobile-ci.yml/badge.svg)

Un'applicazione Social moderna costruita con **Kotlin Multiplatform (KMP)** e **Compose Multiplatform**. Il progetto segue rigorosi standard di **Clean Architecture** e gestione dello stato reattiva per garantire parità di feature e performance native tra Android e iOS.

## 🎯 Project Purpose
Questo repository è uno **showcase tecnico** progettato per dimostrare l'applicazione di standard industriali di alto livello. È il risultato di una ricerca su architetture scalabili, sicurezza dei dati e automazione dei processi di release.

L'obiettivo è illustrare la padronanza di:
* **Architetture Scalabili:** Implementazione di Clean Architecture in contesti multi-modulo.
* **Full-Stack Awareness:** Integrazione con un ecosistema backend complesso.
* **Modern CI/CD:** Automazione della qualità del codice e della distribuzione degli artefatti.

## 🏗 Architettura & Design Patterns
* **Clean Architecture:** Separazione netta tra Domain, Data e UI layer per massimizzare la testabilità.
* **MVI (Model-View-Intent):** Gestione dello stato tramite un flusso unidirezionale (**UDF**) per stati atomici e prevedibili.
* **Reactive Navigation:** Sistema basato su `NavigationAction` tramite `Channels` e `Flow`, disaccoppiando la logica di presentazione dal framework [Voyager](https://voyager.adriel.cafe/).
* **Deterministic Threading:** Astrazione dei dispatcher tramite `AppDispatchers` per il pieno controllo della concorrenza sia in produzione che nei test.

## 🌐 Backend & API Ecosystem
L'app si interfaccia con un backend reale che gestisce la logica di business e la persistenza dei dati.
* **Backend:** Spring Boot 3.x (ospitato in una repository privata).
* **Security:** Gestione di sessioni stateless tramite **JWT**.
* **API Documentation:** Gli endpoint sono consultabili e testabili tramite la Swagger UI ufficiale:
  👉 [**SocialMaster API Documentation**](https://socialmaster.ddns.net/swagger-ui/index.html)

## 🚀 CI/CD Pipeline (GitHub Actions)
Il progetto implementa un workflow di **Continuous Integration** professionale:
* **Automated Testing:** Esecuzione di Unit Tests e UI Tests (Robolectric) su Java 21 ad ogni PR.
* **Multi-Platform Build:** Validazione parallela della compilazione Android (APK) e del framework nativo iOS (macOS runner).
* **Continuous Delivery:** Generazione automatica di artefatti (APK) disponibili per il download immediato dalla tab *Actions*.

## 🛠 Tech Stack
* **Logic:** [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
* **UI:** [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
* **DI:** [Koin](https://insert-koin.io/)
* **Networking:** [Ktor](https://ktor.io/) (Client Auth, Logging, Content Negotiation)
* **Persistence:** [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) + [Google Tink](https://github.com/tink-crypto/tink-kotlin) (AES256_GCM hardware-backed).
* **Secure Storage:** Cifratura dei token tramite Google Tink su Android e **Keychain** su iOS (via C-Interop).
* **Testing:** [Mokkery](https://github.com/lwasyl/Mokkery), [Turbine](https://github.com/cashapp/turbine), [Robolectric](https://robolectric.org/).

## 🧪 Testing Strategy
Il progetto adotta un approccio **State-based testing** focalizzato sull'efficienza:

* **UI Integration:** Utilizzo di **Robolectric** per testare i componenti **Compose** sulla JVM, garantendo la corretta interazione tra UI e Business Logic senza necessità di emulatore.
* **Flow Testing:** Validazione delle transizioni di stato del ViewModel e degli eventi di navigazione tramite **Turbine**.
* **Network Mocking:** Simulazione delle risposte del backend tramite `Ktor-client-mock`.
* **Stability:** Iniezione di `TestAppDispatchers` per eliminare il non-determinismo nei test asincroni.

## 🗺 Roadmap
- [x] Setup Architetturale, BaseTest e CI/CD.
- [x] Implementazione Auth Flow (Login).
- [x] **Secure Persistence:** Integrazione Tink (Android) e Keychain (iOS).
- [ ] **Next Step:** Sviluppo del Feed Social e integrazione Coil Multiplatform.

---
*Sviluppato da **Massimiliano Pugliatti*** *Mobile Developer & Technical Instructor*