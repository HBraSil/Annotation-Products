package com.example.anotafacil.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.domain.repository.CityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cityRepository: CityRepository
): ViewModel() {
    private val _homeUiState = MutableStateFlow(HomeState())
    val uiState = _homeUiState.asStateFlow()

    init {
        searchCity()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun searchCity() {
        viewModelScope.launch {
            _homeUiState
                .map { it.searchQuery }
                .distinctUntilChanged()
                .flatMapLatest {
                    cityRepository.searchCities(it)
                }
                .collect {  cities ->
                    _homeUiState.update {
                        it.copy(cities = cities)
                    }
                }
        }
    }

    fun showAddCityModal() {
        _homeUiState.update {
            it.copy(showDialog = true)
        }
    }

    fun closeDialogs() {
        _homeUiState.update {
            it.copy(
                success = false,
                showDialog = false
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _homeUiState.update { it.copy(searchQuery = query) }
    }

    fun addCity(cityName: String) {
        viewModelScope.launch {
            val city = City(name = cityName)
            val result = cityRepository.addCity(city)


            if(result > 0) {
                _homeUiState.update {
                    it.copy(success = true, error = null)
                }
            } else {
                _homeUiState.update {
                    it.copy(success = false, error = "Erro ao adicionar cidade")
                }
            }
        }
    }
}


data class HomeState(
    val searchQuery: String = "",
    val cities: List<City> = emptyList(),
    val showDialog: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)