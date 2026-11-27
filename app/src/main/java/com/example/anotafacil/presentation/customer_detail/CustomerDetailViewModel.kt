package com.example.anotafacil.presentation.customer_detail

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.formatter.brazilianCurrencyFormatter
import com.example.anotafacil.domain.formatter.parseBrazilianCurrencyInput
import com.example.anotafacil.domain.model.CartItem
import com.example.anotafacil.domain.model.Customer
import com.example.anotafacil.domain.model.Payment
import com.example.anotafacil.domain.model.Product
import com.example.anotafacil.domain.model.Purchase
import com.example.anotafacil.domain.repository.CityRepository
import com.example.anotafacil.domain.repository.CustomerRepository
import com.example.anotafacil.domain.repository.ProductRepository
import com.example.anotafacil.presentation.customers.AddNewCustomersField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlin.uuid.Uuid


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository,
    private val cityRepository: CityRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CustomerDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val customerUuid = savedStateHandle
        .getStateFlow<String?>("customerId", null)
        .filterNotNull()
        .map(Uuid::parse)


    init {
        getCustomer()
        getLastPurchase()
        getProductsWithDefinedPrice()
    }


    private fun getCustomer() {
        viewModelScope.launch {
            customerUuid.flatMapLatest {
                customerRepository.getCustomer(it)
            }.collect {
                _uiState.update { state ->
                    state.copy(customer = it ?: Customer())
                }
            }
        }
    }


    private fun getLastPurchase() {
        viewModelScope.launch {
            customerUuid.flatMapLatest {
                customerRepository.getLastPurchase(it)
            }.collect { purchaseWithItems ->
                _uiState.update {
                    it.copy(
                        purchase = purchaseWithItems?.purchase ?: Purchase(),
                        hasLastPurchase = purchaseWithItems != null,
                        purchaseItems = purchaseWithItems?.items?.map { item ->
                            item.cartItem.copy(product = item.product)
                        } ?: emptyList()
                    )
                }
            }
        }
    }

    private fun getProductsWithDefinedPrice() {
        viewModelScope.launch {
            val products = productRepository.getProductsWithDefinedPrice()
            _uiState.update { state ->
                state.copy(
                    editFields = state.editFields.copy(availableProducts = products)
                )
            }
        }
    }

    fun openEditProfile() {
        val state = _uiState.value
        val customer = state.customer
        if (customer.id == null) {
            _uiState.update { it.copy(message = "Não foi possível carregar o cliente para edição.") }
            return
        }

        val products = state.editFields.availableProducts
        val selectedProducts = state.purchaseItems.associate { item ->
            (products.find { it.id == item.productId } ?: item.product) to item.quantity
        }
        val purchaseDate = state.purchase.purchaseDate
            .takeIf { state.hasLastPurchase && it > 0L }
            ?: customer.lastPurchaseDate?.let(::parseBrazilianDate)

        _uiState.update {
            it.copy(
                showEditProfile = true,
                editSuccess = false,
                editFields = AddNewCustomersField(
                    name = customer.name,
                    extraInfo = customer.extraInfo.takeUnless {
                        it == "Nenhuma informação extra"
                    }.orEmpty(),
                    availableProducts = products,
                    lastPurchasedProducts = selectedProducts,
                    lastPurchaseDateMillis = purchaseDate,
                    openingDebt = TextFieldValue(
                        brazilianCurrencyFormatter.format(customer.owes ?: 0.0)
                    )
                )
            )
        }
    }

    fun updateEditName(name: String) {
        _uiState.update {
            it.copy(editFields = it.editFields.copy(name = name))
        }
    }

    fun updateEditExtraInfo(extraInfo: String) {
        _uiState.update {
            it.copy(editFields = it.editFields.copy(extraInfo = extraInfo))
        }
    }

    fun updateEditPurchaseDate(date: Long?) {
        _uiState.update {
            it.copy(editFields = it.editFields.copy(lastPurchaseDateMillis = date))
        }
    }

    fun updateEditOpeningDebt(value: String) {
        val formatted = brazilianCurrencyFormatter.format(parseBrazilianCurrencyInput(value))
        _uiState.update {
            it.copy(
                editFields = it.editFields.copy(
                    openingDebt = TextFieldValue(
                        formatted,
                        selection = TextRange(formatted.length)
                    )
                )
            )
        }
    }

    fun selectEditProduct(product: Product) {
        _uiState.update { state ->
            val current = state.editFields.lastPurchasedProducts
            val selected = current.keys.find { it.id == product.id }
            val updated = if (selected == null) {
                current + (product to 1)
            } else {
                current.mapKeys { (existing, _) ->
                    if (existing.id == product.id) product else existing
                }.mapValues { (existing, quantity) ->
                    if (existing.id == product.id) quantity + 1 else quantity
                }
            }
            state.copy(editFields = state.editFields.copy(lastPurchasedProducts = updated))
        }
    }

    fun increaseEditProductQuantity(product: Product) {
        _uiState.update { state ->
            val updated = state.editFields.lastPurchasedProducts.mapValues { (selected, quantity) ->
                if (selected.id == product.id) quantity + 1 else quantity
            }
            state.copy(editFields = state.editFields.copy(lastPurchasedProducts = updated))
        }
    }

    fun decreaseEditProductQuantity(product: Product) {
        _uiState.update { state ->
            val updated = state.editFields.lastPurchasedProducts.entries.mapNotNull { (selected, quantity) ->
                when {
                    selected.id != product.id -> selected to quantity
                    quantity > 1 -> selected to quantity - 1
                    else -> null
                }
            }.toMap()
            state.copy(editFields = state.editFields.copy(lastPurchasedProducts = updated))
        }
    }

    fun saveCustomerProfile() {
        val state = _uiState.value
        if (state.isSavingProfile) return

        val customer = state.customer
        val fields = state.editFields
        val name = fields.name.trim()
        if (customer.id == null || name.isBlank()) {
            _uiState.update { it.copy(message = "Informe o nome do cliente.") }
            return
        }

        _uiState.update { it.copy(isSavingProfile = true, message = null) }
        viewModelScope.launch {
            try {
                val lastPurchaseDate = fields.lastPurchaseDateMillis?.let(::formatBrazilianDate)
                val updatedCustomer = customer.copy(
                    name = name,
                    extraInfo = fields.extraInfo.trim().ifBlank { "Nenhuma informação extra" },
                    owes = parseBrazilianCurrencyInput(fields.openingDebt.text)
                        .takeIf { it > 0.0 },
                    lastPurchaseDate = lastPurchaseDate
                )

                val existingPurchase = state.purchase.takeIf { state.hasLastPurchase }
                val shouldCreatePurchase =
                    existingPurchase == null &&
                        (fields.lastPurchaseDateMillis != null ||
                            fields.lastPurchasedProducts.isNotEmpty())

                val ownerId = if (shouldCreatePurchase) {
                    val cityId = customer.cityId
                        ?: throw IllegalStateException("Não foi possível identificar a cidade do cliente.")
                    cityRepository.getCity(cityId).getOrElse { throw it }?.ownerId
                        ?: throw IllegalStateException("Não foi possível identificar o proprietário da cidade.")
                } else {
                    null
                }

                val purchaseId = existingPurchase?.id ?: Uuid.random()
                val updatedPurchase = existingPurchase?.copy(
                    purchaseDate = fields.lastPurchaseDateMillis ?: 0L,
                    totalAmount = fields.lastPurchasedProducts.entries.sumOf { (product, quantity) ->
                        product.price.toDouble() * quantity
                    }
                ) ?: if (shouldCreatePurchase) {
                    Purchase(
                        id = purchaseId,
                        customerId = customer.id,
                        ownerId = requireNotNull(ownerId),
                        purchaseDate = fields.lastPurchaseDateMillis ?: 0L,
                        totalAmount = fields.lastPurchasedProducts.entries.sumOf { (product, quantity) ->
                            product.price.toDouble() * quantity
                        }
                    )
                } else {
                    null
                }

                val cartItems = fields.lastPurchasedProducts.map { (product, quantity) ->
                    CartItem(
                        product = product,
                        productId = product.id,
                        purchaseId = purchaseId,
                        quantity = quantity
                    )
                }

                customerRepository.updateCustomerWithLastPurchase(
                    customer = updatedCustomer,
                    purchase = updatedPurchase,
                    cartItems = cartItems,
                    replacePurchaseId = existingPurchase?.id
                ).onSuccess {
                    _uiState.update { it.copy(editSuccess = true) }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(message = error.message ?: "Não foi possível atualizar o perfil.")
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(message = error.message ?: "Não foi possível atualizar o perfil.")
                }
            } finally {
                _uiState.update { it.copy(isSavingProfile = false) }
            }
        }
    }

    fun dismissEditProfile() {
        _uiState.update {
            it.copy(showEditProfile = false, editSuccess = false)
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun parseBrazilianDate(date: String): Long? =
        SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
            .apply {
                isLenient = false
                timeZone = TimeZone.getTimeZone("UTC")
            }
            .parse(date)
            ?.time

    private fun formatBrazilianDate(millis: Long): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .format(Date(millis))


    fun updatePartialPayment(value: String) {

        val toBrazilianInput = parseBrazilianCurrencyInput(value)
        val visualFormatText = brazilianCurrencyFormatter.format(toBrazilianInput)

        _uiState.update {
            it.copy(
                partialPaymentComponent = TextFieldValue(
                    text = visualFormatText,
                    selection = TextRange(visualFormatText.length)
                ),
                payment = it.payment.copy(amount = toBrazilianInput)
            )
        }
    }


    fun showConfirmationDialog(confirmationAction: ConfirmationAction) {
        _uiState.update {
            it.copy(
                buttonConfirmationType = confirmationAction,
                showConfirmationDialog = true
            )
        }
    }


    fun confirmSelectedOnDialog() {
        when (_uiState.value.buttonConfirmationType) {
            ConfirmationAction.PARTIAL_PAYMENT -> {
                confirmPartialPayment()
            }

            ConfirmationAction.TOTAL_PAYMENT -> {
                onTotalPaymentConfirm()
            }

            else -> {}
        }
    }


    fun confirmPartialPayment() {
        val payment = _uiState.value.payment

        if (payment.amount <= 0 || payment.amount > (_uiState.value.customer.owes ?: 0.0)) {
            _uiState.update {
                it.copy(
                    errorPartialPayment = true,
                    showConfirmationDialog = false
                )
            }
            return
        }

        viewModelScope.launch {
            val totalUpdated = _uiState.value.customer.owes?.minus(payment.amount)

            _uiState.update {
                it.copy(customer = it.customer.copy(owes = totalUpdated))
            }


            val result = customerRepository.partialPayment(
                _uiState.value.customer,
                _uiState.value.purchase,
                payment.copy(
                    customerId = _uiState.value.customer.id,
                    paymentDate = System.currentTimeMillis()
                )
            )

            if (result) {
                _uiState.update {
                    it.copy(
                        showSuccessDialog = true,
                        showConfirmationDialog = false,
                        partialPaymentComponent = TextFieldValue("R$ 0,00"),
                    )
                }
            }
        }
    }


    fun onTotalPaymentConfirm() {
        viewModelScope.launch {
            val customer = _uiState.value.customer.copy(
                owes = 0.0
            )

            val payment = _uiState.value.payment.copy(
                customerId = _uiState.value.customer.id,
                paymentDate = System.currentTimeMillis(),
                amount = _uiState.value.customer.owes ?: 0.0,
                isTotalPayment = true
            )

            val result = customerRepository.payOffTotalDebt(
                customer = customer,
                payment = payment
            )

            if (result.first > 0 && result.second > 0) {
                _uiState.update {
                    it.copy(
                        showSuccessDialog = true,
                        showConfirmationDialog = false
                    )
                }
            }
        }
    }


    fun onDismissToast() {
        _uiState.update { it.copy(errorPartialPayment = false) }
    }

    fun onDismiss() {
        _uiState.update {
            it.copy(
                showConfirmationDialog = false,
                showSuccessDialog = false
            )
        }
    }
}


data class CustomerDetailUiState(
    val customer: Customer = Customer(),
    val purchase: Purchase = Purchase(),
    val hasLastPurchase: Boolean = false,
    val payment: Payment = Payment(),
    val partialPaymentComponent: TextFieldValue = TextFieldValue("R$ 0,00"),
    val purchaseItems: List<CartItem> = emptyList(),
    val editFields: AddNewCustomersField = AddNewCustomersField(),
    val showEditProfile: Boolean = false,
    val isSavingProfile: Boolean = false,
    val editSuccess: Boolean = false,
    val message: String? = null,
    val showConfirmationDialog: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val errorPartialPayment: Boolean = false,
    val buttonConfirmationType: ConfirmationAction? = null,
)


enum class ConfirmationAction {
    PARTIAL_PAYMENT,
    TOTAL_PAYMENT
}