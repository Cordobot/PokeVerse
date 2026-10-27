package com.cordobot.pokeverse.domain.model

import kotlinx.serialization.Serializable

/** What the list shows of each Pokémon. */
data class PokemonSummary(
    val id: Int,
    val name: String,
    val types: List<PokemonType>
) {
    val region: Region get() = Region.fromNationalDex(id)
    val imageUrl: String get() = artworkUrl(id)
    val number: String get() = formatNumber(id)
}

@Serializable
data class PokemonDetail(
    val id: Int,
    val name: String,
    val genus: String,
    val description: String,
    val types: List<PokemonType>,
    val generationName: String,
    val heightMeters: Double,
    val weightKg: Double,
    val abilities: List<Ability>,
    val stats: List<Stat>,
    val games: List<String>,
    val evolutions: List<EvolutionStage>,
    val isLegendary: Boolean,
    val isMythical: Boolean,
    val captureRate: Int,
    val habitat: String?
) {
    val region: Region get() = Region.fromGenerationName(generationName) ?: Region.fromNationalDex(id)
    val imageUrl: String get() = artworkUrl(id)
    val shinyImageUrl: String get() = artworkUrl(id, shiny = true)
    val number: String get() = formatNumber(id)
    val totalStats: Int get() = stats.sumOf { it.value }
}

@Serializable
data class Ability(val name: String, val isHidden: Boolean)

@Serializable
data class Stat(val name: String, val value: Int)

@Serializable
data class EvolutionStage(val id: Int, val name: String, val stage: Int) {
    val imageUrl: String get() = artworkUrl(id)
}

private const val ARTWORK_BASE =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork"

fun artworkUrl(id: Int, shiny: Boolean = false): String =
    if (shiny) "$ARTWORK_BASE/shiny/$id.png" else "$ARTWORK_BASE/$id.png"

fun formatNumber(id: Int): String = "#" + id.toString().padStart(4, '0')
