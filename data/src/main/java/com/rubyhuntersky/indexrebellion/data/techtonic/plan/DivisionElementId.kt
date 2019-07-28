package com.rubyhuntersky.indexrebellion.data.techtonic.plan

sealed class DivisionElementId {

    abstract val shortName: String

    abstract val divisionId: DivisionId

    data class Plate(
        override val divisionId: DivisionId,
        val plate: com.rubyhuntersky.indexrebellion.data.techtonic.plating.Plate
    ) : DivisionElementId() {

        override val shortName: String
            get() = plate.contextualName
    }

    data class Subdivision(override val divisionId: DivisionId) : DivisionElementId() {

        override val shortName: String
            get() = divisionId.name
    }
}