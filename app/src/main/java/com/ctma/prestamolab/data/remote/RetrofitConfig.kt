package com.ctma.prestamolab.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Configuración del cliente HTTP (Semana 8, actividad 25).
 *
 * BASE_URL es un placeholder: PréstamoLab no tiene backend real
 * desplegado (ver docs/CONTRATO_API.md). Los timeouts se dejan
 * deliberadamente CORTOS (3 segundos) para que, si alguna vez se
 * intenta sincronizar contra este host inexistente, la app falle
 * rápido y vuelva a los datos locales en vez de dejar a la persona
 * esperando una respuesta que nunca va a llegar.
 */
object RetrofitConfig {

    private const val BASE_URL = "https://api.prestamolab.ctma.example/v1/"

    /**
     * Interceptor de token CONCEPTUAL (actividad de Semana 8: "token
     * conceptual sin exponer secretos"). "token-demo-no-real" no es un
     * secreto real ni funciona contra ningún servidor: es solo para
     * demostrar dónde y cómo se agregaría la cabecera Authorization.
     */
    private val interceptorToken = Interceptor { chain ->
        val peticionConToken = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer token-demo-no-real")
            .build()
        chain.proceed(peticionConToken)
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .addInterceptor(interceptorToken)
        .build()

    val apiService: PrestamoLabApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PrestamoLabApiService::class.java)
    }
}
