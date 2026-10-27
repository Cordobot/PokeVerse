package com.cordobot.pokeverse.domain.model

import com.cordobot.pokeverse.domain.model.PokemonType.BUG
import com.cordobot.pokeverse.domain.model.PokemonType.DARK
import com.cordobot.pokeverse.domain.model.PokemonType.DRAGON
import com.cordobot.pokeverse.domain.model.PokemonType.ELECTRIC
import com.cordobot.pokeverse.domain.model.PokemonType.FAIRY
import com.cordobot.pokeverse.domain.model.PokemonType.FIGHTING
import com.cordobot.pokeverse.domain.model.PokemonType.FIRE
import com.cordobot.pokeverse.domain.model.PokemonType.FLYING
import com.cordobot.pokeverse.domain.model.PokemonType.GHOST
import com.cordobot.pokeverse.domain.model.PokemonType.GRASS
import com.cordobot.pokeverse.domain.model.PokemonType.GROUND
import com.cordobot.pokeverse.domain.model.PokemonType.ICE
import com.cordobot.pokeverse.domain.model.PokemonType.NORMAL
import com.cordobot.pokeverse.domain.model.PokemonType.POISON
import com.cordobot.pokeverse.domain.model.PokemonType.PSYCHIC
import com.cordobot.pokeverse.domain.model.PokemonType.ROCK
import com.cordobot.pokeverse.domain.model.PokemonType.STEEL
import com.cordobot.pokeverse.domain.model.PokemonType.WATER

/** Damage multipliers of the current games (generation VI onwards). */
object TypeChart {

    private class Row(
        val superEffective: Set<PokemonType> = emptySet(),
        val notVeryEffective: Set<PokemonType> = emptySet(),
        val noEffect: Set<PokemonType> = emptySet()
    )

    // Attacking type -> how it hits each defending type
    private val chart: Map<PokemonType, Row> = mapOf(
        NORMAL to Row(notVeryEffective = setOf(ROCK, STEEL), noEffect = setOf(GHOST)),
        FIRE to Row(setOf(GRASS, ICE, BUG, STEEL), setOf(FIRE, WATER, ROCK, DRAGON)),
        WATER to Row(setOf(FIRE, GROUND, ROCK), setOf(WATER, GRASS, DRAGON)),
        ELECTRIC to Row(setOf(WATER, FLYING), setOf(ELECTRIC, GRASS, DRAGON), setOf(GROUND)),
        GRASS to Row(setOf(WATER, GROUND, ROCK), setOf(FIRE, GRASS, POISON, FLYING, BUG, DRAGON, STEEL)),
        ICE to Row(setOf(GRASS, GROUND, FLYING, DRAGON), setOf(FIRE, WATER, ICE, STEEL)),
        FIGHTING to Row(setOf(NORMAL, ICE, ROCK, DARK, STEEL), setOf(POISON, FLYING, PSYCHIC, BUG, FAIRY), setOf(GHOST)),
        POISON to Row(setOf(GRASS, FAIRY), setOf(POISON, GROUND, ROCK, GHOST), setOf(STEEL)),
        GROUND to Row(setOf(FIRE, ELECTRIC, POISON, ROCK, STEEL), setOf(GRASS, BUG), setOf(FLYING)),
        FLYING to Row(setOf(GRASS, FIGHTING, BUG), setOf(ELECTRIC, ROCK, STEEL)),
        PSYCHIC to Row(setOf(FIGHTING, POISON), setOf(PSYCHIC, STEEL), setOf(DARK)),
        BUG to Row(setOf(GRASS, PSYCHIC, DARK), setOf(FIRE, FIGHTING, POISON, FLYING, GHOST, STEEL, FAIRY)),
        ROCK to Row(setOf(FIRE, ICE, FLYING, BUG), setOf(FIGHTING, GROUND, STEEL)),
        GHOST to Row(setOf(PSYCHIC, GHOST), setOf(DARK), setOf(NORMAL)),
        DRAGON to Row(setOf(DRAGON), setOf(STEEL), setOf(FAIRY)),
        DARK to Row(setOf(PSYCHIC, GHOST), setOf(FIGHTING, DARK, FAIRY)),
        STEEL to Row(setOf(ICE, ROCK, FAIRY), setOf(FIRE, WATER, ELECTRIC, STEEL)),
        FAIRY to Row(setOf(FIGHTING, DRAGON, DARK), setOf(FIRE, POISON, STEEL))
    )

    /** Multiplier of an [attacking] move against a single [defending] type. */
    fun effectiveness(attacking: PokemonType, defending: PokemonType): Double {
        val row = chart.getValue(attacking)
        return when (defending) {
            in row.noEffect -> 0.0
            in row.superEffective -> 2.0
            in row.notVeryEffective -> 0.5
            else -> 1.0
        }
    }

    /** Multiplier against a Pokémon with one or two types: the single-type multipliers multiply. */
    fun effectiveness(attacking: PokemonType, defending: List<PokemonType>): Double =
        defending.fold(1.0) { acc, type -> acc * effectiveness(attacking, type) }

    /** How every attacking type hits a Pokémon with these [types], grouped for the detail screen. */
    fun matchups(types: List<PokemonType>): TypeMatchups {
        val multipliers = PokemonType.entries.associateWith { effectiveness(it, types) }
        return TypeMatchups(
            weaknesses = multipliers.filterValues { it > 1.0 }.sortedByMultiplier(descending = true),
            resistances = multipliers.filterValues { it > 0.0 && it < 1.0 }.sortedByMultiplier(descending = false),
            immunities = multipliers.filterValues { it == 0.0 }.keys.toList()
        )
    }

    private fun Map<PokemonType, Double>.sortedByMultiplier(descending: Boolean): List<Pair<PokemonType, Double>> =
        toList().sortedWith(
            if (descending) compareByDescending<Pair<PokemonType, Double>> { it.second }.thenBy { it.first.ordinal }
            else compareBy<Pair<PokemonType, Double>> { it.second }.thenBy { it.first.ordinal }
        )
}

data class TypeMatchups(
    val weaknesses: List<Pair<PokemonType, Double>>,
    val resistances: List<Pair<PokemonType, Double>>,
    val immunities: List<PokemonType>
)
