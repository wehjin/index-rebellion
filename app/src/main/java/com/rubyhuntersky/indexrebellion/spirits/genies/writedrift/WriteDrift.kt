package com.rubyhuntersky.indexrebellion.spirits.genies.writedrift

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.interaction.precore.spirits.GenieParams2
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.wish.Genie
import io.reactivex.Single

data class WriteDrift(val drift: Drift) : GenieParams2<WriteDrift, Drift> {

    class GENIE(val book: Book<Drift>) : Genie<WriteDrift, Drift> {
        override val paramsClass: Class<WriteDrift> = WriteDrift::class.java

        override fun toSingle(params: WriteDrift): Single<Drift> {
            return Single.fromCallable { params.drift.also(book::write) }
        }
    }
}