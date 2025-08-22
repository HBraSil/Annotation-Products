package com.example.produtosdelimpeza.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.produtosdelimpeza.compose.search.SearchScreenDataStore
import com.example.produtosdelimpeza.compose.search.searchScreenDataStore
import kotlinx.coroutines.launch

class SearchHistoryViewModel(private val searchHistoryManager: SearchScreenDataStore) : ViewModel()  {
    // A lista que irá manter o histórico de pesquisas
    private val _history = mutableStateListOf<String>()

    // A lista que a UI irá observar
    val history: List<String> get() = _history
    //val type: StateFlow<String> = _type.asStateFlow()

    init {
        // Carrega o histórico do DataStore quando o ViewModel é criado
        viewModelScope.launch {
            _history.addAll(searchHistoryManager.getSearchHistory())
        }
    }


    // Função para adicionar um novo item ao histórico
    fun addSearchItem(item: String) {
        if (item.isNotBlank() && !_history.contains(item)) {
            _history.add(0, item) // Adiciona no início da lista
            viewModelScope.launch {
                searchHistoryManager.saveSearchHistory(_history.toSet())
            }
        }
    }

    fun removeSearchItem(item: String) {
        _history.remove(item)
        viewModelScope.launch {
            searchHistoryManager.saveSearchHistory(_history.toSet())
        }
    }

    fun clearSearchHistory() {
        _history.clear()
        viewModelScope.launch {
            searchHistoryManager.saveSearchHistory(emptySet())
        }
    }


    class Factory(context: Context) {
        val cont: Context = context
        val searchHistoryManager: SearchScreenDataStore = SearchScreenDataStore(cont.searchScreenDataStore)

        val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SearchHistoryViewModel::class.java)) {
                    return SearchHistoryViewModel(searchHistoryManager) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}