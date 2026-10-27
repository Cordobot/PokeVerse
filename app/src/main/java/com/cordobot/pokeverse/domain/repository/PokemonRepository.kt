package com.cordobot.pokeverse.domain.repository

import com.cordobot.pokeverse.domain.model.PokemonDetail
import com.cordobot.pokeverse.domain.model.PokemonSummary
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    /** Every cached Pokémon in National Pokédex order; it updates when the cache is refreshed. */
    fun observePokemon(): Flow<List<PokemonSummary>>

    /** Downloads the full list (names and types) unless it is cached already, or [force] is set. */
    suspend fun refreshPokemon(force: Boolean = false): Result<Unit>

    /** Detail of one Pokémon: from the offline cache when available, otherwise from the API. */
    suspend fun getPokemonDetail(id: Int): Result<PokemonDetail>
}
