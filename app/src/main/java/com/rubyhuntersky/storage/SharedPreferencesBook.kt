package com.rubyhuntersky.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.rubyhuntersky.interaction.core.Book
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

class SharedPreferencesBook<T : Any>(
    context: Context,
    preferencesName: String,
    private val serializer: KSerializer<T>,
    alwaysWriteDefault: Boolean = false,
    default: () -> T
) : Book<T> {

    private val readBehavior = BehaviorSubject.create<T>()
    private val writeQueue = PublishSubject.create<T>()

    @Suppress("unused")
    @UnstableDefault
    private val subjectUpdates = Single
        .fromCallable { context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE) }
        .subscribeOn(Schedulers.io())
        .flatMapObservable { sharedPreferences ->
            val storedValue = sharedPreferences.readValue()
                ?: (default().also { if (alwaysWriteDefault) sharedPreferences.writeValue(it) })
            Log.d(preferencesName, "READ $storedValue")
            readBehavior.onNext(storedValue)
            writeQueue.map { Pair(it, sharedPreferences) }
        }
        .observeOn(Schedulers.single())
        .doOnError { Log.e(this.javaClass.simpleName, "ERROR: $preferencesName $it") }
        .subscribe { (value, sharedPreferences) ->
            sharedPreferences.writeValue(value)
            readBehavior.onNext(value)
        }

    override val reader: Observable<T>
        get() = readBehavior.distinctUntilChanged()

    @UnstableDefault
    override fun write(value: T) = writeQueue.onNext(value)

    @UnstableDefault
    private fun SharedPreferences.readValue(): T? = getString(PAGE_KEY, null)?.let { Json.parse(serializer, it) }

    @UnstableDefault
    private fun SharedPreferences.writeValue(value: T) {
        edit().putString(PAGE_KEY, Json.stringify(serializer, value)).apply()
    }

    companion object {
        private const val PAGE_KEY = "page"
    }
}