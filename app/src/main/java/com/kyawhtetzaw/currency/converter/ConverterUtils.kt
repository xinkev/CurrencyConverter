package com.kyawhtetzaw.currency.converter

/**
 *
 * @param amount amount to convert
 * @param fromRate exchange rate for the currency that is being converted [from]
 * @param targetRate exchange rate for the currency that is being converted in[to]
 * @param from the currency to convert the amount from
 * @param to the currency to convert the amount into
 * @param base the currency which is used to get the exchange rate data from the API. Currently, it's set to hard coded as USD. You can change it in Config.kt
 */
fun convertRate(
    amount: Double,
    fromRate: Double,
    targetRate: Double,
    from: String,
    to: String,
    base: String,
): CurrencyConversionResult {
    val convertedAmount = if (from.equals(to, true)) {
        // same currency. so, no need to convert
        CurrencyConversionResult(
            symbol = to,
            rate = 1.0,
            amount = amount
        )
    } else {
        if (base.equals(to, true)) {
            CurrencyConversionResult(
                symbol = to,
                rate = targetRate,
                amount = targetRate * amount
            )
        } else {
            val rate = targetRate / fromRate
            CurrencyConversionResult(
                symbol = to,
                rate = rate,
                amount = rate * amount
            )
        }
    }

    return convertedAmount
}


data class CurrencyConversionResult(
    val symbol: String,
    val rate: Double,
    val amount: Double
)