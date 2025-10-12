package ar.edu.unicen.seminario.core.navigation

/**
 * Define las rutas de navegación para toda la aplicación
 */
object NavigationRoutes {

    /**
     * Ruta para la pantalla principal de lista de juegos
     */
    const val GAME_LIST = "game_list"

    /**
     * Ruta para la pantalla de filtros
     */
    const val FILTERS = "filters"

    /**
     * Ruta para la pantalla de detalle de juego
     * Incluye el parámetro del ID del juego
     */
    const val GAME_DETAIL = "game_detail/{gameId}"

    /**
     * Función helper para crear la ruta de detalle con el ID específico
     */
    fun createGameDetailRoute(gameId: Int): String {
        return "game_detail/$gameId"
    }
}

/**
 * Argumentos de navegación para las pantallas
 */
object NavigationArgs {

    /**
     * Clave para el ID del juego en la navegación
     */
    const val GAME_ID = "gameId"

    /**
     * Clave para los filtros aplicados
     */
    const val FILTERS = "filters"
}
