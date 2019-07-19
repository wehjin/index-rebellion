package com.rubyhuntersky.interaction

import com.rubyhuntersky.interaction.core.wish.GenieParams
import com.rubyhuntersky.interaction.core.wish.Wish

interface GenieParams2<Params, Result> : GenieParams<Result> where Result : Any, Params : GenieParams<Result> {

    fun <Action : Any> toWish2(
        name: String,
        onResult: (Result) -> Action,
        onError: (Throwable) -> Action
    ): Wish<Params, Action> = toWish(name, onResult, onError)
}
