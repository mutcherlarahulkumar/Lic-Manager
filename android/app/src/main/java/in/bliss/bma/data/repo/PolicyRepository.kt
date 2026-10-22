package `in`.bliss.bma.data.repo

import `in`.bliss.bma.data.model.Policy
import `in`.bliss.bma.data.remote.Network

/**
 * PolicyRepository is the single source of truth for policy data in the UI.
 *
 * For the scaffold it reads straight from the backend. As you build offline
 * support, add a Room cache here and expose Flow<List<Policy>>; the ViewModel
 * API stays the same.
 */
class PolicyRepository {
    /** Fetches the agent's policies. Throws on network/parse error. */
    suspend fun policies(): List<Policy> {
        val env = Network.api.listPolicies()
        if (!env.ok) error(env.error ?: "request failed")
        return env.data.orEmpty()
    }
}
