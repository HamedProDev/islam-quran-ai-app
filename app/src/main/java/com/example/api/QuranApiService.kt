package com.example.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class QuranResponse(
    @Json(name = "code") val code: Int,
    @Json(name = "status") val status: String,
    @Json(name = "data") val data: SurahResponseData?
)

@JsonClass(generateAdapter = true)
data class SurahResponseData(
    @Json(name = "number") val number: Int,
    @Json(name = "name") val name: String,
    @Json(name = "englishName") val englishName: String,
    @Json(name = "englishNameTranslation") val englishNameTranslation: String,
    @Json(name = "revelationType") val revelationType: String,
    @Json(name = "numberOfAyahs") val numberOfAyahs: Int,
    @Json(name = "ayahs") val ayahs: List<AyahResponseData>?
)

@JsonClass(generateAdapter = true)
data class AyahResponseData(
    @Json(name = "number") val number: Int,
    @Json(name = "text") val text: String,
    @Json(name = "numberInSurah") val numberInSurah: Int
)

enum class TranslationEdition(val id: String, val displayName: String, val language: String) {
    SAHIH("en.sahih", "Sahih International", "English"),
    YUSUF_ALI("en.yusufali", "Yusuf Ali", "English"),
    PICKTHALL("en.pickthall", "Pickthall", "English"),
    MAUDUDI("ur.maududi", "Maulana Maududi", "Urdu"),
    HAMIDULLAH("fr.hamidullah", "Muhammad Hamidullah", "French"),
    CORTES("es.cortes", "Julio Cortes", "Spanish"),
    BUBENHEIM("de.bubenheim", "Bubenheim & Elyas", "German")
}

interface QuranApi {
    @GET("v1/surah/{surahId}/{edition}")
    suspend fun getSurahWithTranslation(
        @Path("surahId") surahId: Int,
        @Path("edition") edition: String
    ): QuranResponse
}

object QuranApiClient {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val service: QuranApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.alquran.cloud/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(QuranApi::class.java)
    }
}
