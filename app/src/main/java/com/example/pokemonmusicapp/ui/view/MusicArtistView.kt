package com.example.pokemonmusicapp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pokemonmusicapp.ui.theme.*
import com.example.pokemonmusicapp.ui.viewmodel.MusicArtistState
import com.example.pokemonmusicapp.ui.viewmodel.MusicViewModel

@Composable
fun MusicArtistView(viewModel: MusicViewModel, onAlbumClick: (String) -> Unit) {
    var query by remember { mutableStateOf("Coldplay") }
    var isExpanded by remember { mutableStateOf(false) }
    val state by viewModel.artistState.collectAsState()

    Column(Modifier.fillMaxSize().background(DarkBackground).padding(17.dp)) {
        Text("Music Artist", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(13.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search Artist...", color = TextGray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = TextGray) },
            trailingIcon = {
                if (query.isNotEmpty()) IconButton({ query = "" }) { Icon(Icons.Default.Close, null, tint = TextGray) }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                focusedContainerColor = CardBackground, unfocusedContainerColor = CardBackground,
                focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.Transparent
            )
        )

        Button(
            onClick = { viewModel.searchMusicArtist(query) },
            colors = ButtonDefaults.buttonColors(PrimaryGreen),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 7.dp)
        ) { Text("Search", color = Color.Black, fontWeight = FontWeight.Bold) }

        Spacer(Modifier.height(11.dp))

        when (val s = state) {
            is MusicArtistState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = PrimaryGreen) }
            is MusicArtistState.Error -> Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                Text("Connection Failed", color = TextWhite, fontWeight = FontWeight.Bold)
                Text(s.message, color = TextGray, fontSize = 11.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(11.dp))
                Button({ viewModel.searchMusicArtist(query) }, colors = ButtonDefaults.buttonColors(ErrorRed)) {
                    Text("Try Again", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
            is MusicArtistState.Success -> {
                val artist = s.artist ?: return@Column Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Artist not found.", color = TextGray)
                }

                val bioText = artist.strBiographyEN?.takeIf { it.isNotBlank() }
                    ?: "${artist.strArtist} is a prominent ${artist.strGenre ?: "music"} act originating from ${artist.strCountry ?: "around the world"}. Known for their distinctive musical style and critically acclaimed releases, ${artist.strArtist} has built a dedicated global fanbase across multiple studio albums and live performances. Explore their official discography below to view album details, release years, and featured tracks."

                LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item(span = { GridItemSpan(2) }) {
                        Column {
                            AsyncImage(
                                model = artist.strArtistThumb ?: artist.strArtistBanner,
                                contentDescription = artist.strArtist,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)).background(CardBackground)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(artist.strArtist, color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text("${artist.strGenre ?: "Music"} • ${artist.strCountry ?: ""}", color = PrimaryGreen, fontSize = 12.sp)
                            Spacer(Modifier.height(10.dp))
                            Text("About Artist", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = bioText,
                                color = TextGray, fontSize = 11.sp,
                                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isExpanded) "Show less" else "Read more",
                                color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { isExpanded = !isExpanded }.padding(vertical = 4.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                Text("Album Discography", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("${s.albums.size} Albums", color = PrimaryGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(s.albums) { album ->
                        Column(
                            Modifier.background(CardBackground, RoundedCornerShape(10.dp)).clickable { onAlbumClick(album.idAlbum) }.padding(8.dp)
                        ) {
                            AsyncImage(
                                model = album.strAlbumThumb, contentDescription = album.strAlbum, contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp)).background(DarkBackground)
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(album.strAlbum, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${album.intYearReleased ?: "-"} • ${album.strGenre ?: ""}", color = TextGray, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                }
            }
            else -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Type artist name above to search", color = TextGray) }
        }
    }
}
