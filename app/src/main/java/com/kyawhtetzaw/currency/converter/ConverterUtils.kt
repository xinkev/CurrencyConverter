package com.kyawhtetzaw.currency.converter

/**
 *
 * @param amount amount to convert
 * @param rate exchange rate for the currency that is being converted [from]
 * @param targetRate exchange rate for the currency that is being converted in[to]
 * @param from the currency to convert the amount from
 * @param to the currency to convert the amount into
 * @param base the currency which is used to get the exchange rate data from the API. Currently, it's set to hard coded as USD. You can change it in Config.kt
 */
fun convertRate(
    amount: Double,
    rate: Double, // what if source==base?
    targetRate: Double,
    from: String,
    to: String,
    base: String,
): Double {
    if (from.equals(to, true)) return amount

    return if (base.equals(to, true)) {
        targetRate * amount
    } else {
        (amount / rate) * targetRate
    }
}
