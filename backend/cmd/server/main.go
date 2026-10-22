// Command server is the entry point for the BMA rebuild backend.
//
// It wires configuration, the data store, and the HTTP API together. For the
// scaffold it boots with an in-memory/dummy store so the whole app runs end to
// end before Postgres and the LIC-portal integration are implemented.
package main

import (
	"context"
	"errors"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/blissinfosoft/bma-backend/internal/config"
	"github.com/blissinfosoft/bma-backend/internal/httpapi"
	"github.com/blissinfosoft/bma-backend/internal/licportal"
	"github.com/blissinfosoft/bma-backend/internal/store"
)

func main() {
	cfg := config.Load()

	// TODO(store): replace NewDummyStore with a Postgres-backed implementation
	// (store.NewPostgres(cfg.DatabaseURL)) once db/migrations are applied.
	st := store.NewDummyStore()

	// TODO(licportal): replace the stub with your real implementation. The stub
	// returns canned dummy data so the API is fully usable during the rebuild.
	portal := licportal.NewStubClient()

	srv := &http.Server{
		Addr:              cfg.Addr,
		Handler:           httpapi.NewRouter(st, portal),
		ReadHeaderTimeout: 10 * time.Second,
	}

	go func() {
		log.Printf("bma-backend listening on %s (env=%s)", cfg.Addr, cfg.Env)
		if err := srv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			log.Fatalf("server error: %v", err)
		}
	}()

	// Graceful shutdown.
	stop := make(chan os.Signal, 1)
	signal.Notify(stop, syscall.SIGINT, syscall.SIGTERM)
	<-stop
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	_ = srv.Shutdown(ctx)
	log.Println("bma-backend stopped")
}
