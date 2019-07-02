package com.rubyhuntersky.indexrebellion.spirits.readdrift

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.interactions.viewdrift.Action
import com.rubyhuntersky.indexrebellion.spirits.common.ReadBookDjinn
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.wish.Djinn
import com.rubyhuntersky.interaction.core.wish.Wish
import com.rubyhuntersky.interaction.core.wish.WishKind

class ReadDriftsDjinn(book: Book<Drift>) : Djinn<ReadDrifts, Drift> by ReadBookDjinn(book, ReadDrifts::class.java) {
    companion object {
        fun wish(name: String, driftToAction: (Drift) -> Action): Wish<ReadDrifts, Action> {
            return Wish(name, ReadDrifts, WishKind.Many(driftToAction, { error("ReadDrift: $it") }))
        }
    }
}
