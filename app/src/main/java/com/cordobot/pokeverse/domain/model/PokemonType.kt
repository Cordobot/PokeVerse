package com.cordobot.pokeverse.domain.model

/** The 18 Pokémon types, with their Spanish name and the color used to draw them. */
enum class PokemonType(val apiName: String, val displayName: String, val color: Long) {
    NORMAL("normal", "Normal", 0xFFA8A77A),
    FIRE("fire", "Fuego", 0xFFEE8130),
    WATER("water", "Agua", 0xFF6390F0),
    ELECTRIC("electric", "Eléctrico", 0xFFF7D02C),
    GRASS("grass", "Planta", 0xFF7AC74C),
    ICE("ice", "Hielo", 0xFF96D9D6),
    FIGHTING("fighting", "Lucha", 0xFFC22E28),
    POISON("poison", "Veneno", 0xFFA33EA1),
    GROUND("ground", "Tierra", 0xFFE2BF65),
    FLYING("flying", "Volador", 0xFFA98FF3),
    PSYCHIC("psychic", "Psíquico", 0xFFF95587),
    BUG("bug", "Bicho", 0xFFA6B91A),
    ROCK("rock", "Roca", 0xFFB6A136),
    GHOST("ghost", "Fantasma", 0xFF735797),
    DRAGON("dragon", "Dragón", 0xFF6F35FC),
    DARK("dark", "Siniestro", 0xFF705746),
    STEEL("steel", "Acero", 0xFFB7B7CE),
    FAIRY("fairy", "Hada", 0xFFD685AD);

    companion object {
        private val byApiName = entries.associateBy { it.apiName }

        fun fromApiName(name: String): PokemonType? = byApiName[name]
    }
}
