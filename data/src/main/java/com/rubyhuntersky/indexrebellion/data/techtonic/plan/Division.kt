package com.rubyhuntersky.indexrebellion.data.techtonic.plan

interface Division {
    val divisionId: DivisionId
    val divisionElements: List<DivisionElement>

    companion object {
        val EMPTY = object : Division {
            override val divisionId: DivisionId = DivisionId.Portfolio
            override val divisionElements: List<DivisionElement> = emptyList()
        }
    }
}
