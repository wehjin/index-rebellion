package com.rubyhuntersky.robinhood.login

import com.rubyhuntersky.indexrebellion.interactions.refreshholdings.Access
import com.rubyhuntersky.interaction.core.Book

data class SaveAccessToken(
    val accessBook: Book<Access>,
    val token: String
)