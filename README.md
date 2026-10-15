<div align="center">
  <img src="docs/logo.svg" alt="Scontatore Logo" width="100"/>
  <h1>Scontatore</h1>
  <p>
    <img src="https://img.shields.io/badge/Android-minSdk%2026-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
    <img src="https://img.shields.io/badge/Jetpack%20Compose-2025.12-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
    <img src="https://img.shields.io/badge/Kotlin-2.2-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
    <img src="https://img.shields.io/badge/Material%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white" alt="Material 3" />
    <img src="https://img.shields.io/badge/Gradle-8.14-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle" />
    <img src="https://img.shields.io/badge/JUnit-5.13-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit" />
  </p>
</div>

App Android nativa e minimale per calcolare sconti e aumenti percentuali in tempo reale, pensata per un uso rapido con una sola mano.

## Funzionalità

- **Due modalità di calcolo**: *Sconto* (sottrae la percentuale) e *Aumento* (aggiunge la percentuale), selezionabili con un segmented button.
- **Calcolo in tempo reale** mentre si digita, senza pulsante "Calcola".
- **Risultato** con prezzo finale in evidenza e importo della differenza (con segno `+` per gli aumenti).
- **Pulsante Azzera** per svuotare entrambi i campi mantenendo la modalità selezionata.
- **Separatore decimale flessibile**: accetta sia il punto sia la virgola.
- **Validazione dell'input**: il segno meno e i caratteri non numerici vengono bloccati, input vuoti o malformati non mostrano risultati.
- **Errore dedicato** se lo sconto supera il 100%.
- **Calcoli precisi** con `BigDecimal` e arrotondamento `HALF_UP` a 2 decimali, anche con numeri molto grandi.
- **Formattazione locale** dei numeri secondo la lingua del dispositivo, senza simbolo di valuta.
- **Tema chiaro/scuro** automatico con palette Material 3 personalizzata.
- **Localizzazione** in italiano (predefinita) e inglese.
- **Nessun permesso, nessuna rete, nessuna persistenza**: l'app non raccoglie né salva dati.

## Stack tecnologico

| Tecnologia | Ruolo |
|---|---|
| Kotlin 2.2 | Linguaggio |
| Jetpack Compose (BOM 2025.12.00) | UI dichiarativa |
| Material 3 | Componenti e tema |
| AndroidX Lifecycle 2.9 | `ViewModel` e raccolta dello stato in Compose |
| Kotlin Coroutines `StateFlow` | Stato della UI (architettura MVVM) |
| Gradle 8.14 + Kotlin DSL | Build, con version catalog (`gradle/libs.versions.toml`) |
| Android Gradle Plugin 8.13 | Build Android |
| JUnit 5.13 (Jupiter) | Unit test della logica di calcolo |

## Requisiti

- JDK 17
- Android Studio con supporto ad AGP 8.13
- Android SDK 36 (`compileSdk` / `targetSdk`)
- Dispositivo o emulatore con Android 8.0 o superiore (`minSdk 26`)

Gradle 8.14.3 viene scaricato automaticamente dal wrapper.

## Installazione

```bash
git clone https://github.com/FabioLace/Scontatore.git
cd Scontatore
./gradlew assembleDebug
```

In alternativa, apri la cartella del progetto in Android Studio e avvia la configurazione `app` su un emulatore o un dispositivo.

Per installare direttamente la build di debug su un dispositivo collegato:

```bash
./gradlew installDebug
```

## Configurazione

Non sono richieste chiavi API né variabili d'ambiente. Il file `local.properties` (escluso da git) viene generato da Android Studio con il percorso dell'SDK; se compili da riga di comando senza Android Studio, crealo con:

```properties
sdk.dir=/percorso/al/tuo/Android/sdk
```

## Comandi disponibili

| Comando | Descrizione |
|---|---|
| `./gradlew assembleDebug` | Genera l'APK di debug |
| `./gradlew installDebug` | Installa l'APK di debug sul dispositivo collegato |
| `./gradlew assembleRelease` | Genera l'APK di release (minificato con R8) |
| `./gradlew bundleRelease` | Genera l'Android App Bundle (AAB) di release |
| `./gradlew test` | Esegue gli unit test |
| `./gradlew lint` | Esegue l'analisi statica Android Lint |
| `./gradlew clean` | Elimina gli output di build |

## Test

La logica di calcolo è isolata in `PercentageCalculator`, un oggetto Kotlin puro indipendente da Android, coperto da unit test JUnit 5:

```bash
./gradlew test
```

I test in `app/src/test/java/com/example/scontatore/domain/PercentageCalculatorTest.kt` coprono: sconto e aumento standard, percentuale 0, sconto al 100%, sconto oltre il 100% (non valido), aumento oltre il 100% (valido), valori negativi, arrotondamento `HALF_UP`, numeri molto grandi, parsing con virgola o punto e input vuoti o malformati.

## Struttura del progetto

```
app/src/main/java/com/example/scontatore/
├── MainActivity.kt              # Entry point, imposta il tema e la schermata
├── domain/
│   ├── CalculationMode.kt       # Enum DISCOUNT / INCREASE
│   ├── CalculationResult.kt     # Prezzo finale e differenza
│   └── PercentageCalculator.kt  # Logica di calcolo e parsing dell'input
└── ui/
    ├── CalculatorScreen.kt      # Schermata Compose
    ├── CalculatorUiState.kt     # Stato della UI
    ├── CalculatorViewModel.kt   # ViewModel con StateFlow e validazione dell'input
    └── theme/                   # Colori, tema Material 3 e tipografia

app/src/main/res/
├── drawable/                    # Icona adattiva, logo sviluppatore, icone trending
├── values/                      # Stringhe in italiano (predefinite) e temi
└── values-en/                   # Stringhe in inglese

app/src/test/java/com/example/scontatore/domain/
└── PercentageCalculatorTest.kt  # Unit test della logica di calcolo

gradle/libs.versions.toml        # Version catalog delle dipendenze
percentage-calculator-spec.md    # Specifica funzionale dell'app
```

## Build di release

La build di release ha `isMinifyEnabled` e `isShrinkResources` attivi (R8 + regole in `app/proguard-rules.pro`) per ridurre al minimo la dimensione del pacchetto.

```bash
./gradlew bundleRelease     # AAB per il Play Store
./gradlew assembleRelease   # APK
```

Il progetto non include una configurazione di firma: per pubblicare l'app è necessario aggiungere un `signingConfig` in `app/build.gradle.kts` oppure firmare il pacchetto da Android Studio con **Build › Generate Signed App Bundle / APK**.
