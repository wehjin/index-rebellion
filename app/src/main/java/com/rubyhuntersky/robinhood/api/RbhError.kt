package com.rubyhuntersky.robinhood.api

internal sealed class RbhError(message: String?, cause: Throwable?) : Throwable(message, cause) {
    object InsufficientSession : RbhError("Session is not active", null)

    class Unauthorized(message: String) : RbhError("Unauthorized: $message", null)

    class Server(message: String) : RbhError("Server error: $message", null)

    data class RequiresMultiFactor(val username: String, val password: String) :
        RbhError("Login requires multi-factor authentication code", null)

    class Network(cause: Throwable) :
        RbhError("Network error: ${cause.localizedMessage ?: cause.javaClass.simpleName} ", cause)
}