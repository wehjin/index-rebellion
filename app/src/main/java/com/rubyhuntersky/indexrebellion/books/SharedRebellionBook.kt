package com.rubyhuntersky.indexrebellion.books

import android.content.Context
import android.content.SharedPreferences
import com.rubyhuntersky.indexrebellion.data.Rebellion
import com.rubyhuntersky.indexrebellion.interactions.books.RebellionBook
import com.rubyhuntersky.interaction.core.BehaviorBook
import com.rubyhuntersky.interaction.core.Book
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

object SharedRebellionBook : RebellionBook, Book<Rebellion> by BehaviorBook(null) {

    private lateinit var disposable: Disposable

    @UnstableDefault
    fun open(context: Context) {
        val preferences = context.getSharedPreferences("SharedRebellionBook", Context.MODE_PRIVATE)

        disposable = Single.defer { Single.just(loadRebellion(preferences)) }
            .flatMapObservable { loaded ->
                write(loaded)
                reader.scan(loaded) { old, new ->
                    if (new != old) {
                        saveRebellion(preferences, new)
                    }
                    new
                }
            }
            .subscribe()
    }

    @UnstableDefault
    private fun saveRebellion(preferences: SharedPreferences, it: Rebellion) {
        preferences.edit().putString(PAGE_KEY, Json.stringify(Rebellion.serializer(), it)).apply()
    }

    @UnstableDefault
    private fun loadRebellion(preferences: SharedPreferences): Rebellion {
        val jsonString = preferences.getString(PAGE_KEY, null)
        return jsonString?.let { Json.parse(Rebellion.serializer(), it) } ?: Rebellion()
    }

    private const val PAGE_KEY = "page"
}
