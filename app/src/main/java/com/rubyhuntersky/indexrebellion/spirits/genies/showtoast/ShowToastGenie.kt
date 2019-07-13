package com.rubyhuntersky.indexrebellion.spirits.genies.showtoast

import android.content.Context
import android.widget.Toast
import com.rubyhuntersky.interaction.core.wish.Genie
import io.reactivex.Single

class ShowToastGenie(private val context: Context) : Genie<ShowToast, Nothing> {

    override val paramsClass: Class<ShowToast> = ShowToast::class.java

    override fun toSingle(params: ShowToast): Single<Nothing> =
        Single.create {
            val length = if (params.longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            Toast.makeText(context, params.text, length).show()
        }
}