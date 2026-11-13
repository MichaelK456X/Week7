package com.example.pokemonmusicapp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pokemonmusicapp.ui.theme.*
import com.example.pokemonmusicapp.ui.viewmodel.AlbumDetailState
import com.example.pokemonmusicapp.ui.viewmodel.MusicViewModel

@Composable
fun MusicDetailAlbumView(albumId: String, viewModel: MusicViewModel, onBack: () -> Unit) {
    var isDescExpanded by remember { mutableStateOf(false) }
    LaunchedEffect(albumId) { viewModel.loadAlbumDetail(albumId) }
    val state by viewModel.albumState.collectAsState()

    Column(Modifier.fillMaxSize().background(DarkBackground).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextWhite) }
            Text("Album Detail", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))

        when (val s = state) {
            is AlbumDetailState.Loading -> Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = PrimaryGreen)
                Spacer(Modifier.height(10.dp))
                Text("Loading album detail & track list...", color = TextGray, fontSize = 12.sp)
            }
            is AlbumDetailState.Error -> Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                Text("Connection Failed", color = TextWhite, fontWeight = FontWeight.Bold)
                Text(s.message, color = TextGray, fontSize = 12.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Button({ viewModel.loadAlbumDetail(albumId) }, colors = ButtonDefaults.buttonColors(ErrorRed)) {
                    Text("Try Again", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
            is AlbumDetailState.Success -> LazyColumn(Modifier.fillMaxSize()) {
                item {
                    s.album?.let { album ->
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(
                                model = album.strAlbumThumb, contentDescription = album.strAlbum, contentScale = ContentScale.Crop,
                                modifier = Modifier.size(180.dp).clip(RoundedCornerShape(12.dp)).background(CardBackground)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(album.strAlbum, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Text("${album.strArtist ?: ""} • Year ${album.intYearReleased ?: "-"} • ${album.strGenre ?: ""}", color = PrimaryGreen, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(14.dp))

                        val desc = album.strDescriptionEN?.takeIf { it.isNotBlank() }
                            ?: "${album.strAlbum} is an official studio album by ${album.strArtist ?: "this artist"}, originally released in ${album.intYearReleased ?: "an unknown year"} under the ${album.strGenre ?: "Music"} genre.\n\n" +
                            "Recorded with a focus on cohesive production, memorable melodies, and expressive instrumentation, this release marks an important milestone in ${album.strArtist ?: "the artist"}'s musical catalog. The arrangement across the record balances dynamic energy with atmospheric depth, reflecting the creative direction of the era in which it was produced.\n\n" +
                            "Critics and listeners alike have recognized the album for its distinct soundscape and songwriting craft, making it a staple release within the ${album.strGenre ?: "Music"} scene. Every composition is structured to build a unified listening experience from the opening track through to the closing notes.\n\n" +
                            "Explore the track list below to view individual song titles, genre tags, and official track durations retrieved directly from the music database."

                        Text("Album Description", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = desc, color = TextGray, fontSize = 12.sp, lineHeight = 18.sp,
                            maxLines = if (isDescExpanded) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isDescExpanded) "Show less" else "Read more",
                            color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { isDescExpanded = !isDescExpanded }.padding(vertical = 4.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), Arrangement.SpaceBetween) {
                        Text("🎵 Track List", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("${s.tracks.size} Tracks", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                itemsIndexed(s.tracks) { index, track ->
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 6.dp).background(CardBackground, RoundedCornerShape(8.dp)).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(String.format("%02d", index + 1), color = PrimaryGreen, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
                        Column(Modifier.weight(1f)) {
                            Text(track.strTrack, color = TextWhite, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(track.strGenre ?: s.album?.strGenre ?: "Music", color = TextGray, fontSize = 11.sp)
                        }
                        Text(track.formattedDuration(), color = TextGray, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
