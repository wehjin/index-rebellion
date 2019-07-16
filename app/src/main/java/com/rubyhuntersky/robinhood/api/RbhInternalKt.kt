package com.rubyhuntersky.robinhood.api

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.rubyhuntersky.robinhood.api.results.RbhAccountsResult
import com.rubyhuntersky.robinhood.api.results.RbhInstrumentsResult
import com.rubyhuntersky.robinhood.api.results.RbhPositionsResult
import com.rubyhuntersky.robinhood.api.results.RbhQuotesResult
import okhttp3.FormBody
import okhttp3.Request
import java.net.URLEncoder

private const val HTTPS_DOMAIN = "https://api.robinhood.com"
private const val APPLICATION_JSON = "application/json"
private const val API_VERSION = "1.265.0"
private const val X_ROBINHOOD_API_VERSION = "x-robinhood-api-version"
private const val AUTHORIZATION = "authorization"


internal fun loginRequest(username: String, password: String, mfa: String, deviceToken: String): Request {
    val formBody = FormBody.Builder()
        .add("client_id", "c82SH0WZOsabOXGP2sxqcj34FxkvfnWRZBKlBjFS")
        .add("device_token", deviceToken)
        .add("expires_in", "86400")
        .add("grant_type", "password")
        .add("username", username)
        .add("password", password)
        .add("scope", "internal")
        .apply {
            if (mfa.isNotBlank()) {
                add("mfa_code", mfa)
            }
        }
        .build()

    return apiRequestBuilder().post(formBody)
        .url("$HTTPS_DOMAIN/oauth2/token/")
        .addHeader("Accept", APPLICATION_JSON)
        .build()
}

private fun apiRequestBuilder(): Request.Builder = Request.Builder().addHeader(X_ROBINHOOD_API_VERSION, API_VERSION)

internal fun parseLoginBody(body: String, username: String, password: String): String = parseBody(body) { json ->
    if (json.boolean("mfa_required") == true && json.string("mfa_type") == "app") {
        // Deal with: "{"mfa_required":true,"mfa_type":"app"}"
        throw RbhError.RequiresMultiFactor(username, password)
    } else {
        val accessToken = json.string("access_token")
        if (accessToken != null && accessToken.isNotBlank()) {
            accessToken
        } else {
            throw RbhError.Server("No token in body: $body")
        }
    }
}

private fun <T> parseBody(body: String, toFinalResult: (JsonObject) -> T): T {
    val json = Parser.default().parse(StringBuilder(body))
    if (json is JsonObject) {
        return toFinalResult.invoke(json)
    } else {
        throw IllegalStateException("No JsonObject in response: $json")
    }
}

internal fun accountsRequest(token: String): Request =
    bearerRequestBuilder(token).get().url("$HTTPS_DOMAIN/accounts/").build()

private fun bearerRequestBuilder(token: String): Request.Builder =
    apiRequestBuilder().addHeader(AUTHORIZATION, "Bearer $token")


internal fun parseAccountsBody(body: String): List<RbhAccountsResult> {
    return parseResultsBody(body) { result ->
        with(result) {
            RbhAccountsResult(
                accountNumber = stringThrow("account_number"),
                cash = stringThrow("cash").toDouble(),
                positionsLocation = stringThrow("positions"),
                portfolioLocation = stringThrow("portfolio"),
                accountLocation = stringThrow("url"),
                userLocation = stringThrow("user"),
                json = toJsonString()
            )
        }
    }
}


private fun <T> parseResultsBody(body: String, toFinalResult: (JsonObject) -> T): List<T> =
    parseBody(body) { it.array<JsonObject>("results")!!.map(toFinalResult) }

internal fun positionsRequest(token: String): Request {
    return bearerRequestBuilder(token).get()
        .url("$HTTPS_DOMAIN/positions/?nonzero=true")
        .build()
}

internal fun parsePositionsBody(body: String): List<RbhPositionsResult> {
    return parseResultsBody(body) { result ->
        with(result) {
            RbhPositionsResult(
                quantity = stringThrow("quantity").toDouble(),
                instrumentLocation = stringThrow("instrument"),
                averagePrice = stringThrow("average_buy_price").toDouble(),
                json = toJsonString()
            )
        }
    }
}

internal fun instrumentsReques(instrumentNumbers: List<String>): Request {
    return apiRequestBuilder().get()
        .url("$HTTPS_DOMAIN/instruments/?ids=${instrumentNumbers.joinToString("%2C")}")
        .build()
}

internal fun parseInstrumentsBody(body: String): List<RbhInstrumentsResult> {
    return parseResultsBody(body) {
        with(it) {
            RbhInstrumentsResult(
                fundamentalsLocation = stringThrow("fundamentals"),
                id = stringThrow("id"),
                marketLocation = stringThrow("market"),
                quoteLocation = stringThrow("quote"),
                name = stringThrow("name"),
                simpleName = stringThrow("simple_name"),
                symbol = stringThrow("symbol"),
                type = stringThrow("type"),
                json = toJsonString()
            )
        }
    }
}

internal fun quotesRequest(instrumentLocations: List<String>, token: String): Request {
    val instruments = instrumentLocations.map { URLEncoder.encode(it, "utf-8") }.joinToString("%2C")
    return bearerRequestBuilder(token).get()
        .url("$HTTPS_DOMAIN/marketdata/quotes/?instruments=$instruments")
        .build()
}


internal fun parseQuotesBody(body: String): List<RbhQuotesResult> {
    return parseResultsBody(body) {
        with(it) {
            RbhQuotesResult(
                symbol = stringThrow("symbol"),
                lastPrice = stringThrow("last_trade_price").toDouble(),
                updatedAt = stringThrow("updated_at"),
                instrumentLocation = stringThrow("instrument"),
                json = toJsonString()
            )
        }
    }
}

private fun JsonObject.stringThrow(fieldName: String): String =
    string(fieldName) ?: throw Exception("No '$fieldName' in $this")

