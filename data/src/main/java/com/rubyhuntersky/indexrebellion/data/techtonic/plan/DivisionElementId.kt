package com.rubyhuntersky.indexrebellion.data.techtonic.plan

import com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate

sealed class DivisionElementId {
    abstract val shortName: String

    data class ForPlate(val plate: Plate) : DivisionElementId() {
        override val shortName: String
            get() = plate.contextualName
    }

    data class ForDivision(val divisionId: DivisionId) : DivisionElementId() {
        override val shortName: String
            get() = divisionId.name
    }
}