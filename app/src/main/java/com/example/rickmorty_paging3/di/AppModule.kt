package com.example.rickmorty_paging3.di

import com.example.rickmorty_paging3.api.ApiService
import com.example.rickmorty_paging3.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/* @Modules
    - are used to add bindings to Hilt
    - to tell Hilt how to provide instance of different types
    - in Hilt module, can include bindings for types that cannot be constructor-injected
        such as interface or class that are not contained in you project
 */
/* @InstallIn
    - Tells Hilt the container where the bindings are available by specifying a Hilt component
 */
/* @Provides
    - in Hilt modules to tell Hilt how to provide types that cannot be constructor injected
 */
/* @Singleton
    - to limit availability instance only one at time
 */


@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient
    ) : ApiService =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

        return builder.build()
    }

}