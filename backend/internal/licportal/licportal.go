// Package licportal defines the boundary between this application and the
// external LIC portals that the original app integrated with.
//
// ─────────────────────────────────────────────────────────────────────────────
// IMPORTANT — this package is intentionally a STUB.
//
// The original app fetched data from LIC's agent portals (premium status,
// commission statements, revival quotes, policy data, etc.). Reproducing that
// portal-automation/scraping is NOT included in this rebuild: it must be
// implemented by you, the operator, under whatever agreement/authorization you
// hold with LIC, and ideally against an official LIC API where one exists.
//
// What this file gives you:
//   - A clean, stable interface (Client) describing WHAT each portal operation
//     must return — the request inputs and the response shape.
//   - A StubClient that returns deterministic DUMMY data so the whole rebuild
//     (Go backend + Kotlin app) runs end-to-end today.
//   - TODO markers on every method describing exactly what your real
//     implementation needs to do.
//
// Do NOT store LIC portal credentials in this service's database. Pass them in
// per-request from the agent's device/session, or use an official token where
// available. See docs/REBUILD_TODO.md § "LIC portal integration".
// ─────────────────────────────────────────────────────────────────────────────
package licportal

import (
	"context"
	"time"
)

// Credentials carries the per-request secrets needed to act on an agent's
// behalf against a LIC portal. These are supplied by the caller for the
// duration of one operation and must never be persisted by this service.
type Credentials struct {
	AgentCode string
	// Secret is the agent's own portal secret (password/PIN/OTP/token as your
	// implementation requires). Kept opaque here on purpose.
	Secret string
}

// PremiumStatus is the normalized result of a premium-status lookup.
type PremiumStatus struct {
	PolicyNo    string    `json:"policyNo"`
	Status      string    `json:"status"`      // inforce / lapsed / paidup / ...
	NextDueDate string    `json:"nextDueDate"` // ISO date
	DueAmount   float64   `json:"dueAmount"`
	FetchedAt   time.Time `json:"fetchedAt"`
}

// CommissionStatement is the normalized monthly commission result.
type CommissionStatement struct {
	AgentCode string    `json:"agentCode"`
	Month     int       `json:"month"`
	Year      int       `json:"year"`
	TotalComm float64   `json:"totalCommission"`
	FetchedAt time.Time `json:"fetchedAt"`
}

// RevivalQuote is the normalized result of a policy-revival calculation.
type RevivalQuote struct {
	PolicyNo   string    `json:"policyNo"`
	RevivalAmt float64   `json:"revivalAmount"`
	LateFee    float64   `json:"lateFee"`
	ValidUpto  string    `json:"validUpto"`
	FetchedAt  time.Time `json:"fetchedAt"`
}

// Client is the portal integration boundary. Implement this interface with your
// authorized LIC integration; the rest of the app depends only on this.
type Client interface {
	// FetchPremiumStatus returns the current premium status for one policy.
	//
	// TODO(you): implement against your authorized LIC premium-status source.
	//   Input : agent credentials + policy number.
	//   Output: normalized PremiumStatus (see struct).
	//   Must : time out, surface portal errors clearly, and never persist creds.
	FetchPremiumStatus(ctx context.Context, cr Credentials, policyNo string) (PremiumStatus, error)

	// FetchCommissionStatement returns the commission statement for a month.
	//
	// TODO(you): implement against your authorized LIC commission source and map
	// the result into CommissionStatement + the per-line detail the app stores
	// (see domain.CommissionSummary / premium_commission_details).
	FetchCommissionStatement(ctx context.Context, cr Credentials, month, year int) (CommissionStatement, error)

	// FetchRevivalQuote returns a revival/late-fee quote for a lapsed policy.
	//
	// TODO(you): implement against your authorized LIC revival source.
	FetchRevivalQuote(ctx context.Context, cr Credentials, policyNo string) (RevivalQuote, error)
}
