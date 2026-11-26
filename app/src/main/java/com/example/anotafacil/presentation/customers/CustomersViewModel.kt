package com.example.anotafacil.presentation.customers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.formatter.FormatDate
import com.example.anotafacil.domain.formatter.brazilianCurrencyFormatter
import com.example.anotafacil.domain.formatter.parseBrazilianCurrencyInput
import com.example.anotafacil.domain.model.CartItem
import com.example.anotafacil.domain.model.City
import com.example.anotafacil.domain.model.Customer
import com.example.anotafacil.domain.model.Product
import com.example.anotafacil.domain.model.Purchase
import com.example.anotafacil.domain.repository.CityRepository
import com.example.anotafacil.domain.repository.CustomerRepository
import com.example.anotafacil.domain.repository.ProductRepository
import com.example.anotafacil.domain.usecase.RefreshCustomersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid


@OptIn(ExperimentalCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CustomersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository,
    private val cityRepository: CityRepository,
    private val productRepository: ProductRepository,
    private val refreshCustomersUseCase: RefreshCustomersUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomersUiState())
    val uiState = _uiState.asStateFlow()

    val cityIdFlow = savedStateHandle
        .getStateFlow<String?>("cityId", null)
        .filterNotNull()
        .map(Uuid::parse)


    init {
        getCityName()
        getCustomersList()
        getMonthlySalesSummary()
        observeSearchQuery()
        getProductsWithDefinedPrice()
    }


    fun customersEvent(event: CustomersUiEvent) {
        when (event) {
            is CustomersUiEvent.OnSearchQueryChange -> updateSearchQuery(event.query)
            is CustomersUiEvent.OnNameChange -> updateName(event.name)
            is CustomersUiEvent.OnExtraInfoChange -> updateExtraInfo(event.extraInfo)
            is CustomersUiEvent.OnShowModalDeleteCustomer -> showModalDeleteCustomer(event.id)
            is CustomersUiEvent.OnLastPurchaseDateChange -> updateLastPurchaseDate(event.date)
            is CustomersUiEvent.OnLastPurchasedProductsSelected -> updateLastPurchasedProducts(event.product)
            is CustomersUiEvent.OnOpeningDebtChange -> updateOpeningDebt(event.openingDebt)
            is CustomersUiEvent.OnIncreaseQuantityClick -> increaseQuantity(event.product)
            is CustomersUiEvent.OnDecreaseQuantityClick -> decreaseQuantity(event.product)
            is CustomersUiEvent.OnCreateCustomerClick -> saveCustomer()
            is CustomersUiEvent.OnDismissModalDeleteCustomer -> onDismissModalDeleteCustomer()
            is CustomersUiEvent.OnShowModalCreateCustomer -> showModalCreateCustomer()
            is CustomersUiEvent.OnDeleteCustomerClick -> deleteCustomer()
            is CustomersUiEvent.OnDismissOverlayCreatedCustomer -> closeModalAndOverlayCreatedCustomer()
        }

    }


    fun refreshCustomers() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val cityId = cityIdFlow.first()

            refreshCustomersUseCase(cityId)
                .onSuccess { success ->
                    if (success) {
                        _uiState.update {
                            it.copy(message = "Dados atualizados com sucesso")
                        }
                    } else {
                        _uiState.update {
                            it.copy(message = it.message ?: "Erro ao atualizar dados")
                        }
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(message = it.message ?: "Erro ao atualizar dados")
                    }
                }

            delay(400.milliseconds)
            _uiState.update { it.copy(isLoading = false, message = null) }
        }
    }


    private fun getCityName() {
        viewModelScope.launch {
            cityIdFlow.mapLatest { cityId ->
                cityRepository.getCity(cityId)
            }.collect { result ->
                result.onSuccess { city ->
                    _uiState.update { it.copy(currentCity = city) }
                }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(message = error.message ?: "Erro ao carregar cidade")
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
                            _uiState.update {
                                it.copy(customers = customers)
                            }
                        },
                        onFailure = { error ->
                            _uiState.update {
                                it.copy(message = error.message ?: "Erro ao carregar clientes")
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
                    _uiState.update { it.copy(metric = monthlySalesSummary) }
                }
        }
    }


    private fun observeSearchQuery() {
        viewModelScope.launch {
            _uiState
                .map { it.searchQuery }
                .distinctUntilChanged()
                .collect { query ->

                    _uiState.update { state ->
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


    private fun getProductsWithDefinedPrice() {
        viewModelScope.launch {
            val products = productRepository.getProductsWithDefinedPrice()

            _uiState.update {
                it.copy(
                    addNewCustomersField = it.addNewCustomersField.copy(availableProducts = products)
                )
            }
        }
    }

    private fun updateSearchQuery(query: String) =
        _uiState.update {
            it.copy(searchQuery = query)
        }


    private fun updateName(name: String) =
        _uiState.update {
            it.copy(addNewCustomersField = it.addNewCustomersField.copy(name = name))
        }


    private fun updateExtraInfo(extraInfo: String) =
        _uiState.update {
            it.copy(addNewCustomersField = it.addNewCustomersField.copy(extraInfo = extraInfo))
        }


    private fun updateLastPurchasedProducts(product: Product) =
        _uiState.update { state ->
            val findProduct =
                state.addNewCustomersField.lastPurchasedProducts.keys.find { it.id == product.id }

            val updatedProducts = if (findProduct != null) {
                val value = state.addNewCustomersField.lastPurchasedProducts[findProduct]


                state.addNewCustomersField.lastPurchasedProducts.mapValues {
                    if (it.key.id == product.id) {
                        value?.plus(1) ?: 1
                    } else {
                        it.value
                    }
                }
            } else {
                state.addNewCustomersField.lastPurchasedProducts + (product to 1)
            }

            state.copy(
                addNewCustomersField = state.addNewCustomersField.copy(
                    lastPurchasedProducts = updatedProducts
                )
            )
        }

    private fun updateLastPurchaseDate(date: Long?) =
        _uiState.update {
            it.copy(addNewCustomersField = it.addNewCustomersField.copy(lastPurchaseDateMillis = date))
        }

    private fun updateOpeningDebt(openingDebt: String) {
        val toBrazilianInput = parseBrazilianCurrencyInput(openingDebt)
        val visualFormatText = brazilianCurrencyFormatter.format(toBrazilianInput)

        _uiState.update {
            it.copy(
                addNewCustomersField = it.addNewCustomersField.copy(
                    openingDebt = TextFieldValue(
                        visualFormatText,
                        selection = androidx.compose.ui.text.TextRange(visualFormatText.length)
                    )
                )
            )
        }
    }


    fun decreaseQuantity(product: Product) {
        _uiState.update { state ->
            val selectedProducts = state.addNewCustomersField.lastPurchasedProducts
            val selectedProduct = selectedProducts.keys.find { it.id == product.id }
                ?: return@update state
            val quantity = selectedProducts[selectedProduct] ?: return@update state
            val updatedProducts = if (quantity <= 1) {
                selectedProducts.filterKeys { it.id != product.id }
            } else {
                selectedProducts.mapValues { (selected, currentQuantity) ->
                    if (selected.id == product.id) currentQuantity - 1 else currentQuantity
                }
            }

            state.copy(
                addNewCustomersField = state.addNewCustomersField.copy(
                    lastPurchasedProducts = updatedProducts
                )
            )
        }
    }

    private fun increaseQuantity(product: Product) {
        _uiState.update { state ->
            val selectedProducts = state.addNewCustomersField.lastPurchasedProducts
            if (selectedProducts.keys.none { it.id == product.id }) {
                return@update state
            }

            val updatedProducts = selectedProducts.mapValues { (selected, quantity) ->
                if (selected.id == product.id) quantity + 1 else quantity
            }

            state.copy(
                addNewCustomersField = state.addNewCustomersField.copy(
                    lastPurchasedProducts = updatedProducts
                )
            )
        }
    }


    private fun showModalCreateCustomer() =
        _uiState.update {
            it.copy(showModalCreateCustomer = true)
        }


    private fun showModalDeleteCustomer(id: Uuid?) =
        _uiState.update {
            it.copy(showModalDeleteCustomer = id)
        }


    private fun closeModalAndOverlayCreatedCustomer() {
        _uiState.update {
            val updatedFields = if (it.success) {
                AddNewCustomersField(
                    availableProducts = it.addNewCustomersField.availableProducts
                )
            } else {
                it.addNewCustomersField
            }

            it.copy(
                showModalCreateCustomer = false,
                success = false,
                addNewCustomersField = updatedFields
            )
        }
    }


    private fun onDismissModalDeleteCustomer() = _uiState.update {
        it.copy(showModalDeleteCustomer = null)
    }


    private fun deleteCustomer() {
        val customerId = _uiState.value.showModalDeleteCustomer
            ?: return

        _uiState.update {
            it.copy(
                showModalDeleteCustomer = null,
                isDeletingCustomer = true
            )
        }

        viewModelScope.launch {
            val result = customerRepository.deleteCustomer(customerId)

            result
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeletingCustomer = false,
                            showModalDeleteCustomer = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            message = exception.message
                                ?: "Não foi possível excluir o cliente.",
                            isDeletingCustomer = false
                        )
                    }
                }
        }
    }


    private fun saveCustomer() {
        val state = _uiState.value
        if (state.isSavingCustomer) return

        val fields = state.addNewCustomersField
        val name = fields.name.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(message = "Informe o nome do cliente.") }
            return
        }

        val city = state.currentCity
        if (city == null) {
            _uiState.update { it.copy(message = "Não foi possível identificar a cidade do cliente.") }
            return
        }

        val customerId = Uuid.random()
        val openingDebt = parseBrazilianCurrencyInput(fields.openingDebt.text)
        val purchaseId = Uuid.random()
        val purchase = fields.lastPurchasedProducts
            .takeIf { it.isNotEmpty() }
            ?.let { selectedProducts ->
                Purchase(
                    id = purchaseId,
                    customerId = customerId,
                    ownerId = city.ownerId,
                    purchaseDate = fields.lastPurchaseDateMillis ?: 0L,
                    totalAmount = selectedProducts.entries.sumOf { (product, quantity) ->
                        product.price.toDouble() * quantity
                    }
                )
            }
        val cartItems = fields.lastPurchasedProducts.map { (product, quantity) ->
            CartItem(
                product = product,
                productId = product.id,
                purchaseId = purchaseId,
                quantity = quantity
            )
        }
        val lastPurchaseDate = fields.lastPurchaseDateMillis?.let { millis ->
            SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .format(Date(millis))
        }

        val customer = Customer(
            id = customerId,
            name = name,
            extraInfo = fields.extraInfo
                .takeUnless { it.isBlank() }
                ?.trim()
                ?: "Nenhuma informação extra",
            owes = openingDebt.takeIf { it > 0.0 },
            lastPurchaseDate = lastPurchaseDate,
            cityId = city.id
        )

        _uiState.update { it.copy(isSavingCustomer = true, message = null) }
        viewModelScope.launch {
            try {
                customerRepository.addCustomerWithHistory(
                    customer = customer,
                    purchase = purchase,
                    cartItems = cartItems
                )
                    .onSuccess {
                        _uiState.update { customerUiState ->
                            customerUiState.copy(success = true)
                        }
                    }
                    .onFailure { exception ->
                        _uiState.update {
                            it.copy(
                                message = exception.message
                                    ?: "Não foi possível salvar o cliente."
                            )
                        }
                    }
            } finally {
                _uiState.update { it.copy(isSavingCustomer = false) }
            }
        }
    }


}


sealed interface CustomersUiEvent {
    data class OnSearchQueryChange(val query: String) : CustomersUiEvent
    data class OnNameChange(val name: String) : CustomersUiEvent
    data class OnExtraInfoChange(val extraInfo: String) : CustomersUiEvent
    data class OnShowModalDeleteCustomer(val id: Uuid?) : CustomersUiEvent
    data class OnLastPurchasedProductsSelected(val product: Product) : CustomersUiEvent
    data class OnLastPurchaseDateChange(val date: Long?) : CustomersUiEvent
    data class OnOpeningDebtChange(val openingDebt: String) : CustomersUiEvent
    data class OnIncreaseQuantityClick(val product: Product) : CustomersUiEvent
    data class OnDecreaseQuantityClick(val product: Product) : CustomersUiEvent
    object OnCreateCustomerClick : CustomersUiEvent
    object OnDeleteCustomerClick : CustomersUiEvent
    object OnDismissModalDeleteCustomer : CustomersUiEvent
    object OnShowModalCreateCustomer : CustomersUiEvent
    object OnDismissOverlayCreatedCustomer : CustomersUiEvent
}

data class MonthlySalesSummary(
    val totalProducts: Int = 0,
    val totalAmount: Double = 0.0,
)

data class CustomersUiState(
    val isLoading: Boolean = false,
    val isSavingCustomer: Boolean = false,
    val success: Boolean = false,
    val message: String? = null,
    val addNewCustomersField: AddNewCustomersField = AddNewCustomersField(),
    val isDeletingCustomer: Boolean = false,
    val searchQuery: String = "",
    val customers: List<Customer> = emptyList(),
    val metric: MonthlySalesSummary = MonthlySalesSummary(),
    val currentCity: City? = null,
    val showModalCreateCustomer: Boolean = false,
    val showModalDeleteCustomer: Uuid? = null,
)


data class AddNewCustomersField(
    val name: String = "",
    val extraInfo: String = "",
    val availableProducts: List<Product> = emptyList(),
    val lastPurchasedProducts: Map<Product, Int> = emptyMap(),
    val lastPurchaseDateMillis: Long? = null,
    val openingDebt: TextFieldValue = TextFieldValue("R$ 0,00"),
)