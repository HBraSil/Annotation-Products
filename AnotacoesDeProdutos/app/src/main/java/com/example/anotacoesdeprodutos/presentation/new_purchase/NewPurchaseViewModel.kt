package com.example.anotacoesdeprodutos.presentation.new_purchase

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotacoesdeprodutos.domain.model.CartItem
import com.example.anotacoesdeprodutos.domain.model.Customer
import com.example.anotacoesdeprodutos.domain.model.Product
import com.example.anotacoesdeprodutos.domain.model.Purchase
import com.example.anotacoesdeprodutos.domain.repository.CustomerRepository
import com.example.anotacoesdeprodutos.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NewPurchaseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val customerRepository: CustomerRepository
) : ViewModel()  {

    private val customerId: Long = checkNotNull(savedStateHandle["customerId"])

    private val _uiState = MutableStateFlow(NewPurchaseUiState())
    val uiState: StateFlow<NewPurchaseUiState> = _uiState.asStateFlow()

    val customer = customerRepository.getCustomer(customerId)
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Customer()
    )


    init {
        viewModelScope.launch {
            val products = productRepository.getProductsWithDefinedPrice()

            customer.collect { customer ->
                _uiState.update {
                    val debt = customer.owes ?: 0.0
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
            if (customerId <= 0) {
                Log.e("NewPurchaseViewModel", "Cannot finalize purchase: Invalid customerId $customerId")
                return@launch
            }

            try {
                val purchase = Purchase(
                    customerId = customerId,
                    purchaseDate = System.currentTimeMillis(),
                    totalAmount = uiState.value.selectedProductsSubtotal.toDouble()
                )

                val currentCustomer = customer.value
                val updatedCustomer = currentCustomer.copy(
                    id = customerId,
                    owes = uiState.value.totalPrice
                )

                val purchaseId = customerRepository.newPurchase(purchase = purchase)

                customerRepository.updateCustomer(updatedCustomer)

                if (purchaseId > 0) {
                    val cartItems = _uiState.value.selectedProducts.map {
                        it.copy(
                            purchaseId = purchaseId,
                            productId = it.product.id
                        )
                    }
                    Log.d("NewPurchaseViewModel", "Saving cart items: $cartItems")

                    val saveResults = customerRepository.saveCartItems(cartItems)

                    if (saveResults.isNotEmpty()) {
                        _uiState.update { it.copy(success = true) }
                    }
                }
            } catch (e: Exception) {
                Log.e("NewPurchaseViewModel", "Error finalizing purchase", e)
            }
        }
    }
}


data class NewPurchaseUiState(
    val pendingDebt: Double = 0.0, // DEPOIS REMOVER ESSA PROPRIEDADE
    val allProducts: List<Product> = emptyList(),
    val selectedProducts: List<CartItem> = mutableListOf(),
    val selectedProductsSubtotal: Int = 0,
    val totalPrice: Double = pendingDebt,
    val isDropdownExpanded: Boolean = false,
    val success: Boolean = false,
)