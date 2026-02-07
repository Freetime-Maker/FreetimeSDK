# Freetime Payment SDK

Ein vollständig eigenständiges, Open-Source Multi-Cryptocurrency Payment SDK für Android.

## Features

- **Multi-Coin Support**: Bitcoin (BTC), Ethereum (ETH), Litecoin (LTC)
- **Vollständig eigenständig**: Keine externen Abhängigkeiten oder API-Aufrufe erforderlich
- **Lokale Kryptographie**: Alle kryptographischen Operationen werden lokal ausgeführt
- **Wallet Management**: Erstellen und verwalten Sie mehrere Wallets
- **Transaction Builder**: Erstellen und signieren Sie Transaktionen
- **Open Source**: Vollständig transparent und überprüfbarer Code

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

## Beispiel-App

Eine vollständige Beispiel-App ist im `examples/` Verzeichnis enthalten, die alle SDK-Funktionen demonstriert:

- `ExampleApp.kt` - Grundlegende SDK-Funktionen
- `PaymentGatewayExample.kt` - Payment Gateway mit automatischer Weiterleitung

## Lizenz

Dieses Projekt ist unter der MIT-Lizenz veröffentlicht. Siehe die [LICENSE](LICENSE) Datei für Details.

## Beiträge

Beiträge sind willkommen! Bitte erstellen Sie einen Pull Request oder öffnen Sie ein Issue.

## Support

Für Fragen und Support, öffnen Sie bitte ein Issue im GitHub Repository.

---

**Wichtiger Hinweis**: Dieses SDK ist für Bildungszwecke und Entwicklung konzipiert. In Produktionsumgebungen sollten Sie zusätzliche Sicherheitsmaßnahmen implementieren und die Transaktionen mit echten Blockchain-Nodes testen.