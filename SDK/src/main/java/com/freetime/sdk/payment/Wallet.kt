package com.freetime.sdk.payment

import java.math.BigDecimal
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey

/**
 * Represents a cryptocurrency wallet
 */
data class Wallet(
    val address: String,
    val coinType: CoinType,
    val keyPair: KeyPair,
    val name: String? = null
) {
    val publicKey: PublicKey get() = keyPair.public
    val privateKey: PrivateKey get() = keyPair.private
    
    /**
     * Get the current balance of this wallet
     */
    suspend fun getBalance(paymentProvider: PaymentInterface): BigDecimal {
        return paymentProvider.getBalance(address, coinType)
    }
    
    /**
     * Send cryptocurrency to another address
     */
    suspend fun send(
        toAddress: String,
        amount: BigDecimal,
        paymentProvider: PaymentInterface
    ): Transaction {
        return paymentProvider.createTransaction(address, toAddress, amount, coinType)
    }
}

/**
 * Wallet manager for handling multiple wallets
 */
class WalletManager {
    private val wallets = mutableMapOf<String, Wallet>()
    
    /**
     * Add a new wallet to the manager
     */
    fun addWallet(wallet: Wallet) {
        wallets[wallet.address] = wallet
    }
    
    /**
     * Get a wallet by address
     */
    fun getWallet(address: String): Wallet? {
        return wallets[address]
    }
    
    /**
     * Get all wallets for a specific coin type
     */
    fun getWalletsByCoinType(coinType: CoinType): List<Wallet> {
        return wallets.values.filter { it.coinType == coinType }
    }
    
    /**
     * Get all wallets
     */
    fun getAllWallets(): List<Wallet> {
        return wallets.values.toList()
    }
    
    /**
     * Remove a wallet
     */
    fun removeWallet(address: String): Wallet? {
        return wallets.remove(address)
    }
}
