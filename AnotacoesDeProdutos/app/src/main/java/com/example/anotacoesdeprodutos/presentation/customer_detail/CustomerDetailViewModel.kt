package com.example.anotacoesdeprodutos.presentation.customer_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotacoesdeprodutos.data.entity.toCartItemDomain
import com.example.anotacoesdeprodutos.data.entity.toDomain
import com.example.anotacoesdeprodutos.data.entity.toProductDomain
import com.example.anotacoesdeprodutos.domain.model.CartItem
import com.example.anotacoesdeprodutos.domain.model.Customer
import com.example.anotacoesdeprodutos.domain.model.Product
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
    val uiState: StateFlow<CustomerDetailUiState> = _uiState.asStateFlow()


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
            customer
                .filterNotNull()
                .flatMapLatest { customer ->

                    customerRepository.getLastPurchase(customer.id)
                }
                .collect { purchaseWithItems ->
                    val purchase = purchaseWithItems?.purchase?.toDomain()

                    val product = purchaseWithItems?.items?.map {
                        it.product.toProductDomain()
                    } ?: emptyList()

                    val items = purchaseWithItems?.items?.map {
                        it.cartItem.toCartItemDomain()
                    }?.map { cartItem ->
                        cartItem.copy(product = product.find { it.id == cartItem.productId }
                            ?: Product())
                    }

                    _uiState.update {
                        it.copy(
                            purchase = purchase,
                            purchaseItems = items,
                            totalBalance = purchase?.total ?: 0.0
                        )
                    }
                }
        }
    }
}

data class CustomerDetailUiState(
    val customer: Customer = Customer(),
    val purchase: Purchase? = null,
    val purchaseItems: List<CartItem>? = null,
    val partialPayment: String = "",
    val totalBalance: Double = 0.0,
    val showDialog: Boolean = false,
)
