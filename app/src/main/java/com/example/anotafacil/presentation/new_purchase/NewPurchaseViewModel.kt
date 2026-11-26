package com.example.anotafacil.presentation.new_purchase

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.model.CartItem
import com.example.anotafacil.domain.model.Product
import com.example.anotafacil.domain.model.Purchase
import com.example.anotafacil.domain.repository.CustomerRepository
import com.example.anotafacil.domain.repository.ProductRepository
import com.example.anotafacil.domain.usecase.NewPurchaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.let
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid

@HiltViewModel
class NewPurchaseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val customerRepository: CustomerRepository,
    private val newPurchaseUseCase: NewPurchaseUseCase
) : ViewModel()  {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val customer = savedStateHandle.getStateFlow<String?>("customerId", null)
        .filterNotNull()
        .map(Uuid::parse)
        .flatMapLatest(customerRepository::getCustomer)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _uiState = MutableStateFlow(NewPurchaseUiState())
    val uiState: StateFlow<NewPurchaseUiState> = _uiState.asStateFlow()

    init {
        updateAllProducts()
        getProductsWithDefinedPrice()
    }

    private fun updateAllProducts() {
        viewModelScope.launch {
            val products = productRepository.getProductsWithDefinedPrice()

            _uiState.update {
                it.copy(allProducts = products)
            }
        }

    }


    private fun getProductsWithDefinedPrice() {
        viewModelScope.launch {
            customer.collect { customer ->
                val debt = customer?.owes ?: 0.0

                _uiState.update {
                    it.copy(
                        pendingDebt = debt,
                        totalPrice = debt
                    )
                }
            }
        }
    }


    fun onProductSelected(product: Product) {
        _uiState.update { state ->
            val existingProduct = state.selectedProducts.find { it.product.id == product.id }

            val updatedList = if (existingProduct != null) {
                state.selectedProducts.map { item ->
                    if (item.product.id == product.id) {
                        item.copy(quantity = item.quantity + 1)
                    } else {
                        item
                    }
                }
            } else {
                state.selectedProducts + CartItem(product = product, quantity = 1)
            }

            val subtotal = updatedList.sumOf {
                it.subtotal()
            }

            state.copy(
                selectedProducts = updatedList,
                selectedProductsSubtotal = subtotal,
                totalPrice = subtotal + state.pendingDebt
            )
        }

    }

    fun decreaseQuantity(product: Product) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedProducts = currentState.selectedProducts.mapNotNull { item ->
                    when {
                        item.product.id != product.id -> item
                        item.quantity > 1 -> item.copy(quantity = item.quantity - 1)
                        else -> null
                    }
                },
                selectedProductsSubtotal = currentState.selectedProductsSubtotal.minus(product.price),
                totalPrice = currentState.totalPrice.minus(product.price)
            )
        }
    }

    fun increaseQuantity(product: Product) {
        _uiState.update { state ->

            val updatedProducts = state.selectedProducts.map {
                if (it.product.id == product.id) {
                    it.copy(quantity = it.quantity + 1)
                } else {
                    it
                }
            }

            val subtotal = updatedProducts.sumOf {
                it.subtotal()
            }

            state.copy(
                selectedProducts = updatedProducts,
                selectedProductsSubtotal = subtotal,
                totalPrice = subtotal + state.pendingDebt
            )
        }
    }

    fun onDismiss() {
        _uiState.update { it.copy(success = false) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun finalizePurchase() {
        viewModelScope.launch {
            if (customer.value?.id == null) return@launch


            val purchase = Purchase(
                customerId = customer.value?.id,
                purchaseDate = System.currentTimeMillis(),
                totalAmount = uiState.value.selectedProductsSubtotal.toDouble()
            )


            newPurchaseUseCase(purchase = purchase)
                .onSuccess {
                    customer.value?.let {
                        customerRepository.updateCustomer(
                            it.copy(
                                id = customer.value?.id,
                                owes = uiState.value.totalPrice
                            )
                        )
                    }

                    val cartItems = _uiState.value.selectedProducts.map {
                        it.copy(
                            purchaseId = purchase.id,
                            productId = it.product.id
                        )
                    }

                    val saveResults = customerRepository.saveCartItems(cartItems)

                    if (saveResults.isNotEmpty()) {
                        _uiState.update { it.copy(success = true) }
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = true, errorMessage = throwable.message) }
                }

            delay(500.milliseconds)
            _uiState.update { it.copy(error = false, errorMessage = null) }
        }
    }
}


data class NewPurchaseUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: Boolean = false,
    val errorMessage: String? = null,
    val pendingDebt: Double = 0.0, // DEPOIS REMOVER ESSA PROPRIEDADE
    val allProducts: List<Product> = emptyList(),
    val selectedProducts: List<CartItem> = mutableListOf(),
    val selectedProductsSubtotal: Int = 0,
    val totalPrice: Double = pendingDebt,
    val isDropdownExpanded: Boolean = false,
)