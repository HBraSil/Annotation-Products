package com.example.anotafacil.presentation.history

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.model.Payment
import com.example.anotafacil.domain.model.PurchaseWithItemsDomain
import com.example.anotafacil.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.sortedByDescending
import kotlin.uuid.Uuid


@HiltViewModel
class PurchaseHistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository
): ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val customer = savedStateHandle.getStateFlow<String?>("customerId", null)
        .map { idString ->
            idString?.let { Uuid.parse(idString) }
        }
        .flatMapLatest(customerRepository::getCustomer)
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )


    private val _uiState = MutableStateFlow(PurchaseHistoryUiState())
    val uiState = _uiState.asStateFlow()


    init {

        viewModelScope.launch {
            _uiState.update {
                it.copy(clientName = customer.value?.name ?: "Nome não encontrado")
            }

            combine(
                customerRepository.getAllPurchases(customer.value?.id),
                customerRepository.getAllPayments(customer.value?.id)
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