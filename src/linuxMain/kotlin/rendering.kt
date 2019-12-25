import cnames.structs.SDL_Renderer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.cValue
import kotlinx.cinterop.useContents
import sdl2.SDL_Color
import sdl2.SDL_RenderDrawLine
import sdl2.SDL_SetRenderDrawColor
import sdl2.filledCircleRGBA
import sdl2.thickLineRGBA
import kotlin.math.min

val GRID_COLOR: CValue<SDL_Color> = cValue {
    r = 255u
    g = 255u
    b = 255u
}
val PLAYER_X_COLOR: CValue<SDL_Color> = cValue { r = 255u; g = 50u; b = 50u }
val PLAYER_O_COLOR: CValue<SDL_Color> = cValue { r = 50u; g = 100u; b = 255u }
val TIE_COLOR: CValue<SDL_Color> = cValue { r = 100u; g = 100u; b = 100u }

fun CValuesRef<SDL_Renderer>.renderGrid(color: CValue<SDL_Color>) = color.useContents {
    SDL_SetRenderDrawColor(this@renderGrid, r, g, b, 255u)

    for (i in 1 until size) {
        SDL_RenderDrawLine(
            this@renderGrid,
            i * cellWidth, 0,
            i * cellWidth, screenHeight
        )
        SDL_RenderDrawLine(
            this@renderGrid,
            0, i * cellHeight,
            screenWidth, i * cellHeight
        )
    }
}

fun CValuesRef<SDL_Renderer>.renderX(row: Int, column: Int, color: CValue<SDL_Color>) = color.useContents {
    val halfBoxSide: Double = min(cellWidth, cellHeight) * 0.25
    val centerX: Double = cellWidth * 0.5 + column * cellWidth
    val centerY: Double = cellHeight * 0.5 + row * cellHeight

    thickLineRGBA(
        this@renderX,
        (centerX - halfBoxSide).toShort(),
        (centerY - halfBoxSide).toShort(),
        (centerX + halfBoxSide).toShort(),
        (centerY + halfBoxSide).toShort(),
        10,
        r,
        g,
        b,
        255
    )

    thickLineRGBA(
        this@renderX,
        (centerX + halfBoxSide).toShort(),
        (centerY - halfBoxSide).toShort(),
        (centerX - halfBoxSide).toShort(),
        (centerY + halfBoxSide).toShort(),
        10,
        r,
        g,
        b,
        255
    )
}

fun CValuesRef<SDL_Renderer>.renderO(row: Int, column: Int, color: CValue<SDL_Color>) {
    val halfBoxSide: Double = min(cellWidth, cellHeight) * 0.25
    val centerX: Short = (cellWidth * 0.5 + column * cellWidth).toShort()
    val centerY: Short = (cellHeight * 0.5 + row * cellHeight).toShort()

    color.useContents {
        filledCircleRGBA(
            this@renderO, centerX, centerY, (halfBoxSide + 5).toShort(), r, g, b, 255
        )
    }

    filledCircleRGBA(
        this, centerX, centerY, (halfBoxSide - 5).toShort(), 0, 0, 0, 255
    )
}

fun CValuesRef<SDL_Renderer>.renderBoard(
    board: Array<Cell>,
    playerXColor: CValue<SDL_Color>,
    playerOColor: CValue<SDL_Color>
) {
    for (i in 0 until size) {
        for (j in 0 until size) {
            when (board[i * size + j]) {
                Cell.playerX -> renderX(i, j, playerXColor)
                Cell.playerO -> renderO(i, j, playerOColor)
            }
        }
    }
}

fun CValuesRef<SDL_Renderer>.renderRunningState(game: Game) {
    renderGrid(GRID_COLOR)
    renderBoard(game.board, PLAYER_X_COLOR, PLAYER_O_COLOR)
}

fun CValuesRef<SDL_Renderer>.renderGameOverState(game: Game, color: CValue<SDL_Color>) {
    renderGrid(color)
    renderBoard(game.board, color, color)
}

fun CValuesRef<SDL_Renderer>.renderGame(game: Game) {
    when (game.state) {
        State.RUNNING -> renderRunningState(game)
        State.PLAYER_X_WON -> renderGameOverState(game, PLAYER_X_COLOR)
        State.PLAYER_O_WON -> renderGameOverState(game, PLAYER_O_COLOR)
        State.TIE -> renderGameOverState(game, TIE_COLOR)
        else -> {
        }
    }
}
