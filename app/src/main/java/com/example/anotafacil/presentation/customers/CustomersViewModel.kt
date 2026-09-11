package com.example.anotafacil.presentation.customers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.domain.model.Customer
import com.example.anotafacil.domain.repository.CityRepository
import com.example.anotafacil.domain.repository.CustomerRepository
import com.example.anotafacil.ui.util.FormatDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid


@OptIn(ExperimentalCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CustomersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository,
    private val cityRepository: CityRepository,
) : ViewModel() {

    private val _customerUiState = MutableStateFlow(CustomersUiState())
    val customerUiState = _customerUiState.asStateFlow()

    val cityIdFlow = savedStateHandle
        .getStateFlow<String?>("cityId", null)
        .filterNotNull()
        .map(Uuid::parse)


    init {
        getCityName()
        getCustomersList()
        getMonthlySalesSummary()
        observeSearchQuery()
    }


    fun customersEvent(event: CustomersUiEvent) {
        when (event) {
            is CustomersUiEvent.OnSearchQueryChange -> updateSearchQuery(event.query)
            is CustomersUiEvent.OnNameChange -> updateName(event.name)
            is CustomersUiEvent.OnExtraInfoChange -> updateExtraInfo(event.extraInfo)
            is CustomersUiEvent.OnCreateCustomerClick -> saveCustomer()
            is CustomersUiEvent.OnDismissModalDeleteCustomer -> onDismissModalDeleteCustomer()
            is CustomersUiEvent.OnShowModalDeleteCustomer -> showModalDeleteCustomer(event.id)
            is CustomersUiEvent.OnShowModalCreateCustomer -> showModalCreateCustomer()
            is CustomersUiEvent.OnDeleteCustomerClick -> deleteCustomer()
            is CustomersUiEvent.OnDismissOverlayCreatedCustomer -> closeModalAndOverlayCreatedCustomer()
        }

    }


    private fun getCityName() {
        viewModelScope.launch {
            cityIdFlow.mapLatest { cityId ->
                cityRepository.getCity(cityId)
            }.collect { result ->
                result.onSuccess { city ->
                    _customerUiState.update { it.copy(currentCity = city) }
                }
                .onFailure { error ->
                    _customerUiState.update {
                        it.copy(errorMessage = error.message ?: "Erro ao carregar cidade")
                    }
                }
            }
        }
    }

    private fun getCustomersList() {
        viewModelScope.launch {
            cityIdFlow
                .flatMapLatest { cityId ->
                    customerRepository.getAllCustomers(cityId)
                }
                .collect { result ->
                    // Usar .fold() trata de forma funcional e limpa os dois estados
                    result.fold(
                        onSuccess = { customers ->
                            _customerUiState.update {
                                it.copy(customers = customers, errorMessage = null)
                            }
                        },
                        onFailure = { error ->
                            _customerUiState.update {
                                it.copy(errorMessage = error.message ?: "Erro ao carregar clientes")
                            }
                        }
                    )
                }
        }
    }

    private fun getMonthlySalesSummary() {
        viewModelScope.launch {
            cityIdFlow
                .flatMapLatest { cityId ->
                    cityRepository.getMonthlySalesSummary(
                        cityId = cityId,
                        startMonth = FormatDate.currentMonth().start,
                        endMonth = FormatDate.currentMonth().end
                    )
                }
                .collect { monthlySalesSummary ->
                    _customerUiState.update { it.copy(metric = monthlySalesSummary) }
                }
        }
    }


    private fun observeSearchQuery() {
        viewModelScope.launch {
            _customerUiState
                .map { it.searchQuery }
                .distinctUntilChanged()
                .collect { query ->

                    _customerUiState.update { state ->
                        state.copy(
                            customers = state.customers.filter { customer ->
                                customer.name.contains(
                                    query,
                                    ignoreCase = true
                                )
                            }
                        )
                    }
                }
        }
    }


    private fun updateSearchQuery(query: String) = _customerUiState.update { it.copy(searchQuery = query) }


    private fun updateName(name: String) = _customerUiState.update { it.copy(name = name) }


    private fun updateExtraInfo(extraInfo: String) = _customerUiState.update { it.copy(extraInfo = extraInfo) }

    private fun showModalCreateCustomer() = _customerUiState.update {
        it.copy(showModalCreateCustomer = true)
    }

    private fun showModalDeleteCustomer(id: Uuid?) = _customerUiState.update {
        it.copy(showModalDeleteCustomer = id)
    }


    private fun closeModalAndOverlayCreatedCustomer() {
        _customerUiState.update {
            it.copy(
                showModalCreateCustomer = false,
                success = false
            )
        }

        resetAddNewCustomerFields()
    }


    private fun onDismissModalDeleteCustomer() = _customerUiState.update {
        it.copy(showModalDeleteCustomer = null)
    }


    private fun deleteCustomer() {
        viewModelScope.launch {
            val customerId = _customerUiState.value.showModalDeleteCustomer
            val result = customerRepository.deleteCustomer(customerId)

            if (result > 0) {
                _customerUiState.update {
                    it.copy(showModalDeleteCustomer = null)
                }
            }
        }
    }


    private fun saveCustomer() {
        viewModelScope.launch {

            val customer = Customer(
                name = _customerUiState.value.name,
                extraInfo = _customerUiState.value.extraInfo,
                cityId = _customerUiState.value.currentCity?.id
            )
            customerRepository.addCustomer(customer)
                .onSuccess {
                    println("SUCESSO -------------: $it")
                    _customerUiState.update { customerUiState ->
                        customerUiState.copy(
                            success = true,
                            name = "",
                            extraInfo = null
                        )
                    }
                }
                .onFailure { exception ->
                    println("ERRO -------------: $exception")
                    _customerUiState.update {
                        it.copy(
                            errorMessage = exception.message
                        )
                    }
                }
        }
    }


    fun resetAddNewCustomerFields() {
        _customerUiState.update {
            it.copy(
                name = "",
                extraInfo = null,
                success = false
            )
        }
    }
}


sealed interface CustomersUiEvent {
    data class OnSearchQueryChange(val query: String) : CustomersUiEvent
    data class OnNameChange(val name: String) : CustomersUiEvent
    data class OnExtraInfoChange(val extraInfo: String) : CustomersUiEvent
    object OnCreateCustomerClick : CustomersUiEvent
    object OnDeleteCustomerClick : CustomersUiEvent
    object OnDismissModalDeleteCustomer : CustomersUiEvent
    data class OnShowModalDeleteCustomer(val id: Uuid?) : CustomersUiEvent
    object OnShowModalCreateCustomer : CustomersUiEvent
    object OnDismissOverlayCreatedCustomer : CustomersUiEvent
}

data class MonthlySalesSummary(
    val totalProducts: Int = 0,
    val totalAmount: Double = 0.0
)

data class CustomersUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val errorMessage: String? = null,
    val name: String = "",
    val searchQuery: String = "",
    val extraInfo: String? = null,
    val customers: List<Customer> = emptyList(),
    val metric: MonthlySalesSummary = MonthlySalesSummary(),
    val currentCity: City? = null,
    val showModalCreateCustomer: Boolean = false,
    val showModalDeleteCustomer: Uuid? = null,
)