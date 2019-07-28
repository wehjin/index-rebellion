package com.rubyhuntersky.interaction.precore.spirits

import com.rubyhuntersky.interaction.core.wish.DjinnParams
import com.rubyhuntersky.interaction.core.wish.Wish

interface DjinnParams2<Params, Result> : DjinnParams<Result> where Result : Any, Params : DjinnParams<Result> {

    val defaultWishName: String

    fun <Action : Any> toWish(
        onResult: (Result) -> Action,
        onError: (Throwable) -> Action,
        name: String = defaultWishName
    ): Wish<Params, Action> = toWish(name, onResult, onError)
}
