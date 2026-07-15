package com.example.anotacoesdeprodutos.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotacoesdeprodutos.domain.model.City
import com.example.anotacoesdeprodutos.domain.repository.CityRepository
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
        observeCities()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeCities() {
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

    fun updateSearchQuery(query: String) {
        _homeUiState.update { it.copy(searchQuery = query) }
    }

    fun addCity(city: City) {
        viewModelScope.launch {
            val result = cityRepository.addCity(city)
            if(result > 0) {
                _homeUiState.value = _homeUiState.value.copy(
                    success = true,
                    error = null
                )
            } else {
                _homeUiState.value = _homeUiState.value.copy(
                    success = false,
                    error = "Erro ao adicionar cidade"
                )
            }
        }
    }

    fun showDialog() {
        _homeUiState.update {
            it.copy(showDialog = true)
        }
    }

    fun dismissDialog() {
        _homeUiState.update {
            it.copy(showDialog = false, success = false, error = null)
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