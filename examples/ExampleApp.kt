package com.freetime.sdk.examples

import com.freetime.sdk.payment.*
import java.math.BigDecimal

/**
 * Beispiel-App zur Demonstration des Freetime Payment SDK
 */
class ExampleApp {
    
    private val sdk = FreetimePaymentSDK()
    
    suspend fun demonstrateBasicUsage() {
        println("=== Freetime Payment SDK Demo ===\n")
        
        // 1. Wallets für verschiedene Kryptowährungen erstellen
        println("1. Erstelle Wallets...")
        val bitcoinWallet = sdk.createWallet(CoinType.BITCOIN, "Bitcoin Savings")
        val ethereumWallet = sdk.createWallet(CoinType.ETHEREUM, "ETH Trading")
        val litecoinWallet = sdk.createWallet(CoinType.LITECOIN, "LTC Wallet")
        
        println("Bitcoin Wallet: ${bitcoinWallet.address}")
        println("Ethereum Wallet: ${ethereumWallet.address}")
        println("Litecoin Wallet: ${litecoinWallet.address}")
        println()
        
        // 2. Guthaben abfragen
        println("2. Guthaben abfragen...")
        val btcBalance = sdk.getBalance(bitcoinWallet.address)
        val ethBalance = sdk.getBalance(ethereumWallet.address)
        val ltcBalance = sdk.getBalance(litecoinWallet.address)
        
        println("BTC Guthaben: $btcBalance")
        println("ETH Guthaben: $ethBalance")
        println("LTC Guthaben: $ltcBalance")
        println()
        
        // 3. Adressen validieren
        println("3. Adressen validieren...")
        val validBtcAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
        val invalidBtcAddress = "invalid_address"
        
        println("BTC Adresse valid: ${sdk.validateAddress(validBtcAddress, CoinType.BITCOIN)}")
        println("BTC Adresse invalid: ${sdk.validateAddress(invalidBtcAddress, CoinType.BITCOIN)}")
        println()
        
        // 4. Gebührenschätzung
        println("4. Gebührenschätzung...")
        val amount = BigDecimal("0.001")
        val fee = sdk.getFeeEstimate(
            fromAddress = bitcoinWallet.address,
            toAddress = validBtcAddress,
            amount = amount,
            coinType = CoinType.BITCOIN
        )
        println("BTC Gebühr für $amount BTC: $fee")
        println()
        
        // 5. Alle Wallets anzeigen
        println("5. Alle Wallets...")
        val allWallets = sdk.getAllWallets()
        allWallets.forEach { wallet ->
            println("${wallet.coinType.name}: ${wallet.address} (${wallet.name})")
        }
        println()
        
        // 6. Wallets nach Typ filtern
        println("6. Bitcoin Wallets...")
        val btcWallets = sdk.getWalletsByCoinType(CoinType.BITCOIN)
        btcWallets.forEach { wallet ->
            println("${wallet.name}: ${wallet.address}")
        }
        
        println("\n=== Demo abgeschlossen ===")
    }
    
    suspend fun demonstrateTransactionFlow() {
        println("\n=== Transaktions-Flow Demo ===\n")
        
        // Erstelle zwei Wallets für die Demonstration
        val senderWallet = sdk.createWallet(CoinType.BITCOIN, "Sender")
        val receiverWallet = sdk.createWallet(CoinType.BITCOIN, "Receiver")
        
        println("Sender: ${senderWallet.address}")
        println("Receiver: ${receiverWallet.address}")
        
        val amount = BigDecimal("0.0001")
        
        try {
            // 1. Gebühr schätzen
            val fee = sdk.getFeeEstimate(
                fromAddress = senderWallet.address,
                toAddress = receiverWallet.address,
                amount = amount,
                coinType = CoinType.BITCOIN
            )
            println("Geschätzte Gebühr: $fee BTC")
            
            // 2. Transaktion senden (in diesem Demo wird nur simuliert)
            println("Sende $amount BTC von ${senderWallet.address} zu ${receiverWallet.address}...")
            
            // In einer echten Implementierung würde dies die Transaktion broadcasten
            val txHash = "demo_transaction_hash_${System.currentTimeMillis()}"
            println("Transaktion gesendet: $txHash")
            
        } catch (e: Exception) {
            println("Fehler bei der Transaktion: ${e.message}")
        }
        
        println("\n=== Transaktions-Flow abgeschlossen ===")
    }
    
    suspend fun demonstrateWalletManagement() {
        println("\n=== Wallet Management Demo ===\n")
        
        // Erstelle mehrere Wallets
        val wallets = mutableListOf<Wallet>()
        
        repeat(3) { index ->
            val btcWallet = sdk.createWallet(CoinType.BITCOIN, "BTC Wallet $index")
            wallets.add(btcWallet)
        }
        
        repeat(2) { index ->
            val ethWallet = sdk.createWallet(CoinType.ETHEREUM, "ETH Wallet $index")
            wallets.add(ethWallet)
        }
        
        println("Erstelle ${wallets.size} Wallets")
        
        // Zeige alle Wallets
        println("\nAlle Wallets:")
        sdk.getAllWallets().forEachIndexed { index, wallet ->
            println("$index. ${wallet.coinType.name}: ${wallet.name} - ${wallet.address}")
        }
        
        // Zeige nur Bitcoin Wallets
        println("\nNur Bitcoin Wallets:")
        val btcWallets = sdk.getWalletsByCoinType(CoinType.BITCOIN)
        btcWallets.forEach { wallet ->
            println("- ${wallet.name}: ${wallet.address}")
        }
        
        // Zeige nur Ethereum Wallets
        println("\nNur Ethereum Wallets:")
        val ethWallets = sdk.getWalletsByCoinType(CoinType.ETHEREUM)
        ethWallets.forEach { wallet ->
            println("- ${wallet.name}: ${wallet.address}")
        }
        
        println("\n=== Wallet Management abgeschlossen ===")
    }
    
    suspend fun demonstrateAddressValidation() {
        println("\n=== Adressvalidierung Demo ===\n")
        
        val testAddresses = mapOf(
            "Bitcoin gültig" to "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
            "Bitcoin ungültig" to "invalid_btc_address",
            "Ethereum gültig" to "0x742d35Cc6634C0532925a3b8D4C9db96C4b4Db45",
            "Ethereum ungültig" to "0xinvalid",
            "Litecoin gültig" to "LSXtAvTP1hHqH6LkGfLjY3vGnQYvVpZ2bK",
            "Litecoin ungültig" to "invalid_ltc_address"
        )
        
        testAddresses.forEach { (description, address) ->
            val coinType = when {
                description.contains("Bitcoin") -> CoinType.BITCOIN
                description.contains("Ethereum") -> CoinType.ETHEREUM
                description.contains("Litecoin") -> CoinType.LITECOIN
                else -> return@forEach
            }
            
            val isValid = sdk.validateAddress(address, coinType)
            println("$description: $address -> ${if (isValid) "Gültig" else "Ungültig"}")
        }
        
        println("\n=== Adressvalidierung abgeschlossen ===")
    }
}

/**
 * Main Funktion zum Ausführen der Demo
 */
suspend fun main() {
    val app = ExampleApp()
    
    app.demonstrateBasicUsage()
    app.demonstrateTransactionFlow()
    app.demonstrateWalletManagement()
    app.demonstrateAddressValidation()
}
