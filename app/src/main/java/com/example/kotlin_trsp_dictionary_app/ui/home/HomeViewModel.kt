package com.example.kotlin_trsp_dictionary_app.ui.home

import androidx.lifecycle.ViewModel
import com.example.kotlin_trsp_dictionary_app.data.model.Category
import com.example.kotlin_trsp_dictionary_app.data.model.TermOfDay
import com.example.kotlin_trsp_dictionary_app.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Home screen er puro state. UI sudhu ei ta collect korbe.
data class HomeUiState(
    val searchText: String = "",
    val categories: List<Category> = emptyList(),
    val recentList: List<String> = emptyList(),
    val termOfDay: TermOfDay = TermOfDay()
)

// 3. VIEWMODEL (VM in MVVM) - UI er sob data ekhane thake.
// Screen sudhu dekhe, logic ekhane. Repository constructor e ashe (DI).
class HomeViewModel(
    private val homeRepository: HomeRepository
) : ViewModel() {

    // Prothomei repo theke data load
    private val _uiState = MutableStateFlow(
        HomeUiState(
            categories = homeRepository.getCategories(),
            recentList = homeRepository.getRecentSearches(),
            termOfDay = homeRepository.getTermOfDay()
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Search box e type korle
    fun onSearchChange(newText: String) {
        _uiState.update { it.copy(searchText = newText) }
    }

    // Search button press korle
    fun onSearchGo() {
        val q = _uiState.value.searchText.trim()
        if (q.isEmpty()) return
        _uiState.update { state ->
            // Notun ta upore add koro, duplicate bad dao
            val newList = buildList {
                add(q)
                for (item in state.recentList) {
                    if (item != q) add(item)
                }
            }
            state.copy(recentList = newList.take(8), searchText = "")
        }
    }

    // Recent clear button
    fun onClearRecent() {
        _uiState.update { it.copy(recentList = emptyList()) }
    }

    // Recent chip e click korle search box e bosao
    fun onRecentClick(word: String) {
        _uiState.update { it.copy(searchText = word) }
    }
}
