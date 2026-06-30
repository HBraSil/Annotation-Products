package com.example.anotacoesdeprodutos.presentation.customers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotacoesdeprodutos.domain.model.City
import com.example.anotacoesdeprodutos.domain.model.Customer
import com.example.anotacoesdeprodutos.domain.repository.CityRepository
import com.example.anotacoesdeprodutos.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class CustomersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository,
    private val cityRepository: CityRepository,
) : ViewModel() {

    private val _customerUiState = MutableStateFlow(CustomersUiState())
    val customerUiState = _customerUiState.asStateFlow()

    val cityIdFlow: StateFlow<Long?> = savedStateHandle.getStateFlow("cityId", null)

    private var customer by mutableStateOf(Customer())

    @OptIn(ExperimentalCoroutinesApi::class)
    val customerList = cityIdFlow
        .filterNotNull()
        .flatMapLatest { id ->
            customerRepository.getAllCustomers(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentCity = cityIdFlow
        .flatMapLatest { id ->
            if (id != null) {
                flow<City> {
                    val city = cityRepository.getCity(id)
                    customer = customer.copy(cityId = id)
                    emit(city) // Emite a cidade para o fluxo
                }
            } else {
                flowOf(null) // Se não houver ID, emite null
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )



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
            customer = customer.copy(
                    name = _customerUiState.value.name,
                    extraInfo = _customerUiState.value.extraInfo
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
    val extraInfo: String? = null,
    val showModalCreateCustomer: Boolean = false,
    val showModalDeleteCustomer: Long = -1,
    val customerCreatedWithSuccess: Boolean = false,
)