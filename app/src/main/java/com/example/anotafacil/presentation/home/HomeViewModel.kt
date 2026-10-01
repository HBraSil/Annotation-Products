package com.example.anotafacil.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.data.util.NetworkChecker
import com.example.anotafacil.domain.exception.HomeResult
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.repository.CityRepository
import com.example.anotafacil.domain.repository.UserRepository
import com.example.anotafacil.domain.usecase.DownloadAllUserDataUseCase
import com.example.anotafacil.domain.usecase.UploadDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val downloadAllUserDataUseCase: DownloadAllUserDataUseCase,
    private val networkChecker: NetworkChecker
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()


    init { searchCity() }


    fun loadOwnerUser() {
        viewModelScope.launch {
            userRepository.getOwner()
                .collect { result ->
                    when (result) {
                        is HomeResult.Success -> {
                            _uiState.update {
                                it.copy(ownerUser = result.users.owner)
                            }
                        }

                        is HomeResult.NotFound -> {
                            _uiState.update {
                                it.copy(message = "Proprietário não encontrado")
                            }
                        }

                        is HomeResult.Disconnected -> {
                            _uiState.update {
                                it.copy(userDisconnectedMessage = "Você foi desconectado do proprietário")
                            }
                        }

                        is HomeResult.Error -> {
                            _uiState.update {
                                it.copy(message = result.message)
                            }
                        }

                        else -> {}
                    }
                }
        }
    }


    fun loadSellerData() {
        _uiState.update { it.copy(isSyncing = true) }

        viewModelScope.launch {
            var menssage: String? = null

            userRepository.getSellerData()
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
                                it.copy(userDisconnectedMessage = "Você foi desconectado do proprietário")
                            }
                        }
                        else -> {}
                    }


                    menssage?.let { text ->
                        _uiState.update {
                            it.copy(
                                message = text
                            )
                        }
                    }

                    delay(400.milliseconds)
                    _uiState.update { it.copy(message = null, userDisconnectedMessage = null, isSyncing = false) }
                }


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



    fun downloadAllUserData() {
        _uiState.update { it.copy(isSyncing = true) }

        viewModelScope.launch {
            downloadAllUserDataUseCase()
                .onSuccess { result ->
                    if(result) {
                        _uiState.update {
                            it.copy(
                                message = "Sucesso ao atualizar"
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(message = "Erro ao atualizar")
                        }
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(message = throwable.message)
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



    fun verifyingIfSellerCanDisconnect() {
        viewModelScope.launch {
            userRepository.verifyingIfSellerCanDisconnect()
                .onSuccess {
                    _uiState.update {
                        it.copy(sellerCanDisconnect = true)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(message = throwable.message)
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
    val userDisconnectedMessage: String? = null,
    val sellerCanDisconnect: Boolean = false
)