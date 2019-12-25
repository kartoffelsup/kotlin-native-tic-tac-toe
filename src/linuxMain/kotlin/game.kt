const val size = 3
const val screenWidth = 640
const val screenHeight = 480
const val cellWidth: Int = (screenWidth / size)
const val cellHeight: Int = (screenHeight / size)

class Game(
    var board: Array<Cell>,
    var player: Player,
    var state: State
)

data class Cell(val player: Player?) {
    companion object {
        val emptyCell = Cell(null)
        val playerX = Cell(Player.X)
        val playerO = Cell(Player.O)
    }
}

sealed class Player {
    object X : Player()
    object O : Player()
}

enum class State {
    RUNNING, PLAYER_X_WON, PLAYER_O_WON, TIE, QUIT
}