package com.rubyhuntersky.indexrebellion.spirits.genies

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.instrument.InstrumentId
import com.rubyhuntersky.interaction.precore.spirits.GenieParams2
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.wish.Genie
import io.reactivex.Single

data class DeleteGeneralHolding(val instrumentId: InstrumentId) :
    GenieParams2<DeleteGeneralHolding, Unit> {

    class GENIE(private val book: Book<Drift>) :
        Genie<DeleteGeneralHolding, Unit> {

        override val paramsClass: Class<DeleteGeneralHolding> = DeleteGeneralHolding::class.java

        override fun toSingle(params: DeleteGeneralHolding): Single<Unit> {
            return Single.fromCallable {
                val new = book.value.deleteHoldings(params.instrumentId)
                book.write(new)
                Unit
            }
        }
    }
}