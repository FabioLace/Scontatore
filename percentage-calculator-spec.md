# Scontatore — App Android nativa

## Obiettivo

Realizzare una semplice app Android **nativa** che calcola sconti e aumenti percentuali. L'app deve essere minimale, veloce e utilizzabile con una sola mano.

## Stack tecnico

- **Linguaggio**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architettura**: MVVM minimale (un `ViewModel` con `StateFlow`)
- **Build system**: Gradle (Kotlin DSL), version catalog (`libs.versions.toml`)
- **minSdk**: 26
- **targetSdk / compileSdk**: ultima stabile disponibile
- **Test**: JUnit 5 per la logica di calcolo (unit test), nessun test UI richiesto
- **Dipendenze esterne**: nessuna oltre a quelle standard di Compose/AndroidX
- **Package name**: `com.example.scontatore` (modificabile)

## Funzionalità

### 1. Modalità di calcolo

Un selettore (es. `SegmentedButton` / `TabRow`) con due modalità:

- **Sconto** — sottrae la percentuale dal prezzo originale
- **Aumento** — aggiunge la percentuale al prezzo originale

### 2. Input

- Campo **Prezzo originale** (numerico decimale)
- Campo **Percentuale** (numerico decimale)
- Il calcolo avviene **in tempo reale** mentre l'utente digita (nessun pulsante "Calcola")
- Pulsante **Azzera** per svuotare entrambi i campi
- Accettare sia il punto che la virgola come separatore decimale

### 3. Output

Mostrare in modo chiaro:

- **Prezzo finale**
- **Importo dello sconto/aumento** (differenza in valore assoluto)

Esempio:

| Prezzo originale | Percentuale | Modalità | Prezzo finale | Differenza |
|------------------|-------------|----------|---------------|------------|
| 200,00           | 15          | Sconto   | 170,00        | -30,00     |
| 200,00           | 15          | Aumento  | 230,00        | +30,00     |

### 4. Formattazione

- Numeri formattati secondo il locale del dispositivo (`NumberFormat` / `Locale.getDefault()`)
- Sempre 2 cifre decimali nell'output
- Nessun simbolo di valuta fisso (l'app è agnostica rispetto alla valuta)

### 5. Validazione e casi limite

- Campi vuoti → nessun risultato mostrato (non errore)
- Input non valido → nessun crash, risultato non mostrato
- Percentuale > 100 in modalità **Sconto** → mostrare un messaggio di errore sotto il campo ("Lo sconto non può superare il 100%")
- Valori negativi non ammessi (bloccare l'input del segno meno)
- Gestire correttamente numeri molto grandi senza overflow: usare `BigDecimal` per i calcoli, non `Double`

## Logica di calcolo

Isolare la logica in una classe/funzione pura, indipendente da Android, così da testarla facilmente.

- Sconto: `finalPrice = originalPrice * (1 - percentage / 100)`
- Aumento: `finalPrice = originalPrice * (1 + percentage / 100)`
- Differenza: `finalPrice - originalPrice`
- Arrotondamento: `RoundingMode.HALF_UP`, scala 2

### Unit test richiesti

Coprire almeno:

- Sconto standard (200 al 15% → 170)
- Aumento standard (200 al 15% → 230)
- Percentuale 0 → prezzo invariato
- Sconto al 100% → prezzo finale 0
- Sconto > 100% → risultato non valido
- Arrotondamento (es. 99,99 al 33,33%)
- Input con virgola e con punto

## Struttura del progetto

```
app/src/main/java/com/example/percentagecalculator/
├── MainActivity.kt
├── ui/
│   ├── CalculatorScreen.kt
│   ├── CalculatorViewModel.kt
│   ├── CalculatorUiState.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── domain/
    ├── CalculationMode.kt        // enum: DISCOUNT, INCREASE
    ├── CalculationResult.kt      // data class: finalPrice, differenceAmount
    └── PercentageCalculator.kt   // logica pura

app/src/test/java/com/example/percentagecalculator/domain/
└── PercentageCalculatorTest.kt
```

## UI / UX

- Tema Material 3 con **dynamic color** (Material You) dove disponibile, fallback a una palette statica
- Supporto **dark mode** automatico
- Layout verticale, centrato, con padding generoso
- Tastiera numerica decimale (`KeyboardType.Decimal`) per entrambi i campi
- Card per il risultato, con prezzo finale in evidenza (tipografia grande) e differenza sotto, in colore diverso:
  - verde per aumento / rosso per sconto, oppure colori semantici del tema
- Il layout deve funzionare in portrait e landscape senza perdere lo stato (stato nel `ViewModel`, più `rememberSaveable` dove serve)
- Icona dell'app: adaptive icon semplice con il simbolo `%`
- Lingua: stringhe in `strings.xml` con localizzazione **italiano** (default) e **inglese**

## Convenzioni di codice

- Codice, nomi di classi, variabili e commenti in **inglese**
- Usare **nomi di variabile descrittivi** (es. `originalPriceInput`, `discountPercentage`, `calculatedFinalPrice`), evitare abbreviazioni come `p`, `pct`, `res`
- Nessuna stringa hardcoded nella UI: tutto in `strings.xml`
- Composable piccole e componibili, con stato sollevato (state hoisting)
- Nessun codice morto o commentato

## Vincoli

- Nessuna connessione di rete, nessun permesso richiesto nel manifest
- Nessun analytics, nessuna pubblicità
- Nessuna persistenza: l'app non salva la cronologia
- Dimensione dell'APK la più contenuta possibile (abilitare minify/R8 per la release)

## Criteri di accettazione

- [ ] `./gradlew assembleDebug` compila senza errori né warning rilevanti
- [ ] `./gradlew test` passa con tutti i test verdi
- [ ] L'app si avvia e calcola correttamente in tempo reale
- [ ] Il cambio di orientamento mantiene i valori inseriti
- [ ] Il dark mode funziona
- [ ] Nessun crash con input vuoto, malformato o estremo
- [ ] Le stringhe sono disponibili in italiano e in inglese

## Ordine di lavoro suggerito

1. Setup del progetto Gradle (Kotlin DSL, version catalog, Compose)
2. Layer `domain` con `PercentageCalculator` e relativi unit test
3. `CalculatorUiState` e `CalculatorViewModel`
4. `CalculatorScreen` con Compose
5. Tema, icona e localizzazione
6. Verifica finale dei criteri di accettazione
