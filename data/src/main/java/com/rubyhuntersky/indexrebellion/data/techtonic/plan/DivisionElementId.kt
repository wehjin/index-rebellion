package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate

sealed class DivisionElementId {
    data class ForPlate(val plate: Plate) : DivisionElementId()
    data class ForDivision(val divisionId: DivisionId) : DivisionElementId()
}