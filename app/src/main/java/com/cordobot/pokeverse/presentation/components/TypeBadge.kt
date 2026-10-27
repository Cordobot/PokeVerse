package com.cordobot.pokeverse.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cordobot.pokeverse.domain.model.PokemonType

val PokemonType.composeColor: Color get() = Color(color)

@Composable
fun TypeBadge(
    type: PokemonType,
    modifier: Modifier = Modifier,
    label: String = type.displayName,
    fontSize: TextUnit = 12.sp
) {
    Text(
        text = label,
        color = Color.White,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(type.composeColor, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    )
}
