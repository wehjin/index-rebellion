package com.rubyhuntersky.indexrebellion.spirits.genies

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.wish.Genie
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.interaction.precore.spirits.GenieParams3
import io.reactivex.Single

data class RemoveSpecificHolding(val specificHolding: SpecificHolding) :
    GenieParams3<RemoveSpecificHolding, Drift> {

    override val defaultWishName
        get() = DEFAULT_WISH_NAME

    class GENIE(private val book: Book<Drift>) : Genie<RemoveSpecificHolding, Drift> {

        override val paramsClass: Class<RemoveSpecificHolding> = RemoveSpecificHolding::class.java

        override fun toSingle(params: RemoveSpecificHolding): Single<Drift> {
            return Single.fromCallable {
                book.value.removeSpecificHolding(params.specificHolding).also {
                    book.write(it)
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_WISH_NAME: String = "remove-specific-holding"
        fun <A : Any> unwish() = Wish.none<A>(DEFAULT_WISH_NAME)
    }
}
