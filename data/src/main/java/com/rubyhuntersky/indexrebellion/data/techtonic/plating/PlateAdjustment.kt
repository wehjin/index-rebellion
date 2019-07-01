package com.rubyhuntersky.indexrebellion.data.techtonic.plating

data class PlateAdjustment(
    val plate: Plate,
    val plannedPortion: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlateAdjustment) return false

        if (plate != other.plate) return false
        if (plannedPortion != other.plannedPortion) return false

        return true
    }

    override fun hashCode(): Int {
        var result = plate.hashCode()
        result = 31 * result + plannedPortion.toString().hashCode() // Double.toString() available starting in API 24
        return result
    }
}