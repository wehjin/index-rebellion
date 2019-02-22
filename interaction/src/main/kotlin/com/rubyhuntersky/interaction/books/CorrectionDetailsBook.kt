package com.rubyhuntersky.interaction.books

import com.rubyhuntersky.data.report.CorrectionDetails
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Book

class CorrectionDetailsBook(start: CorrectionDetails?, private val rebellionBook: RebellionBook) :
    Book<CorrectionDetails>
    by BehaviorBook(start) {

    fun delete() = rebellionBook.deleteConstituent(value.assetSymbol)
}
