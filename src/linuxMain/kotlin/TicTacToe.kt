import cnames.structs.SDL_Renderer
import cnames.structs.SDL_Window
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.EXIT_FAILURE
import platform.posix.EXIT_SUCCESS
import platform.posix.exit
import platform.posix.printf
import sdl2.SDL_CreateRenderer
import sdl2.SDL_CreateWindow
import sdl2.SDL_DestroyWindow
import sdl2.SDL_Event
import sdl2.SDL_GetError
import sdl2.SDL_INIT_VIDEO
import sdl2.SDL_Init
import sdl2.SDL_MOUSEBUTTONDOWN
import sdl2.SDL_PollEvent
import sdl2.SDL_QUIT
import sdl2.SDL_Quit
import sdl2.SDL_RENDERER_ACCELERATED
import sdl2.SDL_RENDERER_PRESENTVSYNC
import sdl2.SDL_RenderClear
import sdl2.SDL_RenderPresent
import sdl2.SDL_SetRenderDrawColor
import sdl2.SDL_WINDOW_SHOWN

fun main(): Unit = memScoped {
    if (SDL_Init(SDL_INIT_VIDEO) != 0) {
        printf("Could not initialize sdl2: %s", SDL_GetError())
        return exit(EXIT_FAILURE)
    }

    val window: CPointer<SDL_Window>? =
        SDL_CreateWindow("Tic Tac Toe KT", 100, 100, screenWidth, screenHeight, SDL_WINDOW_SHOWN)

    if (window == null) {
        printf("SDL_CreateWindow Error: %s", SDL_GetError())
        return exit(EXIT_FAILURE)
    }

    defer {
        SDL_DestroyWindow(window)
    }

    val renderer: CPointer<SDL_Renderer>? =
        SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED.or(SDL_RENDERER_PRESENTVSYNC))

    if (renderer == null) {
        SDL_DestroyWindow(window)
        printf("SDL_CreateRenderer Error :%s", SDL_GetError())
        return exit(EXIT_FAILURE)
    }

    val game = Game(Array(size * size) { Cell(null) }, Player.X, State.RUNNING)
    val event = alloc<SDL_Event>()
    while (game.state != State.QUIT) {
        while (SDL_PollEvent(event.ptr) != 0) {
            when (event.type) {
                SDL_QUIT -> game.state = State.QUIT

                SDL_MOUSEBUTTONDOWN -> Logic.run {
                    game.clickOnCell(event.button.y / cellHeight, event.button.x / cellWidth)
                }
                else -> {
                }
            }
        }

        SDL_SetRenderDrawColor(renderer, 0, 0, 0, 255)
        SDL_RenderClear(renderer)
        renderer.renderGame(game)
        SDL_RenderPresent(renderer)
    }

    SDL_Quit()

    return exit(EXIT_SUCCESS)
}