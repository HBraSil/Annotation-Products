package com.example.anotafacil.presentation.customer_detail

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.model.CartItem
import com.example.anotafacil.domain.model.Customer
import com.example.anotafacil.domain.model.Payment
import com.example.anotafacil.domain.model.Purchase
import com.example.anotafacil.domain.repository.CustomerRepository
import com.example.anotafacil.presentation.formatter.currencyFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CustomerDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val customerUuid = savedStateHandle
        .getStateFlow<String?>("customerId", null)
        .filterNotNull()
        .map(Uuid::parse)


    init {
        getCustomer()
        getLastPurchase()
    }


    private fun getCustomer() {
        viewModelScope.launch {
            customerUuid.flatMapLatest {
                customerRepository.getCustomer(it)
            }.collect {
                _uiState.update { state ->
                    state.copy(customer = it ?: Customer())
                }
            }
        }
    }


    private fun getLastPurchase() {
        viewModelScope.launch {
            customerUuid.flatMapLatest {
                customerRepository.getLastPurchase(it)
            }.collect { purchaseWithItems ->
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


    fun updatePartialPayment(value: String) {
        val digitsOnly = value.filter { it.isDigit() }

        val cleanString = digitsOnly.toLongOrNull()?.toString() ?: ""

        val cents = cleanString.toLongOrNull() ?: 0L
        val doubleValue = cents / 100.0

        val formattedText = currencyFormatter.format(doubleValue)

        _uiState.update {
            it.copy(
                partialPaymentComponent = TextFieldValue(
                    text = formattedText,
                    selection = TextRange(formattedText.length)
                ),
                payment = it.payment.copy(amount = doubleValue)
            )
        }
    }


    fun showConfirmationDialog(confirmationAction: ConfirmationAction) {

        _uiState.update {
            it.copy(
                buttonConfirmationType = confirmationAction,
                showConfirmationDialog = true
            )
        }
    }

    fun confirmSelectedOnDialog() {
        when (_uiState.value.buttonConfirmationType) {
            ConfirmationAction.PARTIAL_PAYMENT -> {
                confirmPartialPayment()
            }

            ConfirmationAction.TOTAL_PAYMENT -> {
                onTotalPaymentConfirm()
            }

            else -> {}
        }
    }

    fun confirmPartialPayment() {
        val payment = _uiState.value.payment

        if (payment.amount <= 0 || payment.amount > (_uiState.value.customer.owes ?: 0.0)) {
            _uiState.update {
                it.copy(
                    errorPartialPayment = true,
                    showConfirmationDialog = false
                )
            }
            return
        }

        viewModelScope.launch {
            val totalUpdated = _uiState.value.customer.owes?.minus(payment.amount)

            _uiState.update {
                it.copy(customer = it.customer.copy(owes = totalUpdated))
            }


            val result = customerRepository.partialPayment(
                _uiState.value.customer,
                _uiState.value.purchase,
                payment.copy(
                    customerId = _uiState.value.customer.id,
                    paymentDate = System.currentTimeMillis()
                )
            )

            if (result) {
                _uiState.update {
                    it.copy(
                        showSuccessDialog = true,
                        showConfirmationDialog = false,
                        partialPaymentComponent = TextFieldValue("R$ 0,00"),
                    )
                }
            }
        }
    }


    fun onTotalPaymentConfirm() {
        viewModelScope.launch {
            val customer = _uiState.value.customer.copy(
                owes = 0.0
            )

            val payment = _uiState.value.payment.copy(
                customerId = _uiState.value.customer.id,
                paymentDate = System.currentTimeMillis(),
                amount = _uiState.value.customer.owes ?: 0.0,
                isTotalPayment = true
            )

            val result = customerRepository.payOffTotalDebt(
                customer = customer,
                payment = payment
            )

            if (result.first > 0 && result.second > 0) {
                _uiState.update {
                    it.copy(
                        showSuccessDialog = true,
                        showConfirmationDialog = false
                    )
                }
            }
        }
    }


    fun onDismissToast() {
        _uiState.update { it.copy(errorPartialPayment = false) }
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
    val showSuccessDialog: Boolean = false,
    val errorPartialPayment: Boolean = false,
    val buttonConfirmationType: ConfirmationAction? = null,
)


enum class ConfirmationAction {
    PARTIAL_PAYMENT,
    TOTAL_PAYMENT
}