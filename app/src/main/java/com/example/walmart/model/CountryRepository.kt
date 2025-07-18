package com.example.walmart.model

import com.example.walmart.data.Country

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class CountryRepository(private val apiService: ApiService) {

    suspend fun fetchCountries(): Result<List<Country>> {
        return try {
            val response = apiService.getCountries()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success(it)
                } ?: Result.Error("Empty Response")
            } else {
                Result.Error("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Exception: ${e.message}")
        }
    }
}
