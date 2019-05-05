package com.rubyhuntersky.indexrebellion.interactions.cashediting

import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.cash.CashAmount
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.SubjectInteraction
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

sealed class Vision {
    object Loading : Vision()
    data class Editing(val oldCashAmount: CashAmount, val edit: String, val canSave: Boolean) : Vision()
    object Idle : Vision()
}

sealed class Action {
    object Load : Action()
    data class SetEdit(val edit: String) : Action()
    object Save : Action()
}

class CashEditing(private val rebellionBook: Book<Rebellion>) :
    SubjectInteraction<Vision, Action>(Vision.Idle, Action.Load) {

    override fun sendAction(action: Action) {
        val vision = vision
        when (action) {
            is Action.Load -> {
                composite.clear()
                setVision(Vision.Loading)
                oldCashAmounts.subscribe { oldValue ->
                    if (this.vision !is Vision.Idle) {
                        setVision(Vision.Editing(oldValue, "", false))
                    }
                }.addTo(composite)
            }
            is Action.SetEdit -> if (vision is Vision.Editing) {
                val edit = action.edit
                val newValue = edit.toDoubleOrNull()
                val canSave = newValue != null && newValue != vision.oldCashAmount.toDouble()
                setVision(Vision.Editing(vision.oldCashAmount, edit, canSave))
            }
            is Action.Save -> if (vision is Vision.Editing) {
                val newValue = vision.edit.toDoubleOrNull()
                if (newValue != null && newValue != vision.oldCashAmount.toDouble()) {
                    rebellionBook.write(rebellionBook.value.setNewInvestment(CashAmount(newValue)))
                }
                composite.clear()
                setVision(Vision.Idle)
            }
        }
    }

    private val composite = CompositeDisposable()
    private val oldCashAmounts = rebellionBook.reader.map { it.newInvestment }
}