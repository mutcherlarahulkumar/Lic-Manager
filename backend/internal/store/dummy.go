package store

import (
	"context"
	"time"

	"github.com/blissinfosoft/bma-backend/internal/domain"
)

// DummyStore is an in-memory Store returning deterministic sample data so the
// API and app work before Postgres is connected. Not for production.
type DummyStore struct{}

// NewDummyStore returns a Store backed by canned sample data.
func NewDummyStore() Store { return &DummyStore{} }

func date(y int, m time.Month, d int) *time.Time {
	t := time.Date(y, m, d, 0, 0, 0, 0, time.UTC)
	return &t
}

func (s *DummyStore) ListAgencies(_ context.Context, _ string) ([]domain.Agency, error) {
	return []domain.Agency{
		{ID: "ag-1", AgencyCode: "AG001", Name: "Sample Agency One", BranchCode: "BR-883", Status: "active", ClubTier: "Chairman", Ranking: 3},
		{ID: "ag-2", AgencyCode: "AG002", Name: "Sample Agency Two", BranchCode: "BR-883", Status: "active", ClubTier: "Zonal", Ranking: 11},
	}, nil
}

func (s *DummyStore) ListClients(_ context.Context, _ string) ([]domain.Client, error) {
	return []domain.Client{
		{ID: "cl-1", FullName: "Asha Verma", DateOfBirth: date(1986, 4, 12), Mobile: "90000 00001", Email: "asha@example.in"},
		{ID: "cl-2", FullName: "Rohit Nair", DateOfBirth: date(1979, 11, 2), Mobile: "90000 00002"},
		{ID: "cl-3", FullName: "Meera Das", DateOfBirth: date(1991, 1, 23), Mobile: "90000 00003", FamilyHead: "cl-1"},
	}, nil
}

func (s *DummyStore) ListPolicies(_ context.Context, _ string) ([]domain.Policy, error) {
	return []domain.Policy{
		{ID: "pol-1", ClientID: "cl-1", PolicyNo: "123456789", PlanName: "Endowment", SumAssured: 500000, Premium: 24500, Mode: "yly", Status: "inforce", NextDueDate: date(2026, 12, 1), Source: "manual"},
		{ID: "pol-2", ClientID: "cl-2", PolicyNo: "987654321", PlanName: "Jeevan Anand", SumAssured: 1000000, Premium: 51200, Mode: "hly", Status: "lapsed", NextDueDate: date(2026, 3, 15), Source: "manual"},
	}, nil
}

func (s *DummyStore) ListProspects(_ context.Context, _ string) ([]domain.Prospect, error) {
	return []domain.Prospect{
		{ID: "pr-1", FullName: "Karan Shah", Mobile: "90000 11111", Stage: "contacted", Interest: "Child plan", FollowUpDate: date(2026, 11, 5)},
		{ID: "pr-2", FullName: "Divya Rao", Mobile: "90000 22222", Stage: "quoted", Interest: "Pension"},
	}, nil
}

func (s *DummyStore) GetCommission(_ context.Context, _ string, month, year int) (domain.CommissionSummary, error) {
	return domain.CommissionSummary{
		ID: "cs-1", AgentCode: "DEMO-AG-001", Month: month, Year: year,
		VoucherProcessedDate: date(year, time.Month(month), 7),
		PremiumCommission: []domain.PremiumCommissionLine{
			{SlNo: 1, Description: "First year commission", Premium: 180000, Commission: 45000},
			{SlNo: 2, Description: "Renewal commission", Premium: 90000, Commission: 4500},
			{SlNo: 0, Description: "Total", Premium: 270000, Commission: 49500, IsTotal: true},
		},
		PaymentRecovery: []domain.PaymentRecoveryLine{
			{SlNo: 1, Description: "Net payable", Payment: 49500, Recovery: 0, IsTotal: true},
		},
	}, nil
}

var _ Store = (*DummyStore)(nil)
