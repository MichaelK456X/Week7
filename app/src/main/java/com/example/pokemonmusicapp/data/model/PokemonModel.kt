package com.example.pokemonmusicapp.data.model

import com.google.gson.annotations.SerializedName

data class PokemonResponse(
    @SerializedName("data") val data: List<PokemonCard>?
)

data class PokemonCard(
    @SerializedName("tcgPlayerId") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("cardNumber") val cardNumber: String?,
    @SerializedName("setName") val setName: String?,
    @SerializedName("rarity") val rarity: String?,
    @SerializedName("prices") val prices: CardPrices?,
    @SerializedName("imageCdnUrl400") val imageUrl: String?
) {
    val marketPrice: Double? get() = prices?.market
    val lowestPrice: Double? get() = prices?.low
    val activeListings: Int? get() = prices?.listings
    val verifiedSellers: Int? get() = prices?.sellers
    val primaryPrinting: String? get() = prices?.primaryPrinting
}

data class CardPrices(
    @SerializedName("market") val market: Double?,
    @SerializedName("low") val low: Double?,
    @SerializedName("sellers") val sellers: Int?,
    @SerializedName("listings") val listings: Int?,
    @SerializedName("primaryPrinting") val primaryPrinting: String?
)
