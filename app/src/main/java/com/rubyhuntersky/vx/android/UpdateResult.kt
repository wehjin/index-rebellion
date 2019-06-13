package com.rubyhuntersky.vx.android

sealed class UpdateResult<out Data> {
    abstract val data: Data

    data class Continue<Data>(override val data: Data) : UpdateResult<Data>()
    data class Finish<Data>(override val data: Data) : UpdateResult<Data>()
}