package com.rubyhuntersky.indexrebellion.interactions.holdings

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.spirits.readdrift.ReadDriftsDjinn
import com.rubyhuntersky.interaction.core.*
import com.rubyhuntersky.interaction.core.wish.Lamp

class HoldingsStory : Interaction<Vision, Action>
by Story(::start, ::isEnding, ::revise, TAG) {
    companion object {
        const val TAG = "HoldingsStory"

        fun addSpiritsToLamp(lamp: Lamp, driftBook: Book<Drift>) {
            with(lamp) {
                add(ReadDriftsDjinn(driftBook))
            }
        }
    }
}

sealed class Vision {
    object Idle : Vision()
    object Reading : Vision()
    data class Viewing(val drift: Drift) : Vision()
}

private fun start(): Vision = Vision.Idle
private fun isEnding(@Suppress("UNUSED_PARAMETER") maybe: Any?): Boolean = false

sealed class Action {
    object Init : Action()
    data class Load(val drift: Drift) : Action()
}

private fun revise(vision: Vision, action: Action, edge: Edge): Revision<Vision, Action> =
    when {
        vision is Vision.Idle && action is Action.Init -> Revision(
            Vision.Reading,
            ReadDriftsDjinn.wish("read", Action::Load)
        )
        vision is Vision.Reading && action is Action.Load -> Revision(
            Vision.Viewing(action.drift)
        )
        else -> error("${HoldingsStory.TAG}: Invalid revision parameters - $vision, $action")
    }
