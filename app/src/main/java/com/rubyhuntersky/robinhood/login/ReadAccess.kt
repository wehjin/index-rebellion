package com.rubyhuntersky.robinhood.login

import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Access2
import com.rubyhuntersky.interaction.GenieParams2
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.wish.Genie
import io.reactivex.Single

object ReadAccess :
    GenieParams2<Access2, ReadAccess> {

    class GENIE(private val book: Book<Access2>) : Genie<ReadAccess, Access2> {
        override val paramsClass: Class<ReadAccess> = ReadAccess::class.java

        override fun toSingle(params: ReadAccess): Single<Access2> = book.reader.firstOrError()
    }
}