package com.rubyhuntersky.robinhood.login

import com.rubyhuntersky.interaction.core.wish.Genie
import io.reactivex.Completable
import io.reactivex.Single

object SaveAccessTokenGenie :
    Genie<SaveAccessToken, Unit> {

    override val paramsClass: Class<SaveAccessToken> = SaveAccessToken::class.java

    override fun toSingle(params: SaveAccessToken): Single<Unit> {
        val accessBook = params.accessBook
        val newToken = accessBook.value.withToken(params.token)
        return Completable.create { accessBook.write(newToken) }.toSingleDefault(Unit)
    }

}