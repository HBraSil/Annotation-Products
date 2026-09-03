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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.let
import kotlin.uuid.Uuid

@HiltViewModel
class NewPurchaseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val customerRepository: CustomerRepository
) : ViewModel()  {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val customer = savedStateHandle.getStateFlow<String?>("customerId", null)
        .map { idString ->
            idString?.let { Uuid.parse(idString) }
        }
        .flatMapLatest(customerRepository::getCustomer)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _uiState = MutableStateFlow(NewPurchaseUiState())
    val uiState: StateFlow<NewPurchaseUiState> = _uiState.asStateFlow()

    init {

        getProductsWithDefinedPrice()
    }

    private fun getProductsWithDefinedPrice() {
        viewModelScope.launch {
            val products = productRepository.getProductsWithDefinedPrice()

            customer.collect { customer ->
                val debt = customer?.owes ?: 0.0

                _uiState.update {
                    it.copy(
                        pendingDebt = debt,
                        allProducts = products,
                        totalPrice = debt
                    )
                }
            }
        }
    }

    fun onProductSelected(product: Product) {
        _uiState.update { currentState ->
            val existingProduct = currentState.selectedProducts.find { it.product.id == product.id }

            val updatedList = if (existingProduct != null) {
                currentState.selectedProducts.map { item ->
                    if (item.product.id == product.id) {
                        item.copy(quantity = item.quantity + 1)
                    } else {
                        item
                    }
                }
            } else {
                currentState.selectedProducts + CartItem(product = product, quantity = 1)
            }

            val subtotal = updatedList.sumOf {
                it.subtotal()
            }

            currentState.copy(
                selectedProducts = updatedList,
                selectedProductsSubtotal = subtotal,
                totalPrice = subtotal + currentState.pendingDebt
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
        _uiState.update { currentState ->

            val updatedProducts = currentState.selectedProducts.map {
                if (it.product.id == product.id) {
                    it.copy(quantity = it.quantity + 1)
                } else {
                    it
                }
            }

            val subtotal = updatedProducts.sumOf {
                it.subtotal()
            }

            currentState.copy(
                selectedProducts = updatedProducts,
                selectedProductsSubtotal = subtotal,
                totalPrice = subtotal + currentState.pendingDebt
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

            customer.value?.let {
                customerRepository.updateCustomer(
                    it.copy(
                        id = customer.value?.id,
                        owes = uiState.value.totalPrice
                    )
                )
            }
            customerRepository.newPurchase(purchase = purchase)
                .onSuccess {
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
                .onFailure {
                    _uiState.update { it.copy(error = true, errorMessage = it.errorMessage) }
                }

        }
    }
}


data class NewPurchaseUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: Boolean = false,
    val errorMessage: String = "",
    val pendingDebt: Double = 0.0, // DEPOIS REMOVER ESSA PROPRIEDADE
    val allProducts: List<Product> = emptyList(),
    val selectedProducts: List<CartItem> = mutableListOf(),
    val selectedProductsSubtotal: Int = 0,
    val totalPrice: Double = pendingDebt,
    val isDropdownExpanded: Boolean = false,
)