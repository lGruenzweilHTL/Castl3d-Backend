/*
 * Copyright © 2024 Integr
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("MemberVisibilityCanBePrivate")

package net.integr.castl3d.service.game

/**
 * Represents a move in a game.
 * @param from the coordinate of the piece to move
 * @param to the coordinate to move the piece to
 * @param isCapture true if the move is a capture, false otherwise
 * @param isCastle true if the move is a castle, false otherwise
 * @param isEnPassant true if the move is an en passant, false otherwise
 * @see Coordinate
 */
class Move(val from: Coordinate, val to: Coordinate, var isCapture: Boolean = false, val isCastle: Boolean = false, val isEnPassant: Boolean = false) {
    /**
     * Returns a string representation of the move.
     */
    override fun toString(): String {
        return "Move(from=$from, to=$to, isCapture=$isCapture, isCastle=$isCastle, isEnPassant=$isEnPassant)"
    }
}