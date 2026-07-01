package com.example.anotacoesdeprodutos.presentation.customer_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotacoesdeprodutos.domain.model.CartItem
import com.example.anotacoesdeprodutos.domain.model.Customer
import com.example.anotacoesdeprodutos.domain.model.Purchase
import com.example.anotacoesdeprodutos.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository,
) : ViewModel() {
    private val customerId: Long = checkNotNull(savedStateHandle["customerId"])

    private val _uiState = MutableStateFlow(CustomerDetailUiState())
    val uiState = _uiState.asStateFlow()

    val customer: StateFlow<Customer?> = customerRepository.getCustomer(customerId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


    init {
        viewModelScope.launch {
            customer.collect { customer ->
                _uiState.update { it.copy(customer = customer ?: Customer()) }
            }
        }

        viewModelScope.launch {
            customer.filterNotNull()
                .flatMapLatest { customer ->
                    customerRepository.getLastPurchase(customer.id)
                }
                .collect { purchaseWithItems ->
                    _uiState.update {
                        it.copy(
                            purchase = purchaseWithItems?.purchase ?: Purchase(),
                            purchaseItems = purchaseWithItems?.items?.map { item ->
                                item.cartItem.copy(product = item.product)
                            } ?: emptyList()
                        )
                    }
                }

        }
    }
}


data class CustomerDetailUiState(
    val customer: Customer = Customer(),
    val purchase: Purchase = Purchase(),
    val purchaseItems: List<CartItem>? = null,
    val partialPayment: String = "",
    val showDialog: Boolean = false,
)
