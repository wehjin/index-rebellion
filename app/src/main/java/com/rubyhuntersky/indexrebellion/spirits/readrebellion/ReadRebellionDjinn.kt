package com.rubyhuntersky.indexrebellion.spirits.readrebellion

import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.wish.Djinn
import io.reactivex.Observable

class ReadRebellionDjinn(private val book: Book<Rebellion>) : Djinn<ReadRebellionParams, Rebellion> {
    override val paramsClass: Class<ReadRebellionParams> = ReadRebellionParams::class.java

    override fun toObservable(params: ReadRebellionParams): Observable<Rebellion> = book.reader
}
