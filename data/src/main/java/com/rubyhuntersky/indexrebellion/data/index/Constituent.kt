package com.rubyhuntersky.indexrebellion.data.index

import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import kotlinx.serialization.Serializable

@Serializable
data class Constituent(val assetSymbol: AssetSymbol, val marketWeight: MarketWeight)
