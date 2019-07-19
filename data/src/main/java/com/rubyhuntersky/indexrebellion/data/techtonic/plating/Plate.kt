package com.rubyhuntersky.indexrebellion.data.techtonic.plating

enum class Plate(val contextualName: String, val memberTag: String) {
    Unknown("Unassigned", "???"),
    Fiat("Fiat", "Fiat Money"),
    BlockChain("Coins", "Network Money"),
    Debt("Debt", "Debt Fund"),
    GlobalEquity("Global", "Global Stock Fund"),
    ZonalEquity("Zonal", "Sector Stock Fund"),
    LocalEquity("Local", "Company Stock"),
}