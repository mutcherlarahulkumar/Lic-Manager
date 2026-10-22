package `in`.bliss.bma.ui.policies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.bliss.bma.data.model.Policy
import `in`.bliss.bma.data.repo.PolicyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** UI state for the Policies screen. */
data class PoliciesUiState(
    val loading: Boolean = true,
    val policies: List<Policy> = emptyList(),
    val error: String? = null,
)

/**
 * PoliciesViewModel exposes [PoliciesUiState] to the Policies screen and loads
 * data from [PolicyRepository]. This is the template to copy for every other
 * feature's ViewModel.
 */
class PoliciesViewModel(
    private val repo: PolicyRepository = PolicyRepository(),
) : ViewModel() {

    private val _state = MutableStateFlow(PoliciesUiState())
    val state: StateFlow<PoliciesUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { repo.policies() }
                .onSuccess { _state.value = PoliciesUiState(loading = false, policies = it) }
                .onFailure { _state.value = PoliciesUiState(loading = false, error = it.message) }
        }
    }
}
