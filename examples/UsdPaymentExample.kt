package com.freetime.sdk.examples

import com.freetime.sdk.payment.*
import com.freetime.sdk.payment.conversion.*
import java.math.BigDecimal

/**
 * Beispiel für USD-Zahlungen mit automatischer Krypto-Konvertierung
 */
class UsdPaymentExample {
    
    private val sdk = FreetimePaymentSDK()
    
    // Händler Wallet-Adresse (im Code fest definiert)
    private val merchantBitcoinWallet = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
    private val merchantEthereumWallet = "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45"
    
    suspend fun demonstrateUsdPayments() {
        println("=== USD Payment Gateway Demo ===\n")
        
        // 1. USD Payment Gateway für Bitcoin initialisieren
        println("1. Initialisiere USD Payment Gateway...")
        val usdGateway = sdk.createUsdPaymentGateway(
            merchantWalletAddress = merchantBitcoinWallet,
            merchantCoinType = CoinType.BITCOIN
        )
        
        // 2. Zeige aktuelle Wechselkurse
        println("2. Aktuelle Wechselkurse:")
        val converter = sdk.getCurrencyConverter()
        val rates = converter.getAllExchangeRates()
        
        rates.forEach { (coinType, rate) ->
            println("1 ${coinType.coinName} = $${rate.setScale(2, BigDecimal.ROUND_HALF_UP)}")
        }
        println()
        
        // 3. USD Zahlungsanfrage erstellen
        println("3. Erstelle USD Zahlungsanfrage...")
        val usdPayment = usdGateway.createUsdPaymentRequest(
            usdAmount = BigDecimal("100.00"), // $100 USD
            customerReference = "Kunde-USD-12345",
            description = "Produkt #USD-ABC-123"
        )
        
        println("USD Zahlungsanfrage erstellt:")
        println("- ID: ${usdPayment.id}")
        println("- USD Betrag: $${usdPayment.usdAmount}")
        println("- Crypto Betrag: ${usdPayment.cryptoAmount} ${usdPayment.coinType.symbol}")
        println("- Wechselkurs: $${usdPayment.exchangeRate} pro ${usdPayment.coinType.symbol}")
        println("- Kundenadresse: ${usdPayment.customerAddress}")
        println("- Händleradresse: ${usdPayment.merchantAddress}")
        println("- Info: ${usdPayment.getFormattedInfo()}")
        println()
        
        // 4. Manuelle Konvertierungsbeispiele
        println("4. Konvertierungsbeispiele:")
        demonstrateConversions(converter)
        
        // 5. Zahlungsstatus überwachen (simuliert)
        println("5. Überwache USD Zahlungsstatus...")
        var checkCount = 0
        val maxChecks = 5
        
        while (checkCount < maxChecks) {
            val status = usdGateway.checkUsdPaymentStatus(usdPayment.id)
            val details = usdGateway.getUsdPaymentDetails(usdPayment.id)
            
            println("Check ${checkCount + 1}: Status = $status")
            details?.let {
                println("  Aktuelles Crypto-Guthaben: ${it.currentCryptoBalance} ${it.usdPaymentRequest.coinType.symbol}")
                println("  Aktueller USD-Wert: $${it.currentUsdValue.setScale(2, BigDecimal.ROUND_HALF_UP)}")
                println("  Verbleibender USD-Wert: $${it.remainingUsdValue.setScale(2, BigDecimal.ROUND_HALF_UP)}")
            }
            
            when (status) {
                PaymentStatus.CONFIRMED -> {
                    println("✅ USD Zahlung erfolgreich bestätigt und weitergeleitet!")
                    break
                }
                PaymentStatus.EXPIRED -> {
                    println("⏰ USD Zahlung abgelaufen")
                    break
                }
                PaymentStatus.FORWARDING_FAILED -> {
                    println("❌ Weiterleitung fehlgeschlagen")
                    break
                }
                PaymentStatus.PENDING -> {
                    println("⏳ Warte auf USD Zahlung...")
                }
                else -> {
                    println("❓ Unbekannter Status")
                }
            }
            
            checkCount++
            kotlinx.coroutines.delay(2000) // 2 Sekunden warten
        }
        
        // 6. Zusammenfassung anzeigen
        println("\n6. USD Zahlungs-Zusammenfassung:")
        val finalDetails = usdGateway.getUsdPaymentDetails(usdPayment.id)
        finalDetails?.let {
            println("USD Zahlungsdetails:")
            println("- Status: ${it.usdPaymentRequest.status}")
            println("- USD Betrag: $${it.usdPaymentRequest.usdAmount}")
            println("- Empfangenes Crypto: ${it.currentCryptoBalance} ${it.usdPaymentRequest.coinType.symbol}")
            println("- Empfangener USD-Wert: $${it.currentUsdValue.setScale(2, BigDecimal.ROUND_HALF_UP)}")
            println("- Weiterleitungs-Hash: ${it.forwardedTxHash ?: "N/A"}")
        }
        
        println("\n=== USD Payment Gateway Demo abgeschlossen ===")
    }
    
    private suspend fun demonstrateConversions(converter: CurrencyConverter) {
        val usdAmounts = listOf(
            BigDecimal("10.00"),
            BigDecimal("50.00"),
            BigDecimal("100.00"),
            BigDecimal("500.00")
        )
        
        val coins = listOf(CoinType.BITCOIN, CoinType.ETHEREUM, CoinType.LITECOIN)
        
        println("USD zu Krypto Konvertierungen:")
        usdAmounts.forEach { usd ->
            println("\n$${usd} USD:")
            coins.forEach { coin ->
                val result = converter.convertUsdToCrypto(usd, coin)
                if (result.success) {
                    println("  ${result.cryptoAmount?.setScale(8, BigDecimal.ROUND_HALF_UP)} ${coin.symbol}")
                } else {
                    println("  Fehler: ${result.error}")
                }
            }
        }
        
        println("\nKrypto zu USD Konvertierungen:")
        val cryptoAmounts = mapOf(
            CoinType.BITCOIN to BigDecimal("0.001"),
            CoinType.ETHEREUM to BigDecimal("0.05"),
            CoinType.LITECOIN to BigDecimal("1.0")
        )
        
        cryptoAmounts.forEach { (coin, amount) ->
            val result = converter.convertCryptoToUsd(amount, coin)
            if (result.success) {
                println("${amount} ${coin.symbol} = $${result.usdAmount?.setScale(2, BigDecimal.ROUND_HALF_UP)} USD")
            } else {
                println("${amount} ${coin.symbol} = Fehler: ${result.error}")
            }
        }
    }
    
    suspend fun demonstrateMultiCurrencyUsdPayments() {
        println("\n=== Multi-Währung USD Demo ===\n")
        
        val gateways = mapOf(
            CoinType.BITCOIN to sdk.createUsdPaymentGateway(merchantBitcoinWallet, CoinType.BITCOIN),
            CoinType.ETHEREUM to sdk.createUsdPaymentGateway(merchantEthereumWallet, CoinType.ETHEREUM)
        )
        
        val usdAmount = BigDecimal("250.00")
        
        println("Erstelle USD Zahlungen für $${usdAmount} in verschiedenen Kryptowährungen:")
        
        gateways.forEach { (coinType, gateway) ->
            val payment = gateway.createUsdPaymentRequest(
                usdAmount = usdAmount,
                customerReference = "Multi-${coinType.symbol}",
                description = "$${usdAmount} in ${coinType.coinName}"
            )
            
            println("${coinType.coinName}:")
            println("  USD: $${payment.usdAmount}")
            println("  Crypto: ${payment.cryptoAmount} ${coinType.symbol}")
            println("  Rate: $${payment.exchangeRate}")
            println("  Adresse: ${payment.customerAddress}")
            println()
        }
        
        println("=== Multi-Währung USD Demo abgeschlossen ===")
    }
}

/**
 * Main Funktion für USD Payment Demo
 */
suspend fun main() {
    val example = UsdPaymentExample()
    
    example.demonstrateUsdPayments()
    example.demonstrateMultiCurrencyUsdPayments()
}
