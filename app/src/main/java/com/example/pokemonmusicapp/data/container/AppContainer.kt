package com.example.pokemonmusicapp.data.container

import com.example.pokemonmusicapp.data.interceptor.AuthInterceptor
import com.example.pokemonmusicapp.data.repository.AudioRepository
import com.example.pokemonmusicapp.data.repository.PokemonRepository
import com.example.pokemonmusicapp.data.service.AudioApiService
import com.example.pokemonmusicapp.data.service.PokemonApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {

    private val POKEMON_API_KEY = "pokeprice_free_4de381ea6854840ea5706feb97e2fa59319dfae71c2052a3"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(POKEMON_API_KEY))
        .build()
    private val pokemonRetrofit = Retrofit.Builder()
        .baseUrl("https://pokemonpricetracker.com")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val audioRetrofit = Retrofit.Builder()
        .baseUrl("https://www.theaudiodb.com/api/v1/json/123/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val pokemonApiService: PokemonApiService by lazy {
        pokemonRetrofit.create(PokemonApiService::class.java)
    }

    val audioApiService: AudioApiService by lazy {
        audioRetrofit.create(AudioApiService::class.java)
    }

    val pokemonRepository: PokemonRepository by lazy {
        PokemonRepository(pokemonApiService)
    }
    // ini gantian nyala sama audiorepo tergantung app mana yang dibuka

    val audioRepository: AudioRepository by lazy {
        AudioRepository(audioApiService)
    }
}


//pokeprice_free_4de381ea6854840ea5706feb97e2fa59319dfae71c2052a3