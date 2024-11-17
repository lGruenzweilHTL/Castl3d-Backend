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

@file:Suppress("unused")

package net.integr.castl3d.service.game

/**
 * Represents a coordinate on the board.
 * @param x The x coordinate.
 * @param y The y coordinate.
 * @constructor Creates a new coordinate.
 */
class Coordinate(val x: Int, val y: Int) {
    /**
     * Returns true if the coordinate is within the bounds of the board.
     */
    fun isValid(): Boolean {
        return x in 0..7 && y in 0..7
    }

    /**
     * Returns a string representation of the coordinate.
     */
    override fun toString(): String {
        return "(x=$x, y=$y)"
    }
}