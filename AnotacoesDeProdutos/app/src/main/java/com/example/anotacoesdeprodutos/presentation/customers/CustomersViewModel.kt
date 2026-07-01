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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
                    Log.d("CustomersViewModel", "observeSearchQuery: $customers ---|||--- and ${customerUiState.value.customers}")
                }
        }
    }


    val customerList = customerRepository.getAllCustomers(cityIdFlow)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateSearchQuery(query: String) {
        _customerUiState.update { it.copy(searchQuery = query) }
    }

    fun updateName(name: String) {
        _customerUiState.update { it.copy(name = name) }
    }

    fun updateExtraInfo(extraInfo: String) {
        _customerUiState.update { it.copy(extraInfo = extraInfo) }
    }

    fun showModalCreateCustomer() {
        _customerUiState.update {
            it.copy(showModalCreateCustomer = true)
        }
    }

    fun showModalDeleteCustomer(id: Long) {
        _customerUiState.update {
            it.copy(showModalDeleteCustomer = id)
        }
    }


    fun closeModalAndOverlayCreatedCustomer() {
        _customerUiState.update {
            it.copy(
                showModalCreateCustomer = false,
                customerCreatedWithSuccess = false
            )
        }
    }

    fun onDismissModalDeleteCustomer() {
        _customerUiState.update {
            it.copy(showModalDeleteCustomer = -1)
        }
    }

    fun deleteCustomer() {
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

    fun saveCustomer() {
        viewModelScope.launch {
            val customer = Customer(
                name = _customerUiState.value.name,
                extraInfo = _customerUiState.value.extraInfo,
                cityId = _customerUiState.value.currentCity?.id ?: 0
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