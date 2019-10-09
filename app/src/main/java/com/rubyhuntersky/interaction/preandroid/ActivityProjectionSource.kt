package com.rubyhuntersky.interaction.preandroid

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.rubyhuntersky.interaction.android.ProjectionSource
import com.rubyhuntersky.interaction.core.Interaction
import com.rubyhuntersky.vx.android.putActivityInteractionSearchKey

interface ActivityProjectionSource<Vision : Any, Action : Any, Activity : FragmentActivity> :
    ProjectionSource<Vision, Action> {

    val activityClass: Class<Activity>
    val interactionName: String

    override val group: String get() = interactionName

    override fun startProjection(activity: FragmentActivity, interaction: Interaction<Vision, Action>, key: Long) =
        activity.startActivity(Intent(activity, activityClass).putActivityInteractionSearchKey(key))
}