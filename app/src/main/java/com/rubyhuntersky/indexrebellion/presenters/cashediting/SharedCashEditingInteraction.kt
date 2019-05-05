package com.rubyhuntersky.indexrebellion.presenters.cashediting

import com.rubyhuntersky.indexrebellion.books.SharedRebellionBook
import com.rubyhuntersky.indexrebellion.interactions.cashediting.Action
import com.rubyhuntersky.indexrebellion.interactions.cashediting.CashEditing
import com.rubyhuntersky.indexrebellion.interactions.cashediting.Vision
import com.rubyhuntersky.interaction.core.Interaction

object SharedCashEditingInteraction : Interaction<Vision, Action>
by CashEditing(SharedRebellionBook)