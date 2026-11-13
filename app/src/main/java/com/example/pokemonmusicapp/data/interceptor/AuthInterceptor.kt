package com.example.pokemonmusicapp.data.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val pokemonApiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestUrl = originalRequest.url.toString()

        val newRequest = if (requestUrl.contains("pokemonpricetracker.com")) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $pokemonApiKey")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}
