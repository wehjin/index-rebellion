package com.rubyhuntersky.indexrebellion.spirits.common

import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.wish.Djinn
import io.reactivex.Observable

class ReadBookDjinn<Params : Any, Result : Any>(
    private val book: Book<Result>,
    override val paramsClass: Class<Params>
) : Djinn<Params, Result> {
    override fun toObservable(params: Params): Observable<Result> = book.reader
}