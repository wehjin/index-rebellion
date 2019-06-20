package com.rubyhuntersky.indexrebellion.spirits.readdrift

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift
import com.rubyhuntersky.indexrebellion.spirits.common.ReadBookDjinn
import com.rubyhuntersky.interaction.core.Book
import com.rubyhuntersky.interaction.core.wish.Djinn

class ReadDriftDjinn(book: Book<Drift>) :
    Djinn<ReadDrift, Drift> by ReadBookDjinn(book, ReadDrift::class.java)
