package com.rubyhuntersky.indexrebellion.interactions.viewplan

import com.rubyhuntersky.indexrebellion.data.techtonic.Drift

sealed class ViewPlanAction {
    object Start : ViewPlanAction()
    data class Load(val drift: Drift) : ViewPlanAction()
    data class Ignore(val ignore: Any) : ViewPlanAction()
    object End : ViewPlanAction()
}