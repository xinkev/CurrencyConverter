package com.kyawhtetzaw.currency.converter

/**
 * e.g. from(JPY), to(SGD), base(USD)
 *
 * @param amount amount to convert in source currency(JPY)
 * @param rate base rate for source currency(JPY)
 * @param targetRate base rate for target currency(SGD)
 * @param from the currency(JPY) to convert the amount from
 * @param to the currency(SGD) to convert the amount into
 * @param base base currency(USD) is the exchange rate data in our database are based upon
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
