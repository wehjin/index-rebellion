package com.rubyhuntersky.indexrebellion.data.techtonic

import com.rubyhuntersky.indexrebellion.data.techtonic.market.Market
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.*
import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plating
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.CustodianAccount
import com.rubyhuntersky.indexrebellion.data.techtonic.vault.Vault

val DEFAULT_VAULT = Vault(setOf(ZERO_DOLLAR_HOLDING))

val DEFAULT_MARKET = Market(setOf(UNIT_DOLLAR_SAMPLE))

val DEFAULT_PLAN = Plan(
    PortfolioPlan(7.toWeight(), 93.toWeight()),
    CommodityPlan(2.toWeight(), 5.toWeight()),
    SecurityPlan(30.toWeight(), 70.toWeight()),
    EquityPlan(55.toWeight(), 30.toWeight(), 15.toWeight())
)

val DEFAULT_PLATING = Plating(setOf(DOLLAR_FIAT_PLATING))

val DEFAULT_DRIFT = Drift(DEFAULT_VAULT, DEFAULT_MARKET, DEFAULT_PLAN, DEFAULT_PLATING)

val MAIN_ACCOUNT = CustodianAccount("main", "Main")
