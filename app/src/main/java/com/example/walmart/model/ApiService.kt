package com.example.walmart.model

import com.example.walmart.data.Country
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET(COUNTRIES_ENDPOINT)
    suspend fun getCountries(): Response<List<Country>>
}
