package com.rubyhuntersky.indexrebellion.interactions.correctiondetails

import com.rubyhuntersky.indexrebellion.data.report.CorrectionDetails
import com.rubyhuntersky.indexrebellion.interactions.books.CorrectionDetailsBook
import com.rubyhuntersky.interaction.core.wish.Djinn
import io.reactivex.Observable

object ReadCorrectionDetailsDjinn : Djinn<CorrectionDetailsBook, CorrectionDetails> {

    override val paramsClass: Class<CorrectionDetailsBook> = CorrectionDetailsBook::class.java

    override fun toObservable(params: CorrectionDetailsBook): Observable<CorrectionDetails> = params.reader
}