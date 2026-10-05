package com.example.anotafacil.data.util

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.anotafacil.domain.repository.UserRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@Singleton
class AppForegroundManager @Inject constructor(
    private val userRepository: UserRepository
) : DefaultLifecycleObserver {
    private val _showEmailChangedDialog = MutableStateFlow(false)

    val showEmailChangedDialog: StateFlow<Boolean> = _showEmailChangedDialog.asStateFlow()

    override fun onStart(owner: LifecycleOwner) {
        Log.d(
            "APP_LIFECYCLE",
            "onStart() manager = ${
                System.identityHashCode(this)
            }"
        )
        CoroutineScope(Dispatchers.IO).launch {

            val result = userRepository.checkEmailChange()

            if (result.getOrNull() == true) {
                    Log.d(
                        "APP_LIFECYCLE",
                        "Antes: ${_showEmailChangedDialog.value}"
                    )
                _showEmailChangedDialog.update { true }


                    Log.d(
                        "APP_LIFECYCLE",
                        "Depois: ${_showEmailChangedDialog.value}"
                    )
            }
        }
    }

    fun dismissEmailChangedDialog() {
        _showEmailChangedDialog.value = false
    }
}
