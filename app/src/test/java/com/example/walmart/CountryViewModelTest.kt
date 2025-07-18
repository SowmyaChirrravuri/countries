package com.example.walmart

import org.junit.Test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.walmart.data.Country
import com.example.walmart.model.CountryRepository
import com.example.walmart.viewmodel.CountryViewModel
import com.example.walmart.viewmodel.UiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import com.example.walmart.model.Result

@ExperimentalCoroutinesApi
class CountryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var repository: CountryRepository

    private lateinit var viewModel: CountryViewModel

    val mockCountryUSA  = Country(
        name = "USA",
        region = "North",
        code = "USA",
        capital = "Washington"
    )

    val mockCountryCanada = Country(
        name = "Canada",
        region = "North America",
        code = "CA",
        capital = "Ottawa"
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Inject mocked repository into your ViewModel
        viewModel = CountryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCountries emits Loading and then Success`() = runTest {
        // Prepare test data
        val testCountries = listOf(mockCountryUSA,mockCountryCanada)

        // Mock repository to return success
        `when`(repository.fetchCountries()).thenReturn(Result.Success(testCountries))

        // Observe LiveData
        val observer = mock(Observer::class.java) as Observer<UiResult<List<Country>>>
        viewModel.countries.observeForever(observer)

        // Call the method under test
        viewModel.loadCountries()

        // Advance coroutines until idle
        advanceUntilIdle()

        // Verify sequence of emitted states
        val inOrder = inOrder(observer)
        inOrder.verify(observer).onChanged(UiResult.Loading)
        inOrder.verify(observer).onChanged(UiResult.Success(testCountries))

        viewModel.countries.removeObserver(observer)
    }

    @Test
    fun `loadCountries emits Loading and then Error`() = runTest {
        val errorMessage = "Network error"

        // Mock repository to return error
        `when`(repository.fetchCountries()).thenReturn(Result.Error(errorMessage))

        val observer = mock(Observer::class.java) as Observer<UiResult<List<Country>>>
        viewModel.countries.observeForever(observer)

        viewModel.loadCountries()
        advanceUntilIdle()

        val inOrder = inOrder(observer)
        inOrder.verify(observer).onChanged(UiResult.Loading)
        inOrder.verify(observer).onChanged(UiResult.Error(errorMessage))

        viewModel.countries.removeObserver(observer)
    }
}
