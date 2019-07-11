package com.rubyhuntersky.indexrebellion.spirits.writedrift

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.wish.Genie
import io.reactivex.Single

class WriteDriftGenie(val book: Book<Drift>) : Genie<WriteDrift, Drift> {
    override val paramsClass: Class<WriteDrift> = WriteDrift::class.java
    override fun toSingle(params: WriteDrift): Single<Drift> = Single.fromCallable { params.drift.also(book::write) }
}