package com.rubyhuntersky.robinhood.login

import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Access2
import com.rubyhuntersky.interaction.GenieParams2
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.wish.Genie
import io.reactivex.Single

data class WriteAccess(val access: Access2) :
    GenieParams2<WriteAccess, Unit> {

    class GENIE(private val book: Book<Access2>) :
        Genie<WriteAccess, Unit> {

        override val paramsClass: Class<WriteAccess> = WriteAccess::class.java

        override fun toSingle(params: WriteAccess): Single<Unit> =
            Single.just(Unit).doOnSubscribe { book.write(params.access) }
    }
}