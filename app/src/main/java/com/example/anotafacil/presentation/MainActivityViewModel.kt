package com.example.anotafacil.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.data.billing.BillingManager
import com.example.anotafacil.data.datastore.LastScreenDatastore
import com.example.anotafacil.domain.datastore.UserDataStore
import com.example.anotafacil.domain.Response.BillingManagerResponse
import com.example.anotafacil.domain.Response.SellerConnectionState
import com.example.anotafacil.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
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

    private val _sellerConnection = MutableStateFlow<SellerConnectionState>(SellerConnectionState.NotConnected)
    val sellerConnection: StateFlow<SellerConnectionState> = _sellerConnection.asStateFlow()


    private val _subscriptionState = MutableStateFlow<BillingManagerResponse>(BillingManagerResponse.Unknown)
    val subscriptionState: StateFlow<BillingManagerResponse> = _subscriptionState.asStateFlow()


    private val _showPremiumEndedDialog = MutableStateFlow(false)
    val showPremiumEndedDialog: StateFlow<Boolean> = _showPremiumEndedDialog.asStateFlow()


    init {
        observeSellerConnection()
        observeSubscriptionState()
    }


    private fun observeSellerConnection() {
        viewModelScope.launch {
            userRepository
                .observeSellerConnectionWithOwner()
                .collect {
                    _sellerConnection.value = it
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
                            _showPremiumEndedDialog.value = false
                        }

                        is BillingManagerResponse.HasPremium -> {
                            userDataStore.savePreviouslyHadPremium(response.uid, true)
                            _showPremiumEndedDialog.value = false
                        }

                        is BillingManagerResponse.NoPremium -> {
                            val previouslyHadPremium = userDataStore.hasPreviouslyHadPremium(response.uid)

                                if (previouslyHadPremium) {
                                    _showPremiumEndedDialog.value = true
                                    userDataStore.savePreviouslyHadPremium(response.uid, false)
                                }
                            }

                        else -> Unit
                    }

                    _subscriptionState.value = response
                }
        }
    }

    fun dismissPremiumEndedDialog() {
        _showPremiumEndedDialog.value = false
    }

    fun setLastRoute(route: String) {
        viewModelScope.launch { lastScreenDatastore.setLastScreenProfile(route) }
    }

    fun confirmSellerDisconnect() {
        _sellerConnection.value = SellerConnectionState.NotConnected

        viewModelScope.launch { userRepository.clearData() }
    }
}
