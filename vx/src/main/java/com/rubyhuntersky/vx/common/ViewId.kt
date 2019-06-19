package com.rubyhuntersky.vx.common

data class ViewId(val markers: List<Int> = emptyList()) {
    fun extend(marker: Int): ViewId =
        ViewId(markers.toMutableList().also { it.add(marker) })

    fun isDescendentOf(id: ViewId): Boolean {
        val ancestorMarkers = this.markers.subList(0, id.markers.size)
        return ancestorMarkers == id.markers
    }
}