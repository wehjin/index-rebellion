package com.rubyhuntersky.indexrebellion.interactions.refreshholdings

import kotlinx.serialization.Serializable

@Serializable
data class Access(val username: String, val token: String) {

    fun withToken(newToken: String) = Access(username, newToken)
}
