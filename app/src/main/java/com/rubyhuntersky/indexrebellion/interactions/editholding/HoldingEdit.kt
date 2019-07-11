package com.rubyhuntersky.indexrebellion.interactions.editholding

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.MAIN_ACCOUNT
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentType
import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.interaction.stringedit.Ancient
import com.rubyhuntersky.interaction.stringedit.Novel
import com.rubyhuntersky.interaction.stringedit.Seed
import com.rubyhuntersky.interaction.stringedit.StringEdit
import java.math.BigDecimal
import java.util.*

data class HoldingEdit(
    val ancient: SpecificHolding?,
    val drift: Drift? = null,
    val symbolEdit: StringEdit<String> = StringEdit(
        label = "Symbol",
        ancient = ancient?.instrumentId?.symbol?.let(::Ancient)
    ),
    val sizeEdit: StringEdit<BigDecimal> = StringEdit(
        label = "Shares",
        seed = Seed(BigDecimal.ZERO, true),
        ancient = ancient?.size?.let(::Ancient)
    ),
    val priceEdit: StringEdit<CashAmount> = StringEdit(
        label = "Price",
        ancient = symbolEdit.toSharePrice(drift)?.let(::Ancient)
    )
) {
    val writableResult: Pair<Drift, SpecificHolding>?
        get() {
            val drift = drift
            val symbolValue = symbolEdit.writableValue
            val sizeValue = sizeEdit.writableValue
            val priceValue = priceEdit.validValue
            return if (drift != null && symbolValue != null && sizeValue != null && priceValue != null) {
                val instrumentId = symbolValue.toInstrumentId()
                val now = Date()
                val novel = SpecificHolding(instrumentId, Custodian.Wallet, MAIN_ACCOUNT, sizeValue, now)
                if (novel.equalsExcludingDate(ancient)) null
                else {
                    val sample = instrumentId.toSample(drift)?.setPrice(priceValue, now)
                        ?: InstrumentSample(instrumentId, instrumentId.symbol, priceValue, priceValue, now)
                    val novelDrift = drift.replaceSample(sample)
                    Pair(novelDrift.replaceHolding(novel), novel)
                }
            } else null
        }

    fun setSymbolNovel(novel: Novel<String>?): HoldingEdit {
        val symbolEdit = symbolEdit.setNovel(novel)
        return copy(
            symbolEdit = symbolEdit,
            priceEdit = priceEdit.setAncient(symbolEdit.toSharePrice(drift))
        )
    }

    fun setSizeNovel(novel: Novel<BigDecimal>?): HoldingEdit = copy(
        sizeEdit = sizeEdit.setNovel(novel)
    )

    fun setPriceNovel(novel: Novel<CashAmount>?): HoldingEdit = copy(
        priceEdit = priceEdit.setNovel(novel)
    )

    fun setDrift(drift: Drift): HoldingEdit = copy(
        drift = drift,
        priceEdit = priceEdit.setAncient(symbolEdit.toSharePrice(drift))
    )

    companion object {
        private fun StringEdit<String>.toSharePrice(drift: Drift?) = validValue?.toInstrumentId()?.toSharePrice(drift)
        private fun InstrumentId.toSharePrice(drift: Drift?) = toSample(drift)?.let(InstrumentSample::sharePrice)
        private fun InstrumentId.toSample(drift: Drift?) = drift?.findSample(this)
        private fun String.toInstrumentId() = InstrumentId(trim().toUpperCase(), InstrumentType.StockExchange)
    }
}