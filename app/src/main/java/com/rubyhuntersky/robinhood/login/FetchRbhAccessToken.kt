package com.rubyhuntersky.robinhood.login

import com.rubyhuntersky.robinhood.api.RbhApi

data class FetchRbhAccessToken(
    val api: RbhApi,
    val username: String,
    val password: String,
    val multifactor: String
)