package com.example.anotafacil.presentation.sales_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.domain.model.MonthlySalesData
import com.example.anotafacil.domain.repository.CityRepository
import com.example.anotafacil.domain.repository.SalesOverviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class SalesOverviewViewModel @Inject constructor(
    private val salesOverviewRepository: SalesOverviewRepository,
    private val cityRepository: CityRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesOverviewUiState())
    val uiState: StateFlow<SalesOverviewUiState> = _uiState.asStateFlow()

    init {
        observeCities()
    }

    private fun observeCities() {
        viewModelScope.launch {
            cityRepository.getCities().collectLatest { cities ->
                val currentSelection = _uiState.value.selectedCity
                val cityNames = cities.map { it.name }
                _uiState.update {
                    it.copy(
                        availableCities = cities,
                        selectedCity = if (currentSelection == "Todas" || currentSelection in cityNames) currentSelection else "Todas"
                    )
                }
                loadMonthlySales(_uiState.value.selectedCity)
            }
        }
    }

    fun onCitySelected(cityName: String) {
        _uiState.update { it.copy(selectedCity = cityName) }
        loadMonthlySales(cityName)
    }

    private fun loadMonthlySales(cityName: String) {
        val selectedCityId = if (cityName == "Todas") null else _uiState.value.availableCities.firstOrNull { it.name == cityName }?.id

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val now = java.time.LocalDate.now()
            val startDate = java.time.LocalDate.now().minusMonths(5).withDayOfMonth(1)
            val endDate = now.withDayOfMonth(1).plusMonths(1)

            val startMillis = java.time.LocalDateTime.of(
                startDate.year,
                startDate.monthValue,
                startDate.dayOfMonth,
                0,
                0,
                0
            ).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

            val endMillis = java.time.LocalDateTime.of(
                endDate.year,
                endDate.monthValue,
                endDate.dayOfMonth,
                0,
                0,
                0
            ).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

            salesOverviewRepository.getMonthlySales(startMillis, endMillis, selectedCityId)
                .collect { months ->
                    _uiState.update {
                        it.copy(
                            monthlySales = months,
                            isLoading = false
                        )
                    }
                }
        }
    }
}

data class SalesOverviewUiState(
    val selectedCity: String = "Todas",
    val availableCities: List<City> = emptyList(),
    val monthlySales: List<MonthlySalesData> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)