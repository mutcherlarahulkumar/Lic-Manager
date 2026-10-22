// Package config loads runtime configuration from environment variables.
package config

import "os"

// Config holds all backend runtime settings. Keep secrets out of source:
// everything here comes from the environment (or your deployment secrets store).
type Config struct {
	Env         string // "dev" | "staging" | "prod"
	Addr        string // listen address, e.g. ":8080"
	DatabaseURL string // Postgres DSN, e.g. postgres://user:pass@host:5432/bma?sslmode=require

	// BlissAPIBaseURL is YOUR existing backend (bmaservices.in) that the rebuild
	// backend may call during migration. Leave empty to run fully on dummy data.
	BlissAPIBaseURL string
}

// Load reads configuration from the environment, applying sensible dev defaults.
// Hosting platforms (Render, Fly, Cloud Run, …) inject PORT; we honor it so the
// same binary runs locally and in the cloud with no changes.
func Load() Config {
	addr := env("BMA_ADDR", ":8080")
	if p := os.Getenv("PORT"); p != "" {
		addr = ":" + p
	}
	return Config{
		Env:             env("BMA_ENV", "dev"),
		Addr:            addr,
		DatabaseURL:     env("DATABASE_URL", ""),
		BlissAPIBaseURL: env("BLISS_API_BASE_URL", ""),
	}
}

func env(k, def string) string {
	if v := os.Getenv(k); v != "" {
		return v
	}
	return def
}
