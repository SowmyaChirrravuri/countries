package com.example.walmart.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walmart.data.Country
import com.example.walmart.model.CountryRepository
import com.example.walmart.model.Result
import com.example.walmart.model.ServiceLocator
import kotlinx.coroutines.launch


sealed class UiResult<out T> {
    data class Success<T>(val data: T) : UiResult<T>()
    data class Error(val message: String) : UiResult<Nothing>()
    object Loading : UiResult<Nothing>()
}


class CountryViewModel(val repository: CountryRepository = ServiceLocator.repository) : ViewModel() {

    private val _countries = MutableLiveData<UiResult<List<Country>>>()
    val countries: LiveData<UiResult<List<Country>>> = _countries

    fun loadCountries() {
        _countries.value = UiResult.Loading
        viewModelScope.launch {
            when (val result = repository.fetchCountries()) {
                is Result.Success -> _countries.value = UiResult.Success(result.data)
                is Result.Error -> _countries.value = UiResult.Error(result.message)
                else -> {}
            }
        }
    }
}
