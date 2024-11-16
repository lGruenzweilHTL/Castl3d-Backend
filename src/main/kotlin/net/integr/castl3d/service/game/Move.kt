package net.integr.castl3d.service.game

class Move(val from: Coordinate, val to: Coordinate, val isCapture: Boolean) {
    override fun toString(): String {
        return "Move(from=$from, to=$to)"
    }
}