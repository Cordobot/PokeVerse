package com.cordobot.pokeverse.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cordobot.pokeverse.presentation.detail.PokemonDetailScreen
import com.cordobot.pokeverse.presentation.list.PokemonListScreen

private const val LIST = "list"
private const val DETAIL = "detail/{id}"

@Composable
fun PokeVerseNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = LIST) {
        composable(LIST) {
            PokemonListScreen(onPokemonClick = { id -> navController.navigate("detail/$id") })
        }
        composable(
            route = DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
            enterTransition = { slideInHorizontally { it / 3 } + fadeIn() },
            exitTransition = { fadeOut() },
            popExitTransition = { slideOutHorizontally { it / 3 } + fadeOut() }
        ) {
            PokemonDetailScreen(
                onBack = { navController.popBackStack() },
                onPokemonClick = { id ->
                    navController.navigate("detail/$id") { popUpTo(LIST) }
                }
            )
        }
    }
}
