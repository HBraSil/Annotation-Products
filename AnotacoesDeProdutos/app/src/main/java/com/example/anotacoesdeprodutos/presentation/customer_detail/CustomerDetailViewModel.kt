package com.example.anotacoesdeprodutos.presentation.customer_detail

import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotacoesdeprodutos.domain.model.CartItem
import com.example.anotacoesdeprodutos.domain.model.Customer
import com.example.anotacoesdeprodutos.domain.model.Payment
import com.example.anotacoesdeprodutos.domain.model.Purchase
import com.example.anotacoesdeprodutos.domain.repository.CustomerRepository
import com.example.anotacoesdeprodutos.presentation.formatter.currencyFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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

    val customer = customerRepository.getCustomer(customerId)
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
                    Log.d("CustomerDetailViewModel", "getLastPurchase: $purchaseWithItems?.purchase")
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

    fun updatePartialPayment(text: String) {
        val digitsOnly = text.filter { it.isDigit() }

        val cleanString = digitsOnly.toLongOrNull()?.toString() ?: ""

        val cents = cleanString.toLongOrNull() ?: 0L
        val doubleValue = cents / 100.0

        val formattedText = currencyFormatter.format(doubleValue)

        _uiState.update { currentState ->
            currentState.copy(
                partialPaymentComponent = TextFieldValue(
                    text = formattedText,
                    selection = TextRange(formattedText.length)
                ),
                payment = currentState.payment.copy(amount = doubleValue)
            )
        }
    }

    fun onTotalPaymentConfirm() {
        viewModelScope.launch {
            customerRepository.payOffTotalDebt(
                _uiState.value.customer.copy(
                    owes = 0.0
                ),
                Payment(
                    customerId = _uiState.value.customer.id,
                    paymentDate = System.currentTimeMillis(),
                    amount = _uiState.value.customer.owes ?: 0.0,
                    isTotalPayment = true
                )
            )
        }
    }

    fun showConfirmationDialog() {
        _uiState.update { it.copy(showConfirmationDialog = true) }
    }

    fun confirmPartialPayment() {
        viewModelScope.launch {
            val partialPayment = _uiState.value.payment
            val totalUpdated = _uiState.value.customer.owes?.minus(partialPayment.amount)

            _uiState.update {
                it.copy(
                    customer = it.customer.copy(owes = totalUpdated),
                    //purchase = it.purchase.copy(totalAmount = totalUpdated),
                )
            }


            val result = customerRepository.partialPayment(
                _uiState.value.customer,
                _uiState.value.purchase,
                partialPayment.copy(
                    customerId = _uiState.value.customer.id,
                    paymentDate = System.currentTimeMillis()
                )
            )

            if (result) {
                _uiState.update { it.copy(showSuccessDialog = true) }
            }
        }
    }

    fun onDismiss() {
        _uiState.update {
            it.copy(
                showConfirmationDialog = false,
                showSuccessDialog = false
            )
        }
    }
}


data class CustomerDetailUiState(
    val customer: Customer = Customer(),
    val purchase: Purchase = Purchase(),
    val payment: Payment = Payment(),
    val partialPaymentComponent: TextFieldValue = TextFieldValue("R$ 0,00"),
    val purchaseItems: List<CartItem> = emptyList(),
    val showConfirmationDialog: Boolean = false,
    val showSuccessDialog: Boolean = false
)
