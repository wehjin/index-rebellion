package com.rubyhuntersky.indexrebellion.data.cash

fun sum(a: CashEquivalent, b: CashEquivalent): CashEquivalent = when (a) {
    is CashEquivalent.Unknown -> CashEquivalent.Unknown()
    is CashEquivalent.Amount -> when (b) {
        is CashEquivalent.Unknown -> CashEquivalent.Unknown()
        is CashEquivalent.Amount -> CashEquivalent.Amount(
            a.cashAmount + b.cashAmount
        )
    }
}
