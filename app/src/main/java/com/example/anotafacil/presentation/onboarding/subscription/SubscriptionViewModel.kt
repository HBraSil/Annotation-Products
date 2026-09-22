package com.example.anotafacil.presentation.onboarding.subscription

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    fun finalizeSubscription() {
        viewModelScope.launch {
            userRepository.becomeOwner()
                .onSuccess {
                    // Handle success
                    Log.d("SubscriptionViewModel", "Subscription finalized")
                }
                .onFailure {
                    // Handle failure
                    Log.e("SubscriptionViewModel", "Error finalizing subscription: ${it.message}")
                }
        }
    }

}