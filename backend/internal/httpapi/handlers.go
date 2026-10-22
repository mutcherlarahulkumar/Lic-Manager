package httpapi

import (
	"net/http"
	"strconv"
	"time"

	"github.com/blissinfosoft/bma-backend/internal/licportal"
	"github.com/blissinfosoft/bma-backend/internal/store"
	"github.com/go-chi/chi/v5"
)

// handlers bundles the dependencies every endpoint needs.
type handlers struct {
	store  store.Store
	portal licportal.Client
}

// agentID extracts the authenticated agent's id from the request.
//
// TODO(auth): replace this with real auth — validate the bearer token / session
// issued by the auth endpoints and return the agent id from the claims. For the
// scaffold it returns a fixed demo id so protected endpoints work immediately.
func agentID(_ *http.Request) string { return "demo-agent" }

// --- Agencies -----------------------------------------------------------------

// listAgencies GET /api/v1/agencies
// Returns the agencies the authenticated agent manages. (agency_list screen)
func (h *handlers) listAgencies(w http.ResponseWriter, r *http.Request) {
	items, err := h.store.ListAgencies(r.Context(), agentID(r))
	if err != nil {
		fail(w, http.StatusInternalServerError, err.Error())
		return
	}
	okMeta(w, items, map[string]int{"count": len(items)})
}

// --- Clients ------------------------------------------------------------------

// listClients GET /api/v1/clients
// Returns the agent's clients/policyholders. (client_list screen)
func (h *handlers) listClients(w http.ResponseWriter, r *http.Request) {
	items, err := h.store.ListClients(r.Context(), agentID(r))
	if err != nil {
		fail(w, http.StatusInternalServerError, err.Error())
		return
	}
	okMeta(w, items, map[string]int{"count": len(items)})
}

// --- Policies -----------------------------------------------------------------

// listPolicies GET /api/v1/policies
// Returns the agent's policies. (policy_list screen)
func (h *handlers) listPolicies(w http.ResponseWriter, r *http.Request) {
	items, err := h.store.ListPolicies(r.Context(), agentID(r))
	if err != nil {
		fail(w, http.StatusInternalServerError, err.Error())
		return
	}
	okMeta(w, items, map[string]int{"count": len(items)})
}

// --- Prospects ----------------------------------------------------------------

// listProspects GET /api/v1/prospects
// Returns the agent's sales pipeline. (prospect / leads screens)
func (h *handlers) listProspects(w http.ResponseWriter, r *http.Request) {
	items, err := h.store.ListProspects(r.Context(), agentID(r))
	if err != nil {
		fail(w, http.StatusInternalServerError, err.Error())
		return
	}
	okMeta(w, items, map[string]int{"count": len(items)})
}

// --- Commission ---------------------------------------------------------------

// getCommission GET /api/v1/commission?month=MM&year=YYYY
// Returns the locally-stored commission statement for a month. (commission screen)
func (h *handlers) getCommission(w http.ResponseWriter, r *http.Request) {
	month, _ := strconv.Atoi(r.URL.Query().Get("month"))
	year, _ := strconv.Atoi(r.URL.Query().Get("year"))
	if month == 0 {
		month = int(time.Now().Month())
	}
	if year == 0 {
		year = time.Now().Year()
	}
	sum, err := h.store.GetCommission(r.Context(), agentID(r), month, year)
	if err != nil {
		fail(w, http.StatusInternalServerError, err.Error())
		return
	}
	ok(w, sum)
}

// --- LIC portal (stubbed) -----------------------------------------------------

// premiumStatus GET /api/v1/portal/premium-status/{policyNo}
// Proxies to the LIC-portal integration. Returns DUMMY data until you implement
// licportal.Client. The agent's portal secret must arrive per-request via the
// X-Portal-Secret header and is never stored. (policy_status / any_status)
func (h *handlers) premiumStatus(w http.ResponseWriter, r *http.Request) {
	policyNo := chi.URLParam(r, "policyNo")
	cr := licportal.Credentials{AgentCode: r.Header.Get("X-Agent-Code"), Secret: r.Header.Get("X-Portal-Secret")}
	res, err := h.portal.FetchPremiumStatus(r.Context(), cr, policyNo)
	if err != nil {
		fail(w, http.StatusBadGateway, err.Error())
		return
	}
	okMeta(w, res, map[string]any{"stub": true, "note": "dummy data — implement licportal.Client"})
}
