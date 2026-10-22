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

/** A client / policyholder (mirrors backend domain.Client). */
data class Client(
    val id: String,
    val fullName: String,
    val dateOfBirth: String? = null,
    val mobile: String? = null,
    val email: String? = null,
    val address: String? = null,
    val familyHeadId: String? = null,
)

/** A sales-pipeline lead (mirrors backend domain.Prospect). */
data class Prospect(
    val id: String,
    val fullName: String,
    val mobile: String? = null,
    val stage: String = "new",
    val interest: String? = null,
    val followUpDate: String? = null,
)

/** Commission statement for a month (mirrors backend domain.CommissionSummary). */
data class CommissionSummary(
    val id: String,
    val agentCode: String,
    val month: Int,
    val year: Int,
    val voucherProcessedDate: String? = null,
    val premiumCommission: List<CommissionLine> = emptyList(),
    val paymentRecovery: List<RecoveryLine> = emptyList(),
)

data class CommissionLine(
    val slNo: Int = 0,
    val description: String,
    val premium: Double = 0.0,
    val commission: Double = 0.0,
    val isTotal: Boolean = false,
)

data class RecoveryLine(
    val slNo: Int = 0,
    val description: String,
    val payment: Double = 0.0,
    val recovery: Double = 0.0,
    val isTotal: Boolean = false,
)
