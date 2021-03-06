object Logic {
    private fun Game.switchPlayer() {
        if (player == Player.X) {
            player = Player.O
        } else if (player == Player.O) {
            player = Player.X
        }
    }

    private fun Game.checkPlayerWon(player: Player): Boolean {
        var rowCount = 0
        var columnCount = 0

        var diag1Count = 0
        var diag2Count = 0
        for (row in 0 until size) {
            for (column in 0 until size) {
                if (board[row * size + column].player == player) {
                    rowCount++
                }
                if (board[column * size + row].player == player) {
                    columnCount++
                }
            }

            if (rowCount == 3 || columnCount == 3) {
                return true
            }

            rowCount = 0
            columnCount = 0

            if (board[row * size + row].player == player) {
                diag1Count++
            }

            if (board[row * size + size - row - 1].player == player) {
                diag2Count++
            }

            if (diag1Count == 3 || diag2Count == 3) {
                return true
            }
        }

        return false
    }

    private fun Array<Cell>.countCells(cell: Cell): Int {
        return this.count { it == cell }
    }

    private fun Game.gameOverCondition() {
        when {
            checkPlayerWon(Player.X) -> {
                state = State.PLAYER_X_WON
            }
            checkPlayerWon(Player.O) -> {
                state = State.PLAYER_O_WON
            }
            board.countCells(Cell.emptyCell) == 0 -> {
                state = State.TIE
            }
        }
    }

    private fun Game.playerTurn(row: Int, column: Int) {
        val index = row * size + column
        if (board[index] == Cell.emptyCell) {
            board[index] = if (player == Player.X) Cell.playerX else Cell.playerO
            switchPlayer()
            gameOverCondition()
        }
    }

    private fun Game.reset() {
        player = Player.X
        state = State.RUNNING
        board.fill(Cell.emptyCell)
    }

    fun Game.clickOnCell(row: Int, column: Int) {
        if (state == State.RUNNING) {
            playerTurn(row, column)
        } else {
            reset()
        }
    }
}