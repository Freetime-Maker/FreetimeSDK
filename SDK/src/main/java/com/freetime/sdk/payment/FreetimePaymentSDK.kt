package com.freetime.sdk.payment

import java.math.BigDecimal

/**
 * Main SDK class for multi-cryptocurrency payment processing
 * 
 * This is a completely self-contained, open-source SDK that doesn't depend on any external services.
 * All cryptographic operations are performed locally.
 */
class FreetimePaymentSDK {
    
    private val walletManager = WalletManager()
    private val paymentProviders = mutableMapOf<CoinType, PaymentInterface>()
    
    init {
        // Initialize payment providers for each supported coin
        initializeProviders()
    }
    
    /**
     * Initialize payment providers for all supported cryptocurrencies
     */
    private fun initializeProviders() {
        paymentProviders[CoinType.BITCOIN] = BitcoinPaymentProvider()
        paymentProviders[CoinType.ETHEREUM] = EthereumPaymentProvider()
        paymentProviders[CoinType.LITECOIN] = LitecoinPaymentProvider()
    }
    
    /**
     * Create a new wallet for the specified cryptocurrency
     */
    suspend fun createWallet(coinType: CoinType, name: String? = null): Wallet {
        val provider = paymentProviders[coinType] 
            ?: throw UnsupportedOperationException("Unsupported coin type: $coinType")
        
        val address = provider.generateAddress(coinType)
        val keyPair = generateKeyPair(coinType)
        
        val wallet = Wallet(address, coinType, keyPair, name)
        walletManager.addWallet(wallet)
        
        return wallet
    }
    
    /**
     * Get wallet balance
     */
    suspend fun getBalance(address: String): BigDecimal {
        val wallet = walletManager.getWallet(address) 
            ?: throw IllegalArgumentException("Wallet not found: $address")
        
        val provider = paymentProviders[wallet.coinType]
            ?: throw UnsupportedOperationException("No provider for ${wallet.coinType}")
        
        return provider.getBalance(address, wallet.coinType)
    }
    
    /**
     * Send cryptocurrency from one wallet to another
     */
    suspend fun send(
        fromAddress: String,
        toAddress: String,
        amount: BigDecimal,
        coinType: CoinType
    ): String {
        val provider = paymentProviders[coinType]
            ?: throw UnsupportedOperationException("Unsupported coin type: $coinType")
        
        if (!provider.validateAddress(toAddress, coinType)) {
            throw IllegalArgumentException("Invalid recipient address")
        }
        
        val transaction = provider.createTransaction(fromAddress, toAddress, amount, coinType)
        return provider.broadcastTransaction(transaction)
    }
    
    /**
     * Get transaction fee estimate
     */
    suspend fun getFeeEstimate(
        fromAddress: String,
        toAddress: String,
        amount: BigDecimal,
        coinType: CoinType
    ): BigDecimal {
        val provider = paymentProviders[coinType]
            ?: throw UnsupportedOperationException("Unsupported coin type: $coinType")
        
        return provider.getFeeEstimate(fromAddress, toAddress, amount, coinType)
    }
    
    /**
     * Get all wallets
     */
    fun getAllWallets(): List<Wallet> {
        return walletManager.getAllWallets()
    }
    
    /**
     * Get wallets by coin type
     */
    fun getWalletsByCoinType(coinType: CoinType): List<Wallet> {
        return walletManager.getWalletsByCoinType(coinType)
    }
    
    /**
     * Generate key pair for the specified cryptocurrency
     */
    private fun generateKeyPair(coinType: CoinType): java.security.KeyPair {
        return when (coinType) {
            CoinType.BITCOIN -> BitcoinCryptoUtils.generateKeyPair()
            CoinType.ETHEREUM -> EthereumCryptoUtils.generateKeyPair()
            CoinType.LITECOIN -> LitecoinCryptoUtils.generateKeyPair()
        }
    }
    
    /**
     * Validate address format
     */
    fun validateAddress(address: String, coinType: CoinType): Boolean {
        val provider = paymentProviders[coinType]
            ?: return false
        
        return provider.validateAddress(address, coinType)
    }
}
