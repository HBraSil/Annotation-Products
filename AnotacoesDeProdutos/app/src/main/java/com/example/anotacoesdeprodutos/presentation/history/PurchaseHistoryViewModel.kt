package com.example.anotacoesdeprodutos.presentation.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotacoesdeprodutos.domain.model.PurchaseWithItemsDomain
import com.example.anotacoesdeprodutos.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PurchaseHistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository
): ViewModel() {
    private val customerId: Long = checkNotNull(savedStateHandle["customerId"])

    private val _uiState = MutableStateFlow(PurchaseHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            customerRepository.getCustomer(customerId).collect { customer ->
                _uiState.update {
                    it.copy(clientName = customer.name)
                }
            }
        }

        viewModelScope.launch {
            customerRepository.getAllPurchases(customerId).collect { purchases ->
                _uiState.value = _uiState.value.copy(
                    orders = purchases
                )
            }
        }
    }
}


data class PurchaseHistoryUiState(
    val clientName: String = "",
    val orders: List<PurchaseWithItemsDomain> = emptyList(),
)