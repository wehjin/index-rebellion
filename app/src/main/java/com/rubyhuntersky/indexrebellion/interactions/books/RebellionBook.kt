package com.rubyhuntersky.indexrebellion.interactions.books

import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.data.assets.AssetSymbol
import com.rubyhuntersky.interaction.core.Book

interface RebellionBook : Book<Rebellion> {

    fun deleteConstituent(assetSymbol: AssetSymbol) = write(value.deleteConstituent(assetSymbol))
}


