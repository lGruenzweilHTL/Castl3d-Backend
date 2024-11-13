package net.integr.castl3d.socket.packet.s2c

data class SetBoardS2CPacket(val x: Int, val y: Int, val piece: Int, val color: Int, val moveCount: Int, val hasJustMoved: Boolean)