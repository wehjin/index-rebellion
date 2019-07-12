package com.rubyhuntersky.indexrebellion.interactions.viewplan

import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Plan

sealed class ViewPlanVision {
    object Idle : ViewPlanVision()
    object Loading : ViewPlanVision()
    data class Viewing(val plan: Plan) : ViewPlanVision()
    object Ended : ViewPlanVision()
}