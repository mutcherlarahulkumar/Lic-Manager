// Package store defines the persistence interface and a dummy in-memory
// implementation. Implement Store with Postgres (pgx/sqlc) to go live.
package store

import (
	"context"

	"github.com/blissinfosoft/bma-backend/internal/domain"
)

// Store is the persistence boundary the HTTP layer depends on. Keeping this an
// interface means handlers never import a DB driver and the dummy store below
// can back the whole app until Postgres is wired in.
//
// TODO(store): add NewPostgres(dsn) returning a *PostgresStore that implements
// this interface against db/migrations/0001_init.sql.
type Store interface {
	ListAgencies(ctx context.Context, agentID string) ([]domain.Agency, error)
	ListClients(ctx context.Context, agentID string) ([]domain.Client, error)
	ListPolicies(ctx context.Context, agentID string) ([]domain.Policy, error)
	ListProspects(ctx context.Context, agentID string) ([]domain.Prospect, error)
	GetCommission(ctx context.Context, agentID string, month, year int) (domain.CommissionSummary, error)
}
