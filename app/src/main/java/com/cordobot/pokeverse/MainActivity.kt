package com.cordobot.pokeverse

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.cordobot.pokeverse.presentation.navigation.PokeVerseNavHost
import com.cordobot.pokeverse.presentation.theme.PokeVerseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Dark theme only: light status/navigation bar icons
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            PokeVerseTheme {
                // Surface provides the light content color for every Text in the app
                Surface(color = MaterialTheme.colorScheme.background) {
                    PokeVerseNavHost()
                }
            }
        }
    }
}
