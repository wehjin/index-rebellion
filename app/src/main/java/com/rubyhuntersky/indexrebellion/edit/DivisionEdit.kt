package com.rubyhuntersky.indexrebellion.edit

import com.rubyhuntersky.indexrebellion.data.techtonic.plan.Division
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionElement
import com.rubyhuntersky.indexrebellion.data.techtonic.plan.DivisionElementId
import com.rubyhuntersky.indexrebellion.projections.ElementChange
import com.rubyhuntersky.interaction.edit.Ancient
import com.rubyhuntersky.interaction.edit.Edit
import com.rubyhuntersky.interaction.edit.Seed
import com.rubyhuntersky.interaction.stringedit.StringEdit

data class DivisionEdit(

    override val ancient: Ancient<Division>?,

    val elementEdits: Map<DivisionElementId, StringEdit<DivisionElement>> = ancient?.validValue
        ?.divisionElements
        ?.associate { it.id to StringEdit(it.id.shortName, null, Ancient(it)) }
        ?: emptyMap(),

    override val novel: DivisionNovel? = DivisionNovel.fromElementEdits(elementEdits, ancient?.validValue!!)
) : Edit<Division, Map<DivisionElementId, String?>, Map<DivisionElementId, IntRange?>, DivisionNovel> {

    val subeditList: List<Map.Entry<DivisionElementId, StringEdit<DivisionElement>>>
            by lazy { elementEdits.entries.toList() }

    override val label: String
        get() = ancient?.validValue?.divisionId?.name ?: "Division"

    override val seed: Seed<Division>?
        get() = null


    fun updateElement(elementChange: ElementChange): DivisionEdit {
        val elementEdits = elementEdits.mapValues { (id, edit) ->
            if (id == elementChange.elementId) {
                edit.setNovel(elementChange.toNovel())
            } else {
                edit
            }
        }
        return copy(
            elementEdits = elementEdits,
            novel = DivisionNovel.fromElementEdits(elementEdits, ancient?.validValue!!)
        )
    }

    override fun setAncient(value: Division?): DivisionEdit =
        copy(
            ancient = value?.let(::Ancient),
            elementEdits = elementEdits.mapValues { (id, edit) -> edit.setAncient(value?.find(id)) }
        )
}