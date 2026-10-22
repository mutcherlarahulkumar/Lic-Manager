package `in`.bliss.bma.data.remote

import `in`.bliss.bma.data.model.ApiEnvelope
import `in`.bliss.bma.data.model.Policy
import retrofit2.http.GET

/**
 * Typed client for the BMA Go backend.
 *
 * Every endpoint returns the backend's consistent [ApiEnvelope] shape
 * ({ ok, data, error, meta }). Add new feature endpoints here as you build out
 * the screens listed in android/README.md.
 */
interface ApiService {

    /** GET /api/v1/policies — the agent's policies (policy_list screen). */
    @GET("api/v1/policies")
    suspend fun listPolicies(): ApiEnvelope<List<Policy>>

    // TODO: listClients(), listAgencies(), listProspects(), getCommission(...)
    // TODO: portal endpoints (premium status etc.) — these hit the stubbed
    //       backend route until the licportal integration is implemented.
}
