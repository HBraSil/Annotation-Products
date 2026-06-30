package com.example.anotacoesdeprodutos.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotacoesdeprodutos.domain.model.City
import com.example.anotacoesdeprodutos.domain.repository.CityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cityRepository: CityRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            cityRepository.getCitiesWithClientCount().collect { cities ->
                _uiState.value = _uiState.value.copy(
                    cities = cities
                )
            }
        }
    }

    fun addCity(city: City) {
        viewModelScope.launch {
            val result = cityRepository.addCity(city)
            if(result > 0) {
                _uiState.value = _uiState.value.copy(
                    success = true,
                    error = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    success = false,
                    error = "Erro ao adicionar cidade"
                )
            }
        }
    }

    fun showDialog() {
        _uiState.update {
            it.copy(showDialog = true)
        }
    }

    fun dismissDialog() {
        _uiState.update {
            it.copy(showDialog = false, success = false, error = null)
        }
    }
}

data class HomeState(
    val cities: List<City> = emptyList(),
    val showDialog: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)