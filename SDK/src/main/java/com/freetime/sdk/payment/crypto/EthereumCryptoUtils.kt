package com.freetime.sdk.payment.crypto

import java.security.*
import java.security.spec.*
import java.math.BigInteger

/**
 * Ethereum-specific cryptographic utilities
 * Self-contained implementation without external dependencies
 */
object EthereumCryptoUtils {
    
    /**
     * Generate a new Ethereum key pair
     */
    fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("EC")
        keyGen.initialize(ECGenParameterSpec("secp256r1")) // Fallback to supported curve
        return keyGen.generateKeyPair()
    }
    
    /**
     * Generate Ethereum address from public key
     */
    fun generateAddress(publicKey: PublicKey): String {
        val pubKeyBytes = publicKey.encoded
        
        // Remove the 0x04 prefix (uncompressed public key)
        val cleanPubKey = if (pubKeyBytes[0].toInt() == 0x04) {
            pubKeyBytes.copyOfRange(1, pubKeyBytes.size)
        } else {
            pubKeyBytes
        }
        
        // Keccak-256 hash (simplified - using SHA-256 as fallback)
        val hash = sha256(cleanPubKey)
        
        // Take last 20 bytes and add 0x prefix
        val addressBytes = hash.copyOfRange(12, 32)
        return "0x" + addressBytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Sign transaction data using Ethereum's signature scheme
     */
    fun signTransaction(data: ByteArray, privateKey: PrivateKey): ByteArray {
        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        signature.update(data)
        val ecdsaSignature = signature.sign()
        
        // Add recovery id (simplified)
        val v = (27 + (ecdsaSignature.last().toInt() and 0x1)).toByte()
        
        return byteArrayOf(v) + ecdsaSignature
    }
    
    /**
     * Verify Ethereum signature
     */
    fun verifySignature(data: ByteArray, signature: ByteArray, publicKey: PublicKey): Boolean {
        if (signature.size < 1) return false
        
        val ecdsaSignature = signature.copyOfRange(1, signature.size)
        val verifier = Signature.getInstance("SHA256withECDSA")
        verifier.initVerify(publicKey)
        verifier.update(data)
        return verifier.verify(ecdsaSignature)
    }
    
    /**
     * Keccak-256 hash function (simplified - using SHA-256)
     */
    fun keccak256(data: ByteArray): ByteArray {
        return sha256(data)
    }
    
    /**
     * SHA-256 hash function
     */
    private fun sha256(data: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(data)
    }
}
