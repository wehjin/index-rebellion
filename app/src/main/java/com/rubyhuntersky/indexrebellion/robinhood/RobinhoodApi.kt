package com.rubyhuntersky.indexrebellion.robinhood

import io.reactivex.Single
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class RobinhoodApi(private val httpClient: OkHttpClient) {

    data class Holding(
        val symbol: String,
        val shares: Double,
        val sharePrice: Double
    )

    sealed class Error(message: String?, cause: Throwable?) : Throwable(message, cause) {
        object InsufficientSession : RobinhoodApi.Error("Session is not active", null)
        class Server(message: String) : RobinhoodApi.Error("Server error: $message", null)
        class Network(cause: Throwable) : RobinhoodApi.Error("Network error", cause)
    }

    fun login(username: String, password: String): Single<String> =
        Single.create<String> { emitter ->
            val url = "$domain/api-token-auth/"
            val formBody = FormBody.Builder().add("username", username).add("password", password).build()
            val request = Request.Builder().post(formBody).url(url).addHeader("Accept", APPLICATION_JSON).build()
            val result = try {
                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    Result.success(response.body().toString())
                } else {
                    Result.failure(Error.Server(response.message()))
                }
            } catch (tr: Throwable) {
                Result.failure<String>(Error.Network(tr))
            }
            result.onFailure(emitter::onError)
            result.onSuccess(emitter::onSuccess)
        }

    fun holdings(): Single<String> = Single.defer {
        session.let {
            if (it is Session.Active) {
                Single.create<String> { emitter ->
                    val token = it.token
                    val url = "$domain/accounts/${it.accountId}/positions/?nonzero=true"
                    val request = Request.Builder().get().url(url).addHeader("Accept", APPLICATION_JSON)
                        .addHeader("Authorization", "Token $token").build()
                    val result = try {
                        val response = httpClient.newCall(request).execute()
                        if (response.isSuccessful) {
                            Result.success(response.body().toString())
                        } else {
                            Result.failure(Error.Server(response.message()))
                        }
                    } catch (tr: Throwable) {
                        Result.failure<String>(Error.Network(tr))
                    }
                    result.onFailure(emitter::onError)
                    result.onSuccess(emitter::onSuccess)
                }
            } else {
                Single.error(Error.InsufficientSession)
            }
        }
    }

    private var session: Session = Session.None()

    private sealed class Session {

        data class None(val username: String? = null) : Session()
        data class Pending(val username: String, val token: String) : Session()
        data class Active(val username: String, val token: String, val accountId: String) : Session()
    }

    companion object {
        private const val domain = "api.robinhood.com"
        private const val APPLICATION_JSON = "application/json"
    }
}