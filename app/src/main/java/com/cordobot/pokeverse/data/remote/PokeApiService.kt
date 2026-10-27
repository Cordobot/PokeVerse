package com.cordobot.pokeverse.data.remote

import com.cordobot.pokeverse.data.remote.dto.EvolutionChainDto
import com.cordobot.pokeverse.data.remote.dto.NamedResourceListDto
import com.cordobot.pokeverse.data.remote.dto.PokemonDto
import com.cordobot.pokeverse.data.remote.dto.SpeciesDto
import com.cordobot.pokeverse.data.remote.dto.TypeDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** [PokeAPI](https://pokeapi.co/docs/v2): free, no key required. */
interface PokeApiService {

    // A limit above the total returns every species in a single page
    @GET("pokemon-species")
    suspend fun getSpeciesList(@Query("limit") limit: Int = 5000): NamedResourceListDto

    @GET("type/{name}")
    suspend fun getType(@Path("name") name: String): TypeDto

    @GET("pokemon/{id}")
    suspend fun getPokemon(@Path("id") id: Int): PokemonDto

    @GET("pokemon-species/{id}")
    suspend fun getSpecies(@Path("id") id: Int): SpeciesDto

    @GET("evolution-chain/{id}")
    suspend fun getEvolutionChain(@Path("id") id: Int): EvolutionChainDto

    companion object {
        const val BASE_URL = "https://pokeapi.co/api/v2/"
    }
}
