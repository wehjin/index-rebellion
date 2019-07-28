package com.rubyhuntersky.indexrebellion.spirits.genies

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Division
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.wish.Genie
import com.rubyhuntersky.interaction.precore.spirits.GenieParams3
import io.reactivex.Single

data class WriteDivision(val division: Division) : GenieParams3<WriteDivision, Unit> {

    override val defaultWishName: String = "write-division"

    class GENIE(val book: Book<Drift>) : Genie<WriteDivision, Unit> {

        override val paramsClass: Class<WriteDivision> = WriteDivision::class.java

        override fun toSingle(params: WriteDivision): Single<Unit> =
            Single.fromCallable { book.write(book.value.replace(params.division)) }
    }
}