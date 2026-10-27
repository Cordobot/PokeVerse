package com.cordobot.pokeverse.data.repository

import com.cordobot.pokeverse.data.local.PokemonDao
import com.cordobot.pokeverse.data.local.PokemonDetailEntity
import com.cordobot.pokeverse.data.local.PokemonEntity
import com.cordobot.pokeverse.data.mapper.prettyName
import com.cordobot.pokeverse.data.mapper.toDetail
import com.cordobot.pokeverse.data.mapper.toDomain
import com.cordobot.pokeverse.data.mapper.toStages
import com.cordobot.pokeverse.data.remote.PokeApiService
import com.cordobot.pokeverse.domain.model.PokemonDetail
import com.cordobot.pokeverse.domain.model.PokemonSummary
import com.cordobot.pokeverse.domain.model.PokemonType
import com.cordobot.pokeverse.domain.repository.PokemonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApiService,
    private val dao: PokemonDao,
    private val json: Json
) : PokemonRepository {

    override fun observePokemon(): Flow<List<PokemonSummary>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun refreshPokemon(force: Boolean): Result<Unit> = runCatchingNetwork {
        if (!force && dao.count() > 0) return@runCatchingNetwork

        // One call for every species plus one per type (18) gives names and types of all Pokémon
        val entities = coroutineScope {
            val species = async { api.getSpeciesList() }
            val typeLists = PokemonType.entries.map { type -> async { type to api.getType(type.apiName) } }

            val typesById = mutableMapOf<Int, MutableList<Pair<Int, PokemonType>>>()
            typeLists.awaitAll().forEach { (type, dto) ->
                dto.pokemon.forEach { typesById.getOrPut(it.pokemon.id) { mutableListOf() } += it.slot to type }
            }
            species.await().results.map { resource ->
                PokemonEntity(
                    id = resource.id,
                    name = prettyName(resource.name),
                    types = typesById[resource.id].orEmpty().sortedBy { it.first }.joinToString(",") { it.second.apiName }
                )
            }.sortedBy { it.id }
        }
        dao.replaceAll(entities)
    }

    override suspend fun getPokemonDetail(id: Int): Result<PokemonDetail> {
        dao.getDetail(id)?.let { cached ->
            return Result.success(json.decodeFromString<PokemonDetail>(cached.json))
        }
        return runCatchingNetwork {
            val detail = coroutineScope {
                val pokemon = async { api.getPokemon(id) }
                val species = api.getSpecies(id)
                val evolutions = species.evolutionChain
                    ?.let { api.getEvolutionChain(it.id).chain.toStages() }
                    .orEmpty()
                pokemon.await().toDetail(species, evolutions)
            }
            dao.insertDetail(PokemonDetailEntity(id, json.encodeToString(PokemonDetail.serializer(), detail), System.currentTimeMillis()))
            detail
        }
    }

    private inline fun <T> runCatchingNetwork(block: () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: HttpException) {
        Result.failure(Exception(if (e.code() == 404) "Pokémon no encontrado" else "Error del servidor (${e.code()})"))
    } catch (e: IOException) {
        Result.failure(Exception("Sin conexión. Conéctate a internet para descargar estos datos."))
    }
}
