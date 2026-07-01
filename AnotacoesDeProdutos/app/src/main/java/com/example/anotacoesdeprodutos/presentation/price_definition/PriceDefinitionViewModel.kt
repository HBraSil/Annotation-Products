package com.example.anotacoesdeprodutos.presentation.price_definition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotacoesdeprodutos.domain.model.Product
import com.example.anotacoesdeprodutos.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PriceDefinitionViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PriceDefinitionUiState())
    val uiState = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            val products = productRepository.getAllProducts()
            _uiState.update { it.copy(items = products) }
        }
    }


    fun updateProductPrice(productId: Long, newPrice: String) {
        _uiState.update { uiState ->
            uiState.copy(
                items = uiState.items.map { product ->
                    if (product.id == productId) {
                        product.copy(price = newPrice.toInt())
                    } else {
                        product
                    }
                }
            )
        }
    }

    fun dismissPriceSaved() {
        _uiState.update { it.copy(priceSaved = false) }
    }

    fun savePrices() {
        viewModelScope.launch {
            val updatedProducts = _uiState.value.items

            var result = 0
            updatedProducts.forEach { product ->
                result = productRepository.updateProductPrice(product.id, product.price)
            }

            if (result > 0) {
                _uiState.update { uiState ->
                    uiState.copy(priceSaved = true)
                }
            }
        }
    }
}

data class PriceDefinitionUiState(
    val items: List<Product> = emptyList(),
    val priceSaved: Boolean = false
)
