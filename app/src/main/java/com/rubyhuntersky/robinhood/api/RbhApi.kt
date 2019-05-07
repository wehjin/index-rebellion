package com.rubyhuntersky.robinhood.api

import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request

class RbhApi(private val httpClient: OkHttpClient) {

    fun login(username: String, password: String, mfa: String): Single<String> =
        loginRequest(username, password, mfa).fetchBody().map { parseLoginBody(it, username, password) }

    private fun Request.fetchBody(): Single<String> = Single.create<String> { emitter ->
        val result = try {
            val response = httpClient.newCall(this).execute()
            val result = if (response.isSuccessful) {
                val body = response.body()!!.string()
                Result.success(body)
            } else {
                val error =
                    RbhError.Server("${response.code()} ${response.body()?.string() ?: response.message()}")
                Result.failure(error)
            }
            result
        } catch (tr: Throwable) {
            Result.failure<String>(RbhError.Network(tr))
        }
        result.onFailure(emitter::onError)
        result.onSuccess(emitter::onSuccess)
    }

    fun accounts(token: String): Single<List<RbhAccountsResult>> =
        accountsRequest(token).fetchBody().map(::parseAccountsBody)

    fun positions(token: String): Single<List<RbhPositionsResult>> =
        positionsRequest(token).fetchBody().map(::parsePositionsBody)

    fun instruments(instrumentNumbers: List<String>): Single<List<RbhInstrumentsResult>> =
        instrumentsReques(instrumentNumbers).fetchBody().map(::parseInstrumentsBody)

    fun quotes(instrumentLocations: List<String>, token: String): Single<List<RobinhoodQuotesResult>> =
        quotesRequest(instrumentLocations, token).fetchBody().map(::parseQuotesBody)

    companion object {
        val SHARED = RbhApi(OkHttpClient())
    }
}