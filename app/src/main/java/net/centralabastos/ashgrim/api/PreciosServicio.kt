package net.centralabastos.ashgrim.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.centralabastos.ashgrim.datos.SerieBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

private const val BASE_URL= "https://centralabastos.ashgrim.net"

private val moshi=Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface PreciosServicio {
    @GET("api/precios/{fecha}")
    suspend fun getPrecios(@Path("fecha") fecha:String): PreciosResponse


    @GET("api/maxfecha")
    suspend fun getMaxFecha():MaxFechaResponse

    @POST("api/precios")
    suspend fun getSerie(@Body body: SerieBody):List<SerieResponse>

}

object PreciosApi {
    val retrofitServicio:PreciosServicio by lazy {
        retrofit.create(PreciosServicio::class.java)
    }

}

