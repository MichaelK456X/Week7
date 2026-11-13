package com.example.pokemonmusicapp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.pokemonmusicapp.data.model.PokemonCard
import com.example.pokemonmusicapp.ui.theme.*
import com.example.pokemonmusicapp.ui.viewmodel.PokemonUiState
import com.example.pokemonmusicapp.ui.viewmodel.PokemonViewModel

@Composable
fun PokemonListView(viewModel: PokemonViewModel) {
    var query by remember { mutableStateOf("Charizard") }
    var selectedCard by remember { mutableStateOf<PokemonCard?>(null) }
    val state by viewModel.uiState.collectAsState()

    Column(Modifier.fillMaxSize().background(DarkBackground).padding(16.dp)) {
        Text("PokéTracker", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Real-time TCG Card Valuation", color = TextGray, fontSize = 11.sp)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search Card...", color = TextGray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = TextGray) },
            trailingIcon = {
                if (query.isNotEmpty()) IconButton({ query = "" }) { Icon(Icons.Default.Close, null, tint = TextGray) }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                focusedContainerColor = CardBackground, unfocusedContainerColor = CardBackground,
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.Transparent
            )
        )

        Row(Modifier.padding(top = 8.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Charizard", "Pikachu", "Mewtwo", "Gengar").forEach { chip ->
                val active = query.equals(chip, true)
                Text(
                    text = chip,
                    color = if (active) Color.Black else TextGray,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(if (active) PrimaryGreen else CardBackground, RoundedCornerShape(16.dp))
                        .clickable { query = chip; viewModel.searchPokemon(chip) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Button(
            onClick = { viewModel.searchPokemon(query) },
            colors = ButtonDefaults.buttonColors(PrimaryGreen),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) { Text("Search Cards", color = Color.Black, fontWeight = FontWeight.Bold) }

        Spacer(Modifier.height(12.dp))

        when (val s = state) {
            is PokemonUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = PrimaryGreen) }
            is PokemonUiState.Error -> Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                Text("Connection Failed", color = TextWhite, fontWeight = FontWeight.Bold)
                Text(s.message, color = TextGray, fontSize = 12.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Button({ viewModel.searchPokemon(query) }, colors = ButtonDefaults.buttonColors(ErrorRed)) {
                    Text("Try Again", color = TextWhite)
                }
            }
            is PokemonUiState.Success -> {
                Text("Market Results (${s.cards.size})", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxSize()) {
                    items(s.cards) { card -> PokemonCard(card) { selectedCard = card } }
                }
            }
            else -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Pilih nama Pokémon atau klik Search", color = TextGray) }
        }
    }

    selectedCard?.let { card ->
        Dialog({ selectedCard = null }, DialogProperties(usePlatformDefaultWidth = false)) {
            Column(
                Modifier.fillMaxSize().background(DarkBackground).verticalScroll(rememberScrollState()).padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(Modifier.fillMaxWidth(), Alignment.CenterEnd) {
                    IconButton({ selectedCard = null }, Modifier.background(CardBackground, CircleShape)) {
                        Icon(Icons.Default.Close, null, tint = TextWhite)
                    }
                }
                AsyncImage(card.imageUrl, card.name, Modifier.height(260.dp).fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                Text("${card.name} - ${card.cardNumber ?: "-"}", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(card.setName ?: "-", color = TextGray, fontSize = 12.sp)
                Spacer(Modifier.height(14.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PriceBox("Market Price", "$${String.format("%.2f", card.marketPrice ?: 0.0)}", PriceGreen, Modifier.weight(1f))
                    PriceBox("Lowest Price", "$${String.format("%.2f", card.lowestPrice ?: 0.0)}", PriceOrange, Modifier.weight(1f))
                }

                SpecSection("Market Activity", listOf(
                    "Active Listings" to "${card.activeListings ?: 0}",
                    "Verified Sellers" to "${card.verifiedSellers ?: 0}",
                    "Primary Printing" to (card.primaryPrinting ?: "Normal")
                ))

                SpecSection("Card Specifications", listOf(
                    "Card Number" to (card.cardNumber ?: "-"),
                    "Rarity" to (card.rarity ?: "Common"),
                    "Set Name" to (card.setName ?: "-")
                ))

                Spacer(Modifier.height(20.dp))
                Button({ selectedCard = null }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(ErrorRed)) {
                    Text("Done", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PriceBox(label: String, value: String, color: Color, modifier: Modifier) {
    Column(modifier.background(CardBackground, RoundedCornerShape(10.dp)).padding(12.dp)) {
        Text(label, color = TextGray, fontSize = 11.sp)
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SpecSection(title: String, rows: List<Pair<String, String>>) {
    Spacer(Modifier.height(14.dp))
    Text(title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(6.dp))
    Column(Modifier.fillMaxWidth().background(CardBackground, RoundedCornerShape(10.dp)).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { (k, v) ->
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(k, color = TextGray, fontSize = 12.sp)
                Text(v, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
