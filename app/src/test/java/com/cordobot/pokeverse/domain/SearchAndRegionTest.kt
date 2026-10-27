package com.cordobot.pokeverse.domain

import com.cordobot.pokeverse.data.mapper.gameNames
import com.cordobot.pokeverse.data.mapper.prettyName
import com.cordobot.pokeverse.domain.model.PokemonSummary
import com.cordobot.pokeverse.domain.model.PokemonType.FAIRY
import com.cordobot.pokeverse.domain.model.PokemonType.FIRE
import com.cordobot.pokeverse.domain.model.PokemonType.GRASS
import com.cordobot.pokeverse.domain.model.Region
import com.cordobot.pokeverse.domain.usecase.PokemonFilter
import com.cordobot.pokeverse.domain.usecase.SearchPokemonUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchAndRegionTest {

    private val all = listOf(
        PokemonSummary(1, "Bulbasaur", listOf(GRASS)),
        PokemonSummary(4, "Charmander", listOf(FIRE)),
        PokemonSummary(669, "Flabébé", listOf(FAIRY)),
        PokemonSummary(909, "Fuecoco", listOf(FIRE))
    )

    @Test
    fun `regions come from the national dex number and the api generation`() {
        assertEquals(Region.KANTO, Region.fromNationalDex(151))
        assertEquals(Region.JOHTO, Region.fromNationalDex(152))
        assertEquals(Region.PALDEA, Region.fromNationalDex(1025))
        assertEquals(Region.SINNOH, Region.fromGenerationName("generation-iv"))
        assertEquals(Region.GALAR, Region.fromGenerationName("generation-viii"))
    }

    @Test
    fun `search ignores accents and matches numbers`() {
        assertEquals(listOf(669), SearchPokemonUseCase.filter(all, PokemonFilter(query = "flabebe")).map { it.id })
        assertEquals(listOf(4), SearchPokemonUseCase.filter(all, PokemonFilter(query = "#004")).map { it.id })
    }

    @Test
    fun `filters by type and region together`() {
        val fireInPaldea = SearchPokemonUseCase.filter(all, PokemonFilter(type = FIRE, region = Region.PALDEA))

        assertEquals(listOf(909), fireInPaldea.map { it.id })
    }

    @Test
    fun `names and games are shown in spanish and release order`() {
        assertEquals("Mr. Mime", prettyName("mr-mime"))
        assertEquals("Great Tusk", prettyName("great-tusk"))
        assertEquals(listOf("Rojo", "Oro", "Escarlata"), gameNames(listOf("scarlet", "red", "gold", "red")))
    }
}
