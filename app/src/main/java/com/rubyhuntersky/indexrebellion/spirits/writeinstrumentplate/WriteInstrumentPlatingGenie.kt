package com.rubyhuntersky.indexrebellion.spirits.writeinstrumentplate

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.InstrumentPlating
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.wish.Genie
import io.reactivex.Single

class WriteInstrumentPlatingGenie(val book: Book<Drift>) :
    Genie<WriteInstrumentPlating, InstrumentPlating> {

    override val paramsClass: Class<WriteInstrumentPlating> = WriteInstrumentPlating::class.java

    override fun toSingle(params: WriteInstrumentPlating): Single<InstrumentPlating> =
        Single.defer {
            book.write(book.value.replace(params.instrumentPlating))
            Single.just(params.instrumentPlating)
        }
}