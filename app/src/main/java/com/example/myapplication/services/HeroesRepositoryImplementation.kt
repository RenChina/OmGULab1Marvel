package com.example.marvel_app.data

import com.example.marvel_app.data.api.CharactersResponse
import com.example.marvel_app.data.api.MarvelApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.math.BigInteger
import java.security.MessageDigest

private const val PUBLIC_API_KEY = "0d3fae138e0413293d08a0be1d45b931"
private const val PRIVATE_API_KEY = "a5561da3558650adbc2ec5f7099abe7a9e34e577"
private const val MILLIS_IN_SECOND = 1000L

class HeroesRepositoryImplementation : HeroesRepository {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://gateway.marvel.com/v1/public/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val api: MarvelApi = retrofit.create()

    override suspend fun getAllHeroes(): List<Superhero> {
        val timestamp = getCurrentTimestamp()
        val charactersResponse = api.getSuperheroes(
            apiKey = PUBLIC_API_KEY,
            timeStamp = timestamp,
            hash = getHash(timestamp),
        )
        return mapToSuperheroes(charactersResponse)
    }

    override suspend fun getHeroById(id: String): Superhero {
        val timestamp = getCurrentTimestamp()
        val charactersResponse = api.getSuperhero(
            heroId = id,
            apiKey = PUBLIC_API_KEY,
            timeStamp = timestamp,
            hash = getHash(timestamp),
        )
        return mapToSuperheroes(charactersResponse)[0]
    }

    private fun mapToSuperheroes(charactersResponse: CharactersResponse): List<Superhero> {
        return charactersResponse.data.results.map { hero ->
            val imagePath = if (hero.image.path.startsWith("http://")) {
                hero.image.path.replace("http", "https")
            } else {
                hero.image.path
            }

            Superhero(
                id = hero.id,
                imageUrl = (imagePath + "." + hero.image.extension),
                name = hero.name,
                description = hero.description,
            )
        }
    }

    private fun getHash(timestamp: Long): String {
        return md5(timestamp.toString() + PRIVATE_API_KEY + PUBLIC_API_KEY)
    }

    private fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis() / MILLIS_IN_SECOND
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(
            1,
            md.digest(input.toByteArray())
        ).toString(16).padStart(32, '0')
    }
}