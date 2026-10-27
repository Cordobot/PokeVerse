package com.cordobot.pokeverse.data.mapper

import com.cordobot.pokeverse.data.local.PokemonEntity
import com.cordobot.pokeverse.data.remote.dto.ChainLinkDto
import com.cordobot.pokeverse.data.remote.dto.PokemonDto
import com.cordobot.pokeverse.data.remote.dto.SpeciesDto
import com.cordobot.pokeverse.domain.model.Ability
import com.cordobot.pokeverse.domain.model.EvolutionStage
import com.cordobot.pokeverse.domain.model.PokemonDetail
import com.cordobot.pokeverse.domain.model.PokemonSummary
import com.cordobot.pokeverse.domain.model.PokemonType
import com.cordobot.pokeverse.domain.model.Stat

fun PokemonEntity.toDomain() = PokemonSummary(
    id = id,
    name = name,
    types = types.split(',').mapNotNull(PokemonType::fromApiName)
)

fun PokemonDto.toDetail(species: SpeciesDto, evolutions: List<EvolutionStage>) = PokemonDetail(
    id = id,
    name = species.localizedName(),
    genus = species.genera.preferSpanish { it.language.name }?.genus.orEmpty(),
    description = species.description(),
    types = types.sortedBy { it.slot }.mapNotNull { PokemonType.fromApiName(it.type.name) },
    generationName = species.generation.name,
    heightMeters = height / 10.0,
    weightKg = weight / 10.0,
    abilities = abilities.map { Ability(prettyName(it.ability.name), it.isHidden) },
    stats = stats.map { Stat(STAT_NAMES[it.stat.name] ?: prettyName(it.stat.name), it.baseStat) },
    games = gameNames(gameIndices.map { it.version.name } + species.flavorTextEntries.map { it.version.name }),
    evolutions = evolutions,
    isLegendary = species.isLegendary,
    isMythical = species.isMythical,
    captureRate = species.captureRate,
    habitat = species.habitat?.name?.let { HABITATS[it] ?: prettyName(it) }
)

/** Flattens the evolution tree, keeping how many steps each stage is from the base form. */
fun ChainLinkDto.toStages(stage: Int = 0): List<EvolutionStage> =
    listOf(EvolutionStage(species.id, prettyName(species.name), stage)) +
        evolvesTo.flatMap { it.toStages(stage + 1) }

private fun SpeciesDto.localizedName(): String =
    names.preferSpanish { it.language.name }?.name ?: prettyName(name)

// The most recent Spanish entry, otherwise the most recent English one
private fun SpeciesDto.description(): String =
    (flavorTextEntries.lastOrNull { it.language.name == "es" }
        ?: flavorTextEntries.lastOrNull { it.language.name == "en" })
        ?.flavorText
        ?.replace(Regex("[\\n\\f\\r\\u00AD]+"), " ")
        ?.replace(Regex("\\s+"), " ")
        ?.trim()
        .orEmpty()

private inline fun <T> List<T>.preferSpanish(language: (T) -> String): T? =
    firstOrNull { language(it) == "es" } ?: firstOrNull { language(it) == "en" }

/** Games in release order with their Spanish names; versions missing from the table go last. */
fun gameNames(versions: List<String>): List<String> {
    val distinct = versions.distinct()
    val known = GAME_ORDER.filter { (slug, _) -> slug in distinct }.map { it.second }
    val unknown = distinct.filter { slug -> GAME_ORDER.none { it.first == slug } }.map(::prettyName)
    return known + unknown
}

/** "mr-mime" -> "Mr. Mime", "great-tusk" -> "Great Tusk". */
fun prettyName(slug: String): String =
    SPECIAL_NAMES[slug] ?: slug.split('-').joinToString(" ") { part ->
        part.replaceFirstChar { it.uppercase() }
    }

private val SPECIAL_NAMES = mapOf(
    "nidoran-f" to "Nidoran ♀", "nidoran-m" to "Nidoran ♂", "mr-mime" to "Mr. Mime",
    "mime-jr" to "Mime Jr.", "mr-rime" to "Mr. Rime", "farfetchd" to "Farfetch'd",
    "sirfetchd" to "Sirfetch'd", "ho-oh" to "Ho-Oh", "porygon-z" to "Porygon-Z",
    "type-null" to "Type: Null", "jangmo-o" to "Jangmo-o", "hakamo-o" to "Hakamo-o",
    "kommo-o" to "Kommo-o", "flabebe" to "Flabébé", "wo-chien" to "Wo-Chien",
    "chien-pao" to "Chien-Pao", "ting-lu" to "Ting-Lu", "chi-yu" to "Chi-Yu"
)

private val STAT_NAMES = mapOf(
    "hp" to "PS", "attack" to "Ataque", "defense" to "Defensa",
    "special-attack" to "At. Esp.", "special-defense" to "Def. Esp.", "speed" to "Velocidad"
)

private val HABITATS = mapOf(
    "cave" to "Cueva", "forest" to "Bosque", "grassland" to "Pradera", "mountain" to "Montaña",
    "rare" to "Raro", "rough-terrain" to "Terreno agreste", "sea" to "Mar", "urban" to "Urbano",
    "waters-edge" to "Orilla del agua"
)

// Every version PokeAPI knows (GET /version), in release order
private val GAME_ORDER = listOf(
    "red-japan" to "Rojo (Japón)", "green-japan" to "Verde (Japón)", "blue-japan" to "Azul (Japón)",
    "red" to "Rojo", "blue" to "Azul", "yellow" to "Amarillo",
    "gold" to "Oro", "silver" to "Plata", "crystal" to "Cristal",
    "ruby" to "Rubí", "sapphire" to "Zafiro", "emerald" to "Esmeralda",
    "firered" to "Rojo Fuego", "leafgreen" to "Verde Hoja",
    "colosseum" to "Colosseum", "xd" to "XD: Tempestad Oscura",
    "diamond" to "Diamante", "pearl" to "Perla", "platinum" to "Platino",
    "heartgold" to "Oro HeartGold", "soulsilver" to "Plata SoulSilver",
    "black" to "Negro", "white" to "Blanco", "black-2" to "Negro 2", "white-2" to "Blanco 2",
    "x" to "X", "y" to "Y", "omega-ruby" to "Rubí Omega", "alpha-sapphire" to "Zafiro Alfa",
    "sun" to "Sol", "moon" to "Luna", "ultra-sun" to "Ultrasol", "ultra-moon" to "Ultraluna",
    "lets-go-pikachu" to "Let's Go, Pikachu!", "lets-go-eevee" to "Let's Go, Eevee!",
    "sword" to "Espada", "shield" to "Escudo",
    "the-isle-of-armor-sword" to "La isla de la armadura (Espada)",
    "the-isle-of-armor-shield" to "La isla de la armadura (Escudo)",
    "the-crown-tundra-sword" to "Las nieves de la corona (Espada)",
    "the-crown-tundra-shield" to "Las nieves de la corona (Escudo)",
    "brilliant-diamond" to "Diamante Brillante", "shining-pearl" to "Perla Reluciente",
    "legends-arceus" to "Leyendas Pokémon: Arceus",
    "scarlet" to "Escarlata", "violet" to "Púrpura",
    "the-teal-mask-scarlet" to "La máscara turquesa (Escarlata)",
    "the-teal-mask-violet" to "La máscara turquesa (Púrpura)",
    "the-indigo-disk-scarlet" to "El disco índigo (Escarlata)",
    "the-indigo-disk-violet" to "El disco índigo (Púrpura)",
    "legends-za" to "Leyendas Pokémon: Z-A",
    "mega-dimension" to "Leyendas Pokémon: Z-A – Mega Dimension",
    "champions" to "Pokémon Champions"
)
