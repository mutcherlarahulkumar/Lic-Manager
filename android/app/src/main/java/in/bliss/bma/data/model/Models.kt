package `in`.bliss.bma.data.model

/** Mirrors the backend response envelope: { ok, data, error, meta }. */
data class ApiEnvelope<T>(
    val ok: Boolean,
    val data: T? = null,
    val error: String? = null,
    val meta: Map<String, Any?>? = null,
)

/** A LIC policy (mirrors backend domain.Policy). */
data class Policy(
    val id: String,
    val policyNo: String,
    val planName: String? = null,
    val sumAssured: Double? = null,
    val premium: Double? = null,
    val mode: String? = null,
    val status: String? = null,
    val nextDueDate: String? = null,
    val source: String = "manual",
)
