package com.rubyhuntersky.indexrebellion.interactions.refreshholdings

import com.rubyhuntersky.robinhood.login.RbhDeviceTokenBuilder
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class Access2(
    val username: String = "",
    val token: String = "",
    val rbhDeviceToken: String = RbhDeviceTokenBuilder.build(Random)
)
