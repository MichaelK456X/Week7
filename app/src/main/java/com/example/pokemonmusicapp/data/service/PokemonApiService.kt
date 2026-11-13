package com.example.pokemonmusicapp.data.service

import com.example.pokemonmusicapp.data.model.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PokemonApiService {
    @GET("https://www.pokemonpricetracker.com/api/v2/cards")
    suspend fun searchCards(
        @Query("search") query: String,
        @Query("limit") limit: Int = 6
        //6 sudah cukup kebanyakan nnti cepet habis creditnya
    ): PokemonResponse
}
