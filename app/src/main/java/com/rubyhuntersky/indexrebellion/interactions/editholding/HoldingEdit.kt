package com.rubyhuntersky.indexrebellion.interactions.editholding

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.MAIN_ACCOUNT
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.indexrebellion.data.techtonic.market.InstrumentSample
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Custodian
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.CustodianAccount
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.interaction.edit.Ancient
import com.rubyhuntersky.interaction.edit.Seed
import com.rubyhuntersky.interaction.edit.StringNovel
import com.rubyhuntersky.interaction.stringedit.StringEdit
import java.math.BigDecimal
import java.util.*

data class HoldingEdit(
    val type: HoldingEditType,
    val drift: Drift? = null,
    val symbolEdit: StringEdit<String> = StringEdit(
        label = "Symbol",
        seed = type.defaultSymbol?.let { Seed(it, true) },
        ancient = type.ancient?.instrumentId?.symbol?.let(::Ancient),
        enabled = type.isSymbolEditable
    ),
    val sizeEdit: StringEdit<BigDecimal> = StringEdit(
        label = "Shares",
        seed = Seed(BigDecimal.ZERO, true),
        ancient = type.ancient?.size?.let(::Ancient)
    ),
    val priceEdit: StringEdit<CashAmount> = StringEdit(
        label = "Price",
        ancient = symbolEditToAncientPrice(symbolEdit, drift, type)?.let(::Ancient),
        enabled = type.isPriceEditable
    ),
    val accountEdit: StringEdit<CustodianAccount> = StringEdit(
        label = "Account",
        ancient = Ancient(MAIN_ACCOUNT),
        enabled = true
    )
) {
    val writableResult: Pair<Drift, SpecificHolding>?
        get() {
            val drift = drift
            val symbolValue = symbolEdit.writableValue
            val sizeValue = sizeEdit.writableValue
            val priceValue = priceEdit.validValue
            return if (drift != null && symbolValue != null && sizeValue != null && priceValue != null) {
                val instrumentId = symbolValue.toInstrumentId(type)
                val now = Date()
                val holding =
                    SpecificHolding(
                        instrumentId = instrumentId,
                        custodian = Custodian.Wallet,
                        custodianAccount = accountEdit.validValue ?: MAIN_ACCOUNT,
                        size = sizeValue,
                        lastModified = now
                    )
                if (holding.equalsIgnoringModifyDate(type.ancient)) null
                else {
                    val novelDrift = drift
                        .replace(
                            instrumentId.toSample(drift)?.setPrice(priceValue, now)
                                ?: InstrumentSample(
                                    instrumentId = instrumentId,
                                    instrumentName = instrumentId.symbol,
                                    sharePrice = priceValue,
                                    macroPrice = priceValue,
                                    sampleDate = now
                                )
                        )
                        .replace(holding)
                    Pair(novelDrift, holding)
                }
            } else null
        }

    fun setSymbolNovel(novel: StringNovel<String>?): HoldingEdit {
        val symbolEdit = symbolEdit.setNovel(novel)
        return copy(
            symbolEdit = symbolEdit,
            priceEdit = priceEdit.setAncient(symbolEditToAncientPrice(symbolEdit, drift, type))
        )
    }

    fun setSizeNovel(novel: StringNovel<BigDecimal>?): HoldingEdit = copy(
        sizeEdit = sizeEdit.setNovel(novel)
    )

    fun setPriceNovel(novel: StringNovel<CashAmount>?): HoldingEdit = copy(
        priceEdit = priceEdit.setNovel(novel)
    )

    fun setAccountNovel(novel: StringNovel<CustodianAccount>?): HoldingEdit = copy(
        accountEdit = accountEdit.setNovel(novel)
    )

    fun setDrift(drift: Drift): HoldingEdit = copy(
        drift = drift,
        priceEdit = priceEdit.setAncient(symbolEditToAncientPrice(symbolEdit, drift, type))
    )

    companion object {
        private fun InstrumentId.toSharePrice(drift: Drift?) =
            toSample(drift)?.let(InstrumentSample::sharePrice)

        private fun InstrumentId.toSample(drift: Drift?) = drift?.findSample(this)

        private fun sharePriceFromSymbol(
            symbol: String,
            type: HoldingEditType,
            drift: Drift?
        ): CashAmount? =
            symbol.toInstrumentId(type).toSharePrice(drift)

        private fun String.toInstrumentId(type: HoldingEditType) =
            type.toInstrumentId(trim().toUpperCase())

        private fun symbolEditToAncientPrice(
            symbolEdit: StringEdit<String>,
            drift: Drift?,
            type: HoldingEditType
        ): CashAmount? = symbolEdit.validValue?.let { sharePriceFromSymbol(it, type, drift) }
    }
}