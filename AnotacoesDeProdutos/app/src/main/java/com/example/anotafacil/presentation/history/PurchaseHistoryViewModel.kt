package com.example.anotafacil.presentation.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.model.Payment
import com.example.anotafacil.domain.model.PurchaseWithItemsDomain
import com.example.anotafacil.domain.repository.CustomerRepository
import com.example.anotafacil.presentation.formatter.toBrazilianDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.sortedByDescending
import kotlin.uuid.Uuid


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PurchaseHistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(PurchaseHistoryUiState())
    val uiState = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val customerUuid = savedStateHandle
        .getStateFlow<String?>("customerId", null)
        .filterNotNull()
        .map(Uuid::parse)



    init {
        updateCustomer()

        updateHistory()
    }

    private fun updateHistory() {
        viewModelScope.launch {
            customerUuid
                .flatMapLatest { uuid ->
                    combine(
                        customerRepository.getAllPurchases(uuid),
                        customerRepository.getAllPayments(uuid)
                    ) { purchases, payments ->
                        val purchaseHistory = purchases.map {
                            HistoryMovement.UiPurchase(it)
                        }

                        val paymentHistory = payments.map {
                            HistoryMovement.UiPayment(it)
                        }

                        val orderedList =
                            (purchaseHistory + paymentHistory).sortedByDescending { it.date }

                        orderedList.groupBy { movimentacao ->
                            movimentacao.date.toBrazilianDate()
                        }
                    }
                }.collect { history ->
                    _uiState.update {
                        it.copy(history = history)
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun updateCustomer() {
        viewModelScope.launch {
            customerUuid.flatMapLatest { uuid ->
                customerRepository.getCustomer(uuid)
            }.collect { customer ->
                _uiState.update {
                    it.copy(clientName = customer?.name ?: "")
                }
            }
        }
    }
}

sealed interface HistoryMovement {

    val date: Long

    data class UiPurchase(
        val purchaseWithItemsDomain: PurchaseWithItemsDomain
    ) : HistoryMovement {
        override val date = purchaseWithItemsDomain.purchase.purchaseDate
    }

    data class UiPayment(
        val payment: Payment
    ) : HistoryMovement {
        override val date = payment.paymentDate
    }
}

data class PurchaseHistoryUiState(
    val clientName: String = "",
    val history: Map<String, List<HistoryMovement>> = emptyMap()
)