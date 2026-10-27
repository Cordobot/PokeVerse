package com.cordobot.pokeverse.presentation.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.cordobot.pokeverse.domain.model.EvolutionStage
import com.cordobot.pokeverse.domain.model.PokemonDetail
import com.cordobot.pokeverse.domain.model.PokemonType
import com.cordobot.pokeverse.domain.model.TypeMatchups
import com.cordobot.pokeverse.presentation.components.TypeBadge
import com.cordobot.pokeverse.presentation.components.composeColor

@Composable
fun PokemonDetailScreen(
    onBack: () -> Unit,
    onPokemonClick: (Int) -> Unit,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val current = state) {
            PokemonDetailUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            is PokemonDetailUiState.Error -> Column(
                modifier = Modifier.align(Alignment.Center).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(current.message, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Button(onClick = viewModel::load) { Text("Reintentar") }
            }
            is PokemonDetailUiState.Success -> DetailContent(current.pokemon, current.matchups, onPokemonClick)
        }
        // Keeps the status bar readable while the content scrolls under it
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(Color.Black.copy(alpha = 0.45f))
        )
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(50))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
        }
    }
}

@Composable
private fun DetailContent(pokemon: PokemonDetail, matchups: TypeMatchups, onPokemonClick: (Int) -> Unit) {
    val mainColor = pokemon.types.firstOrNull()?.composeColor ?: MaterialTheme.colorScheme.primary
    var shiny by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        // Header with the artwork over the color of the main type
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(mainColor, mainColor.copy(alpha = 0.35f), MaterialTheme.colorScheme.background)))
                .statusBarsPadding()
                .padding(top = 48.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = if (shiny) pokemon.shinyImageUrl else pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier.size(240.dp)
            )
            IconButton(
                onClick = { shiny = !shiny },
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 8.dp)
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = if (shiny) "Ver forma normal" else "Ver variocolor",
                    tint = if (shiny) Color(0xFFFFD54F) else Color.White
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            TitleBlock(pokemon)
            if (pokemon.description.isNotBlank()) {
                Text(pokemon.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            InfoGrid(pokemon)
            Section("Habilidades") {
                FlowChips(pokemon.abilities.map { if (it.isHidden) "${it.name} (oculta)" else it.name })
            }
            Section("Estadísticas base · Total ${pokemon.totalStats}") {
                pokemon.stats.forEach { StatBar(it.name, it.value, mainColor) }
            }
            Section("Debilidades") { MultiplierList(matchups.weaknesses, emptyText = "Ninguna") }
            Section("Resistencias") { MultiplierList(matchups.resistances, emptyText = "Ninguna") }
            if (matchups.immunities.isNotEmpty()) {
                Section("Inmunidades") { MultiplierList(matchups.immunities.map { it to 0.0 }, emptyText = "") }
            }
            if (pokemon.evolutions.size > 1) {
                Section("Cadena evolutiva") { EvolutionRow(pokemon.evolutions, pokemon.id, onPokemonClick) }
            }
            Section("Aparece en ${pokemon.games.size} juegos") {
                FlowChips(pokemon.games)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TitleBlock(pokemon: PokemonDetail) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(pokemon.number, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        Text(pokemon.name, style = MaterialTheme.typography.headlineLarge)
        if (pokemon.genus.isNotBlank()) {
            Text(pokemon.genus, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            pokemon.types.forEach { TypeBadge(it, fontSize = 14.sp) }
            when {
                pokemon.isMythical -> Tag("Singular", Color(0xFFE91E63))
                pokemon.isLegendary -> Tag("Legendario", Color(0xFFFFB300))
            }
        }
    }
}

@Composable
private fun InfoGrid(pokemon: PokemonDetail) {
    val items = listOf(
        "Región" to pokemon.region.displayName,
        "Generación" to pokemon.region.generationLabel,
        "Juegos de debut" to pokemon.region.mainGames,
        "Altura" to "${pokemon.heightMeters} m",
        "Peso" to "${pokemon.weightKg} kg",
        "Ratio de captura" to "${pokemon.captureRate}",
    ) + listOfNotNull(pokemon.habitat?.let { "Hábitat" to it })

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { (label, value) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp)
                    ) {
                        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(value, fontWeight = FontWeight.SemiBold)
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowChips(values: List<String>) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        values.forEach { Tag(it, MaterialTheme.colorScheme.surfaceVariant) }
    }
}

@Composable
private fun Tag(text: String, color: Color) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .background(color, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MultiplierList(values: List<Pair<PokemonType, Double>>, emptyText: String) {
    if (values.isEmpty()) {
        Text(emptyText, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        values.forEach { (type, multiplier) ->
            TypeBadge(type, label = "${type.displayName} ×${formatMultiplier(multiplier)}", fontSize = 13.sp)
        }
    }
}

private fun formatMultiplier(value: Double): String = when (value) {
    0.25 -> "¼"
    0.5 -> "½"
    else -> value.toInt().toString()
}

@Composable
private fun StatBar(name: String, value: Int, color: Color) {
    // Bars grow from zero when the screen opens
    var started by remember { mutableStateOf(false) }
    val fraction by animateFloatAsState(
        targetValue = if (started) (value / 255f).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "stat"
    )
    LaunchedEffect(Unit) { started = true }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(name, modifier = Modifier.width(84.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Text(value.toString(), modifier = Modifier.width(36.dp), fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .background(color, RoundedCornerShape(50))
            )
        }
    }
}

// Stages in order, side by side; branching evolutions (Eevee) wrap onto more lines
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EvolutionRow(stages: List<EvolutionStage>, currentId: Int, onPokemonClick: (Int) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        stages.sortedBy { it.stage }.forEach { stage ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(96.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (stage.id == currentId) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.surface
                    )
                    .clickable(enabled = stage.id != currentId) { onPokemonClick(stage.id) }
                    .padding(8.dp)
            ) {
                AsyncImage(model = stage.imageUrl, contentDescription = stage.name, modifier = Modifier.size(72.dp))
                Text(stage.name, fontSize = 12.sp, maxLines = 1)
                Text("Etapa ${stage.stage + 1}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
