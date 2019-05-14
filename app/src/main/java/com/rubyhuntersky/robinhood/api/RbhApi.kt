package com.rubyhuntersky.robinhood.api

import com.rubyhuntersky.robinhood.api.results.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request

class RbhApi(private val httpClient: OkHttpClient) {

    fun login(username: String, password: String, mfa: String): Single<String> =
        loginRequest(username, password, mfa).fetchBody().map { parseLoginBody(it, username, password) }

    private fun Request.fetchBody(): Single<String> {
        return Single.create<String> { emitter ->
            val result = try {
                val response = httpClient.newCall(this).execute()
                val result = if (response.isSuccessful) {
                    val body = response.body()!!.string()
                    Result.success(body)
                } else {
                    val code = response.code()
                    val responseText = response.body()?.string() ?: response.message()
                    val error = when (code) {
                        401 -> RbhError.Unauthorized(responseText)
                        else -> RbhError.Server("$code $responseText")
                    }
                    Result.failure(error)
                }
                result
            } catch (tr: Throwable) {
                Result.failure<String>(RbhError.Network(tr))
            }
            result.onFailure(emitter::onError)
            result.onSuccess(emitter::onSuccess)
        }.subscribeOn(Schedulers.io())
    }

    fun holdings(token: String): Single<RbhHoldingsResult> {
        return positions(token)
            .map(::RbhHoldingsResult)
            .flatMap { result ->
                val instrumentLocations = result.positions.map { it.instrumentLocation }
                quotes(instrumentLocations, token)
                    .map { result.quotes = it; result }
            }
    }

    fun accounts(token: String): Single<List<RbhAccountsResult>> =
        accountsRequest(token).fetchBody().map(::parseAccountsBody)

    fun positions(token: String): Single<List<RbhPositionsResult>> =
        positionsRequest(token).fetchBody().map(::parsePositionsBody)

    fun instruments(instrumentNumbers: List<String>): Single<List<RbhInstrumentsResult>> =
        instrumentsReques(instrumentNumbers).fetchBody().map(::parseInstrumentsBody)

    fun quotes(instrumentLocations: List<String>, token: String): Single<List<RbhQuotesResult>> =
        quotesRequest(instrumentLocations, token).fetchBody().map(::parseQuotesBody)

    companion object {
        val SHARED = RbhApi(OkHttpClient())
    }
}