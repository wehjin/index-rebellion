package com.rubyhuntersky.storage

import android.content.Context
import android.content.SharedPreferences
import com.rubyhuntersky.interaction.core.Book
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

class PreferencesBook<T : Any>(
    context: Context,
    preferencesName: String,
    private val serializer: KSerializer<T>,
    default: () -> T
) : Book<T> {

    override val reader: Observable<T>
        get() = behavior.distinctUntilChanged()

    private val behavior = BehaviorSubject.create<T>()

    @UnstableDefault
    override fun write(value: T) {
        val string = Json.stringify(serializer, value)
        preferences.edit().putString(PAGE_KEY, string).apply()
        behavior.onNext(value)
    }

    @UnstableDefault
    @Suppress("unused")
    private val loadingPreferences = loadPreferences(context, preferencesName)
        .subscribeOn(Schedulers.io())
        .subscribe { preferences ->
            this.preferences = preferences
            preferences.getString(PAGE_KEY, null)?.let {
                val savedValue = Json.parse(serializer, it)
                behavior.onNext(savedValue)
            } ?: write(default())
        }

    private lateinit var preferences: SharedPreferences

    companion object {
        private fun loadPreferences(context: Context, preferencesName: String): Single<SharedPreferences> =
            Single.fromCallable { context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE) }

        private const val PAGE_KEY = "page"
    }
}