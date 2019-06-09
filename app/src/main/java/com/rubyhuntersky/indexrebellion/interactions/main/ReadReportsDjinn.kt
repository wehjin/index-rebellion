package com.rubyhuntersky.indexrebellion.interactions.main

import com.rubyhuntersky.indexrebellion.data.report.RebellionReport
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionBook
import com.rubyhuntersky.interaction.core.wish.Djinn
import io.reactivex.Observable

object ReadReportsDjinn : Djinn<RebellionBook, RebellionReport> {

    override val paramsClass: Class<RebellionBook> = RebellionBook::class.java

    override fun toObservable(params: RebellionBook): Observable<RebellionReport> = params.reader.map(::RebellionReport)
}