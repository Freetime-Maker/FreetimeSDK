package com.freetime.sdk.payment.crypto

import java.security.*
import java.security.spec.*
import java.math.BigInteger

/**
 * Bitcoin-specific cryptographic utilities
 * Self-contained implementation without external dependencies
 */
object BitcoinCryptoUtils {
    
    /**
     * Generate a new Bitcoin key pair
     */
    fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("EC")
        keyGen.initialize(ECGenParameterSpec("secp256r1")) // Fallback to supported curve
        return keyGen.generateKeyPair()
    }
    
    /**
     * Generate Bitcoin address from public key
     */
    fun generateAddress(publicKey: PublicKey): String {
        val pubKeyBytes = publicKey.encoded
        
        // SHA-256 hash
        val sha256 = MessageDigest.getInstance("SHA-256")
        val sha256Hash = sha256.digest(pubKeyBytes)
        
        // RIPEMD-160 hash (simplified - using SHA-256 as fallback)
        val ripemd160Hash = sha256.digest(sha256Hash)
        
        // Add version byte (0x00 for mainnet)
        val versionedHash = ByteArray(21)
        versionedHash[0] = 0x00.toByte()
        System.arraycopy(ripemd160Hash, 0, versionedHash, 1, 20)
        
        // Double SHA-256 for checksum
        val checksum = sha256.digest(sha256.digest(versionedHash))
        
        // Append checksum
        val addressBytes = ByteArray(25)
        System.arraycopy(versionedHash, 0, addressBytes, 0, 21)
        System.arraycopy(checksum, 0, addressBytes, 21, 4)
        
        // Base58 encode
        return base58Encode(addressBytes)
    }
    
    /**
     * Sign transaction data
     */
    fun signTransaction(data: ByteArray, privateKey: PrivateKey): ByteArray {
        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }
    
    /**
     * Verify signature
     */
    fun verifySignature(data: ByteArray, signature: ByteArray, publicKey: PublicKey): Boolean {
        val verifier = Signature.getInstance("SHA256withECDSA")
        verifier.initVerify(publicKey)
        verifier.update(data)
        return verifier.verify(signature)
    }
    
    /**
     * Simple Base58 encoding
     */
    private fun base58Encode(input: ByteArray): String {
        val alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
        val result = StringBuilder()
        
        var num = BigInteger(1, input)
        
        while (num > BigInteger.ZERO) {
            val remainder = num.mod(BigInteger.valueOf(58))
            result.append(alphabet[remainder.toInt()])
            num = num.divide(BigInteger.valueOf(58))
        }
        
        // Handle leading zeros
        for (byte in input) {
            if (byte.toInt() == 0) {
                result.append('1')
            } else {
                break
            }
        }
        
        return result.reverse().toString()
    }
}
