package com.example.anotacoesdeprodutos.presentation.customers

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotacoesdeprodutos.domain.model.City
import com.example.anotacoesdeprodutos.domain.model.Customer
import com.example.anotacoesdeprodutos.domain.repository.CityRepository
import com.example.anotacoesdeprodutos.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class CustomersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository,
    private val cityRepository: CityRepository,
) : ViewModel() {
    val cityIdFlow: Long = checkNotNull(savedStateHandle["cityId"])

    private val _customerUiState = MutableStateFlow(CustomersUiState())
    val customerUiState = _customerUiState.asStateFlow()


    init {
        viewModelScope.launch {
            val cities = cityRepository.getCity(cityIdFlow)

            customerRepository.getAllCustomers(cityIdFlow).collect { customers ->
                _customerUiState.update {
                    it.copy(customers = customers, currentCity = cities)
                }
            }
        }

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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _customerUiState
                .map { it.searchQuery }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    customerRepository.searchCustomer(query, cityIdFlow)
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

    private fun showModalDeleteCustomer(id: Long) = _customerUiState.update {
        it.copy(showModalDeleteCustomer = id)
    }


    private fun closeModalAndOverlayCreatedCustomer() {
        _customerUiState.update {
            it.copy(
                showModalCreateCustomer = false,
                customerCreatedWithSuccess = false
            )
        }

        resetAddNewCustomerFields()
    }


    private fun onDismissModalDeleteCustomer() = _customerUiState.update {
        it.copy(showModalDeleteCustomer = -1)
    }


    private fun deleteCustomer() {
        viewModelScope.launch {
            val customerId = _customerUiState.value.showModalDeleteCustomer
            val result = customerRepository.deleteCustomer(customerId)

            if (result > 0) {
                _customerUiState.update {
                    it.copy(showModalDeleteCustomer = -1)
                }
            }
        }
    }


    private fun saveCustomer() {
        viewModelScope.launch {
            if (cityIdFlow <= 0) {
                Log.e("CustomersViewModel", "Cannot save customer: Invalid cityId $cityIdFlow")
                return@launch
            }

            val customer = Customer(
                name = _customerUiState.value.name,
                extraInfo = _customerUiState.value.extraInfo,
                cityId = cityIdFlow
            )
            val result = customerRepository.addCustomer(customer)

            if (result > 0) {
                _customerUiState.update {
                    it.copy(
                        customerCreatedWithSuccess = true
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
                customerCreatedWithSuccess = false
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
    data class OnShowModalDeleteCustomer(val id: Long) : CustomersUiEvent
    object OnShowModalCreateCustomer : CustomersUiEvent
    object OnDismissOverlayCreatedCustomer : CustomersUiEvent
}


data class CustomersUiState(
    val name: String = "",
    val searchQuery: String = "",
    val extraInfo: String? = null,
    val customers: List<Customer> = emptyList(),
    val currentCity: City? = null,
    val showModalCreateCustomer: Boolean = false,
    val showModalDeleteCustomer: Long = -1,
    val customerCreatedWithSuccess: Boolean = false,
)