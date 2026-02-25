# Kotlin Multiplatform Social App

Un'applicazione Social moderna costruita con **Kotlin Multiplatform (KMP)** e **Compose Multiplatform**. Il progetto segue rigorosi standard di Clean Architecture e gestione dello stato reattiva per garantire parità di feature tra Android e iOS.

## 🎯 Project Purpose
Questo repository è uno **showcase tecnico** progettato per dimostrare l'applicazione di standard industriali di alto livello in ambito **Kotlin Multiplatform**.

L'obiettivo è illustrare la padronanza di:
* Architetture scalabili e testabili (Clean Architecture).
* Gestione avanzata della sicurezza e della concorrenza.
* Integrazione di pattern moderni (MVI, UDF) in un contesto cross-platform.
## 🚀 Stato del Progetto
Attualmente è completata l'infrastruttura Core e il modulo di Autenticazione. La struttura è progettata per essere scalabile, facilitando l'aggiunta di nuove feature social in modo modulare.

## 🏗 Architettura & Design Patterns
* **Clean Architecture:** Separazione netta tra Domain, Data e UI layer.
* **MVI (Model-View-Intent):** Gestione dello stato tramite un flusso unidirezionale (**UDF**) per stati atomici e prevedibili.
* **Reactive Navigation:** Sistema basato su **NavigationAction** tramite `Channels` e `Flow`, disaccoppiando la logica di presentazione dal framework Voyager.
* **Dependency Injection (Koin):** Gestione centralizzata delle dipendenze con moduli specifici per piattaforma (`AndroidModule` e `IosModule`).
* **Deterministic Threading:** Astrazione dei dispatcher tramite `AppDispatchers` per il pieno controllo della concorrenza sia in produzione che nei test.

## 🛠 Tech Stack
* **Logic:** [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
* **UI:** [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
* **DI:** [Koin](https://insert-koin.io/)
* **Networking:** [Ktor](https://ktor.io/) (Client Auth, Logging, Content Negotiation)
* **Persistence:** [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) + **Google Tink** (AES256_GCM hardware-backed).
* **Secure Storage:** Cifratura dei token tramite Google Tink (AES256_GCM hardware-backed) su Android e Keychain su iOS.
* **Testing:** [Mokkery](https://github.com/lwasyl/Mokkery), [Turbine](https://github.com/cashapp/turbine), [Robolectric](https://robolectric.org/).

## 🧪 Testing Strategy
Il progetto adotta lo **State-based testing**:
* Validazione delle transizioni di stato del ViewModel e degli eventi di navigazione tramite **Turbine**.
* Iniezione di `TestAppDispatchers` nei test unitari per garantire esecuzioni deterministiche.
* Mocking dei servizi di rete tramite `Ktor-client-mock`.

## 🗺 Roadmap
- [x] Setup Architetturale e BaseTest.
- [x] Implementazione Auth Flow (Login).
- [x] **Secure Persistence:** Cifratura dei token con Tink e Android Keystore.
- [ ] **Next Step:** Sviluppo del Feed Social e integrazione Coil Multiplatform.

---
*Sviluppato da **Massimiliano Pugliatti*** *Mobile Developer & Technical Instructor*