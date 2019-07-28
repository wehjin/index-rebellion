package com.rubyhuntersky.interaction.precore

import com.rubyhuntersky.interaction.core.InteractionCompanion

fun <Vision, Action> InteractionCompanion<Vision, Action>.addTag(message: String): String
        where Vision : Any, Action : Any = "$groupId $message"
