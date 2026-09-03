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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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


    val cityIdFlow = savedStateHandle.getStateFlow<String?>("cityId", null)
        .map { idString ->
            idString?.let { Uuid.parse(it) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly, // Eagerly garante que ele leia o argumento imediatamente!
            initialValue = Uuid.NIL
        )




    init {
        println("CustomersViewModel init: ${cityIdFlow.value}")
        observeCityDetails()
        observeCustomersList()

        /*viewModelScope.launch {
            cityIdFlow
                .filterNotNull()
                .flatMapLatest { cityId ->
                    cityRepository.getMonthlySalesSummary(
                        cityId = cityId,
                        startMonth = MonthStartAndEnd.currentMonth().start,
                        endMonth = MonthStartAndEnd.currentMonth().end
                    )
                }
                .collect { monthlySalesSummary ->
                    _customerUiState.update { it.copy(metric = monthlySalesSummary) }
                }
        }*/


        observeSearchQuery()
    }
    private fun observeCityDetails() {
        viewModelScope.launch {
            cityIdFlow
                .filterNotNull()
                .collect { cityId ->
                    cityRepository.getCity(cityId)
                        .onSuccess { city ->
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

    private fun observeCustomersList() {
        viewModelScope.launch {
            cityIdFlow
                .filterNotNull()
                .flatMapLatest { cityId ->
                    // getAllCustomers(cityId) retorna Flow<Result<List<Customer>>>
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _customerUiState
                .map { it.searchQuery }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    customerRepository.searchCustomer(query, cityIdFlow.value)
                }
                .collect { customers ->
                    _customerUiState.update {
                        it.copy(customers = customers)
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
            println("SALVANDO CLIENTE AQUI")
            if (cityIdFlow.value == null) return@launch
            println("PASSOU E CHEGOU AQUI")


            println("ANALISAR VIEWMODEL ----> ${cityIdFlow.value}")
            val customer = Customer(
                name = _customerUiState.value.name,
                extraInfo = _customerUiState.value.extraInfo,
                cityId = cityIdFlow.value
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