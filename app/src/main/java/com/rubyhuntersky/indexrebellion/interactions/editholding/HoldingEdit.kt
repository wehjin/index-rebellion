package com.rubyhuntersky.indexrebellion.interactions.editholding

import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.interaction.stringedit.Ancient
import com.rubyhuntersky.interaction.stringedit.Novel
import com.rubyhuntersky.interaction.stringedit.Seed
import com.rubyhuntersky.interaction.stringedit.StringEdit
import java.math.BigDecimal

data class HoldingEdit(
    val symbolEdit: StringEdit<String> = StringEdit("Symbol"),
    val sizeEdit: StringEdit<BigDecimal> = StringEdit("Shares", seed = Seed(BigDecimal.ZERO, true)),
    val priceEdit: StringEdit<CashAmount> = StringEdit("Price")
) {

    fun setSymbolAncient(symbol: String?): HoldingEdit =
        copy(symbolEdit = symbolEdit.setAncient(symbol?.let(::Ancient)))

    fun setSymbolNovel(novel: Novel<String>?): HoldingEdit =
        copy(symbolEdit = symbolEdit.setNovel(novel))

    fun setSizeAncient(size: BigDecimal?): HoldingEdit =
        copy(sizeEdit = sizeEdit.setAncient(size?.let(::Ancient)))

    fun setSizeNovel(novel: Novel<BigDecimal>?): HoldingEdit =
        copy(sizeEdit = sizeEdit.setNovel(novel))
}