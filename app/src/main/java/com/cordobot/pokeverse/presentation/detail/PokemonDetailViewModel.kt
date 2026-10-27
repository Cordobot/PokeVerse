package com.cordobot.pokeverse.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cordobot.pokeverse.domain.model.PokemonDetail
import com.cordobot.pokeverse.domain.model.TypeMatchups
import com.cordobot.pokeverse.domain.usecase.GetPokemonDetailUseCase
import com.cordobot.pokeverse.domain.usecase.GetTypeMatchupsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PokemonDetailUiState {
    data object Loading : PokemonDetailUiState
    data class Success(val pokemon: PokemonDetail, val matchups: TypeMatchups) : PokemonDetailUiState
    data class Error(val message: String) : PokemonDetailUiState
}

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPokemonDetail: GetPokemonDetailUseCase,
    private val getTypeMatchups: GetTypeMatchupsUseCase
) : ViewModel() {

    private val pokemonId: Int = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow<PokemonDetailUiState>(PokemonDetailUiState.Loading)
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = PokemonDetailUiState.Loading
            _uiState.value = getPokemonDetail(pokemonId).fold(
                onSuccess = { PokemonDetailUiState.Success(it, getTypeMatchups(it.types)) },
                onFailure = { PokemonDetailUiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }
}
