package com.cordobot.pokeverse.domain.usecase

import com.cordobot.pokeverse.domain.model.PokemonSummary
import com.cordobot.pokeverse.domain.model.PokemonType
import com.cordobot.pokeverse.domain.model.Region
import com.cordobot.pokeverse.domain.model.TypeChart
import com.cordobot.pokeverse.domain.model.TypeMatchups
import com.cordobot.pokeverse.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.Normalizer
import javax.inject.Inject

data class PokemonFilter(
    val query: String = "",
    val region: Region? = null,
    val type: PokemonType? = null
)

/** The Pokémon list narrowed by name/number, region and type. */
class SearchPokemonUseCase @Inject constructor(private val repository: PokemonRepository) {

    operator fun invoke(filter: Flow<PokemonFilter>): Flow<List<PokemonSummary>> =
        combine(repository.observePokemon(), filter) { all, current -> filter(all, current) }

    companion object {
        fun filter(all: List<PokemonSummary>, filter: PokemonFilter): List<PokemonSummary> {
            val query = filter.query.trim().normalized()
            return all.filter { pokemon ->
                (filter.region == null || pokemon.region == filter.region) &&
                    (filter.type == null || filter.type in pokemon.types) &&
                    (query.isEmpty() || pokemon.name.normalized().contains(query) ||
                        pokemon.id.toString() == query.trimStart('#', '0'))
            }
        }

        // "Flabébé" matches "flabebe"
        private fun String.normalized(): String =
            Normalizer.normalize(lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")
    }
}

class RefreshPokemonUseCase @Inject constructor(private val repository: PokemonRepository) {
    suspend operator fun invoke(force: Boolean = false) = repository.refreshPokemon(force)
}

class GetPokemonDetailUseCase @Inject constructor(private val repository: PokemonRepository) {
    suspend operator fun invoke(id: Int) = repository.getPokemonDetail(id)
}

class GetTypeMatchupsUseCase @Inject constructor() {
    operator fun invoke(types: List<PokemonType>): TypeMatchups = TypeChart.matchups(types)
}
