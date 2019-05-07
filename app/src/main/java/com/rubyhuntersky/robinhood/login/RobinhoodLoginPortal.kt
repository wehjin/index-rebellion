package com.rubyhuntersky.robinhood.login

import android.support.v4.app.FragmentActivity
import com.rubyhuntersky.indexrebellion.common.MyApplication
import com.rubyhuntersky.interaction.core.InteractionRegistry
import com.rubyhuntersky.interaction.core.Portal
import com.rubyhuntersky.robinhood.api.RbhApi

class RobinhoodLoginPortal(private val originalActivity: FragmentActivity) : Portal<Unit> {
    override fun jump(carry: Unit) {
        open()
    }

    fun open() {
        val interactionKey = MyApplication.RANDOM.nextLong()
        InteractionRegistry.addInteraction(
            key = interactionKey,
            interaction = RobinhoodLoginInteraction(RbhApi.SHARED).apply { sendAction(
                Action.Start(null)
            ) }
        )
        RobinhoodLoginDialogFragment.new(interactionKey)
            .show(originalActivity.supportFragmentManager, "RobinhoodLogin:$interactionKey")
    }
}