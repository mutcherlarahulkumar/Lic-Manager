package httpapi

import (
	"net/http"

	"github.com/blissinfosoft/bma-backend/internal/licportal"
	"github.com/blissinfosoft/bma-backend/internal/store"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
)

// NewRouter builds the HTTP API. Route groups map 1:1 to the app's feature
// domains; see docs/REBUILD_TODO.md for the full endpoint checklist.
func NewRouter(st store.Store, portal licportal.Client) http.Handler {
	h := &handlers{store: st, portal: portal}

	r := chi.NewRouter()
	r.Use(middleware.RequestID)
	r.Use(middleware.RealIP)
	r.Use(middleware.Logger)
	r.Use(middleware.Recoverer)

	// Health.
	r.Get("/healthz", func(w http.ResponseWriter, _ *http.Request) { ok(w, map[string]string{"status": "up"}) })

	r.Route("/api/v1", func(r chi.Router) {
		// TODO(auth): mount auth middleware here (bearer/session) for everything
		// below except the auth endpoints themselves.

		// TODO(auth): POST /auth/register, /auth/login, /auth/otp, /auth/mpin,
		// /auth/forgot-password — replaces the original app's many login flows
		// with one clean scheme. See REBUILD_TODO.md § Auth.

		r.Get("/agencies", h.listAgencies)
		r.Get("/clients", h.listClients)
		r.Get("/policies", h.listPolicies)
		r.Get("/prospects", h.listProspects)
		r.Get("/commission", h.getCommission)

		// LIC portal (stubbed — returns dummy data).
		r.Get("/portal/premium-status/{policyNo}", h.premiumStatus)
		// TODO: /portal/commission, /portal/revival — wire to licportal.Client.
	})

	return r
}
