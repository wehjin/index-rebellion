package com.rubyhuntersky.indexrebellion.data.techtonic.plating

enum class Plate(val contextualName: String, val memberTag: String, val groupTag: String) {
    Unknown("Unassigned", "???", "???"),
    Fiat("Fiat", "Cash", "Cash"),
    BlockChain("Coins", "Coin", "Coins"),
    Debt("Debt", "Bond", "Bonds"),
    GlobalEquity("Global", "Global Fund", "Global Funds"),
    ZonalEquity("Zonal", "Sector Fund", "Sector Funds"),
    LocalEquity("Local", "Stock", "Stocks"),
}