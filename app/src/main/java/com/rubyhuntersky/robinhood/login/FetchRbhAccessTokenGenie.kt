package com.rubyhuntersky.robinhood.login

import com.rubyhuntersky.interaction.core.wish.Genie
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object FetchRbhAccessTokenGenie :
    Genie<FetchRbhAccessToken, String> {

    override val paramsClass: Class<FetchRbhAccessToken> = FetchRbhAccessToken::class.java

    override fun toSingle(params: FetchRbhAccessToken): Single<String> {
        val (api, deviceToken, username, password, secondFactor) = params
        return api.login(username, password, secondFactor, deviceToken)
            .subscribeOn(Schedulers.io())
    }
}