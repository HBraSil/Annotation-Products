package com.example.anotafacil.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.Screens
import com.example.anotafacil.data.billing.BillingManager
import com.example.anotafacil.data.datastore.LastScreenDatastore
import com.example.anotafacil.domain.datastore.UserDataStore
import com.example.anotafacil.domain.Response.BillingManagerResponse
import com.example.anotafacil.domain.Response.SellerConnectionState
import com.example.anotafacil.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val lastScreenDatastore: LastScreenDatastore,
    private val billingManager: BillingManager,
    private val userDataStore: UserDataStore,
): ViewModel() {

    val lastActiveProfile = lastScreenDatastore.lastScreenProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    private val _mainActivityUiState = MutableStateFlow(MainActivityUiState())
    val mainActivityUiState: StateFlow<MainActivityUiState> = _mainActivityUiState.asStateFlow()


    init {
        observeSellerConnection()
        observeSubscriptionState()
    }


    private fun observeSellerConnection() {
        viewModelScope.launch {
            userRepository
                .observeSellerConnectionWithOwner()
                .collect { response ->
                    if (
                        response == SellerConnectionState.Disconnected ||
                        response == SellerConnectionState.DisconnectedBySeller
                    ) {
                        lastScreenDatastore.setLastScreenProfile(Screens.ROLE_SECTION.route)
                        userRepository.clearData()
                            .onFailure { error ->
                                Log.e(
                                    "MainActivityViewModel",
                                    "Falha ao limpar os dados do vendedor desconectado",
                                    error
                                )
                            }
                        userRepository.acknowledgeSellerDisconnection()
                        _mainActivityUiState.update {
                            it.copy(
                                sellerConnection = SellerConnectionState.NotConnected,
                                navigateToRoleSection =
                                    response == SellerConnectionState.DisconnectedBySeller,
                                showSellerDisconnectedDialog =
                                    response == SellerConnectionState.Disconnected
                            )
                        }
                    } else {
                        _mainActivityUiState.update {
                            it.copy(sellerConnection = response)
                        }
                    }
                }
        }
    }
    

    private fun observeSubscriptionState() {
        viewModelScope.launch {
            billingManager
                .observeCurrentSubscriptionState()
                .collect { response ->

                    when (response) {
                        is BillingManagerResponse.Disconnected -> {
                            // ATUALIZAR A ROTA AQUI?
                            _mainActivityUiState.update { it.copy(sellerConnection = SellerConnectionState.NotConnected) }
                        }

                        is BillingManagerResponse.HasPremium -> {
                            val previouslyHadPremium = userDataStore.wasPremiumActive(response.uid)
                            if (!previouslyHadPremium) {
                                _mainActivityUiState.update {
                                    it.copy(navigateToOwner = true)
                                }
                            }

                            userDataStore.savePreviouslyHadPremium(response.uid, true)
                        }

                        is BillingManagerResponse.NoPremium -> {
                            val previouslyHadPremium = userDataStore.wasPremiumActive(response.uid)

                            if (previouslyHadPremium) {

                                // Salva a tela que deverá abrir na próxima inicialização.
                                lastScreenDatastore.setLastScreenProfile(Screens.ROLE_SECTION.route)

                                Log.d("MainActivityViewModel", "RoleSection: ${response.uid}")
                                // Remove o papel OWNER do Room.
                                userRepository.removeOwnerRole(response.uid)

                                _mainActivityUiState.update {
                                    it.copy(
                                        showPremiumEndedDialog = true,
                                        navigateToOwner = false
                                    )
                                }
                            }

                            userDataStore.savePreviouslyHadPremium(response.uid, false)
                        }

                        else -> Unit
                    }

                }
        }
    }

    fun dismissPremiumEndedDialog() {
        _mainActivityUiState.update { it.copy(showPremiumEndedDialog = false) }
    }

    fun setLastRoute(route: String) {
        viewModelScope.launch { lastScreenDatastore.setLastScreenProfile(route) }
    }

    fun dismissSellerDisconnectedDialog() {
        _mainActivityUiState.update { it.copy(showSellerDisconnectedDialog = false) }
    }

    fun onSellerDisconnectionNavigationHandled() {
        _mainActivityUiState.update { it.copy(navigateToRoleSection = false) }
    }

    fun onOwnerNavigationHandled() {
        _mainActivityUiState.update { it.copy(navigateToOwner = false) }
    }
}



data class MainActivityUiState(
    val showPremiumEndedDialog: Boolean = false,
    val navigateToOwner: Boolean = false,
    val navigateToRoleSection: Boolean = false,
    val showSellerDisconnectedDialog: Boolean = false,
    val sellerConnection: SellerConnectionState = SellerConnectionState.NotConnected
)
