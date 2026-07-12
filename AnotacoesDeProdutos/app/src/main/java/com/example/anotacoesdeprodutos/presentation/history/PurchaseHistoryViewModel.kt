package com.example.anotacoesdeprodutos.presentation.history

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotacoesdeprodutos.domain.model.Payment
import com.example.anotacoesdeprodutos.domain.model.PurchaseWithItemsDomain
import com.example.anotacoesdeprodutos.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.sortedByDescending


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
            combine(
                customerRepository.getAllPurchases(customerId),
                customerRepository.getAllPayments(customerId)
            ) { purchases, payments ->
                val purchaseHistory = purchases.map {
                    HistoryMovement.UiPurchase(it)
                }

                val paymentHistory = payments.map {
                    HistoryMovement.UiPayment(it)
                }

                // 1. Junta e ordena por data (como já estava fazendo)
                val listaOrdenada = (purchaseHistory + paymentHistory).sortedByDescending { it.date }

                // 2. A MÁGICA: Agrupa transformando o "it.date" (Long) em String ("Setembro", "Agosto"...)
                listaOrdenada.groupBy { movimentacao ->
                    obterNomeMesAno(movimentacao.date)
                }
            }.collect { mapaAgrupado ->
                _uiState.update {
                    it.copy(history = mapaAgrupado)
                }
            }
        }
    }

    // Cole essa função auxiliar no final da sua ViewModel
    @RequiresApi(Build.VERSION_CODES.O)
    private fun obterNomeMesAno(timestamp: Long): String {
        val localDate = java.time.Instant.ofEpochMilli(timestamp)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()

        val mes = localDate.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale("pt", "BR"))
        return mes.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale("pt", "BR")) else it.toString() }
    }
}

sealed interface HistoryMovement {

    val date: Long

    data class UiPurchase(
        val purchase: PurchaseWithItemsDomain
    ) : HistoryMovement {
        override val date = purchase.purchase.purchaseDate
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