object FenParser {

    fun parse(fen: String): Board {
        val splitted = fen.split(" ")
        val (
            disposition,
            whoMoves,
            castling,
            onPass,
            emptyMove) = splitted
        val moveNum = splitted[5]

        val lines = disposition.split("/")
        val board = Board(Board.getEmpty())

        lines.forEachIndexed { index, line ->
            var position = 0
            val iter = line.toCharArray().iterator()
            while (position < 8) {
                val sign = iter.nextChar()

                if (sign.isDigit()) {
                    val newPosition = position + sign.toString().toInt()
                    for (i in position until newPosition) {
                        board.disposition[index][i] = null
                    }
                    position = newPosition
                    continue
                } else {
                    board.disposition[index][position] = Board.Piece.getByLetter(sign)
                }

                position++
            }
        }

        return board
    }
}