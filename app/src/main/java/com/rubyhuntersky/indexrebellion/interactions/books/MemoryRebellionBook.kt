package com.rubyhuntersky.indexrebellion.interactions.books

import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Book

class MemoryRebellionBook : RebellionBook, Book<Rebellion>
by BehaviorBook(Rebellion.SEED)
