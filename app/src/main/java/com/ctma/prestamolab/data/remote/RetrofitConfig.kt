package com.ctma.prestamolab.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Ambientes de ejecución (Semana 9, actividad 34: "Configurar
 * variables/URLs por ambiente sin exponer secretos"). En un proyecto
 * real, AMBIENTE_ACTUAL vendría de BuildConfig (generado por Gradle a
 * partir del buildType/flavor), nunca hardcodeado así en el código
 * fuente que se sube al repositorio. Aquí queda fijo en DEV a
 * propósito: PréstamoLab no tiene servidores stage/prod reales, así
 * que las tres URLs son placeholders — ninguna existe de verdad, y
 * ninguna contiene un secreto o token real.
 */
enum class Ambiente(val baseUrl: String) {
    DEV("https://api-dev.prestamolab.ctma.example/v1/"),
    STAGE("https://api-stage.prestamolab.ctma.example/v1/"),
    PROD("https://api.prestamolab.ctma.example/v1/")
}

object RetrofitConfig {

    private val AMBIENTE_ACTUAL = Ambiente.DEV

    /**
     * Token CONCEPTUAL — no es un secreto real ni funciona contra
     * ningún servidor. En un proyecto real este valor NUNCA se
     * escribiría en el código: vendría de un almacenamiento seguro
     * (EncryptedSharedPreferences / Android Keystore) o se inyectaría
     * en tiempo de compilación desde un secreto de CI.
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
            .baseUrl(AMBIENTE_ACTUAL.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PrestamoLabApiService::class.java)
    }
}
