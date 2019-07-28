package com.rubyhuntersky.indexrebellion.data.techtonic.plan

interface Division {
    val divisionId: DivisionId
    val divisionElements: List<DivisionElement>

    fun find(divisionElementId: DivisionElementId) = divisionElements.firstOrNull { it.id == divisionElementId }

    fun replace(divisionElements: List<DivisionElement>): Division

    private data class GeneralDivision(
        override val divisionId: DivisionId,
        override val divisionElements: List<DivisionElement>
    ) : Division {

        override fun replace(divisionElements: List<DivisionElement>): Division {
            val mutable = this.divisionElements.associateBy(DivisionElement::id).toMutableMap()
            divisionElements.forEach { mutable[it.id] = it }
            return copy(divisionElements = mutable.values.toList())
        }
    }

    companion object {
        val EMPTY: Division = GeneralDivision(DivisionId.Portfolio, emptyList())
    }
}
