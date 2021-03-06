package com.rubyhuntersky.indexrebellion.spirits.djinns.readdrift

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.interaction.precore.spirits.DjinnParams2

object ReadDrifts : DjinnParams2<ReadDrifts, Drift> {

    override val defaultWishName: String = ReadDrifts::class.java.simpleName
    fun <A : Any> unwish() = Wish.none<A>(defaultWishName)
}
