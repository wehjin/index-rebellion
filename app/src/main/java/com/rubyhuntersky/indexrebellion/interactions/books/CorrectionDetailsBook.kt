package com.rubyhuntersky.indexrebellion.interactions.books

import com.rubyhuntersky.indexrebellion.data.report.CorrectionDetails
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Book

class CorrectionDetailsBook(start: CorrectionDetails?, private val rebellionBook: RebellionBook) :
    Book<CorrectionDetails>
    by BehaviorBook(start) {

    fun delete() = rebellionBook.deleteConstituent(value.assetSymbol)
}
