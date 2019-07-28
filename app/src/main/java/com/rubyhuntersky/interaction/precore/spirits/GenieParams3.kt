package com.rubyhuntersky.interaction.precore.spirits

import com.rubyhuntersky.interaction.core.wish.GenieParams
import com.rubyhuntersky.interaction.core.wish.Wish

interface GenieParams3<Params, Result> :
    GenieParams<Result> where Result : Any, Params : GenieParams<Result> {

    val defaultWishName: String

    fun <Action : Any> toWish(
        onResult: (Result) -> Action,
        onError: (Throwable) -> Action,
        name: String = defaultWishName
    ): Wish<Params, Action> = toWish(name, onResult, onError)
}