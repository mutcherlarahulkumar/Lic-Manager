package `in`.bliss.bma.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.bliss.bma.data.model.Agency
import `in`.bliss.bma.data.model.Client
import `in`.bliss.bma.data.model.CommissionSummary
import `in`.bliss.bma.data.model.Policy
import `in`.bliss.bma.data.model.Prospect
import `in`.bliss.bma.data.repo.DataRepository
import `in`.bliss.bma.data.repo.PolicyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Generic UI state: loading / data / error, for any payload type. */
data class UiState<T>(
    val loading: Boolean = true,
    val data: T? = null,
    val error: String? = null,
)

private val repo = DataRepository()

class ClientsViewModel : ViewModel() {
    private val _state = MutableStateFlow(UiState<List<Client>>())
    val state: StateFlow<UiState<List<Client>>> = _state.asStateFlow()
    init { refresh() }
    fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { repo.clients() }
                .onSuccess { _state.value = UiState(false, it, null) }
                .onFailure { _state.value = UiState(false, null, it.message) }
        }
    }
}

class LeadsViewModel : ViewModel() {
    private val _state = MutableStateFlow(UiState<List<Prospect>>())
    val state: StateFlow<UiState<List<Prospect>>> = _state.asStateFlow()
    init { refresh() }
    fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { repo.prospects() }
                .onSuccess { _state.value = UiState(false, it, null) }
                .onFailure { _state.value = UiState(false, null, it.message) }
        }
    }
}

class AgenciesViewModel : ViewModel() {
    private val _state = MutableStateFlow(UiState<List<Agency>>())
    val state: StateFlow<UiState<List<Agency>>> = _state.asStateFlow()
    init { refresh() }
    fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { repo.agencies() }
                .onSuccess { _state.value = UiState(false, it, null) }
                .onFailure { _state.value = UiState(false, null, it.message) }
        }
    }
}

class ReportsViewModel : ViewModel() {
    private val policyRepo = PolicyRepository()
    private val _state = MutableStateFlow(UiState<List<Policy>>())
    val state: StateFlow<UiState<List<Policy>>> = _state.asStateFlow()
    init { refresh() }
    fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { policyRepo.policies() }
                .onSuccess { _state.value = UiState(false, it, null) }
                .onFailure { _state.value = UiState(false, null, it.message) }
        }
    }
}

class CommissionViewModel : ViewModel() {
    private val _state = MutableStateFlow(UiState<CommissionSummary>())
    val state: StateFlow<UiState<CommissionSummary>> = _state.asStateFlow()
    init { refresh() }
    fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { repo.commission() }
                .onSuccess { _state.value = UiState(false, it, null) }
                .onFailure { _state.value = UiState(false, null, it.message) }
        }
    }
}
