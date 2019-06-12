package com.rubyhuntersky.vx

data class ViewId(val markers: List<Int> = emptyList()) {
    fun extend(marker: Int): ViewId =
        ViewId(markers.toMutableList().also { it.add(marker) })
}