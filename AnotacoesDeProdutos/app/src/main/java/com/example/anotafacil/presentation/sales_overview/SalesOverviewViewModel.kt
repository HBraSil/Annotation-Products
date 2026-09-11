package com.example.anotafacil.presentation.sales_overview

import androidx.lifecycle.ViewModel
import com.example.anotafacil.domain.model.MonthlySalesData
import com.example.anotafacil.domain.repository.SalesOverviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SalesOverviewViewModel @Inject constructor(
    private val salesOverviewRepository: SalesOverviewRepository,
    val getMonthlySalesDataUseCase: GetMonthlySalesDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesOverviewUiState())
    val uiState: StateFlow<SalesOverviewUiState> = _uiState.asStateFlow()

}


data class SalesOverviewUiState(
    val monthlySales: List<MonthlySalesData> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)