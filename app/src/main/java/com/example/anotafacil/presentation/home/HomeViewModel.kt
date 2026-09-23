package com.example.anotafacil.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.repository.CityRepository
import com.example.anotafacil.domain.repository.UserRepository
import com.example.anotafacil.domain.usecase.RefreshHomeUseCase
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
    private val userRepository: UserRepository,
    private val cityRepository: CityRepository,
    private val refreshHomeUseCase: RefreshHomeUseCase
): ViewModel() {
    private val _homeUiState = MutableStateFlow(HomeState())
    val uiState = _homeUiState.asStateFlow()


    init { searchCity() }


    fun loadOwnerUser() {
        viewModelScope.launch {
            userRepository.getOwnerUser().fold(
                onSuccess = { user ->
                    _homeUiState.update {
                        it.copy(user = user ?: User())
                    }
                },
                onFailure = { error ->
                    Log.d(
                        "HomeViewModel",
                        "Erro ao buscar owner: ${error.message}"
                    )
                    _homeUiState.update {
                        it.copy(error = error.message)
                    }
                }
            )
        }
    }

    fun loadSellerUser() {
        viewModelScope.launch {
            userRepository.getSellerUser().fold(
                onSuccess = { user ->
                    Log.d(
                        "HomeViewModel",
                        "Seller: $user"
                    )
                    _homeUiState.update {
                        it.copy(user = user ?: User())
                    }
                },
                onFailure = { error ->
                    Log.d(
                        "HomeViewModel",
                        "Erro ao buscar seller: ${error.message}"
                    )
                    _homeUiState.update {
                        it.copy(error = error.message)
                    }
                }
            )
        }
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    private fun searchCity() {
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
    fun refreshHome() {
        _homeUiState.update { it.copy(isSyncing = true) }

        viewModelScope.launch {
            val result = refreshHomeUseCase()

            if(result) {
                searchCity()
                _homeUiState.update {
                    it.copy(
                        error = null,
                        isSyncing = false
                    )
                }
            } else {
                _homeUiState.update {
                    it.copy(
                        error = "Erro ao atualizar dados",
                        isSyncing = false
                    )
                }
            }
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


    fun closeSuccessDialog() {
        _homeUiState.update {
            it.copy(success = false)
        }
    }
}


data class HomeState(
    val user: User = User(),
    val searchQuery: String = "",
    val cities: List<City> = emptyList(),
    val success: Boolean = false,
    val error: String? = null,
    val isSyncing: Boolean = false,
)