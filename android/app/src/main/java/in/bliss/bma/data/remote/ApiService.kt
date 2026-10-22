package `in`.bliss.bma.data.remote

import `in`.bliss.bma.data.model.ApiEnvelope
import `in`.bliss.bma.data.model.Client
import `in`.bliss.bma.data.model.CommissionSummary
import `in`.bliss.bma.data.model.Policy
import `in`.bliss.bma.data.model.Prospect
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Typed client for the BMA Go backend. Every endpoint returns the backend's
 * consistent [ApiEnvelope] shape ({ ok, data, error, meta }).
 */
interface ApiService {

    /** GET /api/v1/policies — the agent's policies. */
    @GET("api/v1/policies")
    suspend fun listPolicies(): ApiEnvelope<List<Policy>>

    /** GET /api/v1/clients — the agent's clients/policyholders. */
    @GET("api/v1/clients")
    suspend fun listClients(): ApiEnvelope<List<Client>>

    /** GET /api/v1/prospects — the agent's sales pipeline. */
    @GET("api/v1/prospects")
    suspend fun listProspects(): ApiEnvelope<List<Prospect>>

    /** GET /api/v1/commission?month&year — commission statement for a month. */
    @GET("api/v1/commission")
    suspend fun getCommission(
        @Query("month") month: Int? = null,
        @Query("year") year: Int? = null,
    ): ApiEnvelope<CommissionSummary>

    // TODO: agencies, reports, marketing, tools; portal endpoints (stubbed backend).
}
