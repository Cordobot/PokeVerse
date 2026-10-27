package com.cordobot.pokeverse.domain.model

/**
 * Regions in the order their generation was released. [nationalDex] is the range of National
 * Pokédex numbers introduced in that generation, used to place a Pokémon without a network call.
 */
enum class Region(
    val displayName: String,
    val generation: Int,
    val nationalDex: IntRange,
    val mainGames: String
) {
    KANTO("Kanto", 1, 1..151, "Rojo, Azul y Amarillo"),
    JOHTO("Johto", 2, 152..251, "Oro, Plata y Cristal"),
    HOENN("Hoenn", 3, 252..386, "Rubí, Zafiro y Esmeralda"),
    SINNOH("Sinnoh", 4, 387..493, "Diamante, Perla y Platino"),
    UNOVA("Teselia", 5, 494..649, "Negro, Blanco, Negro 2 y Blanco 2"),
    KALOS("Kalos", 6, 650..721, "X e Y"),
    ALOLA("Alola", 7, 722..809, "Sol, Luna, Ultrasol y Ultraluna"),
    GALAR("Galar", 8, 810..905, "Espada, Escudo y Leyendas: Arceus"),
    PALDEA("Paldea", 9, 906..1025, "Escarlata y Púrpura");

    val generationLabel: String get() = "Generación ${ROMAN[generation - 1]}"

    companion object {
        private val ROMAN = listOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")

        /** Region of a National Pokédex number; newer Pokémon than the table fall in the last region. */
        fun fromNationalDex(id: Int): Region =
            entries.firstOrNull { id in it.nationalDex } ?: entries.last()

        /** Region of a generation as the API names it, e.g. "generation-iv". */
        fun fromGenerationName(name: String): Region? {
            val roman = name.substringAfter("generation-").uppercase()
            val index = ROMAN.indexOf(roman)
            return if (index >= 0) entries.getOrNull(index) else null
        }
    }
}
