<div align="center">
<h1>Freetime Payment SDK</h1>
</div>

<div align="center">

[![JitPack](https://jitpack.io/v/FreetimeMaker/FreetimeSDK.svg)](https://jitpack.io/#FreetimeMaker/FreetimeSDK)
</div>
A fully on its own, Open-Source Multi-Cryptocurrency Payment SDK for Android.

<div align="center">
<h2>Features</h2>
</div>

- **Multi-Coin Support**: Bitcoin (BTC), Ethereum (ETH), Litecoin (LTC)
- **Fully on its own**: No external Dependencies or API-Request required
- **Local Cryptographie**: All cryptographic Operations will be run locally
- **Transaction Builder**: Create and sign your Transactions
- **Open Source**: Fully transparent and  Code

## Unterstützte Kryptowährungen

| Kryptowährung | Symbol | Dezimalstellen |
|---------------|--------|---------------|
| Bitcoin | BTC | 8 |
| Ethereum | ETH | 18 |
| Litecoin | LTC | 8 |

## Installation

Fügen Sie die SDK-Bibliothek zu Ihrem Android-Projekt hinzu:

```gradle
dependencies {
    implementation project(':SDK')
}
```

## Schnellstart

### 1. SDK initialisieren

```kotlin
import com.freetime.sdk.payment.FreetimePaymentSDK
import com.freetime.sdk.payment.CoinType

val sdk = FreetimePaymentSDK()
```

### 2. Wallet erstellen

```kotlin
// Bitcoin Wallet erstellen
val bitcoinWallet = sdk.createWallet(CoinType.BITCOIN, "Mein Bitcoin Wallet")

// Ethereum Wallet erstellen
val ethereumWallet = sdk.createWallet(CoinType.ETHEREUM, "Mein Ethereum Wallet")

// Litecoin Wallet erstellen
val litecoinWallet = sdk.createWallet(CoinType.LITECOIN, "Mein Litecoin Wallet")
```

### 3. Guthaben abfragen

```kotlin
val balance = sdk.getBalance(bitcoinWallet.address)
println("Bitcoin Guthaben: $balance BTC")
```

### 4. Kryptowährung senden

```kotlin
import java.math.BigDecimal

val amount = BigDecimal("0.001")
val recipientAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"

val txHash = sdk.send(
    fromAddress = bitcoinWallet.address,
    toAddress = recipientAddress,
    amount = amount,
    coinType = CoinType.BITCOIN
)

println("Transaktion gesendet: $txHash")
```

### 5. Gebührenschätzung

```kotlin
val fee = sdk.getFeeEstimate(
    fromAddress = bitcoinWallet.address,
    toAddress = recipientAddress,
    amount = amount,
    coinType = CoinType.BITCOIN
)

println("Geschätzte Gebühr: $fee BTC")
```

## API Referenz

### FreetimePaymentSDK

Die Hauptklasse für die Interaktion mit dem Payment SDK.

#### Methoden

- `createWallet(coinType: CoinType, name: String?): Wallet` - Erstellt ein neues Wallet
- `getBalance(address: String): BigDecimal` - Ruft das Guthaben einer Adresse ab
- `send(fromAddress: String, toAddress: String, amount: BigDecimal, coinType: CoinType): String` - Sendet Kryptowährung
- `getFeeEstimate(...): BigDecimal` - Schätzt die Transaktionsgebühr
- `getAllWallets(): List<Wallet>` - Gibt alle Wallets zurück
- `getWalletsByCoinType(coinType: CoinType): List<Wallet>` - Gibt Wallets nach Typ zurück
- `validateAddress(address: String, coinType: CoinType): Boolean` - Validiert eine Adresse

### Wallet

Repräsentiert ein Kryptowährung-Wallet.

#### Eigenschaften

- `address: String` - Die Wallet-Adresse
- `coinType: CoinType` - Der Kryptowährungstyp
- `publicKey: PublicKey` - Der öffentliche Schlüssel
- `privateKey: PrivateKey` - Der private Schlüssel (sicher aufbewahren!)

#### Methoden

- `getBalance(paymentProvider: PaymentInterface): BigDecimal` - Guthaben abfragen
- `send(toAddress: String, amount: BigDecimal, paymentProvider: PaymentInterface): Transaction` - Senden

### Transaction

Repräsentiert eine Kryptowährungstransaktion.

#### Eigenschaften

- `id: String` - Transaktions-ID
- `fromAddress: String` - Absenderadresse
- `toAddress: String` - Empfängeradresse
- `amount: BigDecimal` - Betrag
- `fee: BigDecimal` - Gebühr
- `coinType: CoinType` - Kryptowährungstyp
- `status: TransactionStatus` - Transaktionsstatus

## Sicherheit

- **Private Keys**: Private Schlüssel werden niemals außerhalb der App gespeichert oder übertragen
- **Lokale Verarbeitung**: Alle kryptographischen Operationen finden lokal auf dem Gerät statt
- **Open Source**: Der Code ist vollständig überprüfbar
- **Keine externen Abhängigkeiten**: Das SDK benötigt keine externen Dienste oder APIs

## Architektur

Das SDK folgt einer modularen Architektur:

```
FreetimePaymentSDK
├── Wallet Management
├── Payment Providers
│   ├── BitcoinPaymentProvider
│   ├── EthereumPaymentProvider
│   └── LitecoinPaymentProvider
├── Crypto Utils
│   ├── BitcoinCryptoUtils
│   ├── EthereumCryptoUtils
│   └── LitecoinCryptoUtils
└── Core Interfaces
    ├── PaymentInterface
    ├── Transaction
    └── CoinType
```

## Payment Gateway - Automatische Zahlungsweiterleitung

Das SDK enthält einen vollständigen Payment Gateway für die automatische Verarbeitung von Kryptowährungszahlungen.

### Features

- **Automatische Weiterleitung**: Zahlungen werden automatisch an Ihre Händler-Wallet-Adresse weitergeleitet
- **Temporäre Zahlungsadressen**: Erstellen Sie für jede Zahlung eine eindeutige temporäre Adresse
- **Status-Überwachung**: Überwachen Sie den Zahlungsstatus in Echtzeit
- **Multi-Zahlungs-Verarbeitung**: Verarbeiten Sie mehrere Zahlungen gleichzeitig
- **Event-Listener**: Erhalten Sie Benachrichtigungen bei Statusänderungen

### Payment Gateway Setup

```kotlin
import com.freetime.sdk.payment.gateway.*

// Ihre Händler Wallet-Adresse (fest im Code definiert)
val merchantWallet = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"

// Payment Gateway initialisieren
val gateway = PaymentGateway(
    sdk = sdk,
    merchantWalletAddress = merchantWallet,
    merchantCoinType = CoinType.BITCOIN
)

// Automatische Verarbeitung starten
val processor = PaymentProcessor(gateway)
processor.addPaymentListener(LoggingPaymentListener())
processor.startProcessing()
```

### Zahlung annehmen

```kotlin
// Erstelle eine temporäre Zahlungsadresse für den Kunden
val paymentRequest = gateway.createPaymentAddress(
    amount = BigDecimal("0.001"), // 0.001 BTC
    customerReference = "Kunde-12345",
    description = "Produkt #ABC-123"
)

println("Zahlen Sie an: ${paymentRequest.customerAddress}")
println("Betrag: ${paymentRequest.amount} BTC")
```

### Zahlungsstatus überwachen

```kotlin
// Überprüfe den Zahlungsstatus
val status = gateway.checkPaymentStatus(paymentRequest.id)
val details = gateway.getPaymentDetails(paymentRequest.id)

println("Status: $status")
println("Aktuelles Guthaben: ${details?.currentBalance}")
println("Verbleibend: ${details?.remainingAmount}")
```

### Automatische Weiterleitung

Sobald die volle Zahlung auf der temporären Adresse eingeht, wird diese automatisch an Ihre Händler-Wallet-Adresse weitergeleitet:

```kotlin
// Der Payment Gateway überwacht automatisch und leitet weiter
// Sobald status == CONFIRMED, wurde die Zahlung weitergeleitet

if (status == PaymentStatus.CONFIRMED) {
    val details = gateway.getPaymentDetails(paymentRequest.id)
    println("Zahlung bestätigt!")
    println("Weiterleitungs-Hash: ${details?.forwardedTxHash}")
}
```

### Merchant Konfiguration

```kotlin
// Vorkonfigurierte Merchant-Einstellungen
val config = MerchantPresets.bitcoinConfig(merchantWallet)

val gateway = PaymentGateway(
    sdk = sdk,
    merchantWalletAddress = config.walletAddress,
    merchantCoinType = config.coinType
)
```

## USD Payment Gateway - Automatische Konvertierung

Das SDK unterstützt jetzt USD-Zahlungen mit automatischer Konvertierung in Kryptowährungen!

### Features

- **USD Eingabe**: Geben Sie Beträge in US-Dollar an
- **Automatische Konvertierung**: Echtzeit-Wechselkurse von APIs
- **Multi-API Support**: CoinGecko, CoinCap mit Fallback
- **Caching**: 1-Minuten-Cache für Performance
- **Offline-Fallback**: Mock-Kurse wenn API nicht erreichbar

### USD Payment Gateway Setup

```kotlin
import com.freetime.sdk.payment.conversion.*

// USD Payment Gateway initialisieren
val usdGateway = sdk.createUsdPaymentGateway(
    merchantWalletAddress = "your_wallet_address",
    merchantCoinType = CoinType.BITCOIN
)

// USD Zahlung annehmen (automatische Konvertierung)
val usdPayment = usdGateway.createUsdPaymentRequest(
    usdAmount = BigDecimal("100.00"), // $100 USD
    customerReference = "Kunde-12345",
    description = "Produkt #ABC-123"
)

println("Zahlen Sie ${usdPayment.cryptoAmount} ${usdPayment.coinType.symbol}")
println("Entspricht $${usdPayment.usdAmount} USD")
println("Wechselkurs: $${usdPayment.exchangeRate}")
```

### Währungsumrechnung

```kotlin
// Currency Converter für manuelle Konvertierung
val converter = sdk.getCurrencyConverter()

// USD zu Krypto
val result = converter.convertUsdToCrypto(
    usdAmount = BigDecimal("50.00"),
    coinType = CoinType.BITCOIN
)

if (result.success) {
    println("$50.00 USD = ${result.cryptoAmount} BTC")
}

// Krypto zu USD
val reverseResult = converter.convertCryptoToUsd(
    cryptoAmount = BigDecimal("0.001"),
    coinType = CoinType.BITCOIN
)

if (reverseResult.success) {
    println("0.001 BTC = $${reverseResult.usdAmount} USD")
}
```

### Wechselkurs-Überwachung

```kotlin
// Aktuelle Wechselkurse abrufen
val rates = converter.getAllExchangeRates()
rates.forEach { (coinType, rate) ->
    println("1 ${coinType.coinName} = $${rate}")
}
```

### API-Unterstützung

**Unterstützte APIs:**
- **CoinGecko API** (primär, kostenlos)
- **CoinCap API** (alternativ)
- **Offline-Fallback** mit Mock-Kursen

**API-Features:**
- Echtzeit-Wechselkurse
- Automatische Fehlerbehandlung
- 1-Minuten-Caching
- Keine API-Keys erforderlich (CoinGecko)

## Beispiel-App

Eine vollständige Beispiel-App ist im `examples/` Verzeichnis enthalten, die alle SDK-Funktionen demonstriert:

- `ExampleApp.kt` - Grundlegende SDK-Funktionen
- `PaymentGatewayExample.kt` - Payment Gateway mit automatischer Weiterleitung
- `UsdPaymentExample.kt` - USD-Zahlungen mit automatischer Konvertierung

## Lizenz

Dieses Projekt ist unter der MIT-Lizenz veröffentlicht. Siehe die [LICENSE](LICENSE) Datei für Details.

## Beiträge

Beiträge sind willkommen! Bitte erstellen Sie einen Pull Request oder öffnen Sie ein Issue.

## Support

Für Fragen und Support, öffnen Sie bitte ein Issue im GitHub Repository.

---

**Wichtiger Hinweis**: Dieses SDK ist für Bildungszwecke und Entwicklung konzipiert. In Produktionsumgebungen sollten Sie zusätzliche Sicherheitsmaßnahmen implementieren und die Transaktionen mit echten Blockchain-Nodes testen.