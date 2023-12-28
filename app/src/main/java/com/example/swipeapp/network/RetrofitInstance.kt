package com.example.swipeapp.network

import com.itkacher.okprofiler.BuildConfig
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * A singleton class that provides a Retrofit instance.
 *
 * This class configures and builds a Retrofit instance with a base URL and a custom OkHttpClient.
 * The OkHttpClient is configured with a connection timeout, a read timeout, and an optional OkHttpProfilerInterceptor.
 */
class RetrofitInstance {
    companion object{
        /**
         * Returns a Retrofit instance with a custom OkHttpClient and a base URL.
         *
         * The OkHttpClient has a connection timeout of 1 minute and a read timeout of 60 seconds.
         * If the build is a debug build, an OkHttpProfilerInterceptor is added to the OkHttpClient.
         *
         * @return A Retrofit instance.
         */
        fun getRetrofitInstance(): Retrofit {
            val builder = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(60, TimeUnit.SECONDS)
            // .writeTimeout(60,TimeUnit.SECONDS)
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(OkHttpProfilerInterceptor())
            }
            val client = builder.build()
            val baseURL = "https://app.getswipe.in/api/public/"

            return Retrofit.Builder()
                .baseUrl(baseURL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }
    }
}