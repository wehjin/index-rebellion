package com.rubyhuntersky.indexrebellion.spirits.genies.showtoast

import android.content.Context
import android.widget.Toast
import com.rubyhuntersky.interaction.GenieParams2
import com.rubyhuntersky.interaction.core.wish.Genie
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

data class ShowToast(
    val text: String,
    val longDuration: Boolean = false
) : GenieParams2<Nothing, ShowToast> {

    class GENIE(private val context: Context) : Genie<ShowToast, Nothing> {
        override val paramsClass: Class<ShowToast> = ShowToast::class.java

        override fun toSingle(params: ShowToast): Single<Nothing> = Single
            .create<Nothing> {
                val length = if (params.longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
                Toast.makeText(context, params.text, length).show()
            }
            .subscribeOn(AndroidSchedulers.mainThread())
    }
}