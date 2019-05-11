package com.rubyhuntersky.indexrebellion.common

import com.rubyhuntersky.interaction.core.Interaction
import io.reactivex.disposables.Disposable

class PendingInteractions {
    private val endings = mutableMapOf<Long, Disposable>()

    fun <V : Any, A : Any> follow(interaction: Interaction<V, A>, whenEnded: (V) -> Unit) {
        val id = interaction.hashCode().toLong()
        endings[id] = interaction.tailVision.subscribe { ending ->
            endings.remove(id)?.dispose()
            whenEnded(ending)
        }
    }

    fun dispose() {
        endings.values.forEach { it.dispose() }
        endings.clear()
    }
}