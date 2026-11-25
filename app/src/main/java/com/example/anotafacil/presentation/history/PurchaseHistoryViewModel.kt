package com.example.anotafacil.presentation.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.formatter.toBrazilianMonthYear
import com.example.anotafacil.domain.model.Payment
import com.example.anotafacil.domain.model.PurchaseWithItemsDomain
import com.example.anotafacil.domain.repository.CustomerRepository
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

    private val customerUuid = savedStateHandle
        .getStateFlow<String?>("customerId", null)
        .filterNotNull()
        .map(Uuid::parse)



    init {
        updateHistory()
    }

    private fun updateHistory() {
        viewModelScope.launch {
            customerUuid
                .flatMapLatest { uuid ->
                    combine(
                        customerRepository.getAllPurchases(uuid),
                        customerRepository.getAllPayments(uuid),
                        customerRepository.getCustomer(uuid)
                    ) { purchases, payments, customer ->
                        val purchaseHistory = purchases.map {
                            HistoryMovement.UiPurchase(it)
                        }

                        val paymentHistory = payments.map {
                            HistoryMovement.UiPayment(it)
                        }

                        val orderedList =
                            (purchaseHistory + paymentHistory).sortedByDescending { it.date }

                        val history = orderedList.groupBy { movimentacao ->
                            movimentacao.date.toBrazilianMonthYear()
                        }
                        val monthlyBalances = calculateMonthlyBalances(
                            history = history,
                            currentBalance = customer?.owes ?: 0.0
                        )

                        Triple(customer?.name.orEmpty(), history, monthlyBalances)
                    }
                }.collect { history ->
                    _uiState.update {
                        it.copy(
                            clientName = history.first,
                            history = history.second,
                            monthlyBalances = history.third
                        )
                    }
                }
        }
    }
}

private fun calculateMonthlyBalances(
    history: Map<String, List<HistoryMovement>>,
    currentBalance: Double
): Map<String, MonthlyBalance> {
    var closingBalance = currentBalance
    val balances = mutableMapOf<String, MonthlyBalance>()

    history.entries
        .sortedByDescending { (_, movements) -> movements.maxOf { it.date } }
        .forEach { (month, movements) ->
            val monthlyChange = movements.sumOf { movement ->
                when (movement) {
                    is HistoryMovement.UiPurchase ->
                        movement.purchaseWithItemsDomain.purchase.totalAmount

                    is HistoryMovement.UiPayment -> -movement.payment.amount
                }
            }
            val openingBalance = closingBalance - monthlyChange
            balances[month] = MonthlyBalance(
                openingBalance = openingBalance,
                closingBalance = closingBalance
            )
            closingBalance = openingBalance
        }

    return balances
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
    val history: Map<String, List<HistoryMovement>> = emptyMap(),
    val monthlyBalances: Map<String, MonthlyBalance> = emptyMap()
)

data class MonthlyBalance(
    val openingBalance: Double,
    val closingBalance: Double
)