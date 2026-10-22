package `in`.bliss.bma.data.repo

import `in`.bliss.bma.data.model.Client
import `in`.bliss.bma.data.model.CommissionSummary
import `in`.bliss.bma.data.model.Prospect
import `in`.bliss.bma.data.remote.Network

/**
 * Single source of truth for the list/detail screens. Reads from the backend
 * (dummy data today); add a Room cache here later without changing callers.
 */
class DataRepository {
    suspend fun clients(): List<Client> = Network.api.listClients().unwrap().orEmpty()
    suspend fun prospects(): List<Prospect> = Network.api.listProspects().unwrap().orEmpty()
    suspend fun commission(month: Int? = null, year: Int? = null): CommissionSummary {
        return Network.api.getCommission(month, year).unwrap()
            ?: error("no commission data")
    }
}

private fun <T> `in`.bliss.bma.data.model.ApiEnvelope<T>.unwrap(): T? {
    if (!this.ok) throw IllegalStateException(this.error ?: "request failed")
    return this.data
}
