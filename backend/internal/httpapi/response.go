// Package httpapi contains the HTTP layer: router, response helpers, handlers.
package httpapi

import (
	"encoding/json"
	"net/http"
)

// envelope is the single, consistent JSON response shape for every endpoint.
// A clean, predictable envelope is one of the deliberate improvements over the
// original app (which used many ad-hoc response formats).
type envelope struct {
	OK    bool        `json:"ok"`
	Data  interface{} `json:"data,omitempty"`
	Error string      `json:"error,omitempty"`
	// Meta carries pagination / counts where relevant.
	Meta interface{} `json:"meta,omitempty"`
}

func writeJSON(w http.ResponseWriter, status int, e envelope) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(e)
}

func ok(w http.ResponseWriter, data interface{}) {
	writeJSON(w, http.StatusOK, envelope{OK: true, Data: data})
}

func okMeta(w http.ResponseWriter, data, meta interface{}) {
	writeJSON(w, http.StatusOK, envelope{OK: true, Data: data, Meta: meta})
}

func fail(w http.ResponseWriter, status int, msg string) {
	writeJSON(w, status, envelope{OK: false, Error: msg})
}
