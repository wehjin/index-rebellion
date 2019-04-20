package com.rubyhuntersky.indexrebellion.robinhood

import android.app.Activity
import com.rubyhuntersky.indexrebellion.common.MyApplication
import com.rubyhuntersky.interaction.core.InteractionRegistry
import com.rubyhuntersky.interaction.core.Portal

class RobinhoodLoginPortal(private val originalActivity: Activity) : Portal<Unit> {
    override fun jump(carry: Unit) {
        open()
    }

    fun open() {
        val interactionKey = MyApplication.RANDOM.nextLong()
        val interaction = RobinhoodLoginInteraction(RobinhoodApi.SHARED).apply { sendAction(Action.Start(null)) }
        InteractionRegistry.addInteraction(interactionKey, interaction)
        InteractionRegistry.dropInteraction(interactionKey)
    }
}