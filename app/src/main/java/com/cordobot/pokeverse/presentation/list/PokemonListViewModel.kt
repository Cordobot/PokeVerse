package com.cordobot.pokeverse.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cordobot.pokeverse.domain.model.PokemonSummary
import com.cordobot.pokeverse.domain.model.PokemonType
import com.cordobot.pokeverse.domain.model.Region
import com.cordobot.pokeverse.domain.repository.PokemonRepository
import com.cordobot.pokeverse.domain.usecase.PokemonFilter
import com.cordobot.pokeverse.domain.usecase.RefreshPokemonUseCase
import com.cordobot.pokeverse.domain.usecase.SearchPokemonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PokemonListUiState(
    val pokemon: List<PokemonSummary> = emptyList(),
    val totalCount: Int = 0,
    val filter: PokemonFilter = PokemonFilter(),
    val isSyncing: Boolean = false,
    val error: String? = null
) {
    val hasFilters: Boolean get() = filter.query.isNotBlank() || filter.region != null || filter.type != null
}

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    repository: PokemonRepository,
    searchPokemon: SearchPokemonUseCase,
    private val refreshPokemon: RefreshPokemonUseCase
) : ViewModel() {

    private val filter = MutableStateFlow(PokemonFilter())
    private val sync = MutableStateFlow(SyncState())

    val uiState: StateFlow<PokemonListUiState> = combine(
        searchPokemon(filter),
        repository.observePokemon(),
        filter,
        sync
    ) { results, all, currentFilter, syncState ->
        PokemonListUiState(
            pokemon = results,
            totalCount = all.size,
            filter = currentFilter,
            isSyncing = syncState.isSyncing,
            error = syncState.error
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PokemonListUiState(isSyncing = true))

    init {
        refresh(force = false)
    }

    fun refresh(force: Boolean = true) {
        viewModelScope.launch {
            sync.value = SyncState(isSyncing = true)
            val result = refreshPokemon(force)
            sync.value = SyncState(error = result.exceptionOrNull()?.message)
        }
    }

    fun onQueryChange(query: String) = filter.update { it.copy(query = query) }

    fun onRegionSelected(region: Region?) = filter.update { it.copy(region = if (it.region == region) null else region) }

    fun onTypeSelected(type: PokemonType?) = filter.update { it.copy(type = if (it.type == type) null else type) }

    fun clearFilters() {
        filter.value = PokemonFilter()
    }

    private data class SyncState(val isSyncing: Boolean = false, val error: String? = null)
}
