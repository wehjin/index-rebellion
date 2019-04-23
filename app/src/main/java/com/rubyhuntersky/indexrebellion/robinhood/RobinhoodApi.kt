package com.rubyhuntersky.indexrebellion.robinhood

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import io.reactivex.Single
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class RobinhoodApi(private val httpClient: OkHttpClient) {

    data class Holding(
        val symbol: String,
        val shares: Double,
        val sharePrice: Double
    )

    sealed class Error(message: String?, cause: Throwable?) : Throwable(message, cause) {
        object InsufficientSession : RobinhoodApi.Error("Session is not active", null)

        class Server(message: String) : RobinhoodApi.Error("Server error: $message", null)

        class RequiresMfa(val username: String, val password: String) :
            RobinhoodApi.Error("Login requires multi-factor authentication code", null)

        class Network(cause: Throwable) :
            RobinhoodApi.Error("Network error: ${cause.localizedMessage ?: cause.javaClass.simpleName} ", cause)
    }

    fun login(username: String, password: String, mfa: String): Single<String> =
        Single.create<String> { emitter ->
            val url = "$schemeDomain/oauth2/token/"
            val formBody = FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("client_id", "c82SH0WZOsabOXGP2sxqcj34FxkvfnWRZBKlBjFS")
                .add("grant_type", "password")
                .add("scope", "internal")
                .apply {
                    if (mfa.isNotBlank()) {
                        add("mfa_code", mfa)
                    }
                }
                .build()
            val request = Request.Builder().post(formBody).url(url).addHeader("Accept", APPLICATION_JSON).build()
            val result = try {
                val response = httpClient.newCall(request).execute()
                val result = if (response.isSuccessful) {
                    val body = response.body()!!.string()
                    val json = Parser.default().parse(StringBuilder(body))
                    if (json is JsonObject) {
                        if (json.boolean("mfa_required") == true && json.string("mfa_type") == "app") {
                            // Deal with: "{"mfa_required":true,"mfa_type":"app"}"
                            Result.failure(Error.RequiresMfa(username, password))
                        } else {
                            val accessToken = json.string("access_token")
                            if (accessToken != null && accessToken.isNotBlank()) {
                                // Deal with: {"access_token": "...", "expires_in": 86400, "token_type": "Bearer", "scope": "internal", "refresh_token": "...", "mfa_code": "...", "backup_code": null})
                                Result.success(accessToken)
                            } else {
                                Result.success(body)
                            }
                        }
                    } else {
                        throw IllegalStateException("No JsonObject in response: $json")
                    }
                } else {
                    Result.failure(response.toServerError())
                }
                result
            } catch (tr: Throwable) {
                Result.failure<String>(Error.Network(tr))
            }
            result.onFailure(emitter::onError)
            result.onSuccess(emitter::onSuccess)
        }

    private fun Response.toServerError(): Error.Server {
        return Error.Server("${code()} ${body()?.string() ?: message()}")
    }

    fun holdings(): Single<String> = Single.defer {
        session.let {
            if (it is Session.Active) {
                Single.create<String> { emitter ->
                    val token = it.token
                    val url = "$schemeDomain/accounts/${it.accountId}/positions/?nonzero=true"
                    val request = Request.Builder().get().url(url).addHeader("Accept", APPLICATION_JSON)
                        .addHeader("Authorization", "Bearer $token").build()
                    val result = try {
                        val response = httpClient.newCall(request).execute()
                        if (response.isSuccessful) {
                            Result.success(response.body()!!.string())
                        } else {
                            val description = response.body()?.string() ?: response.message()
                            Result.failure(Error.Server("${response.code()} $description"))
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
        private const val schemeDomain = "https://api.robinhood.com"
        private const val APPLICATION_JSON = "application/json"
        val SHARED = RobinhoodApi(OkHttpClient())
    }
}