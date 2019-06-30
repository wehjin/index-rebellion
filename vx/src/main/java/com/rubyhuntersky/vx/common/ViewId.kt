package com.rubyhuntersky.vx.common

import kotlin.math.min

data class ViewId(val markers: List<Int> = emptyList()) {
    fun extend(marker: Int): ViewId =
        ViewId(markers.toMutableList().also { it.add(marker) })

    fun isEqualOrExtends(id: ViewId): Boolean {
        val commonSize = min(markers.size, id.markers.size)
        val ancestorMarkers = this.markers.subList(0, commonSize)
        return ancestorMarkers == id.markers
    }
}