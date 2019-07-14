package com.rubyhuntersky.interaction

import com.rubyhuntersky.interaction.core.wish.GenieParams
import com.rubyhuntersky.interaction.core.wish.Wish

interface GenieParams2<Result : Any, Params : GenieParams<Result>> : GenieParams<Result> {

    fun <Action : Any> toWish2(
        name: String,
        onResult: (Result) -> Action,
        onAction: (Throwable) -> Action
    ): Wish<Params, Action> = toWish(name, onResult, onAction)
}