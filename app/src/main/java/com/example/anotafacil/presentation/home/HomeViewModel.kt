package com.example.anotafacil.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.exception.HomeResult
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.domain.model.User
import com.example.anotafacil.domain.repository.CityRepository
import com.example.anotafacil.domain.repository.UserRepository
import com.example.anotafacil.domain.usecase.DownloadAllUserDataUseCase
import com.example.anotafacil.domain.usecase.UploadDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val cityRepository: CityRepository,
    private val uploadDataUseCase: UploadDataUseCase,
    private val downloadAllUserDataUseCase: DownloadAllUserDataUseCase,
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    private val _homeEvent = MutableSharedFlow<HomeEvent>()
    val uiEvent = _homeEvent.asSharedFlow()

    init {
        searchCity()
        downloadAllUserData()
    }


    fun loadOwnerInfo() {
        viewModelScope.launch {
            userRepository.observeOwnerInfo()
                .collect { result ->
                    when (result) {
                        is HomeResult.Success -> {
                            _uiState.update {
                                it.copy(ownerUser = result.users.owner)
                            }
                        }

                        is HomeResult.NotFound -> {
                            _homeEvent.emit(
                                HomeEvent.ShowMessage(result.message)
                            )
                        }

                        is HomeResult.NotAuthenticated -> {
                            _homeEvent.emit(
                                HomeEvent.UserNotAuthenticated("Você não está autenticado. Faça login novamente.")
                            )
                        }

                        is HomeResult.ErrorToParse -> {
                            _homeEvent.emit(
                                HomeEvent.ShowMessage("Não foi possível carregar os dados. Tente novamente.")
                            )
                        }

                        else -> {}
                    }
                }
        }
    }


    fun loadSellerAndOwnerInfo() {
        _uiState.update { it.copy(isSyncing = true) }

        viewModelScope.launch {
            userRepository.loadSellerAndOwnerInfo()
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
                            _homeEvent.emit(
                                HomeEvent.UserNotAuthenticated(result.message)
                            )
                        }

                        is HomeResult.NotAuthenticated -> {
                            _homeEvent.emit(
                                HomeEvent.UserNotAuthenticated("Você não está autenticado. Faça login novamente.")
                            )
                        }

                        is HomeResult.Error -> {
                            _homeEvent.emit(
                                HomeEvent.ShowMessage(result.message)
                            )
                        }
                        else -> {}
                    }

                    _uiState.update { it.copy(isSyncing = false) }
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
                        _homeEvent.emit(
                            HomeEvent.ShowMessage("Sucesso ao atualizar")
                        )
                    } else {
                        _homeEvent.emit(
                            HomeEvent.ShowMessage("Erro ao atualizar dados")
                        )
                    }
                }
                .onFailure { throwable ->
                    _homeEvent.emit(
                        HomeEvent.ShowMessage(throwable.message ?: "Erro ao atualizar dados")
                    )
                }

            _uiState.update { it.copy(isSyncing = false) }
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
                    it.copy(success = true)
                }
            } else {
                _homeEvent.emit(
                    HomeEvent.ShowMessage("Erro ao adicionar cidade")
                )
            }
        }
    }



    fun verifyingIfSellerCanDisconnect() {
        viewModelScope.launch {
            userRepository.verifyingAndDeletingOwnerFromSeller()
                .onSuccess {
                    Log.d("HomeViewModel", "verifyingAndDeletingOwnerFromSeller: $it -- DELETADO")
                }
                .onFailure { throwable ->
                    _homeEvent.emit(
                        HomeEvent.ShowMessage(throwable.message)
                    )
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
        _uiState.update { it.copy(isUploading = true) }

        viewModelScope.launch {
            uploadDataUseCase()
                .onSuccess {
                    _homeEvent.emit(
                        HomeEvent.ShowMessage("Dados sincronizados com sucesso")
                    )
                }
                .onFailure { throwable ->
                    _homeEvent.emit(
                        HomeEvent.ShowMessage(throwable.message)
                    )
                }

            _uiState.update { it.copy(isUploading = false) }
        }
    }
}


data class HomeState(
    val ownerUser: User = User(),
    val sellerUser: User = User(),
    val searchQuery: String = "",
    val cities: List<City> = emptyList(),
    val success: Boolean = false,
    val isSyncing: Boolean = false,
    val isUploading: Boolean = false,
    val userDisconnectedMessage: String? = null,
    val sellerCanDisconnect: Boolean = false
)

sealed interface HomeEvent {
    data class ShowMessage(val message: String?) : HomeEvent

    data class UserNotAuthenticated(val message: String?) : HomeEvent
}