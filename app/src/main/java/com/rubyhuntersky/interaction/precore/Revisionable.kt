package com.rubyhuntersky.interaction.precore

import com.rubyhuntersky.interaction.core.Revision
import com.rubyhuntersky.interaction.core.wish.Wish

interface Revisionable

fun <V : Revisionable, A : Any> V.revision(): Revision<V, A> = Revision(this)

infix fun <V : Revisionable, A : Any, Params : Any> V.and(wish: Wish<Params, A>) =
    Revision(this, wish)

infix fun <V : Revisionable, A : Any, Params : Any> Revision<V, A>.and(wish: Wish<Params, A>) =
    copy(wishes = wishes + wish)
