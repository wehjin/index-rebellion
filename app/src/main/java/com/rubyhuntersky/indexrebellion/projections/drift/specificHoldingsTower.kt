package com.rubyhuntersky.indexrebellion.projections.drift

import com.rubyhuntersky.indexrebellion.data.techtonic.vault.SpecificHolding
import com.rubyhuntersky.indexrebellion.projections.Standard
import com.rubyhuntersky.vx.common.Span
import com.rubyhuntersky.vx.tower.Tower
import com.rubyhuntersky.vx.tower.additions.Share
import com.rubyhuntersky.vx.tower.additions.handleEvents
import com.rubyhuntersky.vx.tower.additions.replicate.replicate
import com.rubyhuntersky.vx.tower.combine
import com.rubyhuntersky.vx.tower.pack
import com.rubyhuntersky.vx.tower.towerOf
import com.rubyhuntersky.vx.tower.towers.click.clickTowerOf

fun specificHoldingsTower(
    remove: (SpecificHolding) -> Unit,
    edit: (SpecificHolding) -> Unit
): Tower<List<SpecificHolding>, Nothing> {
    val editButton = clickTowerOf<SpecificHolding>("ed").handleEvents(edit)
    val removeButton = clickTowerOf<SpecificHolding>("rm").handleEvents(remove)
    val buttonTower = pack(editButton, Standard.spacing, removeButton)
    val dataTower = combine(
        towerOf(SpecificHolding::toAccountText, Standard.TitleTower()),
        towerOf(SpecificHolding::toSharesText, Standard.SubtitleTower())
    )
    return dataTower
        .shl(Share(Span.EIGHTH * 2, buttonTower))
        .vpad(Standard.uniformPad.height)
        .replicate().handleEvents { }
}

private fun SpecificHolding.toAccountText() = custodianAccount.id
private fun SpecificHolding.toSharesText() = "$size shares"
