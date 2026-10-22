module github.com/blissinfosoft/bma-backend

go 1.23

// Intentionally dependency-light for the scaffold: only chi (router) is used so
// the handlers read clearly. Swap in your preferred stack (sqlc/pgx, zap, etc.)
// as you flesh out the TODOs.
require github.com/go-chi/chi/v5 v5.1.0
