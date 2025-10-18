package ar.edu.unicen.seminario.core.data.model.domain

/**
 * Enumeración para los tipos de ordenamiento disponibles en la API de RAWG
 */
enum class OrderingType(val value: String, val displayName: String) {
    NAME("name", "Nombre (A-Z)"),
    NAME_DESC("-name", "Nombre (Z-A)"),
    RELEASED("-released", "Más recientes"),
    RELEASED_ASC("released", "Más antiguos"),
    ADDED("-added", "Agregados recientemente"),
    CREATED("-created", "Creados recientemente"),
    UPDATED("-updated", "Actualizados recientemente"),
    RATING("-rating", "Mejor valorados"),
    RATING_ASC("rating", "Peor valorados"),
    METACRITIC("-metacritic", "Mejor Metacritic"),
    METACRITIC_ASC("metacritic", "Peor Metacritic");

    companion object {
        /**
         * Encuentra el OrderingType por su valor
         */
        fun fromValue(value: String): OrderingType {
            return values().find { it.value == value } ?: ADDED
        }

        /**
         * Obtiene todos los tipos de ordenamiento como array para spinner
         */
        fun getAllDisplayNames(): Array<String> {
            return values().map { it.displayName }.toTypedArray()
        }
    }
}
