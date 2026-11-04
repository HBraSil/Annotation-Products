package com.example.anotafacil.presentation.price_definition

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anotafacil.domain.formatter.brazilianCurrencyFormatter
import com.example.anotafacil.domain.formatter.parseBrazilianCurrencyInput
import com.example.anotafacil.domain.model.Product
import com.example.anotafacil.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class PriceDefinitionViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PriceDefinitionUiState())
    val uiState = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            val products = productRepository.getAllProducts().map { product ->
                Product(
                    id = product.id,
                    name = product.name,
                    price = product.price
                )
            }
            _uiState.update { it.copy(items = products, originalItems = products) }
        }
    }


    fun addManualProduct() {
        val productName = _uiState.value.manualProductName.trim()

        val toBrazilianInput = parseBrazilianCurrencyInput(_uiState.value.manuelProductPrice.text)
        val product = Product(
            name = productName,
            price = toBrazilianInput.toInt(),
        )

        _uiState.update { uiState ->
            uiState.copy(
                items = listOf(product) + uiState.items,
                manualProductName = "",
                manuelProductPrice = TextFieldValue("R$ 0,00")
            )
        }
    }


    fun updateManualProductName(name: String) {
        _uiState.update { uiState ->
            uiState.copy(manualProductName = name)
        }
    }


    fun updateProductPrice(productId: Uuid, newPrice: String) {
        val parsedPrice = newPrice.toIntOrNull() ?: 0

        _uiState.update { uiState ->
            uiState.copy(
                items = uiState.items.map { product ->
                    if (product.id == productId)
                        product.copy(price = parsedPrice)
                    else
                        product
                }
            )
        }
    }


    fun updateManualProductPrice(newPrice: String) {

        val toBrazilianInput = parseBrazilianCurrencyInput(newPrice)
        val visualFormatText = brazilianCurrencyFormatter.format(toBrazilianInput)

        _uiState.update { uiState ->
            uiState.copy(
                manuelProductPrice = TextFieldValue(
                    visualFormatText,
                    selection = TextRange(visualFormatText.length)
                )
            )
        }
    }


    fun dismissPriceSaved() {
        _uiState.update { it.copy(priceSaved = false) }
    }

    fun savePrices() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            var result = false

            _uiState.value.items.forEach { product ->
                if (product.price == 0) return@forEach
                result = productRepository.saveProductChanges(product)
            }

            if (result) {
                _uiState.update { uiState ->
                    uiState.copy(
                        originalItems = uiState.items,
                        isLoading = false,
                        priceSaved = true
                    )
                }
            }
        }
    }
}

data class PriceDefinitionUiState(
    val items: List<Product> = emptyList(),
    val originalItems: List<Product> = emptyList(),
    val manualProductName: String = "",
    val manuelProductPrice: TextFieldValue = TextFieldValue("R$ 0,00"),
    val itemPrice: String = "",
    val priceSaved: Boolean = false,
    val isLoading: Boolean = false
) {
    val hasChanges: Boolean
        get() = items.size > originalItems.size ||
            items.any { item ->
                val originalItem = originalItems.firstOrNull { it.id == item.id }
                originalItem != null && originalItem.price != item.price
            }
}
