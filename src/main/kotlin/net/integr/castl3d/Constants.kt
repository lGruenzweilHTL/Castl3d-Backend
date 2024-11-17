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

package net.integr.castl3d

class Constants {
    class Piece {
        companion object {
            const val PAWN = 0
            const val KNIGHT = 1
            const val BISHOP = 2
            const val ROOK = 3
            const val QUEEN = 4
            const val KING = 5
            const val NONE = -1
        }
    }

    class Color {
        companion object {
            const val WHITE = 10
            const val BLACK = 11
            const val NO_COLOR = 12
            const val ANY_COLOR = 13

            /**
             * Returns the opposite color of the given color.
             */
            fun Int.oppositeColor(): Int {
                return when (this) {
                    WHITE -> BLACK
                    BLACK -> WHITE
                    else -> NO_COLOR
                }
            }
        }
    }
}