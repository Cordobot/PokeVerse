package com.cordobot.pokeverse.domain

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
import com.cordobot.pokeverse.domain.model.PokemonType.ROCK
import com.cordobot.pokeverse.domain.model.PokemonType.STEEL
import com.cordobot.pokeverse.domain.model.PokemonType.WATER
import com.cordobot.pokeverse.domain.model.TypeChart
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TypeChartTest {

    @Test
    fun `charizard is 4x weak to rock and immune to ground`() {
        val matchups = TypeChart.matchups(listOf(FIRE, FLYING))

        assertEquals(ROCK to 4.0, matchups.weaknesses.first())
        assertTrue(WATER to 2.0 in matchups.weaknesses)
        assertTrue(ELECTRIC to 2.0 in matchups.weaknesses)
        assertEquals(listOf(GROUND), matchups.immunities)
        assertTrue(GRASS to 0.25 in matchups.resistances)
    }

    @Test
    fun `garchomp takes 4x from ice and is immune to electric`() {
        val defending = listOf(DRAGON, GROUND)

        assertEquals(4.0, TypeChart.effectiveness(ICE, defending), 0.0)
        assertEquals(0.0, TypeChart.effectiveness(ELECTRIC, defending), 0.0)
        assertEquals(2.0, TypeChart.effectiveness(FAIRY, defending), 0.0)
    }

    @Test
    fun `ghost and normal cannot hit each other`() {
        assertEquals(0.0, TypeChart.effectiveness(NORMAL, GHOST), 0.0)
        assertEquals(0.0, TypeChart.effectiveness(GHOST, NORMAL), 0.0)
        assertEquals(0.0, TypeChart.effectiveness(FIGHTING, GHOST), 0.0)
    }

    @Test
    fun `steel resists ten types and is immune to poison`() {
        val matchups = TypeChart.matchups(listOf(STEEL))

        assertEquals(10, matchups.resistances.size)
        assertEquals(1, matchups.immunities.size)
        assertEquals(setOf(FIRE, FIGHTING, GROUND), matchups.weaknesses.map { it.first }.toSet())
    }
}
