package com.example.pokemonmusicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.pokemonmusicapp.data.container.AppContainer
import com.example.pokemonmusicapp.ui.route.AppRouting
import com.example.pokemonmusicapp.ui.theme.PokemonmusicappTheme
import com.example.pokemonmusicapp.ui.view.PokemonListView
import com.example.pokemonmusicapp.ui.viewmodel.MusicViewModel
import com.example.pokemonmusicapp.ui.viewmodel.PokemonViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = AppContainer()

        setContent {
            PokemonmusicappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {

                        // Soal 1: Pokemon App
                        val pokemonViewModel = remember { PokemonViewModel(appContainer.pokemonRepository) }
                        PokemonListView(viewModel = pokemonViewModel)

                        // Soal 2: Music App
//                         val musicViewModel = remember { MusicViewModel(appContainer.audioRepository) }
//                         AppRouting(viewModel = musicViewModel)
                        // dua"nya aku coba di hp bisa jalan, tapi yang music ini cuma bisa 1 lagu di albumnya
                    }
                }
            }
        }
    }
}


