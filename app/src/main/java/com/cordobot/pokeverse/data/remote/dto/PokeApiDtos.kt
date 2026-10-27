package com.cordobot.pokeverse.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NamedResourceDto(val name: String, val url: String) {
    /** Resource URLs end with the id: https://pokeapi.co/api/v2/pokemon-species/25/ */
    val id: Int get() = url.trimEnd('/').substringAfterLast('/').toInt()
}

@Serializable
data class NamedResourceListDto(val count: Int, val results: List<NamedResourceDto>)

@Serializable
data class TypeDto(val name: String, val pokemon: List<TypePokemonDto>)

@Serializable
data class TypePokemonDto(val slot: Int, val pokemon: NamedResourceDto)

@Serializable
data class PokemonDto(
    val id: Int,
    val name: String,
    val height: Int, // decimeters
    val weight: Int, // hectograms
    val types: List<PokemonTypeSlotDto>,
    val stats: List<StatDto>,
    val abilities: List<AbilitySlotDto>,
    @SerialName("game_indices") val gameIndices: List<GameIndexDto> = emptyList()
)

@Serializable
data class PokemonTypeSlotDto(val slot: Int, val type: NamedResourceDto)

@Serializable
data class StatDto(@SerialName("base_stat") val baseStat: Int, val stat: NamedResourceDto)

@Serializable
data class AbilitySlotDto(val ability: NamedResourceDto, @SerialName("is_hidden") val isHidden: Boolean)

@Serializable
data class GameIndexDto(val version: NamedResourceDto)

@Serializable
data class SpeciesDto(
    val id: Int,
    val name: String,
    val generation: NamedResourceDto,
    val names: List<LocalizedNameDto> = emptyList(),
    val genera: List<GenusDto> = emptyList(),
    @SerialName("flavor_text_entries") val flavorTextEntries: List<FlavorTextDto> = emptyList(),
    @SerialName("evolution_chain") val evolutionChain: ResourceUrlDto? = null,
    @SerialName("is_legendary") val isLegendary: Boolean = false,
    @SerialName("is_mythical") val isMythical: Boolean = false,
    @SerialName("capture_rate") val captureRate: Int = 0,
    val habitat: NamedResourceDto? = null
)

@Serializable
data class ResourceUrlDto(val url: String) {
    val id: Int get() = url.trimEnd('/').substringAfterLast('/').toInt()
}

@Serializable
data class LocalizedNameDto(val name: String, val language: NamedResourceDto)

@Serializable
data class GenusDto(val genus: String, val language: NamedResourceDto)

@Serializable
data class FlavorTextDto(
    @SerialName("flavor_text") val flavorText: String,
    val language: NamedResourceDto,
    val version: NamedResourceDto
)

@Serializable
data class EvolutionChainDto(val id: Int, val chain: ChainLinkDto)

@Serializable
data class ChainLinkDto(
    val species: NamedResourceDto,
    @SerialName("evolves_to") val evolvesTo: List<ChainLinkDto> = emptyList()
)
