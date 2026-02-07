package com.freetime.sdk.payment

/**
 * Supported cryptocurrency types for the payment SDK
 */
enum class CoinType(val symbol: String, val name: String, val decimalPlaces: Int) {
    BITCOIN("BTC", "Bitcoin", 8),
    ETHEREUM("ETH", "Ethereum", 18),
    LITECOIN("LTC", "Litecoin", 8)
}
