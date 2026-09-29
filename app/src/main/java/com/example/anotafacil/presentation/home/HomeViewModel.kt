package com.example.anotafacil.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.data.util.NetworkChecker
import com.example.anotafacil.domain.exception.HomeResult
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.repository.CityRepository
import com.example.anotafacil.domain.repository.UserRepository
import com.example.anotafacil.domain.usecase.RefreshHomeUseCase
import com.example.anotafacil.domain.usecase.UploadDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val cityRepository: CityRepository,
    private val uploadDataUseCase: UploadDataUseCase,
    private val refreshHomeUseCase: RefreshHomeUseCase,
    private val networkChecker: NetworkChecker
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()


    init { searchCity() }


    fun loadOwnerUser() {
        viewModelScope.launch {
            userRepository.getOwner().fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(ownerUser = user)
                    }
                },
                onFailure = { error ->
                    Log.d(
                        "HomeViewModel",
                        "Erro ao buscar owner: ${error.message}"
                    )
                    _uiState.update {
                        it.copy(message = error.message)
                    }
                }
            )
        }
    }


    fun loadSellerUser() {
        viewModelScope.launch {
            var menssage: String? = null
            userRepository.getUsersForSellerHome()
                .collect { result ->
                    when (result) {
                        is HomeResult.Success -> {
                            _uiState.update {
                                it.copy(
                                    sellerUser = result.users.seller,
                                    ownerUser = result.users.owner
                                )
                            }
                        }

                        is HomeResult.NotFound -> {
                            menssage = "Vendedor não encontrado"
                        }

                        is HomeResult.Disconnected -> {
                            _uiState.update {
                                it.copy(isSellerDisconnected = "Você foi desconectado do proprietário")
                            }
                        }

                        is HomeResult.OwnerNotFound -> {
                            Log.d("HomeViewModel", "owner n encontrado")

                            menssage = "Owner não encontrado"
                        }

                        else -> {}
                    }
                }

            menssage?.let { text ->
                _uiState.update {
                    it.copy(
                        message = text
                    )
                }
            }

            delay(400.milliseconds)
            _uiState.update { it.copy(message = null, isSellerDisconnected = null) }

        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun searchCity() {
        viewModelScope.launch {
            _uiState
                .map { it.searchQuery }
                .distinctUntilChanged()
                .flatMapLatest {
                    cityRepository.searchCities(it)
                }
                .collect {  cities ->
                    _uiState.update {
                        it.copy(cities = cities)
                    }
                }
        }
    }



    fun refreshHome() {
        _uiState.update { it.copy(isSyncing = true) }

        viewModelScope.launch {
            val result = refreshHomeUseCase()

            if(result) {
                searchCity()
            } else {
                _uiState.update {
                    it.copy(
                        message = "Erro ao atualizar dados",
                        isSyncing = false
                    )
                }
            }

            delay(400.milliseconds)
            _uiState.update { it.copy(isSyncing = false, message = null) }
        }
    }



    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }



    fun addCity(cityName: String) {
        viewModelScope.launch {
            val city = City(name = cityName.trim())
            val result = cityRepository.addCity(city)


            if(result > 0) {
                _uiState.update {
                    it.copy(success = true, message = null)
                }
            } else {
                _uiState.update {
                    it.copy(success = false, message = "Erro ao adicionar cidade")
                }
            }
        }
    }



    fun signOutSeller() {
        viewModelScope.launch {
            userRepository.signOut()
        }
    }



    fun closeSuccessDialog() {
        _uiState.update {
            it.copy(success = false)
        }
    }



    fun syncCloudClick() {
        if (!networkChecker.hasInternetConnection()) {
            _uiState.update { it.copy(message = "Sem conexão com internet") }
            return
        }

        _uiState.update { it.copy(isUploading = true) }

        viewModelScope.launch {
            uploadDataUseCase()
                .onSuccess {
                    _uiState.update {
                        it.copy(message = "Dados enviados com sucesso")
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(message = throwable.message)
                    }
                }

            delay(400.milliseconds)
            _uiState.update { it.copy(isUploading = false, message = null) }
        }
    }
}


data class HomeState(
    val ownerUser: User = User(),
    val sellerUser: User = User(),
    val searchQuery: String = "",
    val cities: List<City> = emptyList(),
    val success: Boolean = false,
    val message: String? = null,
    val isSyncing: Boolean = false,
    val isUploading: Boolean = false,
    val isSellerDisconnected: String? = null
)