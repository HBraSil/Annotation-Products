package com.example.anotafacil.presentation.onboarding.subscription

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.exception.HomeResult
import com.example.anotafacil.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _subscriptionState = MutableStateFlow(SubscriptionState())
    val subscriptionState: StateFlow<SubscriptionState> = _subscriptionState.asStateFlow()


    fun finalizeSubscription() {
        _subscriptionState.value = SubscriptionState(isLoading = true)

        viewModelScope.launch {
            userRepository.becomeOwner()
                .onSuccess {
                    // Handle success
                    _subscriptionState.update { it.copy(isLoading = false, success = true) }
                    Log.d("SubscriptionViewModel", "Subscription finalized")
                }
                .onFailure {throwable ->
                    // Handle failure
                    _subscriptionState.update { it.copy(isLoading = false, errorMessage = throwable.message) }
                    Log.e("SubscriptionViewModel", "Error finalizing subscription: ${throwable.message}")
                }
        }
    }
}


data class SubscriptionState(
    val success: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)