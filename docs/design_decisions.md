# Design Decisions

Questa pagina raccoglie le principali decisioni architetturali del progetto e i trade-off che le accompagnano.

---

## 1. Kotlin Multiplatform per la business logic

### Decisione
Usare **Kotlin Multiplatform** per condividere la business logic tra Android e iOS.

### Motivazione
Questo approccio consente di:

- evitare duplicazione della logica applicativa
- mantenere use case, modelli di dominio e navigator in un solo posto
- ridurre divergenze funzionali tra le due piattaforme
- concentrare lo sforzo nativo solo dove realmente necessario

### Trade-off
- bootstrap di piattaforma più articolato
- maggiore attenzione ai confini tra `commonMain`, `androidMain`, `iosMain` e Swift

---

## 2. Compose Multiplatform per la UI condivisa

### Decisione
Usare **Compose Multiplatform** come layer UI condiviso.

### Motivazione
Consente di:

- mantenere omogeneità visiva e comportamentale
- centralizzare screen e presentation logic
- accelerare il ciclo di iterazione sulle feature

### Trade-off
- alcune capability native richiedono adapter dedicati
- integrazione più delicata quando si lavora con surface native come camera/video preview

---

## 3. Feature-based architecture

### Decisione
Organizzare il progetto per feature (`auth`, `register`, `feed`, `camera`, `createpost`, `home`) invece che per layer globali.

### Motivazione
Questo approccio rende più semplice:

- isolare i confini funzionali
- mantenere il codice leggibile
- far crescere la codebase per capability di prodotto
- evitare un unico modulo monolitico diviso artificialmente per tecnologia

### Trade-off
- numero maggiore di package e file
- struttura iniziale più verbosa

---

## 4. `home` come feature di orchestrazione

### Decisione
Introdurre `home` come feature **presentation-only** di orchestrazione.

### Motivazione
`home` compone:

- il pager principale
- la preview camera condivisa
- i controlli foto/video
- il feed

Questa scelta evita di:

- sovraccaricare `feed` con responsabilità di coordinamento
- trasformare `camera` in un entry point improprio dell'app
- duplicare logica di lifecycle/navigation

### Trade-off
- `home` non ha un dominio autonomo
- parte della complessità di orchestrazione vive in presentation

---

## 5. Root bootstrap separato dalle feature

### Decisione
Gestire il routing iniziale tramite `RootScreen` e `RootViewModel`.

### Motivazione
Il bootstrap iniziale dell'app (utente autenticato o meno) è un problema diverso rispetto alle feature funzionali. Tenerlo separato permette di:

- evitare logica di auth-check nelle schermate di business
- rendere chiaro il punto di ingresso dell'app
- testare la decisione di routing in modo isolato

### Trade-off
- un livello in più nello stack iniziale
- necessità di una schermata di bootstrap dedicata

---

## 6. Reactive navigation tramite `AppNavigator`

### Decisione
Disaccoppiare i ViewModel dal framework concreto di navigazione usando `AppNavigator` e `NavigationAction`.

### Motivazione
Questo consente di:

- mantenere la navigation logic dentro il mondo condiviso
- non legare il dominio/presentation a Voyager
- testare la navigazione come emissione di eventi

### Trade-off
- un layer di astrazione in più
- più oggetti piccoli da mantenere (`AppNavigator`, `NavigationAction`, adapter Voyager)

---

## 7. Repository decorator pattern

### Decisione
Aggiungere comportamenti trasversali tramite **decorator** sui repository.

### Motivazione
I repository core rimangono focalizzati sul proprio compito primario, mentre analytics e side effects possono essere aggiunti senza contaminare l'implementazione principale.

Questo pattern è usato in particolare su:

- `AuthRepositoryDecorator`
- `RegisterRepositoryDecorator`
- `FeedRepositoryDecorator`

### Trade-off
- catena di chiamata più profonda
- debugging leggermente più complesso

---

## 8. Platform contracts per i servizi nativi

### Decisione
Esporre le capability native tramite interfacce condivise.

### Esempi
- `CameraService`
- `CameraPermissionService`
- `CameraPermissionRequester`
- `CameraPreviewRenderer`
- `MediaPickerService`
- `MediaPreviewRenderer`
- `AnalyticsService`
- `SecureStorage`

### Motivazione
Questo permette alla logica condivisa di dipendere da **contratti stabili**, lasciando alle piattaforme le implementazioni concrete.

### Trade-off
- più wiring nel DI
- necessità di tenere allineate le implementazioni Android/iOS

---

## 9. Distinzione tra permission check e permission request

### Decisione
Distinguere tra:

- `CameraPermissionService`
- `CameraPermissionRequester`

### Motivazione
La richiesta runtime dei permessi è fortemente piattaforma-dipendente, soprattutto su Android. Separare check e request consente di:

- mantenere il `CameraViewModel` in `commonMain`
- non introdurre API Android nella UI/shared logic
- gestire Android e iOS con la stessa semantica ma meccanismi diversi

### Trade-off
- un livello di astrazione in più
- DI leggermente più articolata

---

## 10. Un solo `CameraViewModel` per il pager home

### Decisione
Usare una sola istanza di `CameraViewModel` per i tre stati logici della home (`Photo`, `Feed`, `Video`).

### Motivazione
La camera è una risorsa unica e condivisa. Un solo ViewModel consente di avere:

- un solo lifecycle camera
- una sola gestione permessi
- un solo timer di registrazione
- uno stato coerente tra modalità foto e video

### Trade-off
- la feature `home` deve orchestrare modalità e visibilità
- serve attenzione in fase di composizione del pager

---

## 11. Preview nativa fuori dal pager

### Decisione
Trattare la preview camera come superficie stabile, fuori dal normale contenuto di pagina del pager.

### Motivazione
Questa scelta è stata necessaria per garantire stabilità cross-platform:

- su Android con `PreviewView`
- su iOS con `UIViewController` e `AVCaptureVideoPreviewLayer`

Ha evitato problemi come:

- attach multipli della stessa `PreviewView`
- layout instabili durante lo scroll
- differenze di lifecycle tra pager e surface native

### Trade-off
- struttura UI più sofisticata
- maggiore esplicitazione della separazione tra preview e controlli

---

## 12. Create Post come feature separata dalla camera

### Decisione
Non far vivere la composizione del post dentro la feature camera.

### Motivazione
Questo mantiene distinti due contesti diversi:

- acquisizione/selezione media
- composizione del contenuto e futura pubblicazione

La feature `createpost` può così evolvere verso upload/repository senza complicare la feature camera.

### Trade-off
- un passaggio di navigazione in più
- necessità di passare `MediaAsset` tra feature

---

## 13. Errori di dominio tipizzati

### Decisione
Non propagare eccezioni tecniche e status HTTP direttamente alla UI.

### Motivazione
La UI deve ragionare in termini di errori semantici, non di dettagli infrastrutturali.

Esempi:
- `401` → `Unauthorized`
- `IOException` → `NetworkError`
- `ImageCaptureException` → `CameraError`
- `IOSCameraEngineError` → `CameraError`

### Trade-off
- presenza di mapper dedicati
- più codice infrastrutturale da mantenere

---

## 14. Doppio mapping degli errori

### Decisione
Usare due passaggi distinti:

1. mapper infrastrutturale → errore di dominio
2. resolver presentation → messaggio UI

### Motivazione
Questo mantiene separati:

- i dettagli tecnici
- la semantica del dominio
- la rappresentazione user-facing

Il pattern è applicato sia alle feature di rete sia alla feature camera.

### Trade-off
- maggiore numero di classi piccole
- più disciplina nel mantenere i confini

---

## 15. Secure storage nativo per piattaforma

### Decisione
Usare la soluzione più adatta per ogni piattaforma:

- Android → DataStore + Google Tink
- iOS → Keychain

### Motivazione
La sicurezza dei token di sessione è una responsabilità infrastrutturale critica e richiede integrazioni native affidabili.

### Trade-off
- bootstrap nativo più articolato
- testing infrastrutturale meno uniforme tra piattaforme

---

## 16. Bootstrap Koin multipiattaforma

### Decisione
Inizializzare Koin con una combinazione di moduli comuni e moduli di piattaforma, con bootstrap Swift lato iOS.

### Motivazione
Questo consente di:

- mantenere il wiring nel mondo KMP dove possibile
- demandare a Swift solo i dettagli strettamente nativi
- registrare servizi Apple come dipendenze del mondo condiviso

### Trade-off
- maggiore attenzione nella registrazione dei moduli
- necessità di mantenere simmetria concettuale tra Android e iOS

---

## 17. Testing focalizzato sulla business logic

### Decisione
Concentrare la strategia di test su:

- use case
- ViewModel
- persistenza
- navigation flow
- dispatcher behavior

### Motivazione
In un progetto KMP il massimo valore arriva dal testare il codice condiviso, dove vive la logica principale.

### Trade-off
- copertura UI non totale
- necessità di integrare test di piattaforma separati per scenari più specifici

---

## 18. Documentazione separata per overview, architecture e decisions

### Decisione
Separare la documentazione in tre livelli:

- `README.md`
- `docs/architecture.md`
- `docs/design_decisions.md`

### Motivazione
Ogni documento ha un ruolo diverso:

- README → overview del progetto e demo
- architecture → struttura e flussi tecnici
- design decisions → motivazioni e trade-off

### Trade-off
- più documenti da mantenere
- necessità di evitare duplicazioni tra i contenuti