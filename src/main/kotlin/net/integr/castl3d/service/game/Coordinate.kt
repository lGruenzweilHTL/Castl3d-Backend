package net.integr.castl3d.service.game

class Coordinate(val x: Int, val y: Int) {
    fun isValid(): Boolean {
        return x in 0..7 && y in 0..7
    }

    override fun toString(): String {
        return "(x=$x, y=$y)"
    }
}